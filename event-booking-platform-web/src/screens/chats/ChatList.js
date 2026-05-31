import { useContext, useEffect, useState } from "react";
import { Alert, Button } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { onValue, ref } from "firebase/database";
import MySpinner from "../../components/MySpinner";
import { isFirebaseConfigured, realtimeDb } from "../../configs/Firebase";
import { MyUserContext } from "../../configs/Contexts";

const ChatList = () => {
    const [chats, setChats] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");
    const [user,] = useContext(MyUserContext);
    const nav = useNavigate();

    useEffect(() => {
        if (!user || !isFirebaseConfigured || !realtimeDb) {
            setLoading(false);
            return;
        }

        setLoading(true);
        const chatsRef = ref(realtimeDb, "chats");
        const unsubscribe = onValue(chatsRef, snapshot => {
            const value = snapshot.val() || {};
            const currentUserKey = `${user.roleId === 2 ? "organizer" : "attendee"}_${user.id}`;

            const nextChats = Object.entries(value)
                .map(([id, chat]) => ({id, ...chat}))
                .filter(chat => {
                    if (user.roleId === 2)
                        return Number(chat.organizerId) === Number(user.id) || chat.participants?.[currentUserKey];

                    return Number(chat.attendeeId) === Number(user.id) || chat.participants?.[currentUserKey];
                })
                .sort((a, b) => (b.updatedAt || 0) - (a.updatedAt || 0));

            setChats(nextChats);
            setLoading(false);
        }, ex => {
            console.error(ex);
            setErr("Không thể tải danh sách tin nhắn.");
            setLoading(false);
        });

        return () => unsubscribe();
    }, [user]);

    const formatTime = (value) => {
        if (!value)
            return "";

        return new Date(value).toLocaleString("vi-VN");
    }

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập để xem tin nhắn.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav("/login?next=/chats")}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (!isFirebaseConfigured) {
        return (
            <div className="chat-screen glass-panel">
                <div className="page-kicker mb-2">Tin nhắn</div>
                <h1>Chưa cấu hình Firebase</h1>
                <p>Điền các biến `REACT_APP_FIREBASE_*` trong `.env` rồi chạy lại frontend để bật chat realtime.</p>
            </div>
        );
    }

    return (
        <div className="chat-screen glass-panel">
            <div className="chat-head">
                <div>
                    <div className="page-kicker mb-2">Chat realtime</div>
                    <h1>Tin nhắn</h1>
                    <p>{user.roleId === 2 ? "Các khách tham dự đã nhắn cho sự kiện của bạn." : "Các cuộc trò chuyện của bạn với nhà tổ chức."}</p>
                </div>
                <Button className="btn-soft-pink" onClick={() => nav(-1)}>Quay lại</Button>
            </div>

            {err && <Alert className="alert-dark-pink">{err}</Alert>}
            {loading && <MySpinner />}

            {!loading && chats.length === 0 && <div className="chat-empty chat-list-empty">
                Chưa có cuộc trò chuyện nào.
            </div>}

            {!loading && chats.length > 0 && <div className="chat-list">
                {chats.map(chat => (
                    <Link className="chat-list-item" to={`/chats/${chat.id}`} state={chat} key={chat.id}>
                        <div>
                            <strong>{chat.eventTitle || "Sự kiện"}</strong>
                            <span>{user.roleId === 2 ? chat.attendeeName || "Khách tham dự" : chat.organizerName || "Nhà tổ chức"}</span>
                            <p>{chat.lastMessage || "Chưa có nội dung"}</p>
                        </div>
                        <time>{formatTime(chat.updatedAt)}</time>
                    </Link>
                ))}
            </div>}
        </div>
    );
}

export default ChatList;
