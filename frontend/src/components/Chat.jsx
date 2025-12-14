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
    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    const getUserId = () => localStorage.getItem("userId") || "Anonymous";
    
    useEffect(() => {
        if (isOpen) {
            connect();
        } 

        return () => {
            if (stompClientRef.current && stompClientRef.current.connected) {
                console.log("Cleaning up WebSocket connection...");
                stompClientRef.current.disconnect();
            }
        };
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isOpen]); 

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, isTyping]);

    const connect = () => {
        if (stompClientRef.current && stompClientRef.current.connected) return;

        console.log("Attempting to connect...");
        const socket = new SockJS("http://localhost:8088/ws");
        const client = Stomp.over(socket);
        
        client.debug = null;

        client.connect({}, (frame) => {
            setConnected(true);
            console.log("✅ Connected to Chat");

            const userId = getUserId();
            client.subscribe(`/topic/${userId}`, (msg) => {
                const receivedMessage = JSON.parse(msg.body);                
                setIsTyping(true);

                setTimeout(() => {
                    setIsTyping(false);
                    addMessage(receivedMessage);
                }, 1000);
            });

        }, (error) => {
            console.error("Chat Connection Error:", error);
            setConnected(false);
        });

        stompClientRef.current = client;
    };

    const addMessage = (msg) => {
        setMessages((prev) => [...prev, msg]);
    };

    const sendMessage = () => {
        if (input.trim() && stompClientRef.current && connected) {
            const userId = getUserId();
            const messagePayload = {
                senderId: userId,
                content: input,
                type: "CHAT"
            };

            console.log("📤 Sending:", messagePayload);
            stompClientRef.current.send("/app/chat", {}, JSON.stringify(messagePayload));
            
            addMessage({ ...messagePayload, isSelf: true });
            setInput("");
        } else {
            console.warn("Cannot send: Disconnected or empty input");
        }
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
                {isOpen ? "✖" : "💬 Support"}
            </button>

            {isOpen && (
                <div className="chat-window">
                    <div className="chat-header">
                        <h3>Live Support</h3>
                        <span className={`status-dot ${connected ? "online" : "offline"}`}></span>
                    </div>
                    
                    <div className="chat-messages">
                        {messages.length === 0 && (
                            <div className="chat-placeholder">How can we help?</div>
                        )}
                        
                        {messages.map((msg, idx) => (
                            <div 
                                key={idx} 
                                className={`message-bubble ${msg.isSelf || msg.senderId === getUserId() ? "self" : "other"}`}
                            >
                                <div className="message-content">{msg.content}</div>
                                <div className="message-sender">{msg.isSelf ? "You" : msg.senderId}</div>
                            </div>
                        ))}

                        {isTyping && (
                            <div className="message-bubble other typing-indicator">
                                <span className="dot"></span>
                                <span className="dot"></span>
                                <span className="dot"></span>
                            </div>
                        )}

                        <div ref={messagesEndRef} />
                    </div>

                    <div className="chat-input-area">
                        <input
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyPress={handleKeyPress}
                            placeholder="Type a message..."
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