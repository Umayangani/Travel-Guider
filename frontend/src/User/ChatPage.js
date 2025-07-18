import React, { useState, useRef, useEffect } from 'react';
import './ChatPage.css';

const ChatPage = ({ onBack }) => {
  const [selectedUser, setSelectedUser] = useState(null);
  const [message, setMessage] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [messages, setMessages] = useState([]);
  const textareaRef = useRef();
  const messagesEndRef = useRef(null);

  // Sample user data for demonstration including Trevora Assistant
  const users = [
    {
      id: 0,
      name: 'Trevora Assistant',
      avatar: '/logo.png',
      lastMessage: 'Hello! How can I help you plan your trip today?',
      time: 'now',
      place: 'AI Travel Assistant',
      isAssistant: true
    },
    {
      id: 1,
      name: 'John Smith',
      avatar: '/icons/user.svg',
      lastMessage: 'Thanks for the travel tips!',
      time: '2m ago',
      place: 'Kandy, Sri Lanka'
    },
    {
      id: 2,
      name: 'Sarah Johnson',
      avatar: '/icons/user.svg',
      lastMessage: 'When is the best time to visit Sigiriya?',
      time: '5m ago',
      place: 'Sigiriya, Sri Lanka'
    },
    {
      id: 3,
      name: 'Mike Wilson',
      avatar: '/icons/user.svg',
      lastMessage: 'Great recommendations for Galle!',
      time: '12m ago',
      place: 'Galle, Sri Lanka'
    },
    {
      id: 4,
      name: 'Emma Davis',
      avatar: '/icons/user.svg',
      lastMessage: 'Can you help with transportation?',
      time: '1h ago',
      place: 'Colombo, Sri Lanka'
    }
  ];

  // Sample messages for regular users
  const sampleMessages = [
    {
      id: 1,
      text: 'Hi! I need some help planning my trip to Kandy.',
      sender: 'user',
      time: '10:30 AM',
      avatar: '/icons/user.svg'
    },
    {
      id: 2,
      text: 'Hello! I\'d be happy to help you plan your Kandy trip. What specific areas are you interested in?',
      sender: 'guide',
      time: '10:32 AM',
      avatar: '/icons/user.svg'
    },
    {
      id: 3,
      text: 'I\'m particularly interested in cultural sites and scenic viewpoints.',
      sender: 'user',
      time: '10:35 AM',
      avatar: '/icons/user.svg'
    },
    {
      id: 4,
      text: 'Perfect! I recommend visiting the Temple of the Tooth Relic and the Royal Botanical Gardens. For viewpoints, try the Kandy Lake and Bahirawakanda Vihara Buddha Statue.',
      sender: 'guide',
      time: '10:37 AM',
      avatar: '/icons/user.svg'
    }
  ];

  // Sample messages for Trevora Assistant
  const assistantMessages = [
    {
      id: 1,
      text: 'Hello! I\'m Trevora, your AI travel assistant. How can I help you plan your perfect trip to Sri Lanka today?',
      sender: 'assistant',
      time: '10:00 AM',
      avatar: '/logo.png'
    },
    {
      id: 2,
      text: '🌟 I can help you with:\n• Destination recommendations\n• Itinerary planning\n• Local attractions\n• Transportation options\n• Weather information\n• Cultural insights',
      sender: 'assistant',
      time: '10:00 AM',
      avatar: '/logo.png'
    }
  ];

  // Auto-expand textarea
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = textareaRef.current.scrollHeight + 'px';
    }
  }, [message]);

  // Auto-scroll to bottom when messages change
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  // Set initial messages when user is selected
  useEffect(() => {
    if (selectedUser) {
      if (selectedUser.isAssistant) {
        setMessages(assistantMessages);
      } else {
        setMessages(sampleMessages);
      }
    }
  }, [selectedUser]);

  const handleUserSelect = (user) => {
    setSelectedUser(user);
  };

  const handleSendMessage = () => {
    if (message.trim() && selectedUser) {
      const newMessage = {
        id: messages.length + 1,
        text: message,
        sender: 'user',
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        avatar: '/icons/user.svg'
      };
      setMessages([...messages, newMessage]);
      setMessage('');

      // Auto-reply for Trevora Assistant
      if (selectedUser.isAssistant) {
        setTimeout(() => {
          const assistantReply = {
            id: messages.length + 2,
            text: getAssistantResponse(message),
            sender: 'assistant',
            time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            avatar: '/logo.png'
          };
          setMessages(prev => [...prev, assistantReply]);
        }, 1500);
      }
    }
  };

  const getAssistantResponse = (userMessage) => {
    const responses = [
      "That's a great question! Let me help you with detailed information about that destination.",
      "🌍 Based on your interest, I recommend exploring the cultural triangle of Sri Lanka. Would you like specific itinerary suggestions?",
      "📍 That's an excellent choice! I can provide you with the best travel routes, local tips, and must-visit spots in that area.",
      "✨ I'd be happy to create a personalized travel plan for you. What's your preferred travel style - adventure, culture, or relaxation?",
      "🏖️ Sri Lanka offers amazing experiences! Let me suggest some hidden gems and local favorites that most tourists miss.",
      "🚗 For transportation, I can help you choose between trains, buses, or private vehicles based on your itinerary and budget."
    ];
    return responses[Math.floor(Math.random() * responses.length)];
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleBackClick = () => {
    if (onBack) {
      onBack(); // Navigate back to previous page
    } else {
      // Default navigation back to user dashboard/home
      window.history.back();
    }
  };

  const handleBackToList = () => {
    setSelectedUser(null);
    setMessages([]);
  };

  const handleClearChat = () => {
    setMessages([]);
  };

  const filteredUsers = users.filter(user =>
    user.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="chat-container">
      {/* Sidebar */}
      <div className="chat-sidebar">
        <div className="sidebar-header">
          <div className="sidebar-header-top">
            <button className="main-back-btn" onClick={handleBackClick} title="Back to Dashboard">
              ← Back
            </button>
            <h2>Messages</h2>
          </div>
          <div className="search-container">
            <input
              type="text"
              placeholder="Search conversations..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
            <span className="search-icon">🔍</span>
          </div>
        </div>
        
        <div className="user-list">
          {filteredUsers.map(user => (
            <div
              key={user.id}
              className={`user-item ${selectedUser?.id === user.id ? 'selected' : ''} ${user.isAssistant ? 'assistant-item' : ''}`}
              onClick={() => handleUserSelect(user)}
            >
              <img src={user.avatar} alt={user.name} className="user-avatar" />
              <div className="user-info">
                <h3>{user.name}</h3>
                <p className="last-message">{user.lastMessage}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="chat-main">
        {selectedUser ? (
          <>
            {/* Chat Header */}
            <div className="chat-header">
              <div className="chat-header-left">
                <button className="chat-back-btn" onClick={handleBackToList}>
                  ←
                </button>
                <div className="chat-header-info">
                  <img src={selectedUser.avatar} alt={selectedUser.name} className={`header-avatar ${selectedUser.isAssistant ? 'assistant-avatar' : ''}`} />
                  <div className="header-user-details">
                    <h3>{selectedUser.name} {selectedUser.isAssistant ? '🤖' : ''}</h3>
                    <span className="travel-place">{selectedUser.place}</span>
                  </div>
                </div>
              </div>
              <button className="chat-clear-btn" onClick={handleClearChat}>
                🗑️
              </button>
            </div>

            {/* Messages Area */}
            <div className="chat-messages">
              {messages.map(msg => (
                <div key={msg.id} className={`message ${msg.sender === 'user' ? 'outgoing' : 'incoming'} ${msg.sender === 'assistant' ? 'assistant-message' : ''}`}>
                  <img src={msg.avatar} alt="Avatar" className={`message-avatar ${msg.sender === 'assistant' ? 'assistant-avatar' : ''}`} />
                  <div className="message-content">
                    <p>{msg.text}</p>
                    <span className="message-time">{msg.time}</span>
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>

            {/* Input Area */}
            <div className="chat-input-container">
              <div className="input-wrapper">
                <textarea
                  ref={textareaRef}
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  onKeyPress={handleKeyPress}
                  placeholder="Type your message..."
                  className="chat-input"
                  rows="1"
                />
                <button className="chat-send-btn" onClick={handleSendMessage}>
                  ➤
                </button>
              </div>
            </div>
          </>
        ) : (
          <div className="chat-empty">
            <div className="empty-state">
              <h3>Select a conversation to start chatting</h3>
              <p>Choose from your existing conversations or start a new one</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ChatPage;
