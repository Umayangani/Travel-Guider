import React, { useState } from "react";
import "./EditPlace.css";

const EditPlace = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [selectedPlace, setSelectedPlace] = useState(null);
  const [entryFee, setEntryFee] = useState(null);

  const handleSearch = async () => {
    if (!searchQuery) return;
    try {
      const response = await fetch(`http://localhost:8080/api/places`);
      const data = await response.json();
      // Simple client-side filter by name or district
      const filtered = data.filter(
        (p) =>
          p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
          p.district.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setSearchResults(filtered);
    } catch (error) {
      console.error("Error fetching search results:", error);
      alert("❌ Error searching for places.");
    }
  };

  const handleSelectPlace = async (place) => {
    setSelectedPlace(place);
    // Fetch entry fee for this place
    try {
      const res = await fetch(`http://localhost:8080/api/entryfees/${place.placeId}`);
      if (res.ok) {
        const fee = await res.json();
        setEntryFee(fee);
      } else {
        setEntryFee(null);
      }
    } catch {
      setEntryFee(null);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    if ([
      "foreignAdult", "foreignChild", "localAdult", "localChild", "student", "freeEntry"
    ].includes(name)) {
      setEntryFee((prev) => ({
        ...prev,
        [name]: type === "checkbox" ? checked : value,
      }));
    } else {
      setSelectedPlace((prev) => ({
        ...prev,
        [name]: type === "checkbox" ? checked : value,
      }));
    }
  };

  const handleUpdate = async () => {
    if (!selectedPlace) return;
    try {
      // Update place
      const response = await fetch(`http://localhost:8080/api/places/${selectedPlace.placeId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(selectedPlace),
      });
      // Update entry fee if present
      let feeOk = true;
      if (entryFee) {
        const feeRes = await fetch(`http://localhost:8080/api/entryfees/${entryFee.feeId}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(entryFee),
        });
        feeOk = feeRes.ok;
      }
      if (response.ok && feeOk) {
        alert("✅ Place and entry fee updated successfully!");
      } else {
        alert("❌ Error updating place or entry fee");
      }
    } catch (error) {
      console.error("Error updating place or entry fee:", error);
      alert("❌ Network error updating place or entry fee.");
    }
  };

  const handleDelete = async () => {
    if (!selectedPlace) return;
    if (!window.confirm("Are you sure you want to delete this place?")) return;
    try {
      const response = await fetch(`http://localhost:8080/api/places/${selectedPlace.placeId}`, {
        method: "DELETE",
      });
      if (response.ok) {
        alert("✅ Place deleted successfully!");
        setSelectedPlace(null);
        setSearchResults([]);
        setSearchQuery("");
      } else {
        const errorText = await response.text();
        alert("❌ Error deleting place: " + errorText);
      }
    } catch (error) {
      console.error("Error deleting place:", error);
      alert("❌ Network error deleting place.");
    }
  };

  return (
    <div className="edit-place-container">
      <h2>Edit or Delete Place</h2>

      <div className="search-section">
        <input
          placeholder="Search by name or district..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button onClick={handleSearch}>Search</button>
      </div>

      {searchResults.length > 0 && (
        <div className="results-list">
          {searchResults.map((place) => (
            <div
              key={place.id}
              className={`result-item ${selectedPlace?.id === place.id ? "selected" : ""}`}
              onClick={() => handleSelectPlace(place)}
            >
              <strong>{place.name}</strong> – {place.district}
            </div>
          ))}
        </div>
      )}

      {selectedPlace && (
        <div className="form-section">
          <h3>Edit Details</h3>
          <label>Name:</label>
          <input name="name" value={selectedPlace.name} onChange={handleInputChange} />

          <label>District:</label>
          <input name="district" value={selectedPlace.district} onChange={handleInputChange} />

          <label>Description:</label>
          <textarea name="description" value={selectedPlace.description} onChange={handleInputChange} />

          <label>Region:</label>
          <input name="region" value={selectedPlace.region} onChange={handleInputChange} />

          <label>Category:</label>
          <input name="category" value={selectedPlace.category} onChange={handleInputChange} />

          <label>Estimated Time to Visit (hours):</label>
          <input name="estimatedTimeToVisit" type="number" value={selectedPlace.estimatedTimeToVisit || ''} onChange={handleInputChange} />

          <label>Latitude:</label>
          <input name="latitude" type="number" value={selectedPlace.latitude || ''} onChange={handleInputChange} />

          <label>Longitude:</label>
          <input name="longitude" type="number" value={selectedPlace.longitude || ''} onChange={handleInputChange} />

          {entryFee ? (
            <>
              <h4>Entry Fee Details</h4>
              <div className="entry-fee-grid">
                <label>Foreign Adult:</label>
                <input name="foreignAdult" type="number" value={entryFee.foreignAdult || ''} onChange={handleInputChange} />
                <label>Foreign Child:</label>
                <input name="foreignChild" type="number" value={entryFee.foreignChild || ''} onChange={handleInputChange} />
                <label>Local Adult:</label>
                <input name="localAdult" type="number" value={entryFee.localAdult || ''} onChange={handleInputChange} />
                <label>Local Child:</label>
                <input name="localChild" type="number" value={entryFee.localChild || ''} onChange={handleInputChange} />
                <label>Student:</label>
                <input name="student" type="number" value={entryFee.student || ''} onChange={handleInputChange} />
                <label>Free Entry:</label>
                <input name="freeEntry" type="checkbox" checked={entryFee.freeEntry || false} onChange={handleInputChange} />
              </div>
              <div className="location-section">
                <h4>Location Details</h4>
                <label>Latitude:</label>
                <input name="latitude" type="number" value={selectedPlace.latitude || ''} onChange={handleInputChange} />
                <label>Longitude:</label>
                <input name="longitude" type="number" value={selectedPlace.longitude || ''} onChange={handleInputChange} />
              </div>
            </>
          ) : (
            <>
              <div className="no-entry-fee">
                <p style={{ color: 'red' }}>No entry fee found for this place.</p>
                <button
                  onClick={() => setEntryFee({
                    feeId: selectedPlace.placeId + '-FEE',
                    placeId: selectedPlace.placeId,
                    foreignAdult: 0,
                    foreignChild: 0,
                    localAdult: 0,
                    localChild: 0,
                    student: 0,
                    freeEntry: false
                  })}
                  style={{ marginBottom: '1em' }}
                >
                  Add Entry Fee
                </button>
              </div>
            </>
          )}

          <div className="button-group">
            <button className="update-btn" onClick={handleUpdate}>Update Place</button>
            <button className="delete-btn" onClick={handleDelete}>Delete Place</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default EditPlace;
