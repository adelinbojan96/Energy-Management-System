import { useState, useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import "./Chat.css";

const Chat = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const [connected, setConnected] = useState(false);
    
    const [isTyping, setIsTyping] = useState(false);

    // ADMIN STATE
    const [replyToUser, setReplyToUser] = useState(null); 
    const [replyToName, setReplyToName] = useState(null); 
    
    // USER IDENTITY STATE
    const [currentUser, setCurrentUser] = useState({ 
        id: "Anonymous", 
        name: "Anonymous", 
        role: "CLIENT" 
    });

    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            try {
                const base64Url = token.split('.')[1];
                const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join(''));
                
                const decoded = JSON.parse(jsonPayload);
                const username = decoded.sub || decoded.username || "Unknown";
                
                let role = "CLIENT";
                if ((decoded.roles && decoded.roles.includes("ADMIN")) || 
                    (decoded.role && decoded.role === "ADMIN") || 
                    username.toLowerCase() === "admin") {
                    role = "ADMIN";
                }

                const storedId = localStorage.getItem("userId") || decoded.id || username; 
                
                setCurrentUser({ 
                    id: storedId, 
                    name: username, 
                    role: role 
                });

            } catch (e) {
                console.error("Token decode error", e);
            }
        }
    }, []);

    useEffect(() => {
        if (isOpen) {
            connect();
        } 
        return () => {
            if (stompClientRef.current && stompClientRef.current.connected) {
                stompClientRef.current.disconnect();
            }
        };
        // eslint-disable-next-line
    }, [isOpen]); 

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, isTyping]); 

    const connect = () => {
        if (stompClientRef.current && stompClientRef.current.connected) return;

        const socket = new SockJS("http://localhost:8089/ws");
        const client = Stomp.over(socket);
        client.debug = null; 

        client.connect({}, (frame) => {
            setConnected(true);
            console.log(`Connected as ${currentUser.role}`);

            const onMessageReceived = (msg) => {
                const body = JSON.parse(msg.body);
                setIsTyping(false);
                addMessage(body);
            };

            if (currentUser.role === "ADMIN") {
                client.subscribe('/topic/admin', (msg) => {
                    const body = JSON.parse(msg.body);
                    setReplyToUser(body.senderId);
                    setReplyToName(body.senderName || body.senderId); 
                    onMessageReceived(msg);
                });
            } else {
                client.subscribe(`/topic/${currentUser.id}`, onMessageReceived);
            }

        }, (error) => {
            console.error("Chat Connection Error:", error);
            setConnected(false);
            setIsTyping(false); 
        });

        stompClientRef.current = client;
    };

    const addMessage = (msg) => {
        setMessages((prev) => {
            return [...prev, msg];
        });
    };

    const sendMessage = () => {
        if (!input.trim() || !stompClientRef.current || !connected) return;

        const effectiveSenderId = currentUser.role === "ADMIN" ? "admin" : currentUser.id;

        let payload = {
            senderId: effectiveSenderId, 
            senderName: currentUser.name,
            content: input,
            type: "CHAT"
        };

        if (currentUser.role === "ADMIN") {
            if (!replyToUser) {
                alert("Wait for a user to message you first!");
                return;
            }
            payload.recipientId = replyToUser; 
        } else {
            payload.recipientId = "admin"; 
        }

        // START LOADING ANIMATION
        setIsTyping(true);

        stompClientRef.current.send("/app/chat", {}, JSON.stringify(payload));
        
        // Optimistically add our own message
        addMessage({ ...payload, isSelf: true });
        setInput("");
    };

    const handleKeyPress = (e) => {
        if (e.key === "Enter") sendMessage();
    };

    return (
        <div className="chat-wrapper">
            <button 
                className={`chat-toggle-btn ${isOpen ? "open" : ""}`} 
                onClick={() => setIsOpen(!isOpen)}
            >
                {isOpen ? "✖" : "Support"}
            </button>

            {isOpen && (
                <div className="chat-window">
                    <div className="chat-header">
                        <div className="header-info">
                            <h3>{currentUser.role === "ADMIN" ? "Admin Panel" : "Live Support"}</h3>
                            {currentUser.role === "ADMIN" && (
                                <span className="replying-to">
                                    {replyToName ? `Replying to: ${replyToName}` : "(Waiting...)"}
                                </span>
                            )}
                        </div>
                        <span className={`status-dot ${connected ? "online" : "offline"}`}></span>
                    </div>
                    
                    <div className="chat-messages">
                        {messages.length === 0 && (
                            <div className="chat-placeholder">
                                {currentUser.role === "ADMIN" ? "No active chats." : "How can we help?"}
                            </div>
                        )}
                        
                        {messages.map((msg, idx) => (
                            <div 
                                key={idx} 
                                className={`message-bubble ${msg.isSelf || (msg.senderId === "admin" && currentUser.role === "ADMIN") || (msg.senderId === currentUser.id && currentUser.role !== "ADMIN") ? "self" : "other"}`}
                            >
                                {!msg.isSelf && (
                                    <div className="message-sender">
                                        {msg.senderName || (msg.senderId === "admin" ? "Support" : "User")}
                                    </div>
                                )}
                                <div className="message-content">{msg.content}</div>
                            </div>
                        ))}

                        {/* TYPING INDICATOR */}
                        {isTyping && (
                            <div className="message-bubble other typing-indicator">
                                <div className="dot"></div>
                                <div className="dot"></div>
                                <div className="dot"></div>
                            </div>
                        )}

                        <div ref={messagesEndRef} />
                    </div>

                    <div className="chat-input-area">
                        <input
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyPress={handleKeyPress}
                            placeholder={currentUser.role === "ADMIN" ? `Reply to ${replyToName || '...'}` : "Type a message..."}
                            disabled={!connected}
                        />
                        <button onClick={sendMessage} disabled={!connected}>
                            ➤
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Chat;