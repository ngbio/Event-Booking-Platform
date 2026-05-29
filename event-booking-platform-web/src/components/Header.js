import { useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import cookies from 'react-cookies'
import { MyUserContext } from "../configs/Contexts";

const Header = () => {
    const [user, dispatch] = useContext(MyUserContext);
    const nav = useNavigate();

    const logout = () => {
        cookies.remove('token');
        cookies.remove('user');
        dispatch({
            type: "LOGOUT"
        });
        nav('/login');
    }

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
                    {/* <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li className="nav-item">
                            <Link className="nav-link" to="/">Trang chu</Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" to="/events">Su kien</Link>
                        </li>
                    </ul> */}

                    <ul className="navbar-nav ms-auto mb-2 mb-lg-0">
                        {user === undefined || user === null ? <>
                            <li className="nav-item">
                                <Link className="nav-link" to="/login">Dang nhap</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="btn btn-pink ms-lg-2 mt-2 mt-lg-0" to="/register">Dang ky</Link>
                            </li>
                        </> : <>
                            <li className="nav-item">
                                <Link className="nav-link" to="/profile">{user.fullName || user.email}</Link>
                            </li>
                            {user.roleId === 2 && <li className="nav-item">
                                <Link className="nav-link" to="/organizer/events">Su kien cua toi</Link>
                            </li>}
                            <li className="nav-item">
                                <button className="btn btn-outline-pink ms-lg-2 mt-2 mt-lg-0" type="button" onClick={logout}>Dang xuat</button>
                            </li>
                        </>}
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Header;
