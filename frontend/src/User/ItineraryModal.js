import React from 'react';
import './ItineraryModal.css';
import ItineraryDisplay from './ItineraryDisplay';

const ItineraryModal = ({ isOpen, itineraryData, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="itinerary-modal-overlay" onClick={onClose}>
      <div className="itinerary-modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="itinerary-modal-header">
          <h2>Your Travel Itinerary</h2>
          <button className="close-modal-btn" onClick={onClose}>
            ✕
          </button>
        </div>
        <div className="itinerary-modal-body">
          <ItineraryDisplay 
            itineraryData={itineraryData} 
            onBack={onClose}
            isModal={true}
          />
        </div>
      </div>
    </div>
  );
};

export default ItineraryModal;
