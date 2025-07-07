import React, { useState } from "react";
import "./ItineraryForm.css";

const shortCategories = [
  "Beach", "Temple", "Wildlife", "Adventure", "Culture", "Nature", "Camping"
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

  return (
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
          <div className="itinerary-pref-list">
            {preferences.map(cat => (
              <span key={cat} className="itinerary-pref-item">
                {cat} <button onClick={() => removePreference(cat)}>&times;</button>
              </span>
            ))}
          </div>
        </div>
        <div className="itinerary-field same-size">
          <label>Transport Mode</label>
          <div className="itinerary-transport">
            <label><input type="radio" name="transport" value="public" checked={transport === "public"} onChange={() => setTransport("public")} /> Public</label>
            <label><input type="radio" name="transport" value="private" checked={transport === "private"} onChange={() => setTransport("private")} /> Private</label>
          </div>
        </div>
      </div>
      <div className="itinerary-row" style={{justifyContent: 'flex-end'}}>
        <button className="itinerary-submit">Get Recommendations</button>
      </div>
    </div>
  );
}

export default ItineraryForm;
