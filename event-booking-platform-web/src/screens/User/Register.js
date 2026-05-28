import { useRef, useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";
import { useNavigate } from "react-router-dom";

const Register = () => {
    const userInfo = [{
        field: "fullName",
        label: "Ho va ten",
        type: "text"
    }, {
        field: "phone",
        label: "So dien thoai",
        type: "tel"
    }, {
        field: "email",
        label: "Email",
        type: "email"
    }, {
        field: "password",
        label: "Mat khau",
        type: "password"
    }, {
        field: "confirm",
        label: "Xac nhan mat khau",
        type: "password"
    }];

    const organizerInfo = [{
        field: "organizationName",
        label: "Ten to chuc",
        type: "text"
    }, {
        field: "identityCard",
        label: "CCCD/CMND",
        type: "text"
    }, {
        field: "businessLicense",
        label: "Giay phep kinh doanh",
        type: "text"
    }, {
        field: "taxCode",
        label: "Ma so thue",
        type: "text"
    }];

    const [role, setRole] = useState("attendee");
    const [attendee, setAttendee] = useState({});
    const [organizer, setOrganizer] = useState({});
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);
    const attendeeAvatar = useRef();
    const organizerAvatar = useRef();
    const nav = useNavigate();

    const currentUser = role === "organizer" ? organizer : attendee;

    const validate = () => {
        if (currentUser.password !== currentUser.confirm) {
            setErr('Mat khau khong khop!');
            return false;
        }

        setErr("");
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
                setErr(ex.response?.data?.message || "Dang ky that bai!");
            } finally {
                setLoading(false);
            }
        }
    }

    return (
        <div className="auth-wrap glass-panel">
            <div className="page-kicker mb-2">Join the list</div>
            <h1 className="auth-title">Dang ky</h1>
            <p className="auth-copy">Tao tai khoan moi cho nguoi mua ve hoac nha to chuc.</p>

            {err && <Alert variant="danger">{err}</Alert>}

            <div className="register-switch mb-4" role="tablist" aria-label="Loai tai khoan">
                <button
                    type="button"
                    className={`register-switch-btn ${role === "attendee" ? "active" : ""}`}
                    onClick={() => {
                        setRole("attendee");
                        setErr("");
                    }}>
                    Nguoi mua ve
                </button>
                <button
                    type="button"
                    className={`register-switch-btn ${role === "organizer" ? "active" : ""}`}
                    onClick={() => {
                        setRole("organizer");
                        setErr("");
                    }}>
                    Nha to chuc
                </button>
            </div>

            {role === "attendee" ? <Form onSubmit={register}>
                {userInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={`attendee-${u.field}`}>
                    <Form.Label>{u.label}</Form.Label>
                    <Form.Control type={u.type} placeholder={u.label} value={attendee[u.field] || ""} onChange={e => setAttendee({...attendee, [u.field]: e.target.value})} required />
                </Form.Group>)}

                <Form.Group className="mb-3" controlId="attendee-avatar">
                    <Form.Label>Anh dai dien</Form.Label>
                    <Form.Control ref={attendeeAvatar} type="file" accept="image/*" />
                </Form.Group>

                <Form.Group className="mb-3">
                    {loading === true ? <MySpinner /> : <Button className="btn-pink w-100" type="submit">Dang ky nguoi mua ve</Button>}
                </Form.Group>
            </Form> : <Form onSubmit={register}>
                {userInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={`organizer-${u.field}`}>
                    <Form.Label>{u.label}</Form.Label>
                    <Form.Control type={u.type} placeholder={u.label} value={organizer[u.field] || ""} onChange={e => setOrganizer({...organizer, [u.field]: e.target.value})} required />
                </Form.Group>)}

                <div className="organizer-fields">
                    {organizerInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={`organizer-${u.field}`}>
                        <Form.Label>{u.label}</Form.Label>
                        <Form.Control type={u.type} placeholder={u.label} value={organizer[u.field] || ""} onChange={e => setOrganizer({...organizer, [u.field]: e.target.value})} required />
                    </Form.Group>)}
                </div>

                <Form.Group className="mb-3" controlId="organizer-avatar">
                    <Form.Label>Anh dai dien</Form.Label>
                    <Form.Control ref={organizerAvatar} type="file" accept="image/*" />
                </Form.Group>

                <Form.Group className="mb-3">
                    {loading === true ? <MySpinner /> : <Button className="btn-pink w-100" type="submit">Dang ky nha to chuc</Button>}
                </Form.Group>
            </Form>}
        </div>
    );
}

export default Register;
