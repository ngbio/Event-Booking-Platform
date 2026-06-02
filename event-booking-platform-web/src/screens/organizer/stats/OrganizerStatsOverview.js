import { useContext, useEffect, useState } from "react";
import { Alert, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "../../../components/MySpinner";
import { authApis, endpoints } from "../../../configs/Apis";
import { MyUserContext } from "../../../configs/Contexts";

const OrganizerStatsOverview = () => {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [user,] = useContext(MyUserContext);
    const nav = useNavigate();

    const formatCurrency = (value) => {
        return Number(value || 0).toLocaleString("vi-VN", {
            style: "currency",
            currency: "VND"
        });
    }

    const formatNumber = (value) => Number(value || 0).toLocaleString("vi-VN");

    const renderRevenuePeriods = (title, periods) => (
        <section className="organizer-finance-panel glass-panel">
            <div className="organizer-finance-head">
                <div>
                    <div className="page-kicker mb-2">Revenue</div>
                    <h2>{title}</h2>
                </div>
            </div>

            <div className="organizer-finance-grid">
                {(periods || []).map(item => <div key={item.period}>
                    <span>{item.label}</span>
                    <strong>{formatCurrency(item.revenue)}</strong>
                </div>)}
            </div>
        </section>
    );

    const loadStats = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await authApis().get(endpoints["organizer-stats-overview"]);
            setStats(res.data.data);
        } catch (ex) {
            console.error(ex);
            setStats(null);
            setErr(ex.response?.data?.message || "Không thể tải báo cáo doanh thu.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        if (user?.roleId === 2)
            loadStats();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [user?.id, user?.roleId]);

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập bằng tài khoản nhà tổ chức để xem báo cáo doanh thu.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav("/login?next=/organizer/stats")}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (user?.roleId !== 2)
        return <Alert className="alert-dark-pink">Chỉ tài khoản nhà tổ chức mới có thể xem báo cáo doanh thu.</Alert>;

    if (loading && !stats)
        return <MySpinner />;

    return (
        <div className="organizer-stats-screen">
            <div className="organizer-list-head">
                <div>
                    <div className="page-kicker mb-2">Revenue</div>
                    <h1 className="organizer-title">Báo cáo doanh thu</h1>
                    <p className="organizer-stats-subtitle">
                        Tổng quan doanh thu và hiệu suất bán vé của {stats?.organizerName || user?.fullName || "nhà tổ chức"}.
                    </p>
                </div>
                <Button className="btn-soft-pink" onClick={loadStats} disabled={loading}>
                    {loading ? "Đang tải..." : "Làm mới"}
                </Button>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}

            <section className="organizer-stats-highlight glass-panel">
                <span>Tổng doanh thu</span>
                <strong>{formatCurrency(stats?.totalRevenue)}</strong>
                <p>Doanh thu từ các booking đã thanh toán.</p>
            </section>

            <section className="organizer-stats-grid">
                <div className="glass-panel">
                    <span>Tổng sự kiện</span>
                    <strong>{formatNumber(stats?.totalEvents)}</strong>
                </div>
                <div className="glass-panel">
                    <span>Booking đã thanh toán</span>
                    <strong>{formatNumber(stats?.totalPaidBookings)}</strong>
                </div>
                <div className="glass-panel">
                    <span>Vé đã bán</span>
                    <strong>{formatNumber(stats?.totalTicketsSold)}</strong>
                </div>
            </section>

            <div className="organizer-detail-toolbar mt-4">
                <Button className="btn-outline-pink" onClick={() => nav("/organizer/events")}>Xem sự kiện của tôi</Button>
            </div>

            {renderRevenuePeriods("Doanh thu theo tháng", stats?.monthlyRevenue)}
            {renderRevenuePeriods("Doanh thu theo quý", stats?.quarterlyRevenue)}
            {renderRevenuePeriods("Doanh thu theo năm", stats?.yearlyRevenue)}
        </div>
    );
}

export default OrganizerStatsOverview;
