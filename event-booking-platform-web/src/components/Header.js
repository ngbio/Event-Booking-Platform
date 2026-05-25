import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import cookies from 'react-cookies'

const Header = () => {
    const [user, setUser] = useState(cookies.load('user'));
    const nav = useNavigate();

    const logout = () => {
        cookies.remove('token');
        cookies.remove('user');
        setUser(null);
        nav('/login');
    }

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-success">
            <div className="container">
                <Link className="navbar-brand fw-bold" to="/">Event Booking</Link>

                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar" aria-controls="mainNavbar" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>

                <div className="collapse navbar-collapse" id="mainNavbar">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li className="nav-item">
                            <Link className="nav-link" to="/">Trang chu</Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" to="/events">Su kien</Link>
                        </li>
                    </ul>

                    <ul className="navbar-nav ms-auto mb-2 mb-lg-0">
                        {user === undefined || user === null ? <>
                            <li className="nav-item">
                                <Link className="nav-link" to="/login">Dang nhap</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/register">Dang ky</Link>
                            </li>
                        </> : <>
                            <li className="nav-item">
                                <Link className="nav-link" to="/profile">{user.fullName || user.email}</Link>
                            </li>
                            {user.roleId === 2 && <li className="nav-item">
                                <Link className="nav-link" to="/organizer/events">Su kien cua toi</Link>
                            </li>}
                            <li className="nav-item">
                                <button className="btn btn-outline-light ms-lg-2" type="button" onClick={logout}>Dang xuat</button>
                            </li>
                        </>}
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Header;
