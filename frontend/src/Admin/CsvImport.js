import React, { useState, useRef } from 'react';
import { API_BASE_URL } from "../api/config";
import './CsvImport.css';

const CsvImport = () => {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [csvStatus, setCsvStatus] = useState(null);
  const fileInputRef = useRef(null);

  // Check CSV status on component mount
  React.useEffect(() => {
    checkCsvStatus();
  }, []);

  const checkCsvStatus = async () => {
    try {
  const response = await fetch(`${API_BASE_URL}/api/csv/status`);
      if (response.ok) {
        const status = await response.json();
        setCsvStatus(status);
      }
    } catch (error) {
      console.error('Error checking CSV status:', error);
    }
  };

  const handleFileChange = (event) => {
    const selectedFile = event.target.files[0];
    if (selectedFile) {
      if (!selectedFile.name.toLowerCase().endsWith('.csv')) {
        setError('Please select a CSV file');
        setFile(null);
        return;
      }
      setFile(selectedFile);
      setError('');
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Please select a CSV file first');
      return;
    }

    setLoading(true);
    setMessage('');
    setError('');

    try {
      const formData = new FormData();
      formData.append('file', file);

  const response = await fetch(`${API_BASE_URL}/api/csv/upload`, {
        method: 'POST',
        body: formData,
      });

      const result = await response.json();

      if (response.ok) {
        setMessage(result.message);
        checkCsvStatus(); // Refresh status
      } else {
        setError(result.error || 'Upload failed');
      }
    } catch (error) {
      setError('Network error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleImport = async () => {
    setLoading(true);
    setMessage('');
    setError('');

    try {
      const response = await fetch(`${API_BASE_URL}/api/csv/import`, {
        method: 'POST',
      });

      const result = await response.json();

      if (response.ok) {
        setMessage(result.message);
        checkCsvStatus(); // Refresh status
      } else {
        setError(result.error || 'Import failed');
      }
    } catch (error) {
      setError('Network error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleUploadAndImport = async () => {
    if (!file) {
      setError('Please select a CSV file first');
      return;
    }

    setLoading(true);
    setMessage('');
    setError('');

    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch(`${API_BASE_URL}/api/csv/upload-and-import`, {
        method: 'POST',
        body: formData,
        headers: {
          'Accept': 'application/json',
        },
      });

      // Try to parse response as JSON
      let result;
      try {
        const text = await response.text();
        result = JSON.parse(text);
      } catch (e) {
        console.error('Failed to parse response:', e);
        throw new Error('Invalid response from server');
      }

      if (response.ok) {
        setMessage(result.message);
        setFile(null);
        if (fileInputRef.current) {
          fileInputRef.current.value = '';
        }
        checkCsvStatus(); // Refresh status
      } else {
        // Extract error message from response
        const errorMessage = result.error || result.message || 'Upload and import failed';
        console.error('Upload failed:', errorMessage);
        setError(errorMessage);
      }
    } catch (error) {
      console.error('Network error:', error);
      setError('Network error: Please check your connection and try again');
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async () => {
    setLoading(true);
    setMessage('');
    setError('');

    try {
      const response = await fetch(`${API_BASE_URL}/api/csv/export`, {
        method: 'POST',
      });

      const result = await response.json();

      if (response.ok) {
        setMessage(result.message);
        checkCsvStatus(); // Refresh status
      } else {
        setError(result.error || 'Export failed');
      }
    } catch (error) {
      setError('Network error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/csv/download`);
      
      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = 'places.csv';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        setMessage('CSV file downloaded successfully');
      } else {
        setError('Download failed');
      }
    } catch (error) {
      setError('Network error: ' + error.message);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete the CSV file?')) {
      return;
    }

    setLoading(true);
    setMessage('');
    setError('');

    try {
      const response = await fetch(`${API_BASE_URL}/api/csv/delete`, {
        method: 'DELETE',
      });

      const result = await response.json();

      if (response.ok) {
        setMessage(result.message);
        checkCsvStatus(); // Refresh status
      } else {
        setError(result.error || 'Delete failed');
      }
    } catch (error) {
      setError('Network error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="csv-import-container">
      <h2>CSV Import/Export Manager</h2>
      
      {/* CSV Status */}
      {csvStatus && (
        <div className="csv-status">
          <h3>Current Status</h3>
          <div className={`status-info ${csvStatus.exists ? 'exists' : 'not-exists'}`}>
            <p><strong>CSV File:</strong> {csvStatus.exists ? 'Available' : 'Not Found'}</p>
            {csvStatus.info && <p><strong>Info:</strong> {csvStatus.info}</p>}
          </div>
        </div>
      )}

      {/* File Upload Section */}
      <div className="upload-section">
        <h3>Upload CSV File</h3>
        <div className="file-input-group">
          <input
            ref={fileInputRef}
            type="file"
            accept=".csv"
            onChange={handleFileChange}
            className="file-input"
            disabled={loading}
          />
          {file && (
            <div className="file-info">
              <span>Selected: {file.name} ({(file.size / 1024).toFixed(2)} KB)</span>
            </div>
          )}
        </div>
        
        <div className="button-group">
          <button
            onClick={handleUpload}
            disabled={!file || loading}
            className="btn btn-secondary"
          >
            {loading ? 'Uploading...' : 'Upload Only'}
          </button>
          
          <button
            onClick={handleUploadAndImport}
            disabled={!file || loading}
            className="btn btn-primary"
          >
            {loading ? 'Processing...' : 'Upload & Import to DB'}
          </button>
        </div>
      </div>

      {/* Import Section */}
      {csvStatus?.exists && (
        <div className="import-section">
          <h3>Import to Database</h3>
          <p>Import places from the uploaded CSV file to the database.</p>
          <button
            onClick={handleImport}
            disabled={loading}
            className="btn btn-success"
          >
            {loading ? 'Importing...' : 'Import to Database'}
          </button>
        </div>
      )}

      {/* Export Section */}
      <div className="export-section">
        <h3>Export from Database</h3>
        <p>Export all places from the database to CSV file.</p>
        <div className="button-group">
          <button
            onClick={handleExport}
            disabled={loading}
            className="btn btn-info"
          >
            {loading ? 'Exporting...' : 'Export to CSV'}
          </button>
          
          {csvStatus?.exists && (
            <button
              onClick={handleDownload}
              disabled={loading}
              className="btn btn-download"
            >
              Download CSV
            </button>
          )}
        </div>
      </div>

      {/* Management Section */}
      {csvStatus?.exists && (
        <div className="management-section">
          <h3>File Management</h3>
          <button
            onClick={handleDelete}
            disabled={loading}
            className="btn btn-danger"
          >
            {loading ? 'Deleting...' : 'Delete CSV File'}
          </button>
        </div>
      )}

      {/* Messages */}
      {message && (
        <div className="alert alert-success">
          {message}
        </div>
      )}
      
      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      {/* Instructions */}
      <div className="instructions">
        <h3>Instructions</h3>
        <ul>
          <li><strong>CSV Format:</strong> place_id, name, district, description, region, category, estimated_time_to_visit, latitude, longitude</li>
          <li><strong>Upload Only:</strong> Saves the CSV file to backend uploads folder</li>
          <li><strong>Upload & Import:</strong> Uploads and immediately imports data to database</li>
          <li><strong>Import:</strong> Imports data from existing uploaded CSV file</li>
          <li><strong>Export:</strong> Exports all database records to CSV file</li>
        </ul>
      </div>
    </div>
  );
};

export default CsvImport;
