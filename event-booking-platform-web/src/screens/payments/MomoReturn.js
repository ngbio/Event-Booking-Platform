import { useEffect, useMemo, useState } from "react";
import { Alert, Button } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";

const MomoReturn = () => {
    const [searchParams] = useSearchParams();
    const [loading, setLoading] = useState(true);
    const [result, setResult] = useState(null);
    const [err, setErr] = useState("");
    const nav = useNavigate();

    const fallbackBookingId = useMemo(() => {
        const orderId = searchParams.get("orderId") || "";
        const match = orderId.match(/^MOMO_BOOKING_(\d+)(?:_\d+)?$/);
        return match ? match[1] : null;
    }, [searchParams]);

    useEffect(() => {
        const confirmPayment = async () => {
            try {
                setLoading(true);
                setErr("");

                const params = Object.fromEntries(searchParams.entries());
                const res = await Apis.get(endpoints["momo-redirect"], { params });
                setResult(res.data);
            } catch (ex) {
                console.error(ex);
                setErr(ex.response?.data?.message || "Không thể xác nhận kết quả thanh toán MoMo.");
            } finally {
                setLoading(false);
            }
        };

        confirmPayment();
    }, [searchParams]);

    if (loading)
        return <MySpinner />;

    const bookingId = result?.bookingId || fallbackBookingId;
    const success = Number(result?.resultCode ?? searchParams.get("resultCode")) === 0;

    return (
        <div className="booking-screen">
            <section className="booking-detail-panel glass-panel">
                <div className="page-kicker mb-2">MoMo</div>
                <h1>{success ? "Thanh toán thành công" : "Thanh toán chưa hoàn tất"}</h1>

                {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
                {!err && <Alert variant={success ? "success" : "warning"} className="mt-3">
                    {result?.message || searchParams.get("message") || "Đã nhận kết quả thanh toán từ MoMo."}
                </Alert>}

                <div className="organizer-detail-toolbar mt-4">
                    {bookingId && <Button className="btn-pink" onClick={() => nav(`/bookings/${bookingId}`)}>
                        Xem booking
                    </Button>}
                    <Button className="btn-outline-pink" onClick={() => nav("/events")}>
                        Về danh sách sự kiện
                    </Button>
                </div>
            </section>
        </div>
    );
};

export default MomoReturn;
