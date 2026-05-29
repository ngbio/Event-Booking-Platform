import { useContext, useEffect, useState } from "react";
import { Alert, Button, Col, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const EventDetail = () => {
    const [event, setEvent] = useState(null);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
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

    const loadEvent = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await Apis.get(endpoints["event-details"](eventId));
            setEvent(res.data.data);
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

    const renderBookingAction = () => {
        if (event.availableTickets <= 0) {
            return <Button className="btn-pink w-100 mt-4" disabled>Hết vé</Button>;
        }

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

        return <Button className="btn-pink w-100 mt-4">Đặt vé</Button>;
    }

    if (loading)
        return <MySpinner />;

    if (err)
        return <Alert className="alert-dark-pink">{err}</Alert>;

    if (!event)
        return null;

    return (
        <div className="event-detail">
            <Button className="btn-soft-pink mb-4" onClick={() => nav(-1)}>Quay lại</Button>

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
        </div>
    );
}

export default EventDetail;
