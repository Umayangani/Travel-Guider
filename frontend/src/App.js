// 📁 src/App.js
import React, { useState } from 'react';
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

        {/* User route placeholder */}
        <Route path="/user" element={<div></div>} />
      </Routes>
    </Router>
  );
}

export default App;
