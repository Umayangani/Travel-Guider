import React, { useState } from 'react';
import ItineraryModal from './ItineraryModal';
import './ItineraryPage.css';

const ItineraryPage = () => {
  const [showModal, setShowModal] = useState(false);
  const [itineraryData, setItineraryData] = useState(null);

  // Sample data - replace with real data from your form
  const sampleItinerary = {
    daily_itinerary: [
      {
        day: 1,
        places: [
          {
            Name: "Temple of the Tooth",
            District: "Kandy",
            Description: "Sacred Buddhist temple",
            Latitude: 7.2936,
            Longitude: 80.6337,
            Ticket_price: "500",
            start_time: "09:00",
            end_time: "11:00"
          }
        ]
      }
    ],
    total_days: 3,
    total_places: 5,
    travel_style: "Cultural",
    interests: ["Religious", "Historical"]
  };

  const handleViewItinerary = () => {
    setItineraryData(sampleItinerary);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  return (
    <div className="itinerary-page">
      <div className="itinerary-page-header">
        <h1>My Travel Itineraries</h1>
        <p>View and manage your personalized travel plans</p>
      </div>

      <div className="itinerary-cards-container">
        <div className="itinerary-card">
          <div className="itinerary-card-header">
            <h3>Sri Lanka Cultural Tour</h3>
            <span className="itinerary-date">Created: March 2024</span>
          </div>
          <div className="itinerary-card-body">
            <div className="itinerary-summary">
              <div className="summary-item">
                <span className="label">Duration:</span>
                <span className="value">3 days</span>
              </div>
              <div className="summary-item">
                <span className="label">Places:</span>
                <span className="value">5 locations</span>
              </div>
              <div className="summary-item">
                <span className="label">Style:</span>
                <span className="value">Cultural</span>
              </div>
            </div>
            <div className="itinerary-actions">
              <button 
                className="view-btn"
                onClick={handleViewItinerary}
              >
                View Details
              </button>
              <button className="edit-btn">Edit</button>
              <button className="download-btn">Download</button>
            </div>
          </div>
        </div>

        {/* Add more itinerary cards here */}
        <div className="itinerary-card new-itinerary">
          <div className="new-itinerary-content">
            <div className="new-itinerary-icon">+</div>
            <h3>Create New Itinerary</h3>
            <p>Plan your next adventure</p>
            <button className="create-btn">Get Started</button>
          </div>
        </div>
      </div>

      <ItineraryModal
        isOpen={showModal}
        itineraryData={itineraryData}
        onClose={handleCloseModal}
      />
    </div>
  );
};

export default ItineraryPage;
