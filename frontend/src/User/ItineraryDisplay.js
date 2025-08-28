import React, { useState, useEffect } from 'react';
import './ItineraryDisplay.css';

const ItineraryDisplay = ({ itineraryData, onBack }) => {
  const [selectedDay, setSelectedDay] = useState(1);
  const [viewMode, setViewMode] = useState('timeline'); // 'timeline' or 'map'

  if (!itineraryData || !itineraryData.daily_itinerary) {
    return (
      <div className="itinerary-display">
        <div className="itinerary-header">
          <button className="back-button" onClick={onBack}>
            ← Back to Form
          </button>
          <h2>Loading Itinerary...</h2>
        </div>
      </div>
    );
  }

  const { daily_itinerary, total_days, total_places, travel_style, interests } = itineraryData;

  const calculateTotalBudget = () => {
    let total = 0;
    daily_itinerary.forEach(day => {
      day.places.forEach(place => {
        const ticketPrice = parseFloat(place.Ticket_price || 0);
        total += isNaN(ticketPrice) ? 0 : ticketPrice;
      });
    });
    return total;
  };

  const calculateTotalDistance = () => {
    let totalDistance = 0;
    daily_itinerary.forEach(day => {
      if (day.places.length > 1) {
        for (let i = 0; i < day.places.length - 1; i++) {
          const place1 = day.places[i];
          const place2 = day.places[i + 1];
          // Simple distance calculation (you can enhance this)
          const dist = Math.sqrt(
            Math.pow(place1.Latitude - place2.Latitude, 2) +
            Math.pow(place1.Longitude - place2.Longitude, 2)
          ) * 111; // Rough km conversion
          totalDistance += dist;
        }
      }
    });
    return Math.round(totalDistance);
  };

  const formatTime = (timeStr) => {
    if (!timeStr) return '';
    const [hours, minutes] = timeStr.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour > 12 ? hour - 12 : hour === 0 ? 12 : hour;
    return `${displayHour}:${minutes} ${ampm}`;
  };

  const exportItinerary = () => {
    const exportData = {
      trip_summary: {
        total_days,
        total_places,
        travel_style,
        interests,
        estimated_budget: calculateTotalBudget(),
        estimated_distance: calculateTotalDistance()
      },
      daily_itinerary
    };
    
    const dataStr = JSON.stringify(exportData, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'travel-itinerary.json';
    link.click();
  };

  return (
    <div className="itinerary-display">
      {/* Header Section */}
      <div className="itinerary-header">
        <button className="back-button" onClick={onBack}>
          ← Back to Form
        </button>
        <div className="header-content">
          <h1>Your Personalized Travel Itinerary</h1>
          <div className="trip-summary">
            <div className="summary-item">
              <span className="label">Duration:</span>
              <span className="value">{total_days} days</span>
            </div>
            <div className="summary-item">
              <span className="label">Places:</span>
              <span className="value">{total_places} locations</span>
            </div>
            <div className="summary-item">
              <span className="label">Style:</span>
              <span className="value">{travel_style}</span>
            </div>
            <div className="summary-item">
              <span className="label">Budget:</span>
              <span className="value">LKR {calculateTotalBudget().toLocaleString()}</span>
            </div>
          </div>
        </div>
        <div className="header-actions">
          <button className="export-button" onClick={exportItinerary}>
            📱 Export Itinerary
          </button>
          <div className="view-toggle">
            <button 
              className={viewMode === 'timeline' ? 'active' : ''}
              onClick={() => setViewMode('timeline')}
            >
              📅 Timeline
            </button>
            <button 
              className={viewMode === 'map' ? 'active' : ''}
              onClick={() => setViewMode('map')}
            >
              🗺️ Map View
            </button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="itinerary-content">
        {/* Day Navigation */}
        <div className="day-navigation">
          {daily_itinerary.map((day, index) => (
            <button
              key={day.day}
              className={`day-tab ${selectedDay === day.day ? 'active' : ''}`}
              onClick={() => setSelectedDay(day.day)}
            >
              <div className="day-number">Day {day.day}</div>
              <div className="day-places">{day.total_places} places</div>
            </button>
          ))}
        </div>

        {/* Day Content */}
        {viewMode === 'timeline' && (
          <div className="timeline-view">
            {daily_itinerary
              .filter(day => day.day === selectedDay)
              .map(day => (
                <div key={day.day} className="day-timeline">
                  <h2>Day {day.day} - {day.places.length} Destinations</h2>
                  
                  <div className="timeline-container">
                    {day.places.map((place, index) => (
                      <div key={index} className="timeline-item">
                        <div className="timeline-marker">
                          <span className="place-number">{index + 1}</span>
                        </div>
                        
                        <div className="place-card">
                          <div className="place-header">
                            <h3>{place.Place || 'Unknown Place'}</h3>
                            <div className="time-slot">
                              <span className="start-time">{formatTime(place.start_time)}</span>
                              <span className="duration">
                                ({place.Eestimated_time_to_visit || '2'}h)
                              </span>
                              <span className="end-time">{formatTime(place.end_time)}</span>
                            </div>
                          </div>
                          
                          <div className="place-details">
                            <div className="detail-grid">
                              <div className="detail-item">
                                <span className="detail-label">Category:</span>
                                <span className="detail-value">{place.Category || 'General'}</span>
                              </div>
                              <div className="detail-item">
                                <span className="detail-label">Region:</span>
                                <span className="detail-value">{place.Region || 'Sri Lanka'}</span>
                              </div>
                              <div className="detail-item">
                                <span className="detail-label">Ticket Price:</span>
                                <span className="detail-value">
                                  LKR {parseFloat(place.Ticket_price || 0).toLocaleString()}
                                </span>
                              </div>
                              <div className="detail-item">
                                <span className="detail-label">Contact:</span>
                                <span className="detail-value">{place.Contact_no || 'N/A'}</span>
                              </div>
                            </div>
                            
                            {place.Description && (
                              <div className="place-description">
                                <p>{place.Description}</p>
                              </div>
                            )}
                            
                            <div className="place-actions">
                              <button className="action-button primary">
                                📍 View on Map
                              </button>
                              <button className="action-button secondary">
                                📝 Add Notes
                              </button>
                              <button className="action-button secondary">
                                📞 Contact
                              </button>
                            </div>
                          </div>
                        </div>
                        
                        {index < day.places.length - 1 && (
                          <div className="travel-connector">
                            <div className="travel-line"></div>
                            <div className="travel-info">
                              <span>🚗 Travel time: ~30 min</span>
                            </div>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              ))}
          </div>
        )}

        {/* Map View */}
        {viewMode === 'map' && (
          <div className="map-view">
            <div className="map-container">
              <div className="map-placeholder">
                <h3>Interactive Map</h3>
                <p>Map integration coming soon...</p>
                <div className="map-features">
                  <div className="feature">📍 All destinations marked</div>
                  <div className="feature">🛣️ Optimized routes</div>
                  <div className="feature">🏨 Nearby accommodations</div>
                  <div className="feature">🍽️ Restaurant recommendations</div>
                </div>
              </div>
            </div>
            
            <div className="map-sidebar">
              <h3>Quick Navigation</h3>
              {daily_itinerary.map(day => (
                <div key={day.day} className="map-day-section">
                  <h4>Day {day.day}</h4>
                  <div className="map-places">
                    {day.places.map((place, index) => (
                      <button 
                        key={index}
                        className="map-place-button"
                        onClick={() => setSelectedDay(day.day)}
                      >
                        <span className="place-marker">{index + 1}</span>
                        <span className="place-name">{place.Place}</span>
                      </button>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Footer with Additional Actions */}
      <div className="itinerary-footer">
        <div className="footer-actions">
          <button className="footer-button">
            💾 Save Itinerary
          </button>
          <button className="footer-button">
            📤 Share with Friends
          </button>
          <button className="footer-button">
            🏨 Find Hotels
          </button>
          <button className="footer-button">
            🚗 Book Transport
          </button>
        </div>
        
        <div className="footer-info">
          <p>Generated with ❤️ by Travel Guider AI</p>
          <div className="interests-tags">
            {interests && interests.map((interest, index) => (
              <span key={index} className="interest-tag">{interest}</span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ItineraryDisplay;
