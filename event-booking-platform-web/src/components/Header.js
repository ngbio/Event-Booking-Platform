import { useContext, useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import cookies from 'react-cookies'
import { MyUserContext } from "../configs/Contexts";

const MenuIcon = ({ type }) => {
    const icons = {
        ticket: <path d="M4 8V5a1 1 0 0 1 1-1h14a1 1 0 0 1 1 1v3a2 2 0 0 0 0 4v3a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-3a2 2 0 0 0 0-4Zm8-3v14" />,
        booking: <path d="M7 3h8l4 4v14H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2Zm7 1v4h4M8 12h8M8 16h8M8 20h5" />,
        event: <path d="M7 3v4M17 3v4M4 9h16M6 5h12a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Zm3 8h3v3H9z" />,
        stats: <path d="M4 19h16M7 16V9M12 16V5M17 16v-4" />,
        chat: <path d="M5 5h14a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H9l-5 4v-4H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Zm3 5h8M8 13h5" />,
        password: <path d="M7 11V8a5 5 0 0 1 10 0v3M6 11h12v9H6zM12 15v2" />,
        logout: <path d="M10 5H6a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h4M14 8l4 4-4 4M18 12H9" />
    };

    return (
        <svg className="profile-dropdown-svg" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <g stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                {icons[type]}
            </g>
        </svg>
    );
}

const Header = () => {
    const [user, dispatch] = useContext(MyUserContext);
    const [profileMenuOpen, setProfileMenuOpen] = useState(false);
    const profileMenuRef = useRef(null);
    const nav = useNavigate();

    const logout = () => {
        cookies.remove('token');
        cookies.remove('user');
        dispatch({
            type: "LOGOUT"
        });
        setProfileMenuOpen(false);
        nav('/login');
    }

    useEffect(() => {
        const closeMenu = (e) => {
            if (profileMenuRef.current && !profileMenuRef.current.contains(e.target))
                setProfileMenuOpen(false);
        };

        const closeWithEscape = (e) => {
            if (e.key === "Escape")
                setProfileMenuOpen(false);
        };

        document.addEventListener("mousedown", closeMenu);
        document.addEventListener("keydown", closeWithEscape);

        return () => {
            document.removeEventListener("mousedown", closeMenu);
            document.removeEventListener("keydown", closeWithEscape);
        };
    }, []);

    const displayName = user?.fullName || user?.email || "Người dùng";
    const avatarLetter = displayName.charAt(0).toUpperCase();
    const closeProfileMenu = () => setProfileMenuOpen(false);

    return (
        <nav className="navbar navbar-expand-lg navbar-dark site-navbar sticky-top">
            <div className="container">
                <Link className="navbar-brand fw-bold brand-mark" to="/">
                    <span className="brand-dot"></span>
                    <span>Event Booking</span>
                </Link>

                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar" aria-controls="mainNavbar" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>

                <div className="collapse navbar-collapse" id="mainNavbar">
                    <ul className="navbar-nav ms-auto mb-2 mb-lg-0">
                        {user === undefined || user === null ? <>
                            <li className="nav-item">
                                <Link className="nav-link" to="/login">Đăng nhập</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="btn btn-pink ms-lg-2 mt-2 mt-lg-0" to="/register">Đăng ký</Link>
                            </li>
                        </> : <>
                            <li className="nav-item profile-menu-wrap" ref={profileMenuRef}>
                                <button
                                    className="profile-menu-toggle"
                                    type="button"
                                    aria-label="Mở menu tài khoản"
                                    aria-expanded={profileMenuOpen}
                                    onClick={() => setProfileMenuOpen(!profileMenuOpen)}
                                >
                                    {user.avatar ? <img className="header-avatar" src={user.avatar} alt={displayName} /> :
                                        <span className="header-avatar header-avatar-fallback">{avatarLetter}</span>}
                                    <span className="profile-menu-caret">v</span>
                                </button>

                                {profileMenuOpen && <div className="profile-dropdown glass-panel">
                                    <div className="profile-dropdown-head">
                                        {user.avatar ? <img className="profile-dropdown-avatar" src={user.avatar} alt={displayName} /> :
                                            <span className="profile-dropdown-avatar profile-dropdown-avatar-fallback">{avatarLetter}</span>}
                                        <div>
                                            <strong>{displayName}</strong>
                                            <span>{user.email}</span>
                                        </div>
                                    </div>

                                    <Link className="profile-dropdown-primary" to="/profile" onClick={closeProfileMenu}>
                                        Xem trang cá nhân
                                    </Link>

                                    <div className="profile-dropdown-divider"></div>

                                    {user.roleId === 3 && <>
                                        <Link className="profile-dropdown-item" to="/tickets" onClick={closeProfileMenu}>
                                            <span className="profile-dropdown-icon"><MenuIcon type="ticket" /></span>
                                            <span>Vé của tôi</span>
                                        </Link>
                                        <Link className="profile-dropdown-item" to="/bookings" onClick={closeProfileMenu}>
                                            <span className="profile-dropdown-icon"><MenuIcon type="booking" /></span>
                                            <span>Đơn đặt chỗ</span>
                                        </Link>
                                        <Link className="profile-dropdown-item" to="/chats" onClick={closeProfileMenu}>
                                            <span className="profile-dropdown-icon"><MenuIcon type="chat" /></span>
                                            <span>Tin nhắn</span>
                                        </Link>
                                    </>}
                                    {user.roleId === 2 && <>
                                        <Link className="profile-dropdown-item" to="/organizer/events" onClick={closeProfileMenu}>
                                            <span className="profile-dropdown-icon"><MenuIcon type="event" /></span>
                                            <span>Sự kiện của tôi</span>
                                        </Link>
                                        <Link className="profile-dropdown-item" to="/chats" onClick={closeProfileMenu}>
                                            <span className="profile-dropdown-icon"><MenuIcon type="chat" /></span>
                                            <span>Tin nhắn</span>
                                        </Link>
                                        <Link className="profile-dropdown-item" to="/organizer/stats" onClick={closeProfileMenu}>
                                            <span className="profile-dropdown-icon"><MenuIcon type="stats" /></span>
                                            <span>Báo cáo doanh thu</span>
                                        </Link>
                                    </>}
                                    <Link className="profile-dropdown-item" to="/profile/password" onClick={closeProfileMenu}>
                                        <span className="profile-dropdown-icon"><MenuIcon type="password" /></span>
                                        <span>Đổi mật khẩu</span>
                                    </Link>

                                    <button className="profile-dropdown-item profile-dropdown-button" type="button" onClick={logout}>
                                        <span className="profile-dropdown-icon"><MenuIcon type="logout" /></span>
                                        <span>Đăng xuất</span>
                                    </button>
                                </div>}
                            </li>
                        </>}
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Header;
