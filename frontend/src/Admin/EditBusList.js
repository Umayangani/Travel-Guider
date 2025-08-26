import React, { useState } from "react";
import { API_BASE_URL } from "../api/config";
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

const EditBusList = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Search buses by routeName, departureLocation, or arrivalLocation
  const handleSearch = async () => {
    if (!searchQuery) return;
    setLoading(true);
    try {
  const response = await fetch(`${API_BASE_URL}/api/bus-schedules`);
      const data = await response.json();
      const filtered = data.filter(
        (b) =>
          b.routeName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
          b.departureLocation?.toLowerCase().includes(searchQuery.toLowerCase()) ||
          b.arrivalLocation?.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setSearchResults(filtered);
    } catch (error) {
      console.error("Error fetching search results:", error);
      alert("❌ Error searching for buses.");
    }
    setLoading(false);
  };

  // Go to edit bus page
  const handleSelectBus = (bus) => {
    navigate(`/admin/edit-bus/${bus.id}`);
  };

  // Delete bus schedule
  const handleDeleteBus = async (bus) => {
    if (!window.confirm(`Are you sure you want to delete ${bus.routeName}?`)) return;
    try {
  const response = await fetch(`${API_BASE_URL}/api/bus-schedules/${bus.id}`, {
        method: "DELETE",
      });
      if (response.ok) {
        setSearchResults((prev) => prev.filter((b) => b.id !== bus.id));
        alert("Bus deleted successfully.");
      } else {
        alert("❌ Error deleting bus.");
      }
    } catch (error) {
      alert("❌ Network error deleting bus.");
    }
  };

  return (
    <div className="add-place-container">
      <h2>Edit Bus Schedule</h2>
      <div className="form-section" style={{display:'flex', flexDirection:'column', alignItems:'flex-start', gap:10}}>
        <input
          type="text"
          placeholder="Search by route, from or to location..."
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
          {searchResults.map((bus) => (
            <div key={bus.id} style={cardStyle} className="bus-card">
              <div>
                <div style={{fontWeight:600, fontSize:'1.1rem'}}>{bus.routeName}</div>
                <div style={{color:'#555', marginTop:2}}>
                  <span style={{fontWeight:500}}>{bus.departureLocation}</span>
                  <span style={{margin:'0 8px', color:'#888'}}>&rarr;</span>
                  <span style={{fontWeight:500}}>{bus.arrivalLocation}</span>
                </div>
                <div style={{fontSize:'0.95rem', color:'#888', marginTop:2}}>
                  Departure: {bus.departureTime?.substring(0,5)} | Arrival: {bus.arrivalTime?.substring(0,5)}
                </div>
                <div style={{fontSize:'0.95rem', color:'#888', marginTop:2}}>
                  Frequency: {bus.frequency} | Duration: {bus.durationMinutes} min
                </div>
              </div>
              <div>
                <button style={editBtn} onClick={() => handleSelectBus(bus)}>Edit</button>
                <button style={deleteBtn} onClick={() => handleDeleteBus(bus)}>Delete</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default EditBusList;
