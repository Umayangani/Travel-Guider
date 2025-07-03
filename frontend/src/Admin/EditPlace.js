import React, { useState } from "react";
import "./EditPlace.css";

const EditPlace = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [selectedPlace, setSelectedPlace] = useState(null);

  const handleSearch = async () => {
    if (!searchQuery) return;

    try {
      const response = await fetch(`http://localhost:8080/api/places/search?query=${searchQuery}`);
      const data = await response.json();
      setSearchResults(data);
    } catch (error) {
      console.error("Error fetching search results:", error);
      alert("❌ Error searching for places.");
    }
  };

  const handleSelectPlace = (place) => {
    setSelectedPlace(place);
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setSelectedPlace((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleUpdate = async () => {
    if (!selectedPlace) return;

    try {
      const response = await fetch(`http://localhost:8080/api/places/${selectedPlace.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(selectedPlace),
      });

      if (response.ok) {
        alert("✅ Place updated successfully!");
      } else {
        const errorText = await response.text();
        alert("❌ Error updating place: " + errorText);
      }
    } catch (error) {
      console.error("Error updating place:", error);
      alert("❌ Network error updating place.");
    }
  };

  const handleDelete = async () => {
    if (!selectedPlace) return;

    if (!window.confirm("Are you sure you want to delete this place?")) return;

    try {
      const response = await fetch(`http://localhost:8080/api/places/${selectedPlace.id}`, {
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

          <label>Free Entry:</label>
          <input
            type="checkbox"
            name="free_entry"
            checked={selectedPlace.free_entry}
            onChange={handleInputChange}
          />

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
