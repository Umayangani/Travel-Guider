import React, { useState } from "react";
import "./AddPlace.css";

const AddBus = () => {
  const [busData, setBusData] = useState({
    route_name: "",
    departure_location: "",
    arrival_location: "",
    departure_time: "",
    arrival_time: "",
    frequency: "Daily",
    duration_minutes: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setBusData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/buses", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(busData),
      });

      if (response.ok) {
        const result = await response.text();
        alert("✅ Bus schedule added successfully!\n" + result);
        setBusData({
          route_name: "",
          departure_location: "",
          arrival_location: "",
          departure_time: "",
          arrival_time: "",
          frequency: "Daily",
          duration_minutes: "",
        });
      } else {
        const errorText = await response.text();
        alert("❌ Error adding bus schedule: " + errorText);
      }
    } catch (error) {
      console.error("Error submitting form:", error);
      alert("❌ Network error submitting form.");
    }
  };

  return (
    <div className="add-place-container">
      <h2>Add New Bus Schedule</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <label>Route Name:</label>
          <input
            name="route_name"
            value={busData.route_name}
            onChange={handleInputChange}
            required
          />

          <label>Departure Location:</label>
          <input
            name="departure_location"
            value={busData.departure_location}
            onChange={handleInputChange}
            required
          />

          <label>Arrival Location:</label>
          <input
            name="arrival_location"
            value={busData.arrival_location}
            onChange={handleInputChange}
            required
          />

          <label>Departure Time:</label>
          <input
            type="time"
            name="departure_time"
            value={busData.departure_time}
            onChange={handleInputChange}
            required
          />

          <label>Arrival Time:</label>
          <input
            type="time"
            name="arrival_time"
            value={busData.arrival_time}
            onChange={handleInputChange}
            required
          />

          <label>Frequency:</label>
          <select
            name="frequency"
            value={busData.frequency}
            onChange={handleInputChange}
          >
            <option value="Daily">Daily</option>
            <option value="Weekdays">Weekdays</option>
            <option value="Weekends">Weekends</option>
          </select>

          <label>Duration (Minutes):</label>
          <input
            type="number"
            name="duration_minutes"
            value={busData.duration_minutes}
            onChange={handleInputChange}
            min="0"
          />
        </div>

        <button type="submit">Add Bus Schedule</button>
      </form>
    </div>
  );
};

export default AddBus; 