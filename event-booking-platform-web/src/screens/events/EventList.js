import { useCallback, useEffect, useRef, useState } from "react";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";
import { Alert, Button, Card, Col, Row } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";

const Home = () => {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const [q] = useSearchParams();
    const lastQueryRef = useRef("");
    const loadedRequestsRef = useRef(new Set());
    const nav = useNavigate();
    const queryString = q.toString();

    const formatDate = (value) => {
        if (!value)
            return "";

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

    const loadEvents = useCallback(async (pageToLoad) => {
        const requestKey = `${queryString}|${pageToLoad}`;
        if (loadedRequestsRef.current.has(requestKey))
            return;

        loadedRequestsRef.current.add(requestKey);

        try {
            setLoading(true);

            const params = new URLSearchParams(queryString);
            params.set("page", pageToLoad);

            if (params.has("cateId")) {
                params.set("categoryId", params.get("cateId"));
                params.delete("cateId");
            }

            let res = await Apis.get(`${endpoints['events']}?${params.toString()}`);
            let data = res.data.data || [];

            setHasMore(data.length > 0);
            if (pageToLoad === 1)
                setEvents(data);
            else
                setEvents(currentEvents => {
                    const existingIds = new Set(currentEvents.map(e => e.id));
                    const newEvents = data.filter(e => !existingIds.has(e.id));
                    return [...currentEvents, ...newEvents];
                });
        } catch (ex) {
            console.error(ex);
            loadedRequestsRef.current.delete(requestKey);
        } finally {
            setLoading(false);
        }
    }, [queryString]);

    useEffect(() => {
        const isNewQuery = lastQueryRef.current !== queryString;

        if (isNewQuery) {
            lastQueryRef.current = queryString;
            loadedRequestsRef.current.clear();
            setEvents([]);
            setHasMore(true);

            if (page !== 1) {
                setPage(1);
                return;
            }
        }

        loadEvents(page);
    }, [queryString, page, loadEvents]);

    const loadMore = () => {
        if (!loading && hasMore)
            setPage(currentPage => currentPage + 1);
    }

    return (
        <>
            <section className="event-hero">
                <div className="page-kicker mb-3">Live events</div>
                <h1 className="page-title mb-3">Find your next night out.</h1>
                <p className="page-subtitle mb-0">Nhung su kien dang mo ban ve, cap nhat theo thoi gian thuc.</p>
            </section>

            {events.length === 0 && loading === false && <Alert className="alert-dark-pink mt-2">Khong co su kien nao.</Alert>}

            <Row className="event-grid">
                {events.map(e => <Col xs={12} sm={6} lg={4} xl={3} key={e.id}>
                    <Card className="event-card">
                        {e.imageUrl ? <Card.Img className="event-card-img" variant="top" src={e.imageUrl} /> :
                            <div className="event-card-placeholder">EVENT</div>}
                        <Card.Body className="d-flex flex-column">
                            <div className="event-badge mb-3">Con {e.availableTickets} ve</div>
                            <Card.Title className="event-title">{e.title}</Card.Title>
                            <Card.Text className="event-meta mb-1">{e.location || "Dang cap nhat dia diem"}</Card.Text>
                            <Card.Text className="event-meta">{formatDate(e.startTime)}</Card.Text>
                            <Card.Text className="event-price mt-auto">{formatPrice(e.price)}</Card.Text>
                        </Card.Body>

                        <Card.Body className="pt-0">
                            <Button className="btn-pink w-100" onClick={() => nav(`/events/${e.id}`)}>Xem chi tiet</Button>
                        </Card.Body>
                    </Card>
                </Col>)}
            </Row>

            {hasMore && <div className="text-center mt-4 mb-2">
                <Button className="btn-soft-pink px-4" onClick={loadMore} disabled={loading}>Xem them</Button>
            </div>}

            {loading && <MySpinner />}
        </>
    );
}

export default Home;
