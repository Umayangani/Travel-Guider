import React, { useState, useEffect } from "react";
import { API_BASE_URL } from "../api/config";
import { useParams, useNavigate } from "react-router-dom";
import "./AddPlace.css";

const EditBus = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [busData, setBusData] = useState({
    route_name: "",
    departure_location: "",
    arrival_location: "",
    departure_time: "",
    arrival_time: "",
    frequency: "Daily",
    duration_minutes: "",
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
  fetch(`${API_BASE_URL}/api/bus-schedules/${id}`)
      .then((res) => {
        if (!res.ok) throw new Error("Not found");
        return res.json();
      })
      .then((data) => {
        setBusData({
          route_name: data.routeName || "",
          departure_location: data.departureLocation || "",
          arrival_location: data.arrivalLocation || "",
          departure_time: data.departureTime ? data.departureTime.substring(0,5) : "",
          arrival_time: data.arrivalTime ? data.arrivalTime.substring(0,5) : "",
          frequency: data.frequency || "Daily",
          duration_minutes: data.durationMinutes || "",
        });
        setLoading(false);
      })
      .catch(() => {
        setError("Bus schedule not found.");
        setLoading(false);
      });
  }, [id]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setBusData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        id,
        routeName: busData.route_name,
        departureLocation: busData.departure_location,
        arrivalLocation: busData.arrival_location,
        departureTime: busData.departure_time + (busData.departure_time.length === 5 ? ':00' : ''),
        arrivalTime: busData.arrival_time + (busData.arrival_time.length === 5 ? ':00' : ''),
        frequency: busData.frequency,
        durationMinutes: busData.duration_minutes ? parseInt(busData.duration_minutes) : null,
      };
  const response = await fetch(`${API_BASE_URL}/api/bus-schedules/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (response.ok) {
        alert("✅ Bus schedule updated successfully!");
        navigate("/admin/edit-bus");
      } else {
        const errorText = await response.text();
        alert("❌ Error updating bus schedule: " + errorText);
      }
    } catch (error) {
      alert("❌ Network error updating bus schedule.");
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="add-place-container">
      <h2>Edit Bus Schedule</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <label>Route Name:</label>
          <input name="route_name" value={busData.route_name} onChange={handleInputChange} required />

          <label>Departure Location:</label>
          <input name="departure_location" value={busData.departure_location} onChange={handleInputChange} required />

          <label>Arrival Location:</label>
          <input name="arrival_location" value={busData.arrival_location} onChange={handleInputChange} required />

          <label>Departure Time:</label>
          <input type="time" name="departure_time" value={busData.departure_time} onChange={handleInputChange} required />

          <label>Arrival Time:</label>
          <input type="time" name="arrival_time" value={busData.arrival_time} onChange={handleInputChange} required />

          <label>Frequency:</label>
          <select name="frequency" value={busData.frequency} onChange={handleInputChange}>
            <option value="Daily">Daily</option>
            <option value="Weekdays">Weekdays</option>
            <option value="Weekends">Weekends</option>
          </select>

          <label>Duration (Minutes):</label>
          <input type="number" name="duration_minutes" value={busData.duration_minutes} onChange={handleInputChange} min="0" />
        </div>
        <button type="submit">Update Bus Schedule</button>
      </form>
    </div>
  );
};

export default EditBus;
