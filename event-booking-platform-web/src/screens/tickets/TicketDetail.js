import { useContext, useEffect, useState } from "react";
import { Alert, Button, Col, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const TicketDetail = () => {
    const [ticket, setTicket] = useState(null);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [user,] = useContext(MyUserContext);
    const { ticketId } = useParams();
    const nav = useNavigate();

    const formatDate = (value) => {
        if (!value)
            return "Đang cập nhật";

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

    const loadTicket = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await authApis().get(endpoints["ticket-details"](ticketId));
            setTicket(res.data.data);
        } catch (ex) {
            console.error(ex);
            setTicket(null);
            setErr(ex.response?.data?.message || "Không thể tải chi tiết vé.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        if (user)
            loadTicket();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [ticketId, user?.id]);

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập để xem chi tiết vé.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav(`/login?next=/tickets/${ticketId}`)}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (loading)
        return <MySpinner />;

    if (!ticket && err)
        return <Alert className="alert-dark-pink">{err}</Alert>;

    if (!ticket)
        return null;

    return (
        <div className="ticket-screen">
            <div className="organizer-detail-toolbar">
                <Button className="btn-soft-pink" onClick={() => nav("/tickets")}>Quay lại</Button>
                <Button className="btn-outline-pink" onClick={() => nav(`/events/${ticket.eventId}`)}>Xem sự kiện</Button>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}

            <Row className="ticket-detail-layout mt-4">
                <Col lg={7}>
                    <section className="ticket-detail-panel glass-panel">
                        <div className="page-kicker mb-2">Ticket #{ticket.id}</div>
                        <h1>{ticket.eventTitle || "Sự kiện"}</h1>
                        <span className="event-badge mt-2">{ticket.statusName || "Đang cập nhật"}</span>

                        <div className="ticket-qr-large mt-4">
                            <span>Mã QR</span>
                            <strong>{ticket.qrCode || "Chưa có mã QR"}</strong>
                        </div>
                    </section>
                </Col>

                <Col lg={5}>
                    <section className="ticket-detail-panel glass-panel">
                        <h2>Thông tin vé</h2>
                        <dl className="event-detail-list">
                            <div>
                                <dt>Email</dt>
                                <dd>{ticket.email || "Đang cập nhật"}</dd>
                            </div>
                            <div>
                                <dt>Booking</dt>
                                <dd>#{ticket.bookingId}</dd>
                            </div>
                            <div>
                                <dt>Giá vé</dt>
                                <dd>{formatPrice(ticket.unitPrice)}</dd>
                            </div>
                            <div>
                                <dt>Địa điểm</dt>
                                <dd>{ticket.eventLocation || "Đang cập nhật"}</dd>
                            </div>
                            <div>
                                <dt>Bắt đầu sự kiện</dt>
                                <dd>{formatDate(ticket.eventStartTime)}</dd>
                            </div>
                            <div>
                                <dt>Kết thúc sự kiện</dt>
                                <dd>{formatDate(ticket.eventEndTime)}</dd>
                            </div>
                            <div>
                                <dt>Ngày phát hành</dt>
                                <dd>{formatDate(ticket.createdDate)}</dd>
                            </div>
                            <div>
                                <dt>Cập nhật</dt>
                                <dd>{formatDate(ticket.updatedDate)}</dd>
                            </div>
                        </dl>

                        <Button className="btn-outline-pink w-100 mt-4" onClick={() => nav(`/bookings/${ticket.bookingId}`)}>
                            Xem booking
                        </Button>
                    </section>
                </Col>
            </Row>
        </div>
    );
}

export default TicketDetail;
