import { useContext, useEffect, useState } from "react";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import EventChatWidget from "../../components/EventChatWidget";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const EventDetail = () => {
    const [event, setEvent] = useState(null);
    const [loading, setLoading] = useState(false);
    const [bookingLoading, setBookingLoading] = useState(false);
    const [err, setErr] = useState("");
    const [bookingErr, setBookingErr] = useState("");
    const [quantity, setQuantity] = useState(1);
    const [user,] = useContext(MyUserContext);
    const { eventId } = useParams();
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

    const loadEvent = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await Apis.get(endpoints["event-details"](eventId));
            setEvent(res.data.data);
            setQuantity(1);
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || "Không thể tải được thông tin sự kiện.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadEvent();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [eventId]);

    const createBooking = async () => {
        try {
            setBookingLoading(true);
            setBookingErr("");

            const res = await authApis().post(endpoints["bookings"], {
                eventId: event.id,
                quantity: Number(quantity),
                paymentMethod: "MOMO"
            });

            const booking = res.data.data;
            const totalPrice = Number(booking.totalPrice || 0);

            if (totalPrice <= 0) {
                nav(`/bookings/${booking.id}`);
                return;
            }

            const paymentRes = await authApis().post(endpoints["momo-payment"], {
                bookingId: booking.id
            });
            const payUrl = paymentRes.data.data?.payUrl;

            if (!payUrl)
                throw new Error("Không nhận được đường dẫn thanh toán MoMo.");

            window.location.href = payUrl;
        } catch (ex) {
            console.error(ex);
            setBookingErr(ex.response?.data?.message || ex.message || "Không thể tạo đơn đặt vé.");
        } finally {
            setBookingLoading(false);
        }
    }

    const renderBookingAction = () => {
        if (event.availableTickets <= 0)
            return <Button className="btn-pink w-100 mt-4" disabled>Hết vé</Button>;

        if (user === null) {
            return (
                <div className="event-login-prompt mt-4">
                    <p>Vui lòng đăng nhập để đặt vé sự kiện này.</p>
                    <Button className="btn-pink w-100" onClick={() => nav(`/login?next=/events/${event.id}`)}>
                        Đăng nhập để đặt vé
                    </Button>
                </div>
            );
        }

        if (user.roleId !== 3) {
            return (
                <div className="event-login-prompt mt-4">
                    <p>Chỉ tài khoản người mua vé mới có thể đặt vé.</p>
                    <Button className="btn-pink w-100" disabled>Đặt vé</Button>
                </div>
            );
        }

        return (
            <div className="booking-box mt-4">
                {bookingErr && <Alert className="alert-dark-pink">{bookingErr}</Alert>}
                <Form.Group controlId="booking-quantity">
                    <Form.Label>Số lượng vé</Form.Label>
                    <Form.Control
                        type="number"
                        min="1"
                        max={event.availableTickets}
                        value={quantity}
                        onChange={e => setQuantity(e.target.value)}
                    />
                </Form.Group>
                <div className="booking-box-total">
                    <span>Tạm tính</span>
                    <strong>{formatPrice(Number(event.price || 0) * Number(quantity || 0))}</strong>
                </div>
                <Button className="btn-pink w-100" onClick={createBooking} disabled={bookingLoading || Number(quantity) < 1 || Number(quantity) > event.availableTickets}>
                    {bookingLoading ? "Đang chuyển sang MoMo..." : "Đặt vé"}
                </Button>
            </div>
        );
    }

    if (loading)
        return <MySpinner />;

    if (err)
        return <Alert className="alert-dark-pink">{err}</Alert>;

    if (!event)
        return null;

    return (
        <div className="event-detail">
            <div className="organizer-detail-toolbar mb-4">
                <Button className="btn-soft-pink" onClick={() => nav(-1)}>Quay lại</Button>
                <Button className="btn-outline-pink" onClick={() => nav(`/events/compare?eventIds=${event.id}`)}>
                    Thêm vào so sánh
                </Button>
            </div>

            <Row className="event-detail-layout">
                <Col lg={7}>
                    {event.imageUrl ? <img className="event-detail-image" src={event.imageUrl} alt={event.title} /> :
                        <div className="event-detail-placeholder">EVENT</div>}
                </Col>

                <Col lg={5}>
                    <div className="event-detail-summary glass-panel">
                        <div className="page-kicker mb-3">{event.categoryName || "Event"}</div>
                        <h1 className="event-detail-title">{event.title}</h1>
                        <div className="event-detail-price">{formatPrice(event.price)}</div>

                        <div className="event-detail-stats">
                            <div>
                                <span>Còn lại</span>
                                <strong>{event.availableTickets} vé</strong>
                            </div>
                            <div>
                                <span>Đã bán</span>
                                <strong>{event.soldTickets} vé</strong>
                            </div>
                        </div>

                        {renderBookingAction()}
                    </div>
                </Col>
            </Row>

            <Row className="event-detail-info">
                <Col lg={7}>
                    <section className="event-detail-section">
                        <h2>Giới thiệu</h2>
                        <p>{event.description || "Sự kiện chưa có mô tả chi tiết."}</p>
                    </section>
                </Col>

                <Col lg={5}>
                    <section className="event-detail-section">
                        <h2>Thông tin</h2>
                        <dl className="event-detail-list">
                            <div>
                                <dt>Bắt đầu</dt>
                                <dd>{formatDate(event.startTime)}</dd>
                            </div>
                            <div>
                                <dt>Kết thúc</dt>
                                <dd>{formatDate(event.endTime)}</dd>
                            </div>
                            <div>
                                <dt>Địa điểm</dt>
                                <dd>{event.location || "Đang cập nhật"}</dd>
                            </div>
                            <div>
                                <dt>Đơn vị tổ chức</dt>
                                <dd>{event.organizerName || "Đang cập nhật"}</dd>
                            </div>
                            <div>
                                <dt>Trạng thái</dt>
                                <dd>{event.statusName || "Đang mở bán"}</dd>
                            </div>
                        </dl>
                    </section>
                </Col>
            </Row>

            <EventChatWidget event={event} />
        </div>
    );
}

export default EventDetail;
