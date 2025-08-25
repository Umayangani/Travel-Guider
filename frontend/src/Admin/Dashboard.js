import React from "react";

const Dashboard = () => {
  return (
    <div style={{ padding: "20px" }}>
      <h1>Dashboard</h1>
      <p>Welcome to the Admin Dashboard.</p>

      <div style={{ marginTop: "30px" }}>
        <h2>Statistics</h2>
        <ul>
          <li>Total Places: 15</li>
          <li>Total Users: 120</li>
          <li>Total Bookings: 340</li>
          <li>New Messages: 7</li>
        </ul>
      </div>
    </div>
  );
};

export default Dashboard;
