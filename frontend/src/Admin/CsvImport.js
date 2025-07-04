import React, { useState } from "react";
import "./CsvImport.css";

const CsvImport = () => {
  const [placesFile, setPlacesFile] = useState(null);
  const [entryFeesFile, setEntryFeesFile] = useState(null);
  const [message, setMessage] = useState("");

  const handleFileChange = (e, type) => {
    if (type === "places") setPlacesFile(e.target.files[0]);
    else setEntryFeesFile(e.target.files[0]);
  };

  const handleUpload = async (type) => {
    const file = type === "places" ? placesFile : entryFeesFile;
    if (!file) return setMessage("Please select a file first.");
    const formData = new FormData();
    formData.append("file", file);
    try {
      // Upload file to backend-upload folder (simulate by POST to /api/upload)
      const uploadRes = await fetch(`http://localhost:8080/api/upload`, {
        method: "POST",
        body: formData,
      });
      if (!uploadRes.ok) throw new Error("Upload failed");
      const { path } = await uploadRes.json();
      // Trigger CSV import
      const importRes = await fetch(
        `http://localhost:8080/api/import/${type}?path=${encodeURIComponent(path)}`,
        { method: "POST" }
      );
      if (importRes.ok) setMessage("✅ Import successful!");
      else setMessage("❌ Import failed.");
    } catch (err) {
      setMessage("❌ Error: " + err.message);
    }
  };

  return (
    <div className="csv-import-container">
      <h2>CSV Import</h2>
      <div className="import-section">
        <label>Places CSV:</label>
        <input type="file" accept=".csv" onChange={e => handleFileChange(e, "places")}/>
        <button onClick={() => handleUpload("places")}>Import Places</button>
      </div>
      <div className="import-section">
        <label>Entry Fees CSV:</label>
        <input type="file" accept=".csv" onChange={e => handleFileChange(e, "entryfees")}/>
        <button onClick={() => handleUpload("entryfees")}>Import Entry Fees</button>
      </div>
      {message && <div className="import-message">{message}</div>}
    </div>
  );
};

export default CsvImport;
