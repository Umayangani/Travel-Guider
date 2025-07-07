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
import AddTrain from "./Admin/AddTrain";
import AddPlace from "./Admin/AddPlace";
import Sidebar from './Admin/Sidebar';
import EditPlace from "./Admin/EditPlace";
import Settings from "./Admin/Settings";
import AdminLayout from "./Admin/AdminLayout";
import CsvImport from "./Admin/CsvImport";
import HeaderUser from "./Header/HeaderUser";
import { fetchCurrentUser } from "./api/user";
import ItineraryForm from "./User/ItineraryForm";

function App() {
  const [activeModal, setActiveModal] = useState(null);

  const handleNavigate = (modal) => setActiveModal(modal);
  const handleCloseModal = () => setActiveModal(null);

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
          <Route path="add-bus" element={<AddBus />} />
          <Route path="settings" element={<Settings />} />
          <Route path="csv-import" element={<CsvImport />} />
          {/* Add more admin routes as needed */}
        </Route>

        {/* User route: HeaderUser and Footer for user page */}
        <Route path="/user" element={<UserPage />} />
      </Routes>
    </Router>
  );
}

// UserPage component to fetch and provide user name
function UserPage() {
  const [userName, setUserName] = useState("");

  useEffect(() => {
    fetchCurrentUser()
      .then(data => {
        console.log("Fetched user data:", data);
        setUserName(data.name || "User");
      })
      .catch(() => setUserName("User"));
  }, []);

  return (
    <>
      <HeaderUser userName={userName} />
      <ItineraryForm />
      <Footer />
    </>
  );
}

export default App;
