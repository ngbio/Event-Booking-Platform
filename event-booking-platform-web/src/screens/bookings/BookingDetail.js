import { useContext, useEffect, useState } from "react";
import { Alert, Button, Col, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const BookingDetail = () => {
    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState("");
    const [msg, setMsg] = useState("");
    const [user,] = useContext(MyUserContext);
    const { bookingId } = useParams();
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

    const loadBooking = async () => {
        try {
            setLoading(true);
            setErr("");
            setMsg("");

            const res = await authApis().get(endpoints["booking-details"](bookingId));
            setBooking(res.data.data);
        } catch (ex) {
            console.error(ex);
            setBooking(null);
            setErr(ex.response?.data?.message || "Không thể tải chi tiết booking.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        if (user)
            loadBooking();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [bookingId, user?.id]);

    const cancelBooking = async () => {
        try {
            setSaving(true);
            setErr("");
            setMsg("");

            await authApis().patch(endpoints["cancel-booking"](bookingId));
            setMsg("Hủy đơn đặt vé thành công.");
            await loadBooking();
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || "Không thể hủy đơn đặt vé.");
        } finally {
            setSaving(false);
        }
    }

    const payWithMomo = async () => {
        try {
            setSaving(true);
            setErr("");
            setMsg("");

            const res = await authApis().post(endpoints["momo-payment"], {
                bookingId: Number(bookingId)
            });
            const payUrl = res.data.data?.payUrl;

            if (!payUrl)
                throw new Error("Không nhận được đường dẫn thanh toán MoMo.");

            window.location.href = payUrl;
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || ex.message || "Không thể tạo lại thanh toán MoMo.");
        } finally {
            setSaving(false);
        }
    }

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập để xem chi tiết booking.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav(`/login?next=/bookings/${bookingId}`)}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (loading)
        return <MySpinner />;

    if (!booking && err)
        return <Alert className="alert-dark-pink">{err}</Alert>;

    if (!booking)
        return null;

    return (
        <div className="booking-screen">
            <div className="organizer-detail-toolbar">
                <Button className="btn-soft-pink" onClick={() => nav("/bookings")}>Quay lại</Button>
                <Button className="btn-soft-pink" onClick={() => nav(`/events/${booking.eventId}`)}>Xem sự kiện</Button>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
            {msg && <Alert variant="success" className="mt-3">{msg}</Alert>}

            <Row className="booking-detail-layout mt-4">
                <Col lg={7}>
                    <section className="booking-detail-panel glass-panel">
                        <div className="page-kicker mb-2">Booking #{booking.id}</div>
                        <h1>{booking.eventTitle || "Sự kiện"}</h1>
                        <span className="event-badge mt-2">{booking.statusName || "Đang cập nhật"}</span>

                        <div className="booking-total mt-4">
                            <span>Tổng thanh toán</span>
                            <strong>{formatPrice(booking.totalPrice)}</strong>
                        </div>
                    </section>
                </Col>

                <Col lg={5}>
                    <section className="booking-detail-panel glass-panel">
                        <h2>Thông tin đơn</h2>
                        <dl className="event-detail-list">
                            <div>
                                <dt>Email</dt>
                                <dd>{booking.email || "Đang cập nhật"}</dd>
                            </div>
                            <div>
                                <dt>Số lượng</dt>
                                <dd>{booking.quantity} vé</dd>
                            </div>
                            <div>
                                <dt>Đơn giá</dt>
                                <dd>{formatPrice(booking.unitPrice)}</dd>
                            </div>
                            <div>
                                <dt>Ngày đặt</dt>
                                <dd>{formatDate(booking.createdDate)}</dd>
                            </div>
                            <div>
                                <dt>Cập nhật</dt>
                                <dd>{formatDate(booking.updatedDate)}</dd>
                            </div>
                        </dl>

                        {booking.statusId === 1 && <>
                            <Button className="btn-pink w-100 mt-4" onClick={payWithMomo} disabled={saving}>
                                {saving ? "Đang chuyển sang MoMo..." : "Thanh toán MoMo"}
                            </Button>
                            <Button className="btn-soft-pink w-100 mt-3" onClick={cancelBooking} disabled={saving}>
                                {saving ? "Đang xử lý..." : "Hủy đơn đặt vé"}
                            </Button>
                        </>}
                    </section>
                </Col>
            </Row>
        </div>
    );
}

export default BookingDetail;
