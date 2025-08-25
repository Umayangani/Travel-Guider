import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AddPlace.css";

const cardStyle = {
  background: "#fff",
  borderRadius: "10px",
  boxShadow: "0 2px 8px rgba(0,0,0,0.08)",
  padding: "18px 24px",
  margin: "16px 0",
  display: "flex",
  alignItems: "center",
  justifyContent: "space-between",
  transition: "box-shadow 0.2s",
};
const btnStyle = {
  marginLeft: 8,
  padding: "6px 16px",
  border: "none",
  borderRadius: "5px",
  cursor: "pointer",
  fontWeight: 600,
  fontSize: "1rem",
  transition: "background 0.2s, color 0.2s",
};
const editBtn = {
  ...btnStyle,
  background: "#1976d2",
  color: "#fff",
};
const deleteBtn = {
  ...btnStyle,
  background: "#e53935",
  color: "#fff",
};

const EditTrainList = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSearch = async () => {
    if (!searchQuery) return;
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/train-schedules`);
      const data = await response.json();
      const filtered = data.filter(
        (t) =>
          t.trainName.toLowerCase().includes(searchQuery.toLowerCase()) ||
          t.fromStation.toLowerCase().includes(searchQuery.toLowerCase()) ||
          t.toStation.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setSearchResults(filtered);
    } catch (error) {
      console.error("Error fetching search results:", error);
      alert("❌ Error searching for trains.");
    }
    setLoading(false);
  };

  const handleSelectTrain = (train) => {
    navigate(`/admin/edit-train/${train.scheduleId}`);
  };

  const handleDeleteTrain = async (train) => {
    if (!window.confirm(`Are you sure you want to delete ${train.trainName}?`)) return;
    try {
      const response = await fetch(`http://localhost:8080/api/train-schedules/${train.scheduleId}`, {
        method: "DELETE",
      });
      if (response.ok) {
        setSearchResults((prev) => prev.filter((t) => t.scheduleId !== train.scheduleId));
        alert("Train deleted successfully.");
      } else {
        alert("❌ Error deleting train.");
      }
    } catch (error) {
      alert("❌ Network error deleting train.");
    }
  };

  return (
    <div className="add-place-container">
      <h2>Edit Train Schedule</h2>
      <div className="form-section" style={{display:'flex', flexDirection:'column', alignItems:'flex-start', gap:10}}>
        <input
          type="text"
          placeholder="Search by train name, from or to station..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          style={{minWidth:220, padding:6, borderRadius:5, border:'1px solid #bbb'}}
        />
        <div style={{width:'100%', display:'flex', justifyContent:'center'}}>
          <button style={{...editBtn, padding:'6px 20px', marginTop:4}} onClick={handleSearch} disabled={loading}>
            {loading ? "Searching..." : "Search"}
          </button>
        </div>
      </div>
      {searchResults.length > 0 && (
        <div className="search-results">
          {searchResults.map((train) => (
            <div key={train.scheduleId} style={cardStyle} className="train-card">
              <div>
                <div style={{fontWeight:600, fontSize:'1.1rem'}}>{train.trainName}</div>
                <div style={{color:'#555', marginTop:2}}>
                  <span style={{fontWeight:500}}>{train.fromStation}</span>
                  <span style={{margin:'0 8px', color:'#888'}}>&rarr;</span>
                  <span style={{fontWeight:500}}>{train.toStation}</span>
                </div>
                <div style={{fontSize:'0.95rem', color:'#888', marginTop:2}}>
                  Departure: {train.departureTime?.substring(0,5)} | Arrival: {train.arrivalTime?.substring(0,5)}
                </div>
              </div>
              <div>
                <button style={editBtn} onClick={() => handleSelectTrain(train)}>Edit</button>
                <button style={deleteBtn} onClick={() => handleDeleteTrain(train)}>Delete</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default EditTrainList;
