import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Container } from "react-bootstrap";
import { useReducer } from "react";
import Header from "./components/Header";
import Footer from "./components/Footer";
import EventList from "./screens/events/EventList";
import EventDetail from "./screens/events/EventDetail";
import Register from "./screens/auth/Register";
import Login from "./screens/auth/Login";
import Profile from "./screens/account/Profile";
import ChangePassword from "./screens/account/ChangePassword";
import BookingList from "./screens/bookings/BookingList";
import BookingDetail from "./screens/bookings/BookingDetail";
import TicketList from "./screens/tickets/TicketList";
import TicketDetail from "./screens/tickets/TicketDetail";
import ChatList from "./screens/chats/ChatList";
import ChatRoom from "./screens/chats/ChatRoom";
import OrganizerEventList from "./screens/organizer/events/OrganizerEventList";
import OrganizerEventDetail from "./screens/organizer/events/OrganizerEventDetail";
import OrganizerEventForm from "./screens/organizer/events/OrganizerEventForm";
import OrganizerStatsOverview from "./screens/organizer/stats/OrganizerStatsOverview";
import OrganizerEventStats from "./screens/organizer/stats/OrganizerEventStats";
import NotFound from "./screens/common/NotFound";
import { MyUserContext } from "./configs/Contexts";
import MyUserReducer from "./reducers/MyUserReducer";
import cookies from "react-cookies";
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

const App = () => {
  const [user, dispatch] = useReducer(MyUserReducer, cookies.load('user') || null);

  return (
    <MyUserContext.Provider value={[user, dispatch]}>
      <BrowserRouter>
        <div className="app-shell">
          <Header />

          <main className="app-main">
            <Container>
              <Routes>
                <Route path="/" element={<EventList />} />
                <Route path="/events" element={<EventList />} />
                <Route path="/events/:eventId" element={<EventDetail />} />
                <Route path="/register" element={<Register />} />
                <Route path="/login" element={<Login />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/profile/password" element={<ChangePassword />} />
                <Route path="/bookings" element={<BookingList />} />
                <Route path="/bookings/:bookingId" element={<BookingDetail />} />
                <Route path="/tickets" element={<TicketList />} />
                <Route path="/tickets/:ticketId" element={<TicketDetail />} />
                <Route path="/chats" element={<ChatList />} />
                <Route path="/chats/:chatId" element={<ChatRoom />} />
                <Route path="/organizer/events" element={<OrganizerEventList />} />
                <Route path="/organizer/events/new" element={<OrganizerEventForm />} />
                <Route path="/organizer/events/:eventId" element={<OrganizerEventDetail />} />
                <Route path="/organizer/events/:eventId/edit" element={<OrganizerEventForm />} />
                <Route path="/organizer/stats" element={<OrganizerStatsOverview />} />
                <Route path="/organizer/stats/events/:eventId" element={<OrganizerEventStats />} />
                <Route path="*" element={<NotFound />} />
              </Routes>
            </Container>
          </main>

          <Footer />
        </div>
      </BrowserRouter>
    </MyUserContext.Provider>
  );
}

export default App;
