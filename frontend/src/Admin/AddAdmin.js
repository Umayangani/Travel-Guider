import React, { useState } from "react";
import "./AddPlace.css";

const AddAdmin = () => {
  const [adminData, setAdminData] = useState({
    name: "",
    email: "",
    password: "",
    dob: "",
    role: "ADMIN",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setAdminData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    try {
      const response = await fetch("http://localhost:8080/api/admin/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(adminData),
      });

      if (response.ok) {
        alert("✅ Admin created successfully!");
        setAdminData({ name: "", email: "", password: "", dob: "", role: "ADMIN" });
      } else {
        alert("❌ Failed to create admin");
      }
    } catch (error) {
      alert("❌ Network error");
    }
  };

  return (
    <div className="add-place-container">
      <h2>Add Admin User</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <label>Name:</label>
          <input name="name" value={adminData.name} onChange={handleInputChange} required />
          <label>Email:</label>
          <input type="email" name="email" value={adminData.email} onChange={handleInputChange} required />
          <label>Password:</label>
          <input type="password" name="password" value={adminData.password} onChange={handleInputChange} required />
          <label>Date of Birth:</label>
          <input type="date" name="dob" value={adminData.dob} onChange={handleInputChange} />
          <label>Role:</label>
          <select name="role" value={adminData.role} onChange={handleInputChange}>
            <option value="ADMIN">Admin</option>
            <option value="USER">User</option>
          </select>
        </div>
        <button type="submit">Create Admin User</button>
      </form>
    </div>
  );
};

export default AddAdmin;
