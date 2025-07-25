import React, { useState } from "react";
import "./AddPlace.css"; // Reuse the same CSS for consistent styling

const AddTrain = () => {
  const [trainData, setTrainData] = useState({
    train_name: "",
    from_station: "",
    to_station: "",
    departure_time: "",
    arrival_time: "",
    duration: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setTrainData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // Map frontend state to backend property names
      const payload = {
        trainName: trainData.train_name,
        fromStation: trainData.from_station,
        toStation: trainData.to_station,
        departureTime: trainData.departure_time + (trainData.departure_time.length === 5 ? ':00' : ''),
        arrivalTime: trainData.arrival_time + (trainData.arrival_time.length === 5 ? ':00' : ''),
        duration: trainData.duration + (trainData.duration.length === 5 ? ':00' : ''),
      };
      const response = await fetch("http://localhost:8080/api/train-schedules", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (response.ok) {
        // const result = await response.json();
        alert("✅ Train schedule added successfully!");

        // Reset form
        setTrainData({
          train_name: "",
          from_station: "",
          to_station: "",
          departure_time: "",
          arrival_time: "",
          duration: "",
        });
      } else {
        const errorText = await response.text();
        alert("❌ Error adding train schedule: " + errorText);
      }
    } catch (error) {
      console.error("Error submitting form:", error);
      alert("❌ Network error submitting form.");
    }
  };

  return (
    <div className="add-place-container">
      <h2>Add New Train Schedule</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <label>Train Name:</label>
          <input
            name="train_name"
            value={trainData.train_name}
            onChange={handleInputChange}
            required
          />

          <label>From Station:</label>
          <input
            name="from_station"
            value={trainData.from_station}
            onChange={handleInputChange}
            required
          />

          <label>To Station:</label>
          <input
            name="to_station"
            value={trainData.to_station}
            onChange={handleInputChange}
            required
          />

          <label>Departure Time:</label>
          <input
            type="time"
            name="departure_time"
            value={trainData.departure_time}
            onChange={handleInputChange}
            required
          />

          <label>Arrival Time:</label>
          <input
            type="time"
            name="arrival_time"
            value={trainData.arrival_time}
            onChange={handleInputChange}
            required
          />

          <label>Duration:</label>
          <input
            type="time"
            name="duration"
            value={trainData.duration}
            onChange={handleInputChange}
          />
        </div>

        <button type="submit">Add Train Schedule</button>
      </form>
    </div>
  );
};

export default AddTrain;
