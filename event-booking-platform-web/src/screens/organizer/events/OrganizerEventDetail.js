import { useContext, useEffect, useState } from "react";
import { Alert, Button, Col, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import MySpinner from "../../../components/MySpinner";
import { authApis, endpoints } from "../../../configs/Apis";
import { MyUserContext } from "../../../configs/Contexts";

const STATUS = {
    PENDING: 1,
    PUBLISHED: 2,
    DRAFT: 3,
    COMPLETED: 4,
    CANCELLED: 5
};

const OrganizerEventDetail = () => {
    const [event, setEvent] = useState(null);
    const [financialStats, setFinancialStats] = useState(null);
    const [loading, setLoading] = useState(false);
    const [savingStatus, setSavingStatus] = useState(false);
    const [err, setErr] = useState("");
    const [statsErr, setStatsErr] = useState("");
    const [msg, setMsg] = useState("");
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
            setStatsErr("");

            const eventRes = await authApis().get(endpoints["organizer-event-details"](eventId));
            setEvent(eventRes.data.data);

            try {
                const statsRes = await authApis().get(endpoints["organizer-event-stats"](eventId));
                setFinancialStats(statsRes.data.data);
            } catch (statsEx) {
                console.error(statsEx);
                setFinancialStats(null);
                setStatsErr(statsEx.response?.data?.message || "Không thể tải báo cáo tài chính sự kiện.");
            }
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || "Không thể tải chi tiết sự kiện.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        if (user)
            loadEvent();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [eventId, user?.id]);

    const changeStatus = async (statusId) => {
        try {
            setSavingStatus(true);
            setErr("");
            setMsg("");

            await authApis().patch(`${endpoints["change-organizer-event-status"](eventId)}?statusId=${statusId}`, null);
            setMsg("Cập nhật trạng thái sự kiện thành công.");
            await loadEvent();
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || "Không thể cập nhật trạng thái sự kiện.");
        } finally {
            setSavingStatus(false);
        }
    }

    const renderStatusActions = () => {
        if (!event || event.statusId === STATUS.CANCELLED || event.statusId === STATUS.COMPLETED)
            return null;

        return (
            <div className="organizer-detail-actions mt-4">
                {event.statusId === STATUS.DRAFT && <Button className="btn-pink" onClick={() => changeStatus(STATUS.PENDING)} disabled={savingStatus}>
                    Gửi duyệt
                </Button>}
                {event.statusId === STATUS.PUBLISHED && <Button className="btn-soft-pink" onClick={() => changeStatus(STATUS.COMPLETED)} disabled={savingStatus}>
                    Đánh dấu hoàn thành
                </Button>}
                <Button className="btn-outline-pink" onClick={() => changeStatus(STATUS.CANCELLED)} disabled={savingStatus}>
                    Hủy sự kiện
                </Button>
            </div>
        );
    }

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập bằng tài khoản nhà tổ chức để xem chi tiết sự kiện.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav(`/login?next=/organizer/events/${eventId}`)}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (user?.roleId !== 2)
        return <Alert className="alert-dark-pink">Chỉ tài khoản nhà tổ chức mới có thể quản lý sự kiện.</Alert>;

    if (loading)
        return <MySpinner />;

    if (!event && err)
        return <Alert className="alert-dark-pink">{err}</Alert>;

    if (!event)
        return null;

    return (
        <div className="event-detail">
            <div className="organizer-detail-toolbar">
                <Button className="btn-soft-pink" onClick={() => nav("/organizer/events")}>Quay lại</Button>
                <Button className="btn-outline-pink" onClick={() => nav(`/organizer/events/${event.id}/edit`)}>Sửa sự kiện</Button>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
            {msg && <Alert variant="success" className="mt-3">{msg}</Alert>}

            <Row className="event-detail-layout mt-4">
                <Col lg={7}>
                    {event.imageUrl ? <img className="event-detail-image" src={event.imageUrl} alt={event.title} /> :
                        <div className="event-detail-placeholder">EVENT</div>}

                    {event.videoUrl && <video className="organizer-detail-video mt-3" src={event.videoUrl} controls />}
                </Col>

                <Col lg={5}>
                    <div className="event-detail-summary glass-panel">
                        <div className="page-kicker mb-3">{event.categoryName || "Organizer"}</div>
                        <h1 className="event-detail-title">{event.title}</h1>
                        <div className="event-badge mt-3">{event.statusName || "Đang cập nhật"}</div>
                        <div className="event-detail-price">{formatPrice(event.price)}</div>

                        <div className="event-detail-stats">
                            <div>
                                <span>Tổng vé</span>
                                <strong>{event.totalTickets} vé</strong>
                            </div>
                            <div>
                                <span>Còn lại</span>
                                <strong>{event.availableTickets} vé</strong>
                            </div>
                            <div>
                                <span>Đã bán</span>
                                <strong>{event.soldTickets} vé</strong>
                            </div>
                            <div>
                                <span>Phí niêm yết</span>
                                <strong>{formatPrice(event.listingFee)}</strong>
                            </div>
                        </div>

                        {renderStatusActions()}
                    </div>
                </Col>
            </Row>

            <section className="organizer-finance-panel glass-panel">
                <div className="organizer-finance-head">
                    <div>
                        <div className="page-kicker mb-2">Finance</div>
                        <h2>Báo cáo tài chính</h2>
                    </div>
                    <Button className="btn-soft-pink" onClick={loadEvent} disabled={loading}>Làm mới</Button>
                </div>

                {statsErr && <Alert className="alert-dark-pink mt-3">{statsErr}</Alert>}

                <div className="organizer-finance-grid">
                    <div>
                        <span>Doanh thu vé</span>
                        <strong>{formatPrice(financialStats?.grossRevenue)}</strong>
                    </div>
                    <div>
                        <span>Phí niêm yết</span>
                        <strong>{formatPrice(financialStats?.listingFee)}</strong>
                    </div>
                    <div>
                        <span>Doanh thu thực nhận</span>
                        <strong>{formatPrice(financialStats?.netRevenue)}</strong>
                    </div>
                    <div>
                        <span>Booking đã thanh toán</span>
                        <strong>{financialStats?.paidBookings || 0}</strong>
                    </div>
                    <div>
                        <span>Vé đã bán</span>
                        <strong>{financialStats?.ticketsSold || 0} vé</strong>
                    </div>
                    <div>
                        <span>Giá vé</span>
                        <strong>{formatPrice(financialStats?.ticketPrice)}</strong>
                    </div>
                </div>
            </section>

            <Row className="event-detail-info">
                <Col lg={7}>
                    <section className="event-detail-section">
                        <h2>Mô tả sự kiện</h2>
                        <p>{event.description || "Sự kiện chưa có mô tả chi tiết."}</p>
                    </section>
                </Col>

                <Col lg={5}>
                    <section className="event-detail-section">
                        <h2>Thông tin quản lý</h2>
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
                                <dt>Mã đối soát</dt>
                                <dd>{event.settlementCode || "Chưa có"}</dd>
                            </div>
                            <div>
                                <dt>Phí đăng tin</dt>
                                <dd>{event.isPaidFee ? "Đã thanh toán" : "Chưa thanh toán"}</dd>
                            </div>
                            <div>
                                <dt>Cập nhật lúc</dt>
                                <dd>{formatDate(event.updatedDate || event.createdDate)}</dd>
                            </div>
                        </dl>
                    </section>
                </Col>
            </Row>
        </div>
    );
}

export default OrganizerEventDetail;
