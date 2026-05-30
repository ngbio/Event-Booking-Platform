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
                setErr(ex.response?.data?.message || "Khong the tai du lieu su kien.");
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
            errors.title = "Vui long nhap ten su kien.";
        if (!form.startTime)
            errors.startTime = "Vui long chon thoi gian bat dau.";
        if (!form.endTime)
            errors.endTime = "Vui long chon thoi gian ket thuc.";
        if (form.startTime && form.endTime && new Date(form.startTime) >= new Date(form.endTime))
            errors.endTime = "Thoi gian ket thuc phai sau thoi gian bat dau.";
        if (!form.location.trim())
            errors.location = "Vui long nhap dia diem.";
        if (Number(form.totalTickets) < 1)
            errors.totalTickets = "So ve phai lon hon 0.";
        if (Number(form.price) < 0)
            errors.price = "Gia ve khong duoc am.";
        if (!form.categoryIds)
            errors.categoryIds = "Vui long chon danh muc.";
        if (image && image.size > MAX_MEDIA_FILE_SIZE)
            errors.image = "Anh toi da 20MB.";
        if (video && video.size > MAX_MEDIA_FILE_SIZE)
            errors.video = "Video toi da 20MB.";

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
            setMsg(isEdit ? "Cap nhat su kien thanh cong." : "Tao su kien thanh cong.");

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
            setErr(data?.message || (uploadError ? "Khong the upload media. Vui long kiem tra video/anh khong vuot qua 20MB." : "Khong the luu su kien. Vui long thu lai."));
            if (data?.errors)
                setFieldErrors(data.errors);
        } finally {
            setSaving(false);
        }
    }

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui long dang nhap bang tai khoan nha to chuc de quan ly su kien.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav(`/login?next=${isEdit ? `/organizer/events/${eventId}/edit` : "/organizer/events/new"}`)}>Dang nhap</Button>
                </div>
            </Alert>
        );
    }

    if (user?.roleId !== 2)
        return <Alert className="alert-dark-pink">Chi tai khoan nha to chuc moi co the quan ly su kien.</Alert>;

    if (loading)
        return <MySpinner />;

    return (
        <div className="organizer-form-screen">
            <div className="organizer-detail-toolbar">
                <Button className="btn-soft-pink" onClick={() => nav(isEdit ? `/organizer/events/${eventId}` : "/organizer/events")}>Quay lai</Button>
                {isEdit && <Button className="btn-outline-pink" onClick={() => nav(`/organizer/events/${eventId}`)}>Xem chi tiet</Button>}
            </div>

            <Row className="organizer-form-layout mt-4">
                <Col lg={8}>
                    <section className="profile-form glass-panel">
                        <div className="page-kicker mb-2">Organizer</div>
                        <h2>{isEdit ? "Sua su kien" : "Tao su kien"}</h2>
                        <p>{isEdit ? "Cap nhat thong tin, anh banner va video gioi thieu neu co." : "Nhap thong tin co ban de tao ban nhap su kien moi."}</p>

                        {err && <Alert className="alert-dark-pink">{err}</Alert>}
                        {msg && <Alert variant="success">{msg}</Alert>}

                        <Form onSubmit={saveEvent}>
                            <Form.Group className="mb-3" controlId="event-title">
                                <Form.Label>Ten su kien</Form.Label>
                                <Form.Control value={form.title} onChange={e => updateForm("title", e.target.value)} isInvalid={!!fieldErrors.title} />
                                <Form.Control.Feedback type="invalid">{fieldErrors.title}</Form.Control.Feedback>
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="event-description">
                                <Form.Label>Mo ta</Form.Label>
                                <Form.Control as="textarea" rows={5} value={form.description} onChange={e => updateForm("description", e.target.value)} />
                            </Form.Group>

                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="event-start">
                                        <Form.Label>Bat dau</Form.Label>
                                        <Form.Control type="datetime-local" value={form.startTime} onChange={e => updateForm("startTime", e.target.value)} isInvalid={!!fieldErrors.startTime} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.startTime}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="event-end">
                                        <Form.Label>Ket thuc</Form.Label>
                                        <Form.Control type="datetime-local" value={form.endTime} onChange={e => updateForm("endTime", e.target.value)} isInvalid={!!fieldErrors.endTime} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.endTime}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Form.Group className="mb-3" controlId="event-location">
                                <Form.Label>Dia diem</Form.Label>
                                <Form.Control value={form.location} onChange={e => updateForm("location", e.target.value)} isInvalid={!!fieldErrors.location} />
                                <Form.Control.Feedback type="invalid">{fieldErrors.location}</Form.Control.Feedback>
                            </Form.Group>

                            <Row>
                                <Col md={4}>
                                    <Form.Group className="mb-3" controlId="event-total-tickets">
                                        <Form.Label>Tong so ve</Form.Label>
                                        <Form.Control type="number" min="1" value={form.totalTickets} onChange={e => updateForm("totalTickets", e.target.value)} isInvalid={!!fieldErrors.totalTickets} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.totalTickets}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={4}>
                                    <Form.Group className="mb-3" controlId="event-price">
                                        <Form.Label>Gia ve</Form.Label>
                                        <Form.Control type="number" min="0" value={form.price} onChange={e => updateForm("price", e.target.value)} isInvalid={!!fieldErrors.price} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.price}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={4}>
                                    <Form.Group className="mb-3" controlId="event-category">
                                        <Form.Label>Danh muc</Form.Label>
                                        <Form.Select value={form.categoryIds} onChange={e => updateForm("categoryIds", e.target.value)} isInvalid={!!fieldErrors.categoryIds}>
                                            <option value="">Chon danh muc</option>
                                            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                                        </Form.Select>
                                        <Form.Control.Feedback type="invalid">{fieldErrors.categoryIds}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="event-image">
                                        <Form.Label>Anh banner</Form.Label>
                                        <Form.Control ref={imageRef} type="file" accept="image/*" isInvalid={!!fieldErrors.image} onChange={() => clearFileError("image")} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.image}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="event-video">
                                        <Form.Label>Video gioi thieu (khong bat buoc)</Form.Label>
                                        <Form.Control ref={videoRef} type="file" accept="video/*" isInvalid={!!fieldErrors.video} onChange={() => clearFileError("video")} />
                                        <Form.Control.Feedback type="invalid">{fieldErrors.video}</Form.Control.Feedback>
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Button className="btn-pink px-4" type="submit" disabled={saving}>
                                {saving ? "Dang luu..." : "Luu su kien"}
                            </Button>
                        </Form>
                    </section>
                </Col>

                <Col lg={4}>
                    <aside className="organizer-form-preview glass-panel">
                        <div className="page-kicker mb-2">Preview</div>
                        {currentEvent?.imageUrl ? <img className="organizer-form-preview-img" src={currentEvent.imageUrl} alt={currentEvent.title} /> :
                            <div className="event-card-placeholder">EVENT</div>}
                        <h3>{form.title || "Ten su kien"}</h3>
                        <p>{form.location || "Dia diem to chuc"}</p>
                        <div className="profile-badges">
                            <span>{currentEvent?.statusName || "Ban nhap"}</span>
                            <span>{categories.find(c => String(c.id) === String(form.categoryIds))?.name || "Danh muc"}</span>
                        </div>
                    </aside>
                </Col>
            </Row>
        </div>
    );
}

export default OrganizerEventForm;
