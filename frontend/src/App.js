import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Sidebar from "./Admin/Sidebar";
import AddPlace from "./Admin/AddPlace";
import Dashboard from "./Admin/Dashboard"; // Dummy dashboard component


function App() {
  return (
    <Router>
      <div style={{ display: "flex" }}>
        <Sidebar adminName="Admin" />
        {/* Add marginLeft equal to sidebar width (240px) */}
        <div style={{ flex: 1, padding: "20px", marginLeft: "240px" }}>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/add-place" element={<AddPlace />} />
            {/* Add other routes here */}
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
