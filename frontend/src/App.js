import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HeaderHome from "./Header/HeaderHome";
import HeroSection from "./Animation/HeroSection";
import CardList from "./Body/CardList";
import Footer from "./Footer/Footer";
import LoginPage from "./Signup/LoginPage";
import RegistrationPage from "./Signup/RegistrationPage";
/*import Sidebar from "./Admin/Sidebar";
import AddPlace from "./Admin/AddPlace";
import Dashboard from "./Admin/Dashboard"; // Dummy dashboard component
import EditPlace from "./Admin/EditPlace";
import AddTrain from "./Admin/AddTrain";
import AddBus from "./Admin/AddBus";
import AddAdmin from "./Admin/AddAdmin";
import Settings from "./Admin/Settings";*/


function App() {
  const [activeModal, setActiveModal] = useState(null); // 'login' or 'signup'

  const handleNavigate = (modal) => {
    setActiveModal(modal);
  };

  const handleCloseModal = () => {
    setActiveModal(null);
  };

  return (
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
  );
}

export default App;



 /*<Router>
      <div style={{ display: "flex" }}>
        <Sidebar adminName="Admin" />
        {/* Add marginLeft equal to sidebar width (240px) *//*}
        /*<div style={{ flex: 1, padding: "20px", marginLeft: "240px" }}>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/add-place" element={<AddPlace />} />
            <Route path="/edit-place" element={<EditPlace/>} />
            <Route path="/add-train" element={<AddTrain />} />
            <Route path="/add-bus" element={<AddBus />} />
            <Route path="/add-admin" element={<AddAdmin />} />
            <Route path="/settings" element={<Settings />} />
            {/* Add other routes here *//*}
          </Routes>
        </div>
      </div>
    </Router>*/