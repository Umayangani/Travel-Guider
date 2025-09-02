import React, { useState, useEffect } from 'react';
import './ItineraryDisplay.css';

const ItineraryDisplay = ({ itineraryData, onBack, isModal = false }) => {
  const [selectedDay, setSelectedDay] = useState(1);
  const [viewMode, setViewMode] = useState('timeline'); // 'timeline' or 'map'
  const [showChecklist, setShowChecklist] = useState(false);
  const [checklist, setChecklist] = useState(null);
  const [selectedPlace, setSelectedPlace] = useState(null);

  if (!itineraryData || (!itineraryData.daily_itinerary && !itineraryData.daily_plans)) {
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

  // Handle both old and new API response formats
  const isEnhancedFormat = itineraryData.daily_plans && itineraryData.trip_summary;
  const dailyData = isEnhancedFormat ? itineraryData.daily_plans : itineraryData.daily_itinerary;
  const totalDays = isEnhancedFormat ? itineraryData.total_days : itineraryData.total_days;
  const tripSummary = isEnhancedFormat ? itineraryData.trip_summary : null;

  const calculateTotalBudget = () => {
    let total = 0;
    dailyData.forEach(day => {
      day.places.forEach(place => {
        const ticketPrice = parseFloat(place.Ticket_price || 0);
        total += isNaN(ticketPrice) ? 0 : ticketPrice;
      });
    });
    return total;
  };

  const calculateTotalDistance = () => {
    if (tripSummary && tripSummary.total_distance_km) {
      return tripSummary.total_distance_km;
    }
    
    let totalDistance = 0;
    dailyData.forEach(day => {
      if (day.day_summary && day.day_summary.total_distance_km) {
        totalDistance += day.day_summary.total_distance_km;
      } else if (day.total_distance_km) {
        totalDistance += day.total_distance_km;
      } else {
        day.places.forEach(place => {
          if (place.distance_from_previous_km) {
            totalDistance += place.distance_from_previous_km;
          }
        });
      }
    });
    return Math.round(totalDistance * 100) / 100;
  };

  const calculateTotalTravelTime = () => {
    if (tripSummary && tripSummary.total_travel_time_hours) {
      return tripSummary.total_travel_time_hours;
    }
    
    let totalTime = 0;
    dailyData.forEach(day => {
      if (day.day_summary && day.day_summary.total_travel_time_hours) {
        totalTime += day.day_summary.total_travel_time_hours;
      } else if (day.estimated_travel_time_hours) {
        totalTime += day.estimated_travel_time_hours;
      } else {
        day.places.forEach(place => {
          if (place.travel_time_hours || place.travel_time_from_previous_hours) {
            totalTime += place.travel_time_hours || place.travel_time_from_previous_hours;
          }
        });
      }
    });
    return Math.round(totalTime * 100) / 100;
  };

  const formatTime = (timeStr) => {
    if (!timeStr) return '';
    const [hours, minutes] = timeStr.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour > 12 ? hour - 12 : hour === 0 ? 12 : hour;
    return `${displayHour}:${minutes} ${ampm}`;
  };

  const getTotalPlaces = () => {
    if (itineraryData.total_places) return itineraryData.total_places;
    return dailyData.reduce((total, day) => total + day.places.length, 0);
  };

  const exportItinerary = () => {
    const exportData = {
      trip_summary: {
        total_days: totalDays,
        total_places: getTotalPlaces(),
        travel_style: itineraryData.travel_style,
        interests: itineraryData.interests,
        estimated_budget: calculateTotalBudget(),
        estimated_distance: calculateTotalDistance(),
        estimated_travel_time: calculateTotalTravelTime(),
        ...(tripSummary && tripSummary)
      },
      daily_itinerary: dailyData
    };
    
    const dataStr = JSON.stringify(exportData, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'travel-itinerary.json';
    link.click();
  };

  const generateChecklist = async (categories = [], placeInfo = null) => {
    try {
      const response = await fetch('http://localhost:5000/api/ml/checklist', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          categories: categories,
          duration: totalDays,
          place_info: placeInfo
        })
      });

      if (response.ok) {
        const data = await response.json();
        setChecklist(data.checklist);
        setShowChecklist(true);
      } else {
        console.error('Failed to generate checklist');
      }
    } catch (error) {
      console.error('Error generating checklist:', error);
    }
  };

  const getItineraryCategories = () => {
    const categories = new Set();
    dailyData.forEach(day => {
      day.places.forEach(place => {
        const category = place.Category || place.category;
        if (category) {
          categories.add(category);
        }
      });
    });
    return Array.from(categories);
  };

  const handleGenerateGeneralChecklist = () => {
    const categories = getItineraryCategories();
    generateChecklist(categories);
  };

  const handleGeneratePlaceChecklist = (place) => {
    setSelectedPlace(place);
    generateChecklist([place.Category || place.category], place);
  };

  return (
    <div className={`itinerary-display ${isModal ? 'modal-mode' : ''}`}>
      {/* Header Section - only show if not in modal */}
      {!isModal && (
        <div className="itinerary-header">
          <button className="back-button" onClick={onBack}>
            ← Back to Form
          </button>
          <div className="header-content">
            <h1>Your Personalized Travel Itinerary</h1>
            <div className="trip-summary">
              <div className="summary-item">
                <span className="label">Duration:</span>
                <span className="value">{totalDays} days</span>
              </div>
              <div className="summary-item">
                <span className="label">Places:</span>
                <span className="value">{getTotalPlaces()} locations</span>
              </div>
              <div className="summary-item">
                <span className="label">Style:</span>
                <span className="value">{itineraryData.travel_style || 'Standard'}</span>
              </div>
              <div className="summary-item">
                <span className="label">Distance:</span>
                <span className="value">{calculateTotalDistance()} km</span>
              </div>
              <div className="summary-item">
                <span className="label">Travel Time:</span>
                <span className="value">
                  {tripSummary?.total_travel_time_formatted || `${calculateTotalTravelTime()} hrs`}
                </span>
              </div>
              <div className="summary-item">
                <span className="label">Budget:</span>
                <span className="value">LKR {calculateTotalBudget().toLocaleString()}</span>
              </div>
              {tripSummary?.trip_type && (
                <div className="summary-item">
                  <span className="label">Type:</span>
                  <span className="value">{tripSummary.trip_type}</span>
                </div>
              )}
            </div>
          </div>
          <div className="header-actions">
            <button className="export-button" onClick={exportItinerary}>
              📱 Export Itinerary
            </button>
            <button className="checklist-button" onClick={handleGenerateGeneralChecklist}>
              📋 Travel Checklist
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
      )}

      {/* Modal Summary Header - only show in modal mode */}
      {isModal && (
        <div className="modal-summary-header">
          <div className="trip-summary-compact">
            <span>{totalDays} days</span>
            <span>•</span>
            <span>{getTotalPlaces()} places</span>
            <span>•</span>
            <span>{calculateTotalDistance()} km</span>
            <span>•</span>
            <span>{tripSummary?.total_travel_time_formatted || `${calculateTotalTravelTime()} hrs`}</span>
            <span>•</span>
            <span>LKR {calculateTotalBudget().toLocaleString()}</span>
            {tripSummary?.trip_type && (
              <>
                <span>•</span>
                <span>{tripSummary.trip_type}</span>
              </>
            )}
          </div>
        </div>
      )}

      {/* Main Content */}
      <div className="itinerary-content">
        {/* Day Navigation */}
        <div className="day-navigation">
          {dailyData.map((day, index) => (
            <button
              key={day.day}
              className={`day-tab ${selectedDay === day.day ? 'active' : ''}`}
              onClick={() => setSelectedDay(day.day)}
            >
              <div className="day-number">Day {day.day}</div>
              <div className="day-places">{day.places.length} places</div>
              {(day.day_summary?.total_distance_km || day.total_distance_km) && (
                <div className="day-distance">
                  {day.day_summary?.total_distance_km || day.total_distance_km} km
                </div>
              )}
            </button>
          ))}
        </div>

        {/* Day Content */}
        {viewMode === 'timeline' && (
          <div className="timeline-view">
            {dailyData
              .filter(day => day.day === selectedDay)
              .map(day => (
                <div key={day.day} className="day-timeline">
                  <div className="day-header">
                    <h2>Day {day.day} - {day.places.length} Destinations</h2>
                    {(day.day_summary || day.total_distance_km) && (
                      <div className="day-stats">
                        <div className="stat-item">
                          <span className="stat-icon">🚗</span>
                          <span className="stat-label">Distance:</span>
                          <span className="stat-value">
                            {day.day_summary?.total_distance_km || day.total_distance_km} km
                          </span>
                        </div>
                        <div className="stat-item">
                          <span className="stat-icon">⏱️</span>
                          <span className="stat-label">Travel Time:</span>
                          <span className="stat-value">
                            {day.day_summary?.total_travel_time_formatted || 
                             `${day.day_summary?.total_travel_time_hours || 0} hrs`}
                          </span>
                        </div>
                        <div className="stat-item">
                          <span className="stat-icon">🏛️</span>
                          <span className="stat-label">Visit Time:</span>
                          <span className="stat-value">
                            {day.day_summary?.total_visit_time_formatted || 
                             `${day.day_summary?.total_visit_time_hours || 0} hrs`}
                          </span>
                        </div>
                      </div>
                    )}
                  </div>
                  
                  <div className="timeline-container">
                    {/* Starting Point */}
                    <div className="timeline-item start-point">
                      <div className="timeline-marker start">
                        <span className="start-icon">🏁</span>
                      </div>
                      <div className="place-card start-card">
                        <h3>Starting Point - Colombo Fort</h3>
                        <p>Departure: 6:00 AM</p>
                      </div>
                    </div>

                    {day.places.map((place, index) => (
                      <div key={index} className="timeline-item">
                        {/* Travel Route */}
                        {place.distance_from_previous_km > 0 && (
                          <div className="travel-route">
                            <div className="route-line"></div>
                            <div className="route-info">
                              <div className="route-distance">
                                <span className="distance-icon">🚗</span>
                                <span>{place.distance_from_previous_km} km</span>
                              </div>
                              <div className="route-time">
                                <span className="time-icon">⏱️</span>
                                <span>{place.travel_time_formatted || 
                                      `${place.travel_time_hours}h`}</span>
                              </div>
                            </div>
                          </div>
                        )}

                        <div className="timeline-marker">
                          <span className="place-number">{index + 1}</span>
                        </div>
                        
                        <div className="place-card enhanced">
                          <div className="place-header">
                            <h3>{place.name || place.Place || 'Unknown Place'}</h3>
                            <div className="time-slot">
                              {place.arrival_time && place.departure_time ? (
                                <>
                                  <span className="arrival-time">
                                    <span className="time-label">Arrival:</span>
                                    {formatTime(place.arrival_time)}
                                  </span>
                                  <span className="departure-time">
                                    <span className="time-label">Departure:</span>
                                    {formatTime(place.departure_time)}
                                  </span>
                                  <span className="visit-duration">
                                    Visit: {place.estimated_time_to_visit || 
                                           place.Eestimated_time_to_visit || '2'}h
                                  </span>
                                </>
                              ) : (
                                <>
                                  <span className="start-time">{formatTime(place.start_time)}</span>
                                  <span className="duration">
                                    ({place.Eestimated_time_to_visit || '2'}h)
                                  </span>
                                  <span className="end-time">{formatTime(place.end_time)}</span>
                                </>
                              )}
                            </div>
                          </div>
                          
                          <div className="place-details">
                            <div className="detail-grid">
                              <div className="detail-item">
                                <span className="detail-label">Category:</span>
                                <span className="detail-value">
                                  {place.category || place.Category || 'General'}
                                </span>
                              </div>
                              <div className="detail-item">
                                <span className="detail-label">District:</span>
                                <span className="detail-value">
                                  {place.district || place.Region || 'Sri Lanka'}
                                </span>
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
                            
                            {(place.description || place.Description) && (
                              <div className="place-description">
                                <p>{place.description || place.Description}</p>
                              </div>
                            )}
                            
                            <div className="place-actions">
                              <button className="action-button primary">
                                📍 View on Map
                              </button>
                              <button 
                                className="action-button secondary"
                                onClick={() => handleGeneratePlaceChecklist(place)}
                              >
                                📋 Checklist
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
                      </div>
                    ))}

                    {/* Return to Start Point (for last day) */}
                    {day.return_to_start && (
                      <div className="timeline-item return-journey">
                        <div className="travel-route">
                          <div className="route-line return"></div>
                          <div className="route-info return">
                            <div className="route-distance">
                              <span className="distance-icon">🚗</span>
                              <span>{day.return_to_start.distance_km} km</span>
                            </div>
                            <div className="route-time">
                              <span className="time-icon">⏱️</span>
                              <span>{day.return_to_start.travel_time_formatted}</span>
                            </div>
                            <div className="route-label">Return Journey</div>
                          </div>
                        </div>
                        <div className="timeline-marker end">
                          <span className="end-icon">🏁</span>
                        </div>
                        <div className="place-card end-card">
                          <h3>End Point - Colombo Fort</h3>
                          <p>Trip Completed Successfully!</p>
                        </div>
                      </div>
                    )}
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
                  <div className="feature">🛣️ Optimized routes with distances</div>
                  <div className="feature">⏱️ Travel time calculations</div>
                  <div className="feature">🔄 Round-trip planning</div>
                </div>
              </div>
            </div>
            
            <div className="map-sidebar">
              <h3>Quick Navigation</h3>
              {dailyData.map(day => (
                <div key={day.day} className="map-day-section">
                  <h4>Day {day.day}</h4>
                  <div className="day-distance-info">
                    {(day.day_summary?.total_distance_km || day.total_distance_km) && (
                      <span>🚗 {day.day_summary?.total_distance_km || day.total_distance_km} km</span>
                    )}
                  </div>
                  <div className="map-places">
                    {day.places.map((place, index) => (
                      <button 
                        key={index}
                        className="map-place-button"
                        onClick={() => setSelectedDay(day.day)}
                      >
                        <span className="place-marker">{index + 1}</span>
                        <span className="place-name">{place.name || place.Place}</span>
                        {place.distance_from_previous_km > 0 && (
                          <span className="place-distance">
                            {place.distance_from_previous_km} km
                          </span>
                        )}
                      </button>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Enhanced Footer with ML Information */}
      <div className="itinerary-footer">
        <div className="footer-actions">
          <button className="footer-button" onClick={exportItinerary}>
            💾 Export Itinerary
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
          <p>Generated with ❤️ by Travel Guider ML</p>
          {itineraryData.generated_by && (
            <p className="ml-info">
              🤖 Powered by {itineraryData.generated_by} 
              {itineraryData.ml_confidence && 
                ` (Confidence: ${Math.round(itineraryData.ml_confidence * 100)}%)`}
            </p>
          )}
          {itineraryData.features && (
            <div className="enhanced-features">
              <h4>✨ Enhanced Features:</h4>
              <div className="features-list">
                {itineraryData.features.map((feature, index) => (
                  <span key={index} className="feature-tag">✓ {feature}</span>
                ))}
              </div>
            </div>
          )}
          <div className="interests-tags">
            {itineraryData.interests && itineraryData.interests.map((interest, index) => (
              <span key={index} className="interest-tag">{interest}</span>
            ))}
          </div>
        </div>
      </div>

      {/* Checklist Modal */}
      {showChecklist && checklist && (
        <div className="checklist-modal">
          <div className="checklist-content">
            <div className="checklist-header">
              <h3>Travel Checklist</h3>
              {selectedPlace && (
                <h4>For {selectedPlace.Name || selectedPlace.Place}</h4>
              )}
              <button 
                className="close-checklist"
                onClick={() => setShowChecklist(false)}
              >
                ✕
              </button>
            </div>
            <div className="checklist-body">
              {Object.entries(checklist).map(([category, items]) => (
                <div key={category} className="checklist-section">
                  <h5>{category.replace(/_/g, ' ').toUpperCase()}</h5>
                  <ul>
                    {Array.isArray(items) ? items.map((item, index) => (
                      <li key={index}>
                        <input type="checkbox" id={`${category}-${index}`} />
                        <label htmlFor={`${category}-${index}`}>{item}</label>
                      </li>
                    )) : (
                      <li>{items}</li>
                    )}
                  </ul>
                </div>
              ))}
            </div>
            <div className="checklist-footer">
              <button 
                className="download-checklist-btn"
                onClick={() => {
                  const checklistStr = JSON.stringify(checklist, null, 2);
                  const blob = new Blob([checklistStr], { type: 'application/json' });
                  const url = URL.createObjectURL(blob);
                  const link = document.createElement('a');
                  link.href = url;
                  link.download = 'travel-checklist.json';
                  link.click();
                }}
              >
                📥 Download Checklist
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ItineraryDisplay;
