import { useContext, useEffect, useRef, useState } from "react";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const TicketList = () => {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [statusId, setStatusId] = useState("");
    const loadedRequestRef = useRef("");
    const [user,] = useContext(MyUserContext);
    const nav = useNavigate();

    const buildQrImageUrl = (value, size = 120) => {
        if (!value)
            return "";

        return `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&data=${encodeURIComponent(value)}`;
    }

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

    const loadTickets = async () => {
        const requestKey = statusId || "all";
        if (loadedRequestRef.current === requestKey)
            return;

        loadedRequestRef.current = requestKey;

        try {
            setLoading(true);
            setErr("");

            const url = statusId ? `${endpoints["tickets"]}?statusId=${statusId}` : endpoints["tickets"];
            const res = await authApis().get(url);
            setTickets(res.data.data || []);
        } catch (ex) {
            console.error(ex);
            loadedRequestRef.current = "";
            setTickets([]);
            setErr(ex.response?.data?.message || "Không thể tải danh sách vé.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadedRequestRef.current = "";
        if (user?.roleId === 3)
            loadTickets();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [statusId, user?.id, user?.roleId]);

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập để xem vé của bạn.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav("/login?next=/tickets")}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (user?.roleId !== 3)
        return <Alert className="alert-dark-pink">Chỉ tài khoản người mua vé mới có danh sách vé.</Alert>;

    return (
        <div className="ticket-screen">
            <div className="organizer-list-head">
                <div>
                    <div className="page-kicker mb-2">Tickets</div>
                    <h1 className="organizer-title">Vé của tôi</h1>
                </div>

                <Form.Select className="booking-status-filter" value={statusId} onChange={e => setStatusId(e.target.value)}>
                    <option value="">Tất cả trạng thái</option>
                    <option value="1">Còn hiệu lực</option>
                    <option value="2">Đã check-in</option>
                </Form.Select>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
            {loading && <MySpinner />}

            {!loading && tickets.length === 0 && !err && <Alert className="alert-dark-pink mt-3">Bạn chưa có vé nào.</Alert>}

            <Row className="ticket-grid mt-3">
                {tickets.map(t => <Col xs={12} md={6} xl={4} key={t.id}>
                    <article className="ticket-card glass-panel">
                        <div className="ticket-card-head">
                            <span className="event-badge">{t.statusName || "Đang cập nhật"}</span>
                            <strong>#{t.id}</strong>
                        </div>

                        <h2>{t.eventTitle || "Sự kiện"}</h2>

                        <div className="ticket-qr-preview">
                            {t.qrCode ? <img src={buildQrImageUrl(t.qrCode)} alt={`QR ${t.qrCode}`} /> : "QR"}
                        </div>

                        <dl className="booking-card-list">
                            <div>
                                <dt>Booking</dt>
                                <dd>#{t.bookingId}</dd>
                            </div>
                            <div>
                                <dt>Giá vé</dt>
                                <dd>{formatPrice(t.unitPrice)}</dd>
                            </div>
                            <div>
                                <dt>Bắt đầu</dt>
                                <dd>{formatDate(t.eventStartTime)}</dd>
                            </div>
                        </dl>

                        <Button className="btn-pink w-100 mt-3" onClick={() => nav(`/tickets/${t.id}`)}>Xem chi tiết</Button>
                    </article>
                </Col>)}
            </Row>
        </div>
    );
}

export default TicketList;
