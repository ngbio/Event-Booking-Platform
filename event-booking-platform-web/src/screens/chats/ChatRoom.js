import { useContext, useEffect, useMemo, useState } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { onValue, push, ref, serverTimestamp, set, update } from "firebase/database";
import MySpinner from "../../components/MySpinner";
import { isFirebaseConfigured, realtimeDb } from "../../configs/Firebase";
import { MyUserContext } from "../../configs/Contexts";

const ChatRoom = () => {
    const [messages, setMessages] = useState([]);
    const [chatMeta, setChatMeta] = useState(null);
    const [text, setText] = useState("");
    const [loading, setLoading] = useState(true);
    const [sending, setSending] = useState(false);
    const [err, setErr] = useState("");
    const [user,] = useContext(MyUserContext);
    const { chatId } = useParams();
    const location = useLocation();
    const nav = useNavigate();

    const currentRole = user?.roleId === 2 ? "ORGANIZER" : "ATTENDEE";
    const currentUserKey = `${currentRole.toLowerCase()}_${user?.id}`;

    const initialMeta = useMemo(() => location.state || {}, [location.state]);

    useEffect(() => {
        if (!isFirebaseConfigured || !realtimeDb || !user)
            return;

        setLoading(true);
        const chatRef = ref(realtimeDb, `chats/${chatId}`);
        const unsubscribeChat = onValue(chatRef, snapshot => {
            const value = snapshot.val();
            setChatMeta(value || initialMeta || null);
        });

        const messagesRef = ref(realtimeDb, `messages/${chatId}`);
        const unsubscribeMessages = onValue(messagesRef, snapshot => {
            const value = snapshot.val() || {};
            const nextMessages = Object.entries(value)
                .map(([id, message]) => ({id, ...message}))
                .sort((a, b) => (a.createdAt || 0) - (b.createdAt || 0));
            setMessages(nextMessages);
            setLoading(false);
        }, ex => {
            console.error(ex);
            setErr("Không thể tải tin nhắn.");
            setLoading(false);
        });

        return () => {
            unsubscribeChat();
            unsubscribeMessages();
        };
    }, [chatId, initialMeta, user]);

    const sendMessage = async (e) => {
        e.preventDefault();
        if (!text.trim() || !realtimeDb || !user)
            return;

        try {
            setSending(true);
            setErr("");

            const messageText = text.trim();
            const messageRef = push(ref(realtimeDb, `messages/${chatId}`));
            await set(messageRef, {
                senderId: user.id,
                senderName: user.fullName || user.email,
                senderRole: currentRole,
                text: messageText,
                createdAt: serverTimestamp()
            });

            await update(ref(realtimeDb, `chats/${chatId}`), {
                eventId: initialMeta.eventId || chatMeta?.eventId || null,
                eventTitle: initialMeta.eventTitle || chatMeta?.eventTitle || "Sự kiện",
                attendeeId: initialMeta.attendeeId || chatMeta?.attendeeId || (user.roleId === 3 ? user.id : null),
                attendeeName: initialMeta.attendeeName || chatMeta?.attendeeName || (user.roleId === 3 ? user.fullName || user.email : null),
                organizerId: initialMeta.organizerId || chatMeta?.organizerId || (user.roleId === 2 ? user.id : null),
                organizerName: initialMeta.organizerName || chatMeta?.organizerName || (user.roleId === 2 ? user.fullName || user.email : null),
                [`participants/${currentUserKey}`]: true,
                lastMessage: messageText,
                updatedAt: serverTimestamp()
            });

            setText("");
        } catch (ex) {
            console.error(ex);
            setErr("Không thể gửi tin nhắn.");
        } finally {
            setSending(false);
        }
    }

    if (user === null) {
        return (
            <Alert className="alert-dark-pink">
                Vui lòng đăng nhập để sử dụng chat.
                <div className="mt-3">
                    <Button className="btn-pink" onClick={() => nav(`/login?next=/chats/${chatId}`)}>Đăng nhập</Button>
                </div>
            </Alert>
        );
    }

    if (!isFirebaseConfigured) {
        return (
            <div className="chat-screen glass-panel">
                <div className="page-kicker mb-2">Chat</div>
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
                    <h1>{chatMeta?.eventTitle || initialMeta.eventTitle || "Trao đổi sự kiện"}</h1>
                    <p>{chatMeta?.organizerName || initialMeta.organizerName || "Nhà tổ chức"}</p>
                </div>
                <Button className="btn-soft-pink" onClick={() => nav(-1)}>Quay lại</Button>
            </div>

            {err && <Alert className="alert-dark-pink mt-3">{err}</Alert>}
            {loading && <MySpinner />}

            <div className="chat-messages">
                {!loading && messages.length === 0 && <div className="chat-empty">Chưa có tin nhắn nào.</div>}
                {messages.map(message => {
                    const mine = message.senderId === user.id && message.senderRole === currentRole;
                    return (
                        <div className={`chat-message ${mine ? "mine" : ""}`} key={message.id}>
                            <span>{message.senderName || message.senderRole}</span>
                            <p>{message.text}</p>
                        </div>
                    );
                })}
            </div>

            <Form className="chat-form" onSubmit={sendMessage}>
                <Form.Control value={text} onChange={e => setText(e.target.value)} placeholder="Nhập tin nhắn..." />
                <Button className="btn-pink" type="submit" disabled={sending || !text.trim()}>
                    {sending ? "Đang gửi..." : "Gửi"}
                </Button>
            </Form>
        </div>
    );
}

export default ChatRoom;
