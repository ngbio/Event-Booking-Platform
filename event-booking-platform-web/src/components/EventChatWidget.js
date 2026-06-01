import { useContext, useEffect, useMemo, useRef, useState } from "react";
import { onValue, push, ref, serverTimestamp, set, update } from "firebase/database";
import { useNavigate } from "react-router-dom";
import { isFirebaseConfigured, realtimeDb } from "../configs/Firebase";
import { MyUserContext } from "../configs/Contexts";

const EventChatWidget = ({event}) => {
    const [user,] = useContext(MyUserContext);
    const [minimized, setMinimized] = useState(false);
    const [messages, setMessages] = useState([]);
    const [chatMeta, setChatMeta] = useState(null);
    const [text, setText] = useState("");
    const [loading, setLoading] = useState(false);
    const [sending, setSending] = useState(false);
    const [err, setErr] = useState("");
    const messagesEndRef = useRef(null);
    const nav = useNavigate();

    const currentRole = user?.roleId === 2 ? "ORGANIZER" : "ATTENDEE";
    const currentUserKey = `${currentRole.toLowerCase()}_${user?.id}`;
    const attendeeId = user?.roleId === 3 ? user.id : "attendee";
    const chatId = event?.id && event?.organizerId
        ? `event_${event.id}_attendee_${attendeeId}_organizer_${event.organizerId}`
        : null;

    const initialMeta = useMemo(() => ({
        eventId: event?.id || null,
        eventTitle: event?.title || "Su kien",
        attendeeId: user?.roleId === 3 ? user.id : null,
        attendeeName: user?.fullName || user?.email || null,
        organizerId: event?.organizerId || null,
        organizerName: event?.organizerName || "Nha to chuc"
    }), [event, user]);

    useEffect(() => {
        if (!chatId || !user || !isFirebaseConfigured || !realtimeDb)
            return;

        setLoading(true);
        const chatRef = ref(realtimeDb, `chats/${chatId}`);
        const unsubscribeChat = onValue(chatRef, snapshot => {
            setChatMeta(snapshot.val() || null);
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
            setErr("Khong the tai tin nhan.");
            setLoading(false);
        });

        return () => {
            unsubscribeChat();
            unsubscribeMessages();
        };
    }, [chatId, user]);

    useEffect(() => {
        if (!minimized)
            messagesEndRef.current?.scrollIntoView({behavior: "smooth"});
    }, [messages, minimized]);

    const sendMessage = async (e) => {
        e.preventDefault();

        if (!user) {
            nav(`/login?next=/events/${event.id}`);
            return;
        }

        if (!text.trim() || !realtimeDb || !chatId)
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
                eventId: initialMeta.eventId,
                eventTitle: initialMeta.eventTitle,
                attendeeId: initialMeta.attendeeId || chatMeta?.attendeeId || (user.roleId === 3 ? user.id : null),
                attendeeName: initialMeta.attendeeName || chatMeta?.attendeeName || (user.fullName || user.email),
                organizerId: initialMeta.organizerId,
                organizerName: initialMeta.organizerName,
                [`participants/${currentUserKey}`]: true,
                lastMessage: messageText,
                updatedAt: serverTimestamp()
            });

            setText("");
        } catch (ex) {
            console.error(ex);
            setErr("Khong the gui tin nhan.");
        } finally {
            setSending(false);
        }
    }

    if (!event?.organizerId)
        return null;

    if (minimized) {
        return (
            <button className="event-chat-bubble" type="button" onClick={() => setMinimized(false)} aria-label="Mo chat">
                <span>{(event.organizerName || "O").charAt(0)}</span>
            </button>
        );
    }

    return (
        <aside className="event-chat-widget" aria-label="Chat voi nha to chuc">
            <div className="event-chat-header">
                <div className="event-chat-avatar">{(event.organizerName || "O").charAt(0)}</div>
                <div className="event-chat-title">
                    <strong>{event.organizerName || "Nha to chuc"}</strong>
                    <span>{event.title || "Su kien"}</span>
                </div>
                <button type="button" onClick={() => setMinimized(true)} aria-label="Thu nho">-</button>
            </div>

            <div className="event-chat-body">
                {!user && <div className="event-chat-notice">
                    <strong>Can dang nhap de chat</strong>
                    <p>Dang nhap de trao doi truc tiep voi nha to chuc.</p>
                    <button type="button" onClick={() => nav(`/login?next=/events/${event.id}`)}>Dang nhap</button>
                </div>}

                {user && !isFirebaseConfigured && <div className="event-chat-notice">
                    <strong>Chua cau hinh Firebase</strong>
                    <p>Dien bien REACT_APP_FIREBASE_* de bat chat realtime.</p>
                </div>}

                {user && isFirebaseConfigured && <>
                    {err && <div className="event-chat-error">{err}</div>}
                    {loading && <div className="event-chat-empty">Dang tai tin nhan...</div>}
                    {!loading && messages.length === 0 && <div className="event-chat-empty">Chua co tin nhan nao.</div>}
                    {messages.map(message => {
                        const mine = message.senderId === user.id && message.senderRole === currentRole;
                        return (
                            <div className={`event-chat-message ${mine ? "mine" : ""}`} key={message.id}>
                                {!mine && <span>{message.senderName || message.senderRole}</span>}
                                <p>{message.text}</p>
                            </div>
                        );
                    })}
                    <div ref={messagesEndRef}></div>
                </>}
            </div>

            <form className="event-chat-compose" onSubmit={sendMessage}>
                <input
                    value={text}
                    onChange={e => setText(e.target.value)}
                    placeholder="Aa"
                    disabled={!user || !isFirebaseConfigured || sending}
                    aria-label="Nhap tin nhan" />
                <button type="submit" disabled={!text.trim() || !user || !isFirebaseConfigured || sending}>
                    Gui
                </button>
            </form>
        </aside>
    );
}

export default EventChatWidget;
