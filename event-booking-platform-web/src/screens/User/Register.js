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

    const [user, setUser] = useState({
        role: "attendee"
    })
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);
    const avatar = useRef();
    const nav = useNavigate();

    const validate = () => {
        if (user.password !== user.confirm) {
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

            for (var key of Object.keys(user)) {
                if (key !== 'confirm' && key !== 'role')
                    form.append(key, user[key]);
            }

            if (avatar.current.files.length > 0)
                form.append('avatar', avatar.current.files[0]);

            try {
                setLoading(true);
                let endpoint = user.role === "organizer" ? endpoints['register-organizer'] : endpoints['register-attendee'];
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
        <>
            <h1 className="text-center text-success mt-1">DANG KY NGUOI DUNG</h1>

            {err && <Alert variant="danger">{err}</Alert>}

            <Form onSubmit={register}>
                <Form.Group className="mb-3" controlId="role">
                    <Form.Label>Loai tai khoan</Form.Label>
                    <Form.Select value={user.role} onChange={e => setUser({...user, role: e.target.value})}>
                        <option value="attendee">Nguoi mua ve</option>
                        <option value="organizer">Nha to chuc</option>
                    </Form.Select>
                </Form.Group>

                {userInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={u.field}>
                    <Form.Label>{u.label}</Form.Label>
                    <Form.Control type={u.type} placeholder={u.label} value={user[u.field] || ""} onChange={e => setUser({...user, [u.field]: e.target.value})} required />
                </Form.Group>)}

                {user.role === "organizer" && organizerInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={u.field}>
                    <Form.Label>{u.label}</Form.Label>
                    <Form.Control type={u.type} placeholder={u.label} value={user[u.field] || ""} onChange={e => setUser({...user, [u.field]: e.target.value})} />
                </Form.Group>)}

                <Form.Group className="mb-3" controlId="avatar">
                    <Form.Label>Anh dai dien</Form.Label>
                    <Form.Control ref={avatar} type="file" accept="image/*" />
                </Form.Group>

                <Form.Group className="mb-3">
                    {loading === true ? <MySpinner /> : <Button variant="success" type="submit">Dang ky</Button>}
                </Form.Group>
            </Form>
        </>
    );
}

export default Register;
