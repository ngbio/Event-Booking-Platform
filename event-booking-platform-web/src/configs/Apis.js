import axios from "axios";
import cookies from 'react-cookies'

const BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8081/SpringEventBookingPlatform/api/";

export const endpoints = {
    'categories': '/categories',
    'events': '/events',
    'event-details': (eventId) => `/events/${eventId}`,
    'available-tickets': (eventId) => `/events/${eventId}/available-tickets`,
    'login': '/users/login',
    'logout': '/users/logout',
    'register-attendee': '/users/register/attendee',
    'register-organizer': '/users/register/organizer',
    'profile': '/users/secure/profile',
    'organizer-events': '/events/organizer/my-events',
    'create-organizer-event': '/events/organizer',
    'update-organizer-event': (eventId) => `/events/organizer/${eventId}`,
    'delete-organizer-event': (eventId) => `/events/organizer/${eventId}`,
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
