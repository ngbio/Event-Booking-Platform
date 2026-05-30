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
            setStatsErr("");

            const eventRes = await authApis().get(endpoints["organizer-event-details"](eventId));
            setEvent(eventRes.data.data);

            try {
                const statsRes = await authApis().get(endpoints["organizer-event-stats"](eventId));
                setFinancialStats(statsRes.data.data);
            } catch (statsEx) {
                console.error(statsEx);
                setFinancialStats(null);
                setStatsErr(statsEx.response?.data?.message || "Khong the tai bao cao tai chinh su kien.");
            }
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || "Khong the tai chi tiet su kien.");
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
            setMsg("Cap nhat trang thai su kien thanh cong.");
            await loadEvent();
        } catch (ex) {
            console.error(ex);
            setErr(ex.response?.data?.message || "Khong the cap nhat trang thai su kien.");
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
                    Gui duyet
                </Button>}
                {event.statusId === STATUS.PUBLISHED && <Button className="btn-soft-pink" onClick={() => changeStatus(STATUS.COMPLETED)} disabled={savingStatus}>
                    Danh dau hoan thanh
                </Button>}
                <Button className="btn-outline-pink" onClick={() => changeStatus(STATUS.CANCELLED)} disabled={savingStatus}>
                    Huy su kien
                </Button>
            </div>
        );
    }

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui long dang nhap bang tai khoan nha to chuc de xem chi tiet su kien.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav(`/login?next=/organizer/events/${eventId}`)}>Dang nhap</Button>
                </div>
            </Alert>
        );
    }

    if (user?.roleId !== 2)
        return <Alert className="alert-dark-pink">Chi tai khoan nha to chuc moi co the quan ly su kien.</Alert>;

    if (loading)
        return <MySpinner />;

    if (!event && err)
        return <Alert className="alert-dark-pink">{err}</Alert>;

    if (!event)
        return null;

    return (
        <div className="event-detail">
            <div className="organizer-detail-toolbar">
                <Button className="btn-soft-pink" onClick={() => nav("/organizer/events")}>Quay lai</Button>
                <Button className="btn-outline-pink" onClick={() => nav(`/organizer/events/${event.id}/edit`)}>Sua su kien</Button>
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
                        <div className="event-badge mt-3">{event.statusName || "Dang cap nhat"}</div>
                        <div className="event-detail-price">{formatPrice(event.price)}</div>

                        <div className="event-detail-stats">
                            <div>
                                <span>Tong ve</span>
                                <strong>{event.totalTickets} ve</strong>
                            </div>
                            <div>
                                <span>Con lai</span>
                                <strong>{event.availableTickets} ve</strong>
                            </div>
                            <div>
                                <span>Da ban</span>
                                <strong>{event.soldTickets} ve</strong>
                            </div>
                            <div>
                                <span>Phi niem yet</span>
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
                        <h2>Bao cao tai chinh</h2>
                    </div>
                    <Button className="btn-soft-pink" onClick={loadEvent} disabled={loading}>Lam moi</Button>
                </div>

                {statsErr && <Alert className="alert-dark-pink mt-3">{statsErr}</Alert>}

                <div className="organizer-finance-grid">
                    <div>
                        <span>Doanh thu ve</span>
                        <strong>{formatPrice(financialStats?.grossRevenue)}</strong>
                    </div>
                    <div>
                        <span>Phi niem yet</span>
                        <strong>{formatPrice(financialStats?.listingFee)}</strong>
                    </div>
                    <div>
                        <span>Doanh thu thuc nhan</span>
                        <strong>{formatPrice(financialStats?.netRevenue)}</strong>
                    </div>
                    <div>
                        <span>Booking da thanh toan</span>
                        <strong>{financialStats?.paidBookings || 0}</strong>
                    </div>
                    <div>
                        <span>Ve da ban</span>
                        <strong>{financialStats?.ticketsSold || 0} ve</strong>
                    </div>
                    <div>
                        <span>Gia ve</span>
                        <strong>{formatPrice(financialStats?.ticketPrice)}</strong>
                    </div>
                </div>
            </section>

            <Row className="event-detail-info">
                <Col lg={7}>
                    <section className="event-detail-section">
                        <h2>Mo ta su kien</h2>
                        <p>{event.description || "Su kien chua co mo ta chi tiet."}</p>
                    </section>
                </Col>

                <Col lg={5}>
                    <section className="event-detail-section">
                        <h2>Thong tin quan ly</h2>
                        <dl className="event-detail-list">
                            <div>
                                <dt>Bat dau</dt>
                                <dd>{formatDate(event.startTime)}</dd>
                            </div>
                            <div>
                                <dt>Ket thuc</dt>
                                <dd>{formatDate(event.endTime)}</dd>
                            </div>
                            <div>
                                <dt>Dia diem</dt>
                                <dd>{event.location || "Dang cap nhat"}</dd>
                            </div>
                            <div>
                                <dt>Ma doi soat</dt>
                                <dd>{event.settlementCode || "Chua co"}</dd>
                            </div>
                            <div>
                                <dt>Phi dang tin</dt>
                                <dd>{event.isPaidFee ? "Da thanh toan" : "Chua thanh toan"}</dd>
                            </div>
                            <div>
                                <dt>Cap nhat luc</dt>
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
