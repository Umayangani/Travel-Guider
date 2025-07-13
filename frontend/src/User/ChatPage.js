import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ChatPage.css";

const DUMMY_USERS = [
  { id: 'trevora', name: 'Trevora (AI Assistant)', avatar: '/icons/admin.png', last: 'How can I help you?' },
  { id: 'user2', name: 'Jane Doe', avatar: '/icons/users.png', last: 'Hey, are you joining the trip?' },
  { id: 'user3', name: 'John Smith', avatar: '/icons/users.png', last: 'Let\'s plan our itinerary!' }
];

const DUMMY_MESSAGES = {
  trevora: [
    { from: "trevora", text: "Hi! How can I help you today?" },
    { from: "me", text: "What are the best places to visit?" },
    { from: "trevora", text: "Here are some top recommendations for your trip!" }
  ],
  user2: [
    { from: "user2", text: "Hey, are you joining the trip?" },
    { from: "me", text: "Yes, I will!" }
  ],
  user3: [
    { from: "user3", text: "Let's plan our itinerary!" },
    { from: "me", text: "Sure, let's do it." }
  ]
};

const ChatPage = () => {
  const [selectedUser, setSelectedUser] = useState('trevora');
  const [messages, setMessages] = useState(DUMMY_MESSAGES);
  const [input, setInput] = useState("");
  const [search, setSearch] = useState("");
  const navigate = useNavigate();

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

  const handleClear = () => {
    setMessages(prev => ({ ...prev, [selectedUser]: [] }));
  };

  const filteredUsers = DUMMY_USERS.filter(u =>
    u.name.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="chat-page-flex">
      <div className="chat-sidebar">
        <div className="chat-sidebar-title">Chats</div>
        <input
          className="chat-search"
          placeholder="Search..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        {filteredUsers.map(u => (
          <div
            key={u.id}
            className={`chat-user-item${selectedUser === u.id ? ' active' : ''}`}
            onClick={() => setSelectedUser(u.id)}
          >
            <img src={u.avatar} alt={u.name} className="chat-user-avatar" />
            <div className="chat-user-info">
              <span className="chat-user-name">{u.name}</span>
              <span className="chat-user-last">{u.last}</span>
            </div>
          </div>
        ))}
      </div>
      <div className="chat-main">
        <div className="chat-header-row">
          <button className="chat-back-btn" onClick={() => navigate('/user')}>&larr; Back</button>
          <div className="chat-header">{DUMMY_USERS.find(u => u.id === selectedUser)?.name}</div>
          <button className="chat-clear-btn" onClick={handleClear}>Clear Chat</button>
        </div>
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
