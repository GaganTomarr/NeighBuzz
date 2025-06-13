import React from "react";

import Navbar from "./Navbar";

const Profile: React.FC = () => {
  const userStr = localStorage.getItem("user");

  const user = userStr ? JSON.parse(userStr) : { username: "User" };

  return (
    <>
      <Navbar />

      <div className="profile-container">
        <h1>Profile Page</h1>

        <p>Welcome, {user.username}!</p>

        <p>This is your profile page.</p>
      </div>
    </>
  );
};

export default Profile;
