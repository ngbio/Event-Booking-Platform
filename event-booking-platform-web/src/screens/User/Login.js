import { useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { useNavigate, useSearchParams } from "react-router-dom";
import cookies from 'react-cookies'

const Login = () => {
    const userInfo = [{
        field: "email",
        label: "Email",
        type: "email"
    }, {
        field: "password",
        label: "Mat khau",
        type: "password"
    }];

    const [user, setUser] = useState({})
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();

    const nav = useNavigate();

    const validate = () => {
        if (!user.email || !user.password) {
            setErr("Vui long nhap email va mat khau!");
            return false;
        }

        setErr("");
        return true;
    }

    const login = async (e) => {
        e.preventDefault();

        if (validate()) {
            try {
                setLoading(true);

                let res = await Apis.post(endpoints['login'], {...user});
                let loginData = res.data.data;
                cookies.save('token', loginData.token);

                let p = await authApis().get(endpoints['profile']);
                cookies.save('user', p.data.data);

                let next = q.get('next')
                if (next)
                    nav(next);
                else
                    nav('/');
            } catch (ex) {
                console.error(ex);
                setErr(ex.response?.data?.message || "Dang nhap that bai!");
            } finally {
                setLoading(false);
            }
        }
    }

    return (
        <>
            <h1 className="text-center text-success mt-1">DANG NHAP NGUOI DUNG</h1>

            {err && <Alert variant="danger">{err}</Alert>}

            <Form onSubmit={login}>
                {userInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={u.field}>
                    <Form.Label>{u.label}</Form.Label>
                    <Form.Control type={u.type} placeholder={u.label} value={user[u.field] || ""} onChange={e => setUser({...user, [u.field]: e.target.value})} required />
                </Form.Group>)}

                <Form.Group className="mb-3">
                    {loading === true ? <MySpinner /> : <Button variant="success" type="submit">Dang nhap</Button>}
                </Form.Group>
            </Form>
        </>
    );
}

export default Login;
