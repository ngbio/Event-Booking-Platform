import { useContext, useEffect, useRef, useState } from "react";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import MySpinner from "../../../components/MySpinner";
import { authApis, endpoints } from "../../../configs/Apis";
import { MyUserContext } from "../../../configs/Contexts";

const OrganizerEventBookings = () => {
    const [bookings, setBookings] = useState([]);
    const [statusId, setStatusId] = useState("2");
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const loadedRequestRef = useRef("");
    const [user,] = useContext(MyUserContext);
    const { eventId } = useParams();
    const nav = useNavigate();

    const formatDate = (value) => {
        if (!value)
            return "Dang cap nhat";

        return new Date(value.replace(" ", "T")).toLocaleString("vi-VN");
    }

    const formatPrice = (value) => {
        if (value === null || value === undefined || Number(value) === 0)
            return "Free";

        return Number(value).toLocaleString("vi-VN", {
            style: "currency",
            currency: "VND"
        });
    }

    const loadBookings = async () => {
        const requestKey = `${eventId}|${statusId || "all"}`;
        if (loadedRequestRef.current === requestKey)
            return;

        loadedRequestRef.current = requestKey;

        try {
            setLoading(true);
            setErr("");

            const params = statusId ? `?statusId=${statusId}` : "";
            const res = await authApis().get(`${endpoints["organizer-event-bookings"](eventId)}${params}`);
            setBookings(res.data.data || []);
        } catch (ex) {
            console.error(ex);
            loadedRequestRef.current = "";
            setBookings([]);
            setErr(ex.response?.data?.message || "Khong the tai danh sach khach mua ve.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadedRequestRef.current = "";
        if (user?.roleId === 2)
            loadBookings();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [eventId, statusId, user?.id, user?.roleId]);

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui long dang nhap bang tai khoan nha to chuc de xem danh sach khach mua ve.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav(`/login?next=/organizer/events/${eventId}/bookings`)}>Dang nhap</Button>
                </div>
            </Alert>
        );
    }

    if (user?.roleId !== 2)
        return <Alert className="alert-dark-pink">Chi tai khoan nha to chuc moi co the xem danh sach khach mua ve.</Alert>;

    return (
        <div className="booking-screen">
            <div className="organizer-list-head">
                <div>
                    <div className="page-kicker mb-2">Attendees</div>
                    <h1 className="organizer-title">Khach da mua ve</h1>
                </div>
                <div className="organizer-detail-toolbar">
                    <Button className="btn-soft-pink" onClick={() => nav(`/organizer/events/${eventId}`)}>Quay lai su kien</Button>
                    <Form.Select className="booking-status-filter" value={statusId} onChange={e => setStatusId(e.target.value)}>
                        <option value="">Tat ca trang thai</option>
                        <option value="1">Cho thanh toan</option>
                        <option value="2">Da thanh toan</option>
                        <option value="3">Dang hoan tien</option>
                        <option value="5">Da huy</option>
                    </Form.Select>
                </div>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
            {loading && <MySpinner />}
            {!loading && bookings.length === 0 && !err && <Alert className="alert-dark-pink mt-3">Chua co booking nao cho bo loc nay.</Alert>}

            <Row className="booking-grid mt-3">
                {bookings.map(b => <Col xs={12} md={6} xl={4} key={b.id}>
                    <article className="booking-card glass-panel">
                        <div className="booking-card-head">
                            <span className="event-badge">{b.statusName || "Dang cap nhat"}</span>
                            <strong>#{b.id}</strong>
                        </div>

                        <h2>{b.attendeeName || b.email || "Khach tham du"}</h2>

                        <dl className="booking-card-list">
                            <div>
                                <dt>Email</dt>
                                <dd>{b.email || "Dang cap nhat"}</dd>
                            </div>
                            <div>
                                <dt>So luong</dt>
                                <dd>{b.quantity} ve</dd>
                            </div>
                            <div>
                                <dt>Tong tien</dt>
                                <dd>{formatPrice(b.totalPrice)}</dd>
                            </div>
                            <div>
                                <dt>Ngay dat</dt>
                                <dd>{formatDate(b.createdDate)}</dd>
                            </div>
                        </dl>
                    </article>
                </Col>)}
            </Row>
        </div>
    );
}

export default OrganizerEventBookings;
