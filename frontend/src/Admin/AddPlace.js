import React, { useState } from "react";
import "./AddPlace.css";

const AddPlace = () => {
  const [placeData, setPlaceData] = useState({
    name: "",
    district: "",
    description: "",
    region: "",
    category: "",
    estimated_time_to_visit: "",
    latitude: "",
    longitude: "",
    free_entry: false,
    foreign_adult: "",
    foreign_child: "",
    local_adult: "",
    local_child: "",
    student: "",
  });

  const [images, setImages] = useState([]);
  const [video, setVideo] = useState(null);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setPlaceData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleImageChange = (e) => {
    setImages([...e.target.files].slice(0, 5));
  };

  const handleVideoChange = (e) => {
    setVideo(e.target.files[0]);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // For now just log data to verify form works
    console.log("Place Data:", placeData);
    console.log("Images:", images);
    console.log("Video:", video);

    alert("Form submitted! (Check console for data)");

    // Reset form
    setPlaceData({
      name: "",
      district: "",
      description: "",
      region: "",
      category: "",
      estimated_time_to_visit: "",
      latitude: "",
      longitude: "",
      free_entry: false,
      foreign_adult: "",
      foreign_child: "",
      local_adult: "",
      local_child: "",
      student: "",
    });
    setImages([]);
    setVideo(null);
  };

  return (
    <div className="add-place-container">
      <h2>Add New Place</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <label>Name:</label>
          <input
            name="name"
            value={placeData.name}
            onChange={handleInputChange}
            required
          />

          <label>District:</label>
          <input
            name="district"
            value={placeData.district}
            onChange={handleInputChange}
            required
          />

          <label>Description:</label>
          <textarea
            name="description"
            value={placeData.description}
            onChange={handleInputChange}
            required
          />

          <label>Region:</label>
          <select
            name="region"
            value={placeData.region}
            onChange={handleInputChange}
            required
          >
            <option value="">Select Region</option>
            <option value="North">North</option>
            <option value="South">South</option>
            <option value="East">East</option>
            <option value="West">West</option>
            <option value="Central">Central</option>
          </select>

          <label>Category:</label>
          <select
            name="category"
            value={placeData.category}
            onChange={handleInputChange}
            required
          >
            <option value="">Select Category</option>
            <option value="Historical">Historical</option>
            <option value="Natural">Natural</option>
            <option value="Religious">Religious</option>
            <option value="Other">Other</option>
          </select>

          <label>Estimated Time to Visit (hrs):</label>
          <input
            type="number"
            step="0.1"
            name="estimated_time_to_visit"
            value={placeData.estimated_time_to_visit}
            onChange={handleInputChange}
          />

          <label>Latitude:</label>
          <input
            type="number"
            step="0.000001"
            name="latitude"
            value={placeData.latitude}
            onChange={handleInputChange}
          />

          <label>Longitude:</label>
          <input
            type="number"
            step="0.000001"
            name="longitude"
            value={placeData.longitude}
            onChange={handleInputChange}
          />
        </div>

        <div className="form-section">
          <h3>Entry Fees</h3>
          <label>
            <input
              type="checkbox"
              name="free_entry"
              checked={placeData.free_entry}
              onChange={handleInputChange}
            />
            Free Entry
          </label>

          <label>Foreign Adult:</label>
          <input
            type="number"
            step="0.01"
            name="foreign_adult"
            value={placeData.foreign_adult}
            onChange={handleInputChange}
          />

          <label>Foreign Child:</label>
          <input
            type="number"
            step="0.01"
            name="foreign_child"
            value={placeData.foreign_child}
            onChange={handleInputChange}
          />

          <label>Local Adult:</label>
          <input
            type="number"
            step="0.01"
            name="local_adult"
            value={placeData.local_adult}
            onChange={handleInputChange}
          />

          <label>Local Child:</label>
          <input
            type="number"
            step="0.01"
            name="local_child"
            value={placeData.local_child}
            onChange={handleInputChange}
          />

          <label>Student:</label>
          <input
            type="number"
            step="0.01"
            name="student"
            value={placeData.student}
            onChange={handleInputChange}
          />
        </div>

        <div className="form-section">
          <h3>Media Uploads</h3>
          <label>Upload Images (Max 5):</label>
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={handleImageChange}
          />
          {images.length > 0 && (
            <p>Selected Images: {images.map((file) => file.name).join(", ")}</p>
          )}

          <label>Upload Video:</label>
          <input type="file" accept="video/*" onChange={handleVideoChange} />
          {video && <p>Selected Video: {video.name}</p>}
        </div>

        <button type="submit">Add Place</button>
      </form>
    </div>
  );
};

export default AddPlace;
