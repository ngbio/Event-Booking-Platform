import axios from "axios";
import cookies from 'react-cookies'

const BASE_URL = process.env.REACT_APP_API_BASE_URL || "https://event-booking-backend-rubp.onrender.com/api/";

export const endpoints = {
    // Auth
    'login': '/auth/login',
    'logout': '/auth/logout',
    'register-attendee': '/auth/register/attendee',
    'register-organizer': '/auth/register/organizer',

    // User
    'profile': '/users/secure/profile',
    'change-password': '/users/secure/password',

    // Categories
    'categories': '/categories',

    // Public events
    'events': '/events',
    'event-details': (eventId) => `/events/${eventId}`,
    'available-tickets': (eventId) => `/events/${eventId}/available`,
    'compare-events': '/events/compare',

    // Bookings
    'bookings': '/secure/bookings',
    'booking-details': (bookingId) => `/secure/bookings/${bookingId}`,
    'cancel-booking': (bookingId) => `/secure/bookings/${bookingId}/cancel`,

    // Tickets
    'tickets': '/secure/tickets',
    'ticket-details': (ticketId) => `/secure/tickets/${ticketId}`,

    // Organizer events
    'organizer-events': '/secure/organizer/events',
    'organizer-event-details': (eventId) => `/secure/organizer/events/${eventId}`,
    'create-organizer-event': '/secure/organizer/events',
    'update-organizer-event': (eventId) => `/secure/organizer/events/${eventId}`,
    'change-organizer-event-status': (eventId) => `/secure/organizer/events/${eventId}/status`,

    // Organizer stats
    'organizer-stats-overview': '/secure/organizer/stats/overview',
    'organizer-event-stats': (eventId) => `/secure/organizer/stats/events/${eventId}`,
}

export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            'Authorization': `Bearer ${cookies.load('token')}`
        }
    })
}

export default axios.create({
    baseURL: BASE_URL
})
