import { useContext, useEffect, useRef, useState } from "react";
import { Alert, Button, Card, Col, Row } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";
import MySpinner from "../../../components/MySpinner";
import { authApis, endpoints } from "../../../configs/Apis";
import { MyUserContext } from "../../../configs/Contexts";

const pendingOrganizerEventRequests = new Map();

const OrganizerEventList = () => {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const [err, setErr] = useState("");
    const [user,] = useContext(MyUserContext);
    const userId = user?.id;
    const [q] = useSearchParams();
    const lastQueryRef = useRef("");
    const loadedRequestRef = useRef("");
    const nav = useNavigate();
    const queryString = q.toString();

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

    const loadEvents = async () => {
        const requestKey = `${userId}|${queryString}|${page}`;
        if (loadedRequestRef.current === requestKey)
            return;

        loadedRequestRef.current = requestKey;

        try {
            setLoading(true);
            setErr("");

            let url = `${endpoints['organizer-events']}?page=${page}`;

            const kw = q.get("kw");
            if (kw) {
                url = `${url}&kw=${kw}`;
            }

            const statusId = q.get("statusId");
            if (statusId) {
                url = `${url}&statusId=${statusId}`;
            }

            let request = pendingOrganizerEventRequests.get(requestKey);
            if (!request) {
                request = authApis().get(url);
                pendingOrganizerEventRequests.set(requestKey, request);
            }

            let res = await request;
            let data = res.data.data || [];

            if (data.length === 0)
                setHasMore(false);
            if (page === 1)
                setEvents(data);
            else
                setEvents(currentEvents => [...currentEvents, ...data]);
        } catch (ex) {
            console.error(ex);
            loadedRequestRef.current = "";
            setErr(ex.response?.data?.message || "Không thể tải danh sách sự kiện của bạn.");
        } finally {
            pendingOrganizerEventRequests.delete(requestKey);
            setLoading(false);
        }
    }

    useEffect(() => {
        if (user === null)
            return;

        const isNewQuery = lastQueryRef.current !== queryString;

        if (isNewQuery) {
            lastQueryRef.current = queryString;
            loadedRequestRef.current = "";
            setEvents([]);
            setHasMore(true);

            if (page !== 1) {
                setPage(1);
                return;
            }
        }

        loadEvents();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [queryString, page, userId]);

    const loadMore = () => {
        if (!loading && hasMore)
            setPage(page + 1);
    }

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập bằng tài khoản nhà tổ chức để xem sự kiện của bạn.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav("/login?next=/organizer/events")}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (user.roleId !== 2) {
        return <Alert className="alert-dark-pink">Chỉ tài khoản nhà tổ chức mới có thể quản lý sự kiện.</Alert>;
    }

    return (
        <>
            <div className="organizer-list-head">
                <div>
                    <div className="page-kicker mb-2">Organizer</div>
                    <h1 className="organizer-title">Sự kiện của tôi</h1>
                </div>

                <Button className="btn-pink" onClick={() => nav("/organizer/events/new")}>Tạo sự kiện</Button>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
            {events.length === 0 && loading === false && !err && <Alert className="alert-dark-pink mt-3">Bạn chưa có sự kiện nào.</Alert>}

            <Row className="event-grid mt-3">
                {events.map(e => <Col xs={12} md={6} xl={4} key={e.id}>
                    <Card className="event-card">
                        {e.imageUrl ? <Card.Img className="event-card-img" variant="top" src={e.imageUrl} /> :
                            <div className="event-card-placeholder">EVENT</div>}

                        <Card.Body className="d-flex flex-column">
                            <div className="event-badge mb-3">{e.statusName || "Đang cập nhật"}</div>
                            <Card.Title className="event-title">{e.title}</Card.Title>
                            <Card.Text className="event-meta mb-1">{e.location || "Đang cập nhật địa điểm"}</Card.Text>
                            <Card.Text className="event-meta">{formatDate(e.startTime)}</Card.Text>

                            <div className="organizer-event-stats mt-auto">
                                <div>
                                    <span>Tổng vé</span>
                                    <strong>{e.totalTickets}</strong>
                                </div>
                                <div>
                                    <span>Còn lại</span>
                                    <strong>{e.availableTickets}</strong>
                                </div>
                                <div>
                                    <span>Đã bán</span>
                                    <strong>{e.soldTickets}</strong>
                                </div>
                            </div>

                            <Card.Text className="event-price mt-3">{formatPrice(e.price)}</Card.Text>
                        </Card.Body>

                        <Card.Body className="pt-0 organizer-event-actions">
                            <Button className="btn-soft-pink" onClick={() => nav(`/organizer/events/${e.id}`)}>Chi tiết</Button>
                            <Button className="btn-soft-pink" onClick={() => nav(`/organizer/events/${e.id}/edit`)}>Sửa</Button>
                            {[2, 4].includes(e.statusId) && <Button className="btn-soft-pink" onClick={() => nav(`/organizer/events/${e.id}/bookings`)}>Khách mua vé</Button>}
                        </Card.Body>
                    </Card>
                </Col>)}
            </Row>

            {hasMore && <div className="text-center mt-4 mb-2">
                <Button className="btn-soft-pink px-4" onClick={loadMore} disabled={loading}>Xem thêm</Button>
            </div>}

            {loading && <MySpinner />}
        </>
    );
}

export default OrganizerEventList;
