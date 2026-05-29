import { useRef, useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";
import { useNavigate } from "react-router-dom";

const Register = () => {
    const userInfo = [{
        field: "fullName",
        label: "Họ và tên",
        type: "text"
    }, {
        field: "phone",
        label: "Số điện thoại",
        type: "tel"
    }, {
        field: "email",
        label: "Email",
        type: "email"
    }, {
        field: "password",
        label: "Mật khẩu",
        type: "password"
    }, {
        field: "confirm",
        label: "Xác nhận mật khẩu",
        type: "password"
    }];

    const organizerInfo = [{
        field: "organizationName",
        label: "Tên tổ chức",
        type: "text"
    }, {
        field: "identityCard",
        label: "CCCD/CMND",
        type: "text"
    }, {
        field: "taxCode",
        label: "Mã số thuế",
        type: "text"
    }];

    const [role, setRole] = useState("attendee");
    const [attendee, setAttendee] = useState({});
    const [organizer, setOrganizer] = useState({});
    const [err, setErr] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const attendeeAvatar = useRef();
    const organizerAvatar = useRef();
    const nav = useNavigate();

    const currentUser = role === "organizer" ? organizer : attendee;

    const validate = () => {
        if (currentUser.password !== currentUser.confirm) {
            setErr('Mật khẩu không khớp!');
            setFieldErrors({confirm: 'Mat khau khong khop!'});
            return false;
        }

        setErr("");
        setFieldErrors({});
        return true;
    }

    const register = async (e) => {
        e.preventDefault();

        if (validate()) {
            let form = new FormData();

            for (var key of Object.keys(currentUser)) {
                if (key !== 'confirm')
                    form.append(key, currentUser[key]);
            }

            const avatarRef = role === "organizer" ? organizerAvatar : attendeeAvatar;
            if (avatarRef.current.files.length > 0)
                form.append('avatar', avatarRef.current.files[0]);

            try {
                setLoading(true);
                let endpoint = role === "organizer" ? endpoints['register-organizer'] : endpoints['register-attendee'];
                const res = await Apis.post(endpoint, form, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });
                if (res.status === 201)
                    nav('/login');
            } catch (ex) {
                console.error(ex);
                const data = ex.response?.data;
                setErr(data?.message || "Dang ky that bai!");
                setFieldErrors(data?.errors || {});
            } finally {
                setLoading(false);
            }
        }
    }

    return (
        <div className="auth-wrap glass-panel">
            <div className="page-kicker mb-2">Join the list</div>
            <h1 className="auth-title">Đăng ký</h1>
            <p className="auth-copy">Tạo tài khoản mới cho người mua vé hoặc nhà tổ chức.</p>

            {err && <Alert variant="danger">
                <div>{err}</div>
                {Object.keys(fieldErrors).length > 0 && <ul className="mb-0 mt-2">
                    {Object.entries(fieldErrors).map(([field, message]) => <li key={field}>{message}</li>)}
                </ul>}
            </Alert>}

            <div className="register-switch mb-4" role="tablist" aria-label="Loại tài khoản">
                <button
                    type="button"
                    className={`register-switch-btn ${role === "attendee" ? "active" : ""}`}
                    onClick={() => {
                        setRole("attendee");
                        setErr("");
                        setFieldErrors({});
                    }}>
                    Người mua vé
                </button>
                <button
                    type="button"
                    className={`register-switch-btn ${role === "organizer" ? "active" : ""}`}
                    onClick={() => {
                        setRole("organizer");
                        setErr("");
                        setFieldErrors({});
                    }}>
                    Nhà tổ chức
                </button>
            </div>

            {role === "attendee" ? <Form onSubmit={register}>
                {userInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={`attendee-${u.field}`}>
                    <Form.Label>{u.label}</Form.Label>
                    <Form.Control type={u.type} placeholder={u.label} value={attendee[u.field] || ""} onChange={e => setAttendee({...attendee, [u.field]: e.target.value})} isInvalid={!!fieldErrors[u.field]} required />
                    <Form.Control.Feedback type="invalid">{fieldErrors[u.field]}</Form.Control.Feedback>
                </Form.Group>)}

                <Form.Group className="mb-3" controlId="attendee-avatar">
                    <Form.Label>Ảnh đại diện</Form.Label>
                    <Form.Control ref={attendeeAvatar} type="file" accept="image/*" required />
                </Form.Group>

                <Form.Group className="mb-3">
                    {loading === true ? <MySpinner /> : <Button className="btn-pink w-100" type="submit">Đăng ký người mua vé</Button>}
                </Form.Group>
            </Form> : <Form onSubmit={register}>
                {userInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={`organizer-${u.field}`}>
                    <Form.Label>{u.label}</Form.Label>
                    <Form.Control type={u.type} placeholder={u.label} value={organizer[u.field] || ""} onChange={e => setOrganizer({...organizer, [u.field]: e.target.value})} isInvalid={!!fieldErrors[u.field]} required />
                    <Form.Control.Feedback type="invalid">{fieldErrors[u.field]}</Form.Control.Feedback>
                </Form.Group>)}

                <div className="organizer-fields">
                    {organizerInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={`organizer-${u.field}`}>
                        <Form.Label>{u.label}</Form.Label>
                        <Form.Control type={u.type} placeholder={u.label} value={organizer[u.field] || ""} onChange={e => setOrganizer({...organizer, [u.field]: e.target.value})} isInvalid={!!fieldErrors[u.field]} required />
                        <Form.Control.Feedback type="invalid">{fieldErrors[u.field]}</Form.Control.Feedback>
                    </Form.Group>)}
                </div>

                <Form.Group className="mb-3" controlId="organizer-avatar">
                    <Form.Label>Ảnh đại diện</Form.Label>
                    <Form.Control ref={organizerAvatar} type="file" accept="image/*" required />
                </Form.Group>

                <Form.Group className="mb-3">
                    {loading === true ? <MySpinner /> : <Button className="btn-pink w-100" type="submit">Đăng ký nhà tổ chức</Button>}
                </Form.Group>
            </Form>}
        </div>
    );
}

export default Register;
