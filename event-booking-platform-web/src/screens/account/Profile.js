import { useContext, useEffect, useRef, useState } from "react";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";
import cookies from "react-cookies";
import MySpinner from "../../components/MySpinner";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const Profile = () => {
    const [profile, setProfile] = useState(null);
    const [form, setForm] = useState({});
    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [, dispatch] = useContext(MyUserContext);
    const avatarRef = useRef();

    const isOrganizer = profile?.roleId === 2;

    const toInputDate = (value) => {
        if (!value)
            return "";

        return value.replace(" ", "T").slice(0, 10);
    }

    const fillForm = (data) => {
        setForm({
            fullName: data.fullName || "",
            phone: data.phone || "",
            birthDate: toInputDate(data.birthDate),
            gender: data.gender || "",
            organizationName: data.organizationName || "",
            identityCard: data.identityCard || "",
            taxCode: data.taxCode || ""
        });
    }

    const loadProfile = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await authApis().get(endpoints["profile"]);
            const data = res.data.data;
            setProfile(data);
            fillForm(data);
            cookies.save("user", data);
            dispatch({
                type: "LOGIN",
                payload: data
            });
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || "Không thể tải được thông tin cá nhân.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadProfile();
        
    }, []);

    const updateForm = (field, value) => {
        setForm({...form, [field]: value});
        setFieldErrors({...fieldErrors, [field]: ""});
    }

    const saveProfile = async (e) => {
        e.preventDefault();

        const data = new FormData();
        data.append("fullName", form.fullName || "");
        data.append("phone", form.phone || "");

        if (isOrganizer) {
            data.append("organizationName", form.organizationName || "");
            data.append("identityCard", form.identityCard || "");
            data.append("taxCode", form.taxCode || "");
        } else {
            data.append("birthDate", form.birthDate || "");
            data.append("gender", form.gender || "");
        }

        if (avatarRef.current?.files?.length > 0)
            data.append("avatar", avatarRef.current.files[0]);

        try {
            setSaving(true);
            setErr("");
            setSuccess("");
            setFieldErrors({});

            const res = await authApis().patch(endpoints["profile"], data, {
                headers: {
                    "Content-Type": "multipart/form-data"
                }
            });

            const updated = res.data.data;
            setProfile(updated);
            fillForm(updated);
            cookies.save("user", updated);
            dispatch({
                type: "LOGIN",
                payload: updated
            });
            if (avatarRef.current)
                avatarRef.current.value = "";
            setSuccess(res.data.message || "Cập nhật thông tin thành công.");
        } catch (ex) {
            console.error(ex);
            const data = ex.response?.data;
            setErr(data?.message || "Cập nhật thông tin thất bại.");
            setFieldErrors(data?.errors || {});
        } finally {
            setSaving(false);
        }
    }

    if (loading)
        return <MySpinner />;

    if (!profile && err)
        return <Alert className="alert-dark-pink">{err}</Alert>;

    return (
        <div className="profile-screen">
            <Row className="profile-layout">
                <Col lg={4}>
                    <aside className="profile-card glass-panel">
                        <div className="profile-avatar-wrap">
                            {profile?.avatar ? <img className="profile-avatar" src={profile.avatar} alt={profile.fullName || profile.email} /> :
                                <div className="profile-avatar-fallback">{(profile?.fullName || profile?.email || "U").charAt(0)}</div>}
                        </div>

                        <h1>{profile?.fullName || "Người dùng"}</h1>
                        <p>{profile?.email}</p>

                        <div className="profile-badges">
                            <span>{profile?.roleName || "ROLE_USER"}</span>
                            <span>{profile?.statusName || "ACTIVE"}</span>
                        </div>

                    </aside>
                </Col>

                <Col lg={8}>
                    <section className="profile-form glass-panel">
                        <div className="page-kicker mb-2">Account</div>
                        <h2>Thông tin cá nhân</h2>
                        <p>Cập nhật hồ sơ và ảnh đại diện của tài khoản.</p>

                        {err && <Alert variant="danger">
                            <div>{err}</div>
                            {Object.keys(fieldErrors).length > 0 && <ul className="mb-0 mt-2">
                                {Object.entries(fieldErrors).map(([field, message]) => <li key={field}>{message}</li>)}
                            </ul>}
                        </Alert>}

                        {success && <Alert variant="success">{success}</Alert>}

                        <Form onSubmit={saveProfile}>
                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="profile-fullName">
                                        <Form.Label>Họ và tên</Form.Label>
                                        <Form.Control value={form.fullName || ""} onChange={e => updateForm("fullName", e.target.value)} isInvalid={!!fieldErrors.fullName} required />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.fullName}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="profile-phone">
                                        <Form.Label>Số điện thoại</Form.Label>
                                        <Form.Control value={form.phone || ""} onChange={e => updateForm("phone", e.target.value)} isInvalid={!!fieldErrors.phone} required />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.phone}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Form.Group className="mb-3" controlId="profile-avatar">
                                <Form.Label>Ảnh đại diện</Form.Label>
                                <Form.Control ref={avatarRef} type="file" accept="image/*" />
                            </Form.Group>

                            {!isOrganizer && <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="profile-birthDate">
                                        <Form.Label>Ngày sinh</Form.Label>
                                        <Form.Control type="date" value={form.birthDate || ""} onChange={e => updateForm("birthDate", e.target.value)} isInvalid={!!fieldErrors.birthDate} required />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.birthDate}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="profile-gender">
                                        <Form.Label>Giới tính</Form.Label>
                                        <Form.Select value={form.gender || ""} onChange={e => updateForm("gender", e.target.value)} isInvalid={!!fieldErrors.gender} required>
                                            <option value="">Chọn giới tính</option>
                                            <option value="male">Nam</option>
                                            <option value="female">Nữ</option>
                                        </Form.Select>
                                        <Form.Control.Feedback type="invalid">{fieldErrors.gender}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>}

                            {isOrganizer && <div className="organizer-fields">
                                <Row>
                                    <Col md={12}>
                                        <Form.Group className="mb-3" controlId="profile-organizationName">
                                            <Form.Label>Tên tổ chức</Form.Label>
                                            <Form.Control value={form.organizationName || ""} onChange={e => updateForm("organizationName", e.target.value)} isInvalid={!!fieldErrors.organizationName} required />
                                            <Form.Control.Feedback type="invalid">{fieldErrors.organizationName}</Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="profile-identityCard">
                                            <Form.Label>CCCD/CMND</Form.Label>
                                            <Form.Control value={form.identityCard || ""} onChange={e => updateForm("identityCard", e.target.value)} isInvalid={!!fieldErrors.identityCard} required />
                                            <Form.Control.Feedback type="invalid">{fieldErrors.identityCard}</Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="profile-taxCode">
                                            <Form.Label>Mã số thuế</Form.Label>
                                            <Form.Control value={form.taxCode || ""} onChange={e => updateForm("taxCode", e.target.value)} isInvalid={!!fieldErrors.taxCode} required />
                                            <Form.Control.Feedback type="invalid">{fieldErrors.taxCode}</Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                </Row>
                            </div>}

                            <Button className="btn-pink px-4" type="submit" disabled={saving}>
                                {saving ? <MySpinner /> : "Lưu thay đổi"}
                            </Button>
                        </Form>
                    </section>
                </Col>
            </Row>
        </div>
    );
}

export default Profile;
