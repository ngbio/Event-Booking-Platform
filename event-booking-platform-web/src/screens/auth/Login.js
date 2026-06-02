import { useContext, useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import cookies from 'react-cookies'
import { MyUserContext } from "../../configs/Contexts";

const Login = () => {
    const userInfo = [{
        field: "email",
        label: "Email",
        type: "email"
    }, {
        field: "password",
        label: "Mật khẩu",
        type: "password"
    }];

    const [user, setUser] = useState({})
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);
    const [, dispatch] = useContext(MyUserContext);
    const [q] = useSearchParams();
    const location = useLocation();

    const nav = useNavigate();
    const registerMessage = location.state?.message;

    const validate = () => {
        if (!user.email || !user.password) {
            setErr("Vui lòng nhập email và mật khẩu!");
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
                let profile = p.data.data;
                cookies.save('user', profile);
                dispatch({
                    type: "LOGIN",
                    payload: profile
                });

                let next = q.get('next')
                if (next)
                    nav(next);
                else
                    nav('/');
            } catch (ex) {
                console.error(ex);
                setErr(ex.response?.data?.message || "Đăng nhập thất bại!");
            } finally {
                setLoading(false);
            }
        }
    }

    return (
        <div className="auth-wrap glass-panel">
            <div className="page-kicker mb-2">Welcome back</div>
            <h1 className="auth-title">Đăng nhập</h1>
            <p className="auth-copy">Tiếp tục với tài khoản Event Booking của bạn.</p>

            {registerMessage && <Alert variant="success">{registerMessage}</Alert>}
            {err && <Alert variant="danger">{err}</Alert>}

            <Form onSubmit={login}>
                {userInfo.map(u => <Form.Group key={u.field} className="mb-3" controlId={u.field}>
                    <Form.Label>{u.label}</Form.Label>
                    <Form.Control type={u.type} placeholder={u.label} value={user[u.field] || ""} onChange={e => setUser({...user, [u.field]: e.target.value})} required />
                </Form.Group>)}

                <Form.Group className="mb-3">
                    {loading === true ? <MySpinner /> : <Button className="btn-pink w-100" type="submit">Đăng nhập</Button>}
                </Form.Group>
            </Form>
        </div>
    );
}

export default Login;
