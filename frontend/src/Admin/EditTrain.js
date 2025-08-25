import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./AddPlace.css";

const EditTrain = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [trainData, setTrainData] = useState({
    train_name: "",
    from_station: "",
    to_station: "",
    departure_time: "",
    arrival_time: "",
    duration: "",
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    // Fetch train schedule by ID
    fetch(`http://localhost:8080/api/train-schedules/${id}`)
      .then((res) => {
        if (!res.ok) throw new Error("Not found");
        return res.json();
      })
      .then((data) => {
        setTrainData({
          train_name: data.trainName || "",
          from_station: data.fromStation || "",
          to_station: data.toStation || "",
          departure_time: data.departureTime ? data.departureTime.substring(0,5) : "",
          arrival_time: data.arrivalTime ? data.arrivalTime.substring(0,5) : "",
          duration: data.duration ? data.duration.substring(0,5) : "",
        });
        setLoading(false);
      })
      .catch((err) => {
        setError("Train schedule not found.");
        setLoading(false);
      });
  }, [id]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setTrainData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        trainName: trainData.train_name,
        fromStation: trainData.from_station,
        toStation: trainData.to_station,
        departureTime: trainData.departure_time + (trainData.departure_time.length === 5 ? ':00' : ''),
        arrivalTime: trainData.arrival_time + (trainData.arrival_time.length === 5 ? ':00' : ''),
        duration: trainData.duration + (trainData.duration.length === 5 ? ':00' : ''),
      };
      const response = await fetch(`http://localhost:8080/api/train-schedules/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (response.ok) {
        alert("✅ Train schedule updated successfully!");
        navigate("/admin/edit-train");
      } else {
        const errorText = await response.text();
        alert("❌ Error updating train schedule: " + errorText);
      }
    } catch (error) {
      alert("❌ Network error updating train schedule.");
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="add-place-container">
      <h2>Edit Train Schedule</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <label>Train Name:</label>
          <input name="train_name" value={trainData.train_name} onChange={handleInputChange} required />

          <label>From Station:</label>
          <input name="from_station" value={trainData.from_station} onChange={handleInputChange} required />

          <label>To Station:</label>
          <input name="to_station" value={trainData.to_station} onChange={handleInputChange} required />

          <label>Departure Time:</label>
          <input type="time" name="departure_time" value={trainData.departure_time} onChange={handleInputChange} required />

          <label>Arrival Time:</label>
          <input type="time" name="arrival_time" value={trainData.arrival_time} onChange={handleInputChange} required />

          <label>Duration:</label>
          <input type="time" name="duration" value={trainData.duration} onChange={handleInputChange} />
        </div>
        <button type="submit">Update Train Schedule</button>
      </form>
    </div>
  );
};

export default EditTrain;
