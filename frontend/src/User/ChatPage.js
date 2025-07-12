import React, { useState } from "react";
import "./ChatPage.css";

// Dummy users for sidebar
const DUMMY_USERS = [
  { id: 'trevora', name: 'Trevora (AI Assistant)' },
  { id: 'user2', name: 'Jane Doe' },
  { id: 'user3', name: 'John Smith' }
];

const DUMMY_MESSAGES = {
  trevora: [
    { from: "trevora", text: "Hi! How can I help you today?" }
  ],
  user2: [
    { from: "user2", text: "Hey, are you joining the trip?" }
  ],
  user3: [
    { from: "user3", text: "Let's plan our itinerary!" }
  ]
};

const ChatPage = () => {
  const [selectedUser, setSelectedUser] = useState('trevora');
  const [messages, setMessages] = useState(DUMMY_MESSAGES);
  const [input, setInput] = useState("");

  const handleSend = (e) => {
    e.preventDefault();
    if (!input.trim()) return;
    setMessages(prev => ({
      ...prev,
      [selectedUser]: [...(prev[selectedUser] || []), { from: "me", text: input }]
    }));
    setInput("");
    // Add backend integration here
  };

  return (
    <div className="chat-page-flex">
      <div className="chat-sidebar">
        <div className="chat-sidebar-title">Chats</div>
        {DUMMY_USERS.map(u => (
          <div
            key={u.id}
            className={`chat-user-item${selectedUser === u.id ? ' active' : ''}`}
            onClick={() => setSelectedUser(u.id)}
          >
            {u.name}
          </div>
        ))}
      </div>
      <div className="chat-main">
        <div className="chat-header">{DUMMY_USERS.find(u => u.id === selectedUser)?.name}</div>
        <div className="chat-messages">
          {(messages[selectedUser] || []).map((msg, idx) => (
            <div key={idx} className={`chat-msg ${msg.from === 'me' ? 'user' : 'system'}`}>{msg.text}</div>
          ))}
        </div>
        <form className="chat-input-row" onSubmit={handleSend}>
          <input
            type="text"
            value={input}
            onChange={e => setInput(e.target.value)}
            placeholder="Type your message..."
            className="chat-input"
          />
          <button type="submit" className="chat-send-btn">Send</button>
        </form>
      </div>
    </div>
  );
};

export default ChatPage;
