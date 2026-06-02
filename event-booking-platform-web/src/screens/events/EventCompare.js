import { Fragment, useEffect, useState } from "react";
import { Alert, Button, Col, Form, Row } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";

const EventCompare = () => {
    const [q, setSearchParams] = useSearchParams();
    const [eventIdsInput, setEventIdsInput] = useState(q.getAll("eventIds").join(", "));
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const nav = useNavigate();

    const eventIds = q.getAll("eventIds").filter(Boolean);

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

    const loadCompare = async () => {
        if (eventIds.length < 2) {
            setEvents([]);
            setErr("Chon tu 2 den 3 su kien de so sanh.");
            return;
        }

        try {
            setLoading(true);
            setErr("");

            const params = new URLSearchParams();
            eventIds.slice(0, 3).forEach(id => params.append("eventIds", id));
            const res = await Apis.get(`${endpoints["compare-events"]}?${params.toString()}`);
            setEvents(res.data.data || []);
        } catch (ex) {
            console.error(ex);
            setEvents([]);
            setErr(ex.response?.data?.message || "Khong the tai du lieu so sanh su kien.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        setEventIdsInput(eventIds.join(", "));
        loadCompare();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [q.toString()]);

    const submitCompare = (e) => {
        e.preventDefault();
        const ids = eventIdsInput
            .split(",")
            .map(id => id.trim())
            .filter(Boolean)
            .slice(0, 3);

        const next = new URLSearchParams();
        ids.forEach(id => next.append("eventIds", id));
        setSearchParams(next);
    }

    const removeEvent = (id) => {
        const nextIds = eventIds.filter(eventId => String(eventId) !== String(id));
        const next = new URLSearchParams();
        nextIds.forEach(eventId => next.append("eventIds", eventId));
        setSearchParams(next);
    }

    const compareRows = [
        {label: "Linh vuc", render: e => e.categoryName || "Dang cap nhat"},
        {label: "Thoi gian", render: e => formatDate(e.startTime)},
        {label: "Dia diem", render: e => e.location || "Dang cap nhat"},
        {label: "Gia ve", render: e => formatPrice(e.price)},
        {label: "Nha to chuc", render: e => e.organizerName || "Dang cap nhat"},
        {label: "Ve con lai", render: e => `${e.availableTickets || 0} ve`},
        {label: "Mo ta", render: e => e.description || "Chua co mo ta"}
    ];

    return (
        <div className="event-detail">
            <div className="organizer-list-head">
                <div>
                    <div className="page-kicker mb-2">Compare</div>
                    <h1 className="organizer-title">So sanh su kien</h1>
                </div>
                <Button className="btn-soft-pink" onClick={() => nav("/events")}>Chon su kien</Button>
            </div>

            <Form className="event-search-panel glass-panel mt-3" onSubmit={submitCompare}>
                <Row className="g-3 align-items-end">
                    <Col lg={9}>
                        <Form.Label>ID su kien can so sanh</Form.Label>
                        <Form.Control
                            value={eventIdsInput}
                            onChange={e => setEventIdsInput(e.target.value)}
                            placeholder="VD: 1, 2, 3"
                        />
                    </Col>
                    <Col lg={3}>
                        <Button className="btn-pink w-100" type="submit">So sanh</Button>
                    </Col>
                </Row>
            </Form>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
            {loading && <MySpinner />}

            {!loading && events.length > 0 && <section className="glass-panel mt-4">
                <div className="compare-grid" style={{gridTemplateColumns: `minmax(120px, 0.8fr) repeat(${events.length}, minmax(180px, 1fr))`}}>
                    <div className="compare-cell compare-head">Tieu chi</div>
                    {events.map(e => <div className="compare-cell compare-head" key={e.id}>
                        <strong>{e.title}</strong>
                        <Button className="btn-outline-pink btn-sm mt-2" onClick={() => removeEvent(e.id)}>Bo chon</Button>
                    </div>)}

                    {compareRows.map(row => <Fragment key={row.label}>
                        <div className="compare-cell compare-label" key={`${row.label}-label`}>{row.label}</div>
                        {events.map(e => <div className="compare-cell" key={`${row.label}-${e.id}`}>{row.render(e)}</div>)}
                    </Fragment>)}
                </div>
            </section>}
        </div>
    );
}

export default EventCompare;
