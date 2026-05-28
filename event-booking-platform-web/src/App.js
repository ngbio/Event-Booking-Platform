import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container } from "react-bootstrap";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./screens/Home/Home";
import Register from "./screens/User/Register";
import Login from "./screens/User/Login";
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

const App = () => {
  return (
    <BrowserRouter>
      <div className="app-shell">
        <Header />

        <main className="app-main">
          <Container>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/events" element={<Home />} />
              <Route path="/register" element={<Register />} />
              <Route path="/login" element={<Login />} />
              <Route path="*" element={<h2 className="text-center text-danger">Khong tim thay trang</h2>} />
            </Routes>
          </Container>
        </main>

        <Footer />
      </div>
    </BrowserRouter>
  );
}

export default App;
