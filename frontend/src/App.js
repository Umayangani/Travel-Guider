import { useState } from 'react';
import HeaderHome from "./Header/HeaderHome";
import HeroSection from "./Animation/HeroSection";
import Footer from "./Footer/Footer";
import CardList from "./Body/CardList"; 
import RegistrationPage from "./Signup/RegistrationPage";
import LoginPage from "./Signup/LoginPage";

function App() {
  const [currentModal, setCurrentModal] = useState(null);

  const openModal = (modalType) => setCurrentModal(modalType);
  const closeModal = () => setCurrentModal(null);

  return (
    <>
      <HeaderHome onNavigate={openModal} />
      <HeroSection />
      <CardList />
      <Footer />
      {currentModal === 'signup' && (
        <RegistrationPage onClose={closeModal} onNavigate={setCurrentModal} />
      )}
      {currentModal === 'login' && (
        <LoginPage onClose={closeModal} onNavigate={setCurrentModal} />
      )}
    </>
  );
}

export default App;
