import cookies from 'react-cookies'

const MyUserReducer = (currentState, action) => {
    switch (action.type) {
        case "LOGIN":
            return action.payload;
        case "LOGOUT":
            cookies.remove('token');
            cookies.remove('user');
            return null;
        default:
            return currentState;
    }
}

export default MyUserReducer;
