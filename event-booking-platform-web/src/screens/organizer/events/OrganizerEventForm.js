import { useContext, useEffect, useRef, useState } from "react";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import MySpinner from "../../../components/MySpinner";
import Apis, { authApis, endpoints } from "../../../configs/Apis";
import { MyUserContext } from "../../../configs/Contexts";

const initialForm = {
    title: "",
    description: "",
    startTime: "",
    endTime: "",
    location: "",
    totalTickets: 1,
    price: 0,
    categoryIds: ""
};

const MAX_MEDIA_FILE_SIZE = 20 * 1024 * 1024;

const OrganizerEventForm = () => {
    const [form, setForm] = useState(initialForm);
    const [categories, setCategories] = useState([]);
    const [currentEvent, setCurrentEvent] = useState(null);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState("");
    const [msg, setMsg] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const imageRef = useRef();
    const videoRef = useRef();
    const [user,] = useContext(MyUserContext);
    const { eventId } = useParams();
    const nav = useNavigate();
    const isEdit = !!eventId;

    const toInputDate = (value) => {
        if (!value)
            return "";

        return value.replace(" ", "T").slice(0, 16);
    }

    const updateForm = (field, value) => {
        setForm(current => ({ ...current, [field]: value }));
        setFieldErrors(current => ({ ...current, [field]: "" }));
    }

    const fillForm = (event) => {
        setForm({
            title: event.title || "",
            description: event.description || "",
            startTime: toInputDate(event.startTime),
            endTime: toInputDate(event.endTime),
            location: event.location || "",
            totalTickets: event.totalTickets || 1,
            price: event.price || 0,
            categoryIds: event.categoryId ? String(event.categoryId) : ""
        });
    }

    const loadCategories = async () => {
        const res = await Apis.get(endpoints["categories"]);
        setCategories((res.data.data || []).filter(c => c.active !== false));
    }

    const loadEvent = async () => {
        const res = await authApis().get(endpoints["organizer-event-details"](eventId));
        const data = res.data.data;
        setCurrentEvent(data);
        fillForm(data);
    }

    useEffect(() => {
        const loadData = async () => {
            if (!user)
                return;

            try {
                setLoading(true);
                setErr("");
                await loadCategories();
                if (isEdit)
                    await loadEvent();
            } catch (ex) {
                console.error(ex);
                setErr(ex.response?.data?.message || "Không thể tải dữ liệu sự kiện.");
            } finally {
                setLoading(false);
            }
        }

        loadData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [eventId, user?.id]);

    const validate = () => {
        const errors = {};
        const image = imageRef.current?.files?.[0];
        const video = videoRef.current?.files?.[0];

        if (!form.title.trim())
            errors.title = "Vui lòng nhập tên sự kiện.";
        if (!form.startTime)
            errors.startTime = "Vui lòng chọn thời gian bắt đầu.";
        if (!form.endTime)
            errors.endTime = "Vui lòng chọn thời gian kết thúc.";
        if (form.startTime && form.endTime && new Date(form.startTime) >= new Date(form.endTime))
            errors.endTime = "Thời gian kết thúc phải sau thời gian bắt đầu.";
        if (!form.location.trim())
            errors.location = "Vui lòng nhập địa điểm.";
        if (Number(form.totalTickets) < 1)
            errors.totalTickets = "Số vé phải lớn hơn 0.";
        if (Number(form.price) < 0)
            errors.price = "Giá vé không được âm.";
        if (!form.categoryIds)
            errors.categoryIds = "Vui lòng chọn danh mục.";
        if (image && image.size > MAX_MEDIA_FILE_SIZE)
            errors.image = "Ảnh quá lớn. Vui lòng chọn ảnh nhỏ hơn 20MB.";
        if (video && video.size > MAX_MEDIA_FILE_SIZE)
            errors.video = "Video quá lớn. Vui lòng chọn video nhỏ hơn 20MB.";

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    }

    const clearFileError = (field) => {
        setFieldErrors(current => ({ ...current, [field]: "" }));
    }

    const buildData = () => {
        const data = new FormData();
        data.append("title", form.title.trim());
        data.append("description", form.description || "");
        data.append("startTime", form.startTime);
        data.append("endTime", form.endTime);
        data.append("location", form.location.trim());
        data.append("totalTickets", form.totalTickets);
        data.append("price", form.price);
        data.append("categoryIds", form.categoryIds);

        if (imageRef.current?.files?.length > 0)
            data.append("image", imageRef.current.files[0]);
        if (videoRef.current?.files?.length > 0)
            data.append("video", videoRef.current.files[0]);

        return data;
    }

    const saveEvent = async (e) => {
        e.preventDefault();
        if (!validate())
            return;

        try {
            setSaving(true);
            setErr("");
            setMsg("");

            const data = buildData();
            const config = { headers: { "Content-Type": "multipart/form-data" } };
            const res = isEdit
                ? await authApis().put(endpoints["update-organizer-event"](eventId), data, config)
                : await authApis().post(endpoints["create-organizer-event"], data, config);

            const saved = res.data.data;
            setMsg(isEdit ? "Cập nhật sự kiện thành công." : "Tạo sự kiện thành công.");

            if (imageRef.current)
                imageRef.current.value = "";
            if (videoRef.current)
                videoRef.current.value = "";

            if (isEdit) {
                setCurrentEvent(saved);
                fillForm(saved);
            } else {
                nav(`/organizer/events/${saved.id}`);
            }
        } catch (ex) {
            console.error(ex);
            const data = ex.response?.data;
            const uploadError = ex.response?.status === 413 || !ex.response;
            setErr(data?.message || (uploadError ? "Không thể upload media. Vui lòng kiểm tra video/ảnh không vượt quá 20MB." : "Không thể lưu sự kiện. Vui lòng thử lại."));
            if (data?.errors)
                setFieldErrors(data.errors);
        } finally {
            setSaving(false);
        }
    }

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập bằng tài khoản nhà tổ chức để quản lý sự kiện.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav(`/login?next=${isEdit ? `/organizer/events/${eventId}/edit` : "/organizer/events/new"}`)}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (user?.roleId !== 2)
        return <Alert className="alert-dark-pink">Chỉ tài khoản nhà tổ chức mới có thể quản lý sự kiện.</Alert>;

    if (loading)
        return <MySpinner />;

    return (
        <div className="organizer-form-screen">
            <div className="organizer-detail-toolbar">
                <Button className="btn-soft-pink" onClick={() => nav(isEdit ? `/organizer/events/${eventId}` : "/organizer/events")}>Quay lại</Button>
                {isEdit && <Button className="btn-soft-pink" onClick={() => nav(`/organizer/events/${eventId}`)}>Xem chi tiết</Button>}
            </div>

            <Row className="organizer-form-layout mt-4">
                <Col lg={8}>
                    <section className="profile-form glass-panel">
                        <div className="page-kicker mb-2">Organizer</div>
                        <h2>{isEdit ? "Sửa sự kiện" : "Tạo sự kiện"}</h2>
                        <p>{isEdit ? "Cập nhật thông tin, ảnh banner và video giới thiệu nếu có." : "Nhập thông tin cơ bản để tạo sự kiện mới."}</p>

                        {err && <Alert className="alert-dark-pink">{err}</Alert>}
                        {msg && <Alert variant="success">{msg}</Alert>}

                        <Form onSubmit={saveEvent}>
                            <Form.Group className="mb-3" controlId="event-title">
                                <Form.Label>Tên sự kiện</Form.Label>
                                <Form.Control value={form.title} onChange={e => updateForm("title", e.target.value)} isInvalid={!!fieldErrors.title} />
                                <Form.Control.Feedback type="invalid">{fieldErrors.title}</Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="event-description">
                                <Form.Label>Mô tả</Form.Label>
                                <Form.Control as="textarea" rows={5} value={form.description} onChange={e => updateForm("description", e.target.value)} />
                            </Form.Group>

                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="event-start">
                                        <Form.Label>Bắt đầu</Form.Label>
                                        <Form.Control type="datetime-local" value={form.startTime} onChange={e => updateForm("startTime", e.target.value)} isInvalid={!!fieldErrors.startTime} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.startTime}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="event-end">
                                        <Form.Label>Kết thúc</Form.Label>
                                        <Form.Control type="datetime-local" value={form.endTime} onChange={e => updateForm("endTime", e.target.value)} isInvalid={!!fieldErrors.endTime} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.endTime}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Form.Group className="mb-3" controlId="event-location">
                                <Form.Label>Địa điểm</Form.Label>
                                <Form.Control value={form.location} onChange={e => updateForm("location", e.target.value)} isInvalid={!!fieldErrors.location} />
                                <Form.Control.Feedback type="invalid">{fieldErrors.location}</Form.Control.Feedback>
                            </Form.Group>

                            <Row>
                                <Col md={4}>
                                    <Form.Group className="mb-3" controlId="event-total-tickets">
                                        <Form.Label>Tổng số vé</Form.Label>
                                        <Form.Control type="number" min="1" value={form.totalTickets} onChange={e => updateForm("totalTickets", e.target.value)} isInvalid={!!fieldErrors.totalTickets} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.totalTickets}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={4}>
                                    <Form.Group className="mb-3" controlId="event-price">
                                        <Form.Label>Giá vé</Form.Label>
                                        <Form.Control type="number" min="0" value={form.price} onChange={e => updateForm("price", e.target.value)} isInvalid={!!fieldErrors.price} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.price}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={4}>
                                    <Form.Group className="mb-3" controlId="event-category">
                                        <Form.Label>Danh mục</Form.Label>
                                        <Form.Select value={form.categoryIds} onChange={e => updateForm("categoryIds", e.target.value)} isInvalid={!!fieldErrors.categoryIds}>
                                            <option value="">Chọn danh mục</option>
                                            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                                        </Form.Select>
                                        <Form.Control.Feedback type="invalid">{fieldErrors.categoryIds}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="event-image">
                                        <Form.Label>Ảnh banner</Form.Label>
                                        <Form.Control ref={imageRef} type="file" accept="image/*" isInvalid={!!fieldErrors.image} onChange={() => clearFileError("image")} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.image}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="event-video">
                                        <Form.Label>Video giới thiệu (không bắt buộc)</Form.Label>
                                        <Form.Control ref={videoRef} type="file" accept="video/*" isInvalid={!!fieldErrors.video} onChange={() => clearFileError("video")} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.video}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Button className="btn-pink px-4" type="submit" disabled={saving}>
                                {saving ? "Đang lưu..." : "Lưu sự kiện"}
                            </Button>
                        </Form>
                    </section>
                </Col>

                <Col lg={4}>
                    <aside className="organizer-form-preview glass-panel">
                        <div className="page-kicker mb-2">Preview</div>
                        {currentEvent?.imageUrl ? <img className="organizer-form-preview-img" src={currentEvent.imageUrl} alt={currentEvent.title} /> :
                            <div className="event-card-placeholder">EVENT</div>}
                        <h3>{form.title || "Tên sự kiện"}</h3>
                        <p>{form.location || "Địa điểm tổ chức"}</p>
                        <div className="profile-badges">
                            <span>{currentEvent?.statusName || "Bản nháp"}</span>
                            <span>{categories.find(c => String(c.id) === String(form.categoryIds))?.name || "Danh mục"}</span>
                        </div>
                    </aside>
                </Col>
            </Row>
        </div>
    );
}

export default OrganizerEventForm;
