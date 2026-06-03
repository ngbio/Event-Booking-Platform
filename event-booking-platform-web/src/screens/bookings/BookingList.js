import { useContext, useEffect, useRef, useState } from "react";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const BookingList = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [statusId, setStatusId] = useState("");
    const loadedRequestRef = useRef("");
    const [user,] = useContext(MyUserContext);
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

    const payWithMomo = async (bookingId) => {
        try {
            const res = await authApis().post(endpoints["momo-payment"], { bookingId });
            const payUrl = res.data.data?.payUrl;

            if (!payUrl)
                throw new Error("Không nhận được đường dẫn thanh toán MoMo.");

            window.location.href = payUrl;
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || ex.message || "Không thể tạo lại thanh toán MoMo.");
        }
    }

    const loadBookings = async () => {
        const requestKey = statusId || "all";
        if (loadedRequestRef.current === requestKey)
            return;

        loadedRequestRef.current = requestKey;

        try {
            setLoading(true);
            setErr("");

            const url = statusId ? `${endpoints["bookings"]}?statusId=${statusId}` : endpoints["bookings"];
            const res = await authApis().get(url);
            setBookings(res.data.data || []);
        } catch (ex) {
            console.error(ex);
            loadedRequestRef.current = "";
            setBookings([]);
            setErr(ex.response?.data?.message || "Không thể tải lịch sử đặt vé.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadedRequestRef.current = "";
        if (user?.roleId === 3)
            loadBookings();
        
    }, [statusId, user?.id, user?.roleId]);

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập để xem lịch sử đặt vé.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav("/login?next=/bookings")}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (user?.roleId !== 3)
        return <Alert className="alert-dark-pink">Chỉ tài khoản người mua vé mới có lịch sử đặt vé.</Alert>;

    return (
        <div className="booking-screen">
            <div className="organizer-list-head">
                <div>
                    <div className="page-kicker mb-2">Bookings</div>
                    <h1 className="organizer-title">Đơn đặt vé của tôi</h1>
                </div>

                <Form.Select className="booking-status-filter" value={statusId} onChange={e => setStatusId(e.target.value)}>
                    <option value="">Tất cả trạng thái</option>
                    <option value="1">Chờ thanh toán</option>
                    <option value="2">Đã thanh toán</option>
                    <option value="5">Đã hủy</option>
                </Form.Select>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
            {loading && <MySpinner />}

            {!loading && bookings.length === 0 && !err && <Alert className="alert-dark-pink mt-3">Bạn chưa có đơn đặt vé nào.</Alert>}

            <Row className="booking-grid mt-3">
                {bookings.map(b => <Col xs={12} md={6} xl={4} key={b.id}>
                    <article className="booking-card glass-panel">
                        <div className="booking-card-head">
                            <span className="event-badge">{b.statusName || "Đang cập nhật"}</span>
                            <strong>#{b.id}</strong>
                        </div>

                        <h2>{b.eventTitle || "Sự kiện"}</h2>

                        <dl className="booking-card-list">
                            <div>
                                <dt>Số lượng</dt>
                                <dd>{b.quantity} vé</dd>
                            </div>
                            <div>
                                <dt>Tổng tiền</dt>
                                <dd>{formatPrice(b.totalPrice)}</dd>
                            </div>
                            <div>
                                <dt>Ngày đặt</dt>
                                <dd>{formatDate(b.createdDate)}</dd>
                            </div>
                        </dl>

                        {b.statusId === 1 && <Button className="btn-pink w-100 mt-3" onClick={() => payWithMomo(b.id)}>
                            Thanh toán MoMo
                        </Button>}
                        <Button className={b.statusId === 1 ? "btn-soft-pink w-100 mt-2" : "btn-pink w-100 mt-3"} onClick={() => nav(`/bookings/${b.id}`)}>Xem chi tiết</Button>
                    </article>
                </Col>)}
            </Row>
        </div>
    );
}

export default BookingList;
