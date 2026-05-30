import { useEffect, useRef, useState } from "react";
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
        const requestKey = `${queryString}|${page}`;
        if (loadedRequestRef.current === requestKey)
            return;

        loadedRequestRef.current = requestKey;

        try {
            setLoading(true);

            let url = `${endpoints['events']}?page=${page}`;

            const cateId = q.get("cateId");
            if (cateId) {
                url = `${url}&categoryId=${cateId}`;
            }

            const kw = q.get("kw");
            if (kw) {
                url = `${url}&kw=${kw}`;
            }

            let res = await Apis.get(url);
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
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
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
    }, [queryString, page]);

    const loadMore = () => {
        if (!loading && hasMore)
            setPage(page + 1);
    }

    return (
        <>
            {events.length === 0 && loading === false && <Alert className="alert-dark-pink mt-2">Khong co su kien nao.</Alert>}

            <Row className="event-grid">
                {events.map(e => <>
                    <Col xs={12} sm={6} lg={4} xl={3} key={e.id}>
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
                    </Col>
                </>)}
            </Row>

            {hasMore && <div className="text-center mt-4 mb-2">
                <Button className="btn-soft-pink px-4" onClick={loadMore} disabled={loading}>Xem them</Button>
            </div>}

            {loading && <MySpinner />}
        </>
    );
}

export default Home;
