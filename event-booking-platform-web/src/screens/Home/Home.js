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
            {events.length === 0 && loading === false && <Alert variant="info" className="mt-2">KHONG co su kien nao!</Alert>}
            <Row>
                {events.map(e => <Col xs={6} md={3} key={e.id} className="p-2">
                    <Card className="h-100">
                        {e.imageUrl && <Card.Img variant="top" src={e.imageUrl} />}
                        <Card.Body>
                            <Card.Title>{e.title}</Card.Title>
                            <Card.Text>{e.location}</Card.Text>
                            <Card.Text>{formatDate(e.startTime)}</Card.Text>
                            <Card.Text>Ve con lai: {e.availableTickets}</Card.Text>
                            <Card.Text>{formatPrice(e.price)}</Card.Text>
                        </Card.Body>

                        <Card.Body>
                            <Button variant="info" onClick={() => nav(`/events/${e.id}`)}>Xem chi tiet</Button>
                        </Card.Body>
                    </Card>
                </Col>)}
            </Row>
            {page > 0 && <div className="text-center mb-2">
                <Button variant="success" onClick={loadMore}>Xem them...</Button>
            </div>}

            {loading && <MySpinner />}
        </>
    );
}

export default Home;
