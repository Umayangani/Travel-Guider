import React, { useState } from "react";
import axios from "axios";
import "./ItineraryForm.css";
import ItineraryModal from "./ItineraryModal";

// Enhanced categories matching ML system capabilities
const shortCategories = [
  "Beach", "Temple", "Wildlife", "Adventure", "Culture", "Nature", "Camping"
];

// Activity levels for better trip planning
const activityLevels = [
  { value: "relaxed", label: "Relaxed (2-3 places/day)" },
  { value: "moderate", label: "Moderate (3-4 places/day)" },
  { value: "active", label: "Active (4-5 places/day)" }
];

function ItineraryForm() {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [startLocation, setStartLocation] = useState("");
  const [adults, setAdults] = useState(1);
  const [children, setChildren] = useState(0);
  const [preference, setPreference] = useState("");
  const [preferences, setPreferences] = useState([]);
  const [transport, setTransport] = useState("public");
  const [activityLevel, setActivityLevel] = useState("moderate"); // New activity level state
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [itinerary, setItinerary] = useState(null);
  const [showItineraryModal, setShowItineraryModal] = useState(false);

  const addPreference = () => {
    if (preference && !preferences.includes(preference)) {
      setPreferences([...preferences, preference]);
      setPreference("");
    }
  };

  const removePreference = (cat) => {
    setPreferences(preferences.filter((p) => p !== cat));
  };

  // Placeholder for map location picker
  const handlePickLocation = () => {
    // In real app, open map and set location
    setStartLocation("Colombo");
  };

  const handleGenerateItinerary = async () => {
    setLoading(true);
    setError("");
    setItinerary(null);

    try {
      // Validate required fields
      if (!startDate || !endDate) {
        setError("Please select start and end dates");
        return;
      }
      if (!startLocation) {
        setError("Please select a starting location");
        return;
      }

      // Prepare request data for backend
      const totalDays = Math.ceil((new Date(endDate) - new Date(startDate)) / (1000 * 60 * 60 * 24));
      
      // Ensure preferences is always an array
      const preferencesArray = Array.isArray(preferences) ? preferences : [];
      
      const requestData = {
        title: "My Travel Itinerary",
        startDate: startDate,
        endDate: endDate,
        totalDays: totalDays,
        startingLocation: startLocation,
        adultsCount: adults,
        childrenCount: children,
        studentsCount: 0,
        foreignersCount: 0,
        budgetRange: "medium",
        transportPreference: transport === "public" ? "bus" : "car",
        activityLevel: activityLevel, // Include activity level
        preferredCategories: preferencesArray,
        specificInterests: preferencesArray,
        includeWeather: true
      };

      console.log("🚀 Sending request to:", 'http://localhost:8090/api/itinerary/generate?userId=1');
      console.log("📋 Request data:", JSON.stringify(requestData, null, 2));
      console.log("📋 Preferences type:", typeof requestData.preferredCategories, requestData.preferredCategories);

      const response = await axios.post(
        'http://localhost:8090/api/itinerary/generate',
        requestData,
        {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          timeout: 30000, // 30 second timeout
          transformRequest: [(data) => {
            // Ensure data is properly serialized as JSON
            return JSON.stringify(data);
          }]
        }
      );

      console.log("✅ Response received:", response);
      
      // Transform the response data to match our display component format
      const transformedItinerary = {
        success: true,
        total_days: response.data.totalDays || Math.ceil((new Date(endDate) - new Date(startDate)) / (1000 * 60 * 60 * 24)),
        total_places: response.data.days ? response.data.days.reduce((total, day) => total + (day.places?.length || 0), 0) : 0,
        travel_style: "Moderate",
        interests: preferences,
        daily_itinerary: response.data.days ? response.data.days.map(day => ({
          day: day.dayNumber,
          date: day.date,
          places: day.places ? day.places.map(place => ({
            Place: place.placeName,
            Category: place.category || 'General',
            Region: place.district || 'Sri Lanka',
            Latitude: place.latitude || 6.9271,
            Longitude: place.longitude || 79.8612,
            Ticket_price: place.totalEntryCost || 0,
            Eestimated_time_to_visit: place.estimatedVisitDurationHours || 2,
            Contact_no: place.contact || 'N/A',
            Description: place.description || '',
            start_time: place.arrivalTime || '09:00',
            end_time: place.departureTime || '11:00'
          })) : [],
          total_places: day.places?.length || 0
        })) : []
      };
      
      setItinerary(transformedItinerary);
      setShowItineraryModal(true);
      console.log("🎯 Transformed itinerary:", transformedItinerary);

    } catch (error) {
      console.error("❌ Full error object:", error);
      
      if (error.response) {
        // Server responded with error status
        console.error("🔴 Server error:", error.response.status, error.response.data);
        setError(`Server error (${error.response.status}): ${error.response.data.message || error.response.data || 'Failed to generate itinerary'}`);
      } else if (error.request) {
        // Request was made but no response received
        console.error("🔴 Network error - no response received");
        console.error("Request details:", error.request);
        setError('Network error: Unable to connect to server. Please check if backend is running on port 8090.');
      } else {
        // Something else happened
        console.error("🔴 Other error:", error.message);
        setError(`Error: ${error.message}`);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCloseModal = () => {
    setShowItineraryModal(false);
  };

  // Remove the old return statement that showed ItineraryDisplay in hero section

  return (
    <div>
      <div className="itinerary-form">
        <h2 className="itinerary-title">Plan Your Trip</h2>
        <div className="itinerary-row">
          <div className="itinerary-field">
            <label>Start Date</label>
            <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} />
          </div>
          <div className="itinerary-field">
            <label>End Date</label>
            <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} />
          </div>
        </div>
      <div className="itinerary-row">
        <div className="itinerary-field same-size">
          <label>Start Location</label>
          <div className="itinerary-location">
            <input type="text" value={startLocation} readOnly placeholder="Pick on map" />
            <button type="button" className="map-btn" onClick={handlePickLocation}>Pick</button>
          </div>
        </div>
        <div className="itinerary-field same-size">
          <label>Adults</label>
          <input type="number" min="1" value={adults} onChange={e => setAdults(Number(e.target.value))} />
        </div>
        <div className="itinerary-field same-size">
          <label>Children</label>
          <input type="number" min="0" value={children} onChange={e => setChildren(Number(e.target.value))} />
        </div>
        <div className="itinerary-field same-size">
          <label>Transport Mode</label>
          <div className="itinerary-transport">
            <label><input type="radio" name="transport" value="public" checked={transport === "public"} onChange={() => setTransport("public")} /> Public</label>
            <label><input type="radio" name="transport" value="private" checked={transport === "private"} onChange={() => setTransport("private")} /> Private</label>
          </div>
        </div>
        <div className="itinerary-field same-size">
          <label>Activity Level</label>
          <select value={activityLevel} onChange={e => setActivityLevel(e.target.value)}>
            {activityLevels.map(level => (
              <option key={level.value} value={level.value}>{level.label}</option>
            ))}
          </select>
        </div>
      </div>
      {/* Preferences selector on its own row */}
      <div className="itinerary-row">
        <div className="itinerary-field same-size">
          <label>Preferences</label>
          <div className="itinerary-pref-row">
            <select value={preference} onChange={e => setPreference(e.target.value)}>
              <option value="">Select</option>
              {shortCategories.map(cat => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
            <button type="button" onClick={addPreference}>Add</button>
          </div>
        </div>
      </div>
      {/* Preference list on its own row */}
      {preferences.length > 0 && (
        <div className="itinerary-row">
          <div className="itinerary-field same-size" style={{width: '100%'}}>
            <div className="itinerary-pref-list">
              {preferences.map(cat => (
                <span key={cat} className="itinerary-pref-item">
                  {cat} <button onClick={() => removePreference(cat)}>&times;</button>
                </span>
              ))}
            </div>
          </div>
        </div>
      )}
      
      {/* Error Message */}
      {error && (
        <div className="itinerary-row">
          <div className="error-message">
            {error}
          </div>
        </div>
      )}

      <div className="itinerary-row" style={{justifyContent: 'flex-end'}}>
        <button 
          className="itinerary-submit" 
          onClick={handleGenerateItinerary}
          disabled={loading}
        >
          {loading ? "Generating..." : "Get Started"}
        </button>
      </div>
      </div>
      
      {/* Itinerary Modal */}
      <ItineraryModal
        isOpen={showItineraryModal}
        itineraryData={itinerary}
        onClose={handleCloseModal}
      />
    </div>
  );
}

export default ItineraryForm;
