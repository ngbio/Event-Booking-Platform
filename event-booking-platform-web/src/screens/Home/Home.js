import { useEffect, useState } from "react";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";
import { Alert, Button, Card, Col, Row } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";

const Home = () => {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [q] = useSearchParams();
    const nav = useNavigate();

    const formatDate = (value) => {
        if (!value)
            return "";

        return new Date(value.replace(" ", "T")).toLocaleString("vi-VN");
    }

    const formatPrice = (value) => {
        if (value === null || value === undefined)
            return "Mien phi";

        return Number(value).toLocaleString("vi-VN", {
            style: "currency",
            currency: "VND"
        });
    }

    const loadEvents = async () => {
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
                setPage(0);
            if (page === 1)
                setEvents(data);
            else
                setEvents([...events, ...data]);
        } catch (ex) {
            console.error(ex);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadEvents();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [q, page]);

    useEffect(() => {
        setPage(1);
        setEvents([]);
    }, [q]);

    const loadMore = () => {
        setPage(page + 1);
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

            {page > 0 && <div className="text-center mt-4 mb-2">
                <Button className="btn-outline-pink px-4" onClick={loadMore}>Xem them</Button>
            </div>}

            {loading && <MySpinner />}
        </>
    );
}

export default Home;
