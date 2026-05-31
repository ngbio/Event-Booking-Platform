import { useEffect, useRef, useState } from "react";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";
import { Alert, Button, Card, Col, Form, Row } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";

const Home = () => {
    const [events, setEvents] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const [q, setSearchParams] = useSearchParams();
    const lastQueryRef = useRef("");
    const loadedRequestRef = useRef("");
    const nav = useNavigate();
    const queryString = q.toString();
    const bannerEvents = events.filter(e => e.imageUrl).slice(0, 8);
    const [activeBanner, setActiveBanner] = useState(0);
    const [filters, setFilters] = useState({
        kw: q.get("kw") || "",
        categoryId: q.get("categoryId") || q.get("cateId") || "",
        location: q.get("location") || "",
        fromDate: q.get("fromDate") || "",
        toDate: q.get("toDate") || "",
        minPrice: q.get("minPrice") || "",
        maxPrice: q.get("maxPrice") || "",
        sort: q.get("sort") || ""
    });

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

    const loadCategories = async () => {
        try {
            const res = await Apis.get(endpoints["categories"]);
            setCategories(res.data.data || []);
        } catch (ex) {
            console.error(ex);
            setCategories([]);
        }
    }

    const buildEventUrl = () => {
        const params = new URLSearchParams(q);
        params.set("page", page);
        return `${endpoints["events"]}?${params.toString()}`;
    }

    const loadEvents = async () => {
        const requestKey = `${queryString}|${page}`;
        if (loadedRequestRef.current === requestKey)
            return;

        loadedRequestRef.current = requestKey;

        try {
            setLoading(true);

            const res = await Apis.get(buildEventUrl());
            const data = res.data.data || [];

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
        loadCategories();
    }, []);

    useEffect(() => {
        setFilters({
            kw: q.get("kw") || "",
            categoryId: q.get("categoryId") || q.get("cateId") || "",
            location: q.get("location") || "",
            fromDate: q.get("fromDate") || "",
            toDate: q.get("toDate") || "",
            minPrice: q.get("minPrice") || "",
            maxPrice: q.get("maxPrice") || "",
            sort: q.get("sort") || ""
        });
    }, [queryString, q]);

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

    useEffect(() => {
        if (activeBanner >= bannerEvents.length)
            setActiveBanner(0);
    }, [activeBanner, bannerEvents.length]);

    useEffect(() => {
        if (bannerEvents.length <= 1)
            return;

        const timer = setInterval(() => {
            setActiveBanner(current => (current + 1) % bannerEvents.length);
        }, 3500);

        return () => clearInterval(timer);
    }, [bannerEvents.length]);

    const updateFilter = (field, value) => {
        setFilters(current => ({...current, [field]: value}));
    }

    const submitFilters = (e) => {
        e.preventDefault();

        const nextParams = new URLSearchParams();
        Object.entries(filters).forEach(([key, value]) => {
            if (value !== null && value !== undefined && `${value}`.trim() !== "")
                nextParams.set(key, `${value}`.trim());
        });

        setSearchParams(nextParams);
    }

    const clearFilters = () => {
        setFilters({
            kw: "",
            categoryId: "",
            location: "",
            fromDate: "",
            toDate: "",
            minPrice: "",
            maxPrice: "",
            sort: ""
        });
        setSearchParams({});
    }

    const loadMore = () => {
        if (!loading && hasMore)
            setPage(page + 1);
    }

    const moveBanner = (step) => {
        setActiveBanner(current => (current + step + bannerEvents.length) % bannerEvents.length);
    }

    return (
        <>
            {bannerEvents.length > 0 && <section className="movie-banner-carousel glass-panel">
                {bannerEvents.length > 1 && <button className="movie-banner-arrow movie-banner-arrow-left" type="button" aria-label="Ảnh trước" onClick={() => moveBanner(-1)}>
                    ‹
                </button>}

                <div className="movie-banner-viewport">
                    <div className="movie-banner-track" style={{ transform: `translateX(-${activeBanner * 100}%)` }}>
                        {bannerEvents.map(e => <button className="movie-banner-slide" key={e.id} type="button" onClick={() => nav(`/events/${e.id}`)}>
                            <img src={e.imageUrl} alt={e.title} />
                            <div className="movie-banner-caption">
                                <span>Đang mở bán</span>
                                <strong>{e.title}</strong>
                            </div>
                        </button>)}
                    </div>
                </div>

                {bannerEvents.length > 1 && <button className="movie-banner-arrow movie-banner-arrow-right" type="button" aria-label="Ảnh tiếp theo" onClick={() => moveBanner(1)}>
                    ›
                </button>}

                {bannerEvents.length > 1 && <div className="movie-banner-dots">
                    {bannerEvents.map((e, index) => <button
                        key={e.id}
                        className={`movie-banner-dot ${index === activeBanner ? "active" : ""}`}
                        type="button"
                        aria-label={`Chọn ảnh ${index + 1}`}
                        onClick={() => setActiveBanner(index)}
                    ></button>)}
                </div>}
            </section>}

            <Form className="event-search-panel glass-panel" onSubmit={submitFilters}>
                <Row className="g-3">
                    <Col lg={4} md={6}>
                        <Form.Label>Tên sự kiện</Form.Label>
                        <Form.Control value={filters.kw} onChange={e => updateFilter("kw", e.target.value)} placeholder="Nhập tên, lĩnh vực, địa điểm..." />
                    </Col>
                    <Col lg={2} md={6}>
                        <Form.Label>Lĩnh vực</Form.Label>
                        <Form.Select value={filters.categoryId} onChange={e => updateFilter("categoryId", e.target.value)}>
                            <option value="">Tất cả</option>
                            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                        </Form.Select>
                    </Col>
                    <Col lg={3} md={6}>
                        <Form.Label>Địa điểm</Form.Label>
                        <Form.Control value={filters.location} onChange={e => updateFilter("location", e.target.value)} placeholder="VD: TP.HCM, Hà Nội..." />
                    </Col>
                    <Col lg={3} md={6}>
                        <Form.Label>Sắp xếp</Form.Label>
                        <Form.Select value={filters.sort} onChange={e => updateFilter("sort", e.target.value)}>
                            <option value="">Mặc định</option>
                            <option value="dateAsc">Ngày gần nhất</option>
                            <option value="dateDesc">Ngày xa nhất</option>
                            <option value="priceAsc">Giá thấp đến cao</option>
                            <option value="priceDesc">Giá cao đến thấp</option>
                            <option value="popular">Phổ biến nhất</option>
                        </Form.Select>
                    </Col>
                    <Col lg={3} md={6}>
                        <Form.Label>Từ ngày</Form.Label>
                        <Form.Control type="date" value={filters.fromDate} onChange={e => updateFilter("fromDate", e.target.value)} />
                    </Col>
                    <Col lg={3} md={6}>
                        <Form.Label>Đến ngày</Form.Label>
                        <Form.Control type="date" value={filters.toDate} onChange={e => updateFilter("toDate", e.target.value)} />
                    </Col>
                    <Col lg={2} md={6}>
                        <Form.Label>Giá từ</Form.Label>
                        <Form.Control type="number" min="0" value={filters.minPrice} onChange={e => updateFilter("minPrice", e.target.value)} placeholder="0" />
                    </Col>
                    <Col lg={2} md={6}>
                        <Form.Label>Giá đến</Form.Label>
                        <Form.Control type="number" min="0" value={filters.maxPrice} onChange={e => updateFilter("maxPrice", e.target.value)} placeholder="1000000" />
                    </Col>
                    <Col lg={2} md={12} className="event-search-actions">
                        <Button className="btn-pink" type="submit">Tìm kiếm</Button>
                        <Button className="btn-outline-pink" type="button" onClick={clearFilters}>Xóa lọc</Button>
                    </Col>
                </Row>
            </Form>

            {events.length === 0 && loading === false && <Alert className="alert-dark-pink mt-3">Không có sự kiện nào.</Alert>}

            <Row className="event-grid mt-3">
                {events.map(e =>
                    <Col xs={12} sm={6} lg={4} xl={3} key={e.id}>
                        <Card className="event-card">
                            {e.imageUrl ? <Card.Img className="event-card-img" variant="top" src={e.imageUrl} /> :
                                <div className="event-card-placeholder">EVENT</div>}

                            <Card.Body className="d-flex flex-column">
                                <div className="event-badge mb-3">Còn {e.availableTickets} vé</div>
                                <Card.Title className="event-title">{e.title}</Card.Title>
                                <Card.Text className="event-meta mb-1">{e.location || "Đang cập nhật địa điểm"}</Card.Text>
                                <Card.Text className="event-meta">{formatDate(e.startTime)}</Card.Text>
                                <Card.Text className="event-price mt-auto">{formatPrice(e.price)}</Card.Text>
                            </Card.Body>

                            <Card.Body className="pt-0">
                                <Button className="btn-pink w-100" onClick={() => nav(`/events/${e.id}`)}>Xem chi tiết</Button>
                            </Card.Body>
                        </Card>
                    </Col>
                )}
            </Row>

            {hasMore && <div className="text-center mt-4 mb-2">
                <Button className="btn-soft-pink px-4" onClick={loadMore} disabled={loading}>Xem thêm</Button>
            </div>}

            {loading && <MySpinner />}
        </>
    );
}

export default Home;
