
import React, { useState } from "react";
import imageCompression from 'browser-image-compression';
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
  const [imageUrls, setImageUrls] = useState([]); // URLs from backend
  const [video, setVideo] = useState(null);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setPlaceData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };


  // Compress and upload images to backend, then store URLs
  const handleImageChange = async (e) => {
    const files = [...e.target.files].slice(0, 5);
    setImages(files);
    const uploadedUrls = [];
    for (const file of files) {
      try {
        // Compress
        const compressedFile = await imageCompression(file, {
          maxSizeMB: 0.5,
          maxWidthOrHeight: 1024,
          useWebWorker: true,
        });
        // Upload
        const formData = new FormData();
        formData.append('file', compressedFile);
        const res = await fetch('http://localhost:8080/api/upload', {
          method: 'POST',
          body: formData,
        });
        // Defensive: check for empty response
        const text = await res.text();
        if (!text) continue;
        let data;
        try {
          data = JSON.parse(text);
        } catch (err) {
          console.error('Invalid JSON from upload:', text);
          continue;
        }
        if (data.url) uploadedUrls.push(data.url);
      } catch (err) {
        console.error('Image upload failed:', err);
      }
    }
    setImageUrls(uploadedUrls);
  };

  const handleVideoChange = (e) => {
    setVideo(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Split fields for backend
    const place = {
      name: placeData.name,
      district: placeData.district,
      description: placeData.description,
      region: placeData.region,
      category: placeData.category,
      estimatedTimeToVisit: parseFloat(placeData.estimated_time_to_visit),
      latitude: parseFloat(placeData.latitude),
      longitude: parseFloat(placeData.longitude),
    };
    const entryFee = {
      foreignAdult: parseFloat(placeData.foreign_adult),
      foreignChild: parseFloat(placeData.foreign_child),
      localAdult: parseFloat(placeData.local_adult),
      localChild: parseFloat(placeData.local_child),
      student: parseFloat(placeData.student),
      freeEntry: placeData.free_entry,
    };
    try {
      // 1. Create place and entry fee
      const response = await fetch("http://localhost:8080/api/places", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ place, entryFee }),
      });
      if (!response.ok) {
        const errorText = await response.text();
        alert("❌ Error adding place: " + errorText);
        return;
      }
      const savedPlace = await response.json();
      // 2. Save image URLs to place_media
      for (const url of imageUrls) {
        const resp = await fetch("http://localhost:8080/api/place-media", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ placeId: savedPlace.placeId, mediaUrl: url, mediaType: "image" }),
        });
        if (!resp.ok) {
          const errText = await resp.text();
          alert("❌ Failed to save image URL: " + errText);
          console.error("Failed to save image URL:", errText);
        } else {
          const respData = await resp.json();
          console.log("Saved image to place_media:", respData);
        }
      }
      alert("✅ Place added successfully!");
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
      setImageUrls([]);
      setVideo(null);
    } catch (error) {
      console.error("Error submitting form:", error);
      alert("❌ Network error submitting form.");
    }
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
  <optgroup label="Heritage & Culture">
    <option value="Cultural">Cultural</option>
    <option value="Religious">Religious</option>
    <option value="Historical">Historical</option>
    <option value="Festivals & Events">Festivals & Events</option>
  </optgroup>
  <optgroup label="Nature & Outdoors">
    <option value="Nature">Nature</option>
    <option value="Wildlife">Wildlife</option>
    <option value="Adventure">Adventure</option>
    <option value="Camping">Camping</option>
    <option value="Scenic">Scenic</option>
    <option value="Waterfalls">Waterfalls</option>
    <option value="Tea Estates">Tea Estates</option>
    <option value="Lakes & Reservoirs">Lakes & Reservoirs</option>
  </optgroup>
  <optgroup label="Leisure & Urban">
    <option value="Beach">Beach</option>
    <option value="Urban">Urban</option>
    <option value="Agricultural">Agricultural</option>
  </optgroup>
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
            <p>Selected Images: {images.map((f) => f.name).join(", ")}</p>
          )}
          {imageUrls.length > 0 && (
            <ul style={{ color: 'green', fontSize: '0.9em' }}>
              {imageUrls.map((url, i) => (
                <li key={i}>Uploaded: {url}</li>
              ))}
            </ul>
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
