import { useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import { Link } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import { authApis, endpoints } from "../../configs/Apis";

const ChangePassword = () => {
    const [form, setForm] = useState({
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
    });
    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const updateForm = (field, value) => {
        setForm({...form, [field]: value});
        setFieldErrors({...fieldErrors, [field]: ""});
        setErr("");
        setSuccess("");
    }

    const validate = () => {
        const errors = {};

        if (!form.oldPassword.trim())
            errors.oldPassword = "Vui lòng nhập mật khẩu cũ";
        if (!form.newPassword)
            errors.newPassword = "Vui lòng nhập mật khẩu mới";
        else if (!/^(?=.*[A-Za-z])(?=.*\d)(?!.*\s)[A-Za-z\d@$!%*#?&]{6,}$/.test(form.newPassword))
            errors.newPassword = "Mật khẩu mới phải từ 6 ký tự, có ít nhất 1 chữ cái và 1 số";
        if (!form.confirmPassword)
            errors.confirmPassword = "Vui lòng xác nhận mật khẩu mới";
        else if (form.newPassword !== form.confirmPassword)
            errors.confirmPassword = "Mật khẩu xác nhận không trùng khớp";

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    }

    const changePassword = async (e) => {
        e.preventDefault();

        if (!validate())
            return;

        try {
            setLoading(true);
            setErr("");
            setSuccess("");
            setFieldErrors({});

            const res = await authApis().patch(endpoints["change-password"], form);
            setForm({
                oldPassword: "",
                newPassword: "",
                confirmPassword: ""
            });
            setSuccess(res.data.message || "Đổi mật khẩu thành công");
        } catch (ex) {
            console.error(ex);
            const data = ex.response?.data;
            setErr(data?.message || "Đổi mật khẩu thất bại");
            setFieldErrors(data?.errors || {});
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="profile-screen">
            <section className="profile-form glass-panel">
                <div className="page-kicker mb-2">Account</div>
                <h2>Đổi mật khẩu</h2>
                <p>Cập nhật mật khẩu đăng nhập của tài khoản.</p>

                {err && <Alert variant="danger">
                    <div>{err}</div>
                    {Object.keys(fieldErrors).length > 0 && <ul className="mb-0 mt-2">
                        {Object.entries(fieldErrors).map(([field, message]) => <li key={field}>{message}</li>)}
                    </ul>}
                </Alert>}

                {success && <Alert variant="success">{success}</Alert>}

                <Form onSubmit={changePassword}>
                    <Form.Group className="mb-3" controlId="oldPassword">
                        <Form.Label>Mật khẩu cũ</Form.Label>
                        <Form.Control
                            type="password"
                            value={form.oldPassword}
                            onChange={e => updateForm("oldPassword", e.target.value)}
                            isInvalid={!!fieldErrors.oldPassword}
                            autoComplete="current-password"
                            required />
                        <Form.Control.Feedback type="invalid">{fieldErrors.oldPassword}</Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="newPassword">
                        <Form.Label>Mật khẩu mới</Form.Label>
                        <Form.Control
                            type="password"
                            value={form.newPassword}
                            onChange={e => updateForm("newPassword", e.target.value)}
                            isInvalid={!!fieldErrors.newPassword}
                            autoComplete="new-password"
                            required />
                        <Form.Control.Feedback type="invalid">{fieldErrors.newPassword}</Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-4" controlId="confirmPassword">
                        <Form.Label>Xác nhận mật khẩu mới</Form.Label>
                        <Form.Control
                            type="password"
                            value={form.confirmPassword}
                            onChange={e => updateForm("confirmPassword", e.target.value)}
                            isInvalid={!!fieldErrors.confirmPassword}
                            autoComplete="new-password"
                            required />
                        <Form.Control.Feedback type="invalid">{fieldErrors.confirmPassword}</Form.Control.Feedback>
                    </Form.Group>

                    <div className="d-flex gap-2 flex-wrap">
                        <Button className="btn-pink px-4" type="submit" disabled={loading}>
                            {loading ? <MySpinner /> : "Đổi mật khẩu"}
                        </Button>
                        <Button as={Link} to="/profile" variant="outline-light" className="px-4">
                            Quay lại hồ sơ
                        </Button>
                    </div>
                </Form>
            </section>
        </div>
    );
}

export default ChangePassword;
