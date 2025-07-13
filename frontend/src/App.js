import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import HeaderHome from "./Header/HeaderHome";
import HeroSection from "./Animation/HeroSection";
import CardList from "./Body/CardList";
import Footer from "./Footer/Footer";
import LoginPage from "./Signup/LoginPage";
import RegistrationPage from "./Signup/RegistrationPage";
import Dashboard from "./Admin/Dashboard";
import AddAdmin from "./Admin/AddAdmin";
import AddBus from "./Admin/AddBus";
import EditBusList from "./Admin/EditBusList";
import EditBus from "./Admin/EditBus";
import AddTrain from "./Admin/AddTrain";
import AddPlace from "./Admin/AddPlace";
import Sidebar from './Admin/Sidebar';
import EditPlace from "./Admin/EditPlace";
import EditTrain from "./Admin/EditTrain";
import EditTrainList from "./Admin/EditTrainList";
import Settings from "./Admin/Settings";
import AdminLayout from "./Admin/AdminLayout";
import CsvImport from "./Admin/CsvImport";
import HeaderUser from "./Header/HeaderUser";
import { fetchCurrentUser } from "./api/user";
import ItineraryForm from "./User/ItineraryForm";
import UserHero from './User/UserHero';
import UserBody from './User/UserBody';
import UpcomingEvents from './User/UpcomingEvents';
import FloatingIcon from './User/FloatingIcon';
import ChatPage from './User/ChatPage';
import UserProfile from "./User/UserProfile";

function App() {
  const [activeModal, setActiveModal] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const handleNavigate = (modal) => setActiveModal(modal);
  const handleCloseModal = () => {
    if (activeModal === 'login') setIsLoggedIn(true);
    setActiveModal(null);
  };

  return (
    <Router>
      <Routes>
        {/* 🔹 Home Page with Modals */}
        <Route
          path="/"
          element={
            <div>
              <HeaderHome onNavigate={handleNavigate} />
              <HeroSection />
              <CardList />
              <Footer />
              {activeModal === 'login' && (
                <LoginPage onNavigate={handleNavigate} onClose={handleCloseModal} />
              )}
              {activeModal === 'signup' && (
                <RegistrationPage onNavigate={handleNavigate} onClose={handleCloseModal} />
              )}
              {/* Floating chat icon only after login, over home page */}
              {isLoggedIn && (
                <FloatingIcon icon="/icons/chat.png" alt="Chat" onClick={() => window.location.href='/user/chat'} />
              )}
            </div>
          }
        />
        {/* Admin layout with sidebar and dashboard by default */}
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="add-admin" element={<AddAdmin />} />
          <Route path="add-place" element={<AddPlace />} />
          <Route path="edit-place" element={<EditPlace />} />
          <Route path="add-train" element={<AddTrain />} />
          <Route path="edit-train" element={<EditTrainList />} />
          <Route path="edit-train/:id" element={<EditTrain />} />
          <Route path="add-bus" element={<AddBus />} />
          <Route path="edit-bus" element={<EditBusList />} />
          <Route path="edit-bus/:id" element={<EditBus />} />
          <Route path="settings" element={<Settings />} />
          <Route path="csv-import" element={<CsvImport />} />
          {/* Add more admin routes as needed */}
        </Route>
        {/* User route: HeaderUser and Footer for user page */}
        <Route path="/user" element={<UserPage />} />
        <Route path="/user/chat" element={<ChatPage />} />
        <Route path="/profile" element={<UserProfile />} />
      </Routes>
    </Router>
  );
}

// UserPage component to fetch and provide user name
function UserPage() {
  const [userName, setUserName] = useState("");
  // Always show chat icon for demo/testing
  const isLoggedIn = true;

  // Optionally, keep the fetchCurrentUser for userName only
  useEffect(() => {
    fetchCurrentUser()
      .then(data => {
        setUserName(data.name || "User");
      })
      .catch(() => {
        setUserName("User");
      });
  }, []);

  return (
    <div style={{ position: 'relative', minHeight: '100vh' }}>
      <HeaderUser userName={userName} />
      <UserHero>
        <ItineraryForm />
      </UserHero>
      <UserBody />
      <UpcomingEvents />
      <Footer />
      {/* Floating chat icon always visible for demo */}
      <div style={{ position: 'fixed', right: 32, bottom: 32, zIndex: 9999 }}>
        <FloatingIcon icon="/icons/chat.png" alt="Chat" onClick={() => window.location.href='/user/chat'} />
      </div>
    </div>
  );
}

export default App;
