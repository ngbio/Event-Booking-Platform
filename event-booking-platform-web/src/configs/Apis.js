import axios from "axios";
import cookies from 'react-cookies'
//http://localhost:8081/SpringEventBookingPlatform/api/
//https://event-booking-backend-rubp.onrender.com/api/
const BASE_URL = process.env.REACT_APP_API_BASE_URL || "https://event-booking-backend-rubp.onrender.com/api/";

export const endpoints = {
    
    'login': '/auth/login',
    'register-attendee': '/auth/register/attendee',
    'register-organizer': '/auth/register/organizer',

    
    'profile': '/users/secure/profile',
    'change-password': '/users/secure/password',

    
    'categories': '/categories',

    
    'events': '/events',
    'event-details': (eventId) => `/events/${eventId}`,
    'available-tickets': (eventId) => `/events/${eventId}/available`,
    'compare-events': '/events/compare',

    
    'bookings': '/secure/bookings',
    'booking-details': (bookingId) => `/secure/bookings/${bookingId}`,
    'cancel-booking': (bookingId) => `/secure/bookings/${bookingId}/cancel`,
    'momo-payment': '/secure/payments/momo',
    'momo-redirect': '/payment/momo/redirect',

    
    'tickets': '/secure/tickets',
    'ticket-details': (ticketId) => `/secure/tickets/${ticketId}`,

    
    'organizer-events': '/secure/organizer/events',
    'organizer-event-details': (eventId) => `/secure/organizer/events/${eventId}`,
    'create-organizer-event': '/secure/organizer/events',
    'update-organizer-event': (eventId) => `/secure/organizer/events/${eventId}`,
    'change-organizer-event-status': (eventId) => `/secure/organizer/events/${eventId}/status`,
    'organizer-event-bookings': (eventId) => `/secure/organizer/events/${eventId}/bookings`,

    
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
