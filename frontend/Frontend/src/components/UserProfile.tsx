import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./UserProfile.css";
import { useState, useEffect } from "react";
import { useAuth } from "../AuthContext";

interface UserDTO {
  userId: number;
  email: string;
  username: string;
  registrationDate: string;
  lastLogin: string;
  accountStatus: string;
  isAdmin: boolean;
}

interface Profile {
  profileId: number;
  userDTO: UserDTO;
  displayName: string;
  profilePicture: string | null;
  bio: string;
  location: string;
  profileVisibility: string;
  contactType: string;
}

interface ActionLog {
  activityId: number;
  activityType:
  | "LOGIN"
  | "POST_CREATED"
  | "COMMENT"
  | "RSVP"
  | "FORUM_POST"
  | "SURVEY_SUBMIT"
  | "EVENT_CREATED";
  entityType: "POST" | "EVENT" | "THREAD" | "SURVEY" | "COMMENT" | "SYSTEM";
  entityId: number;
  occurredAt: string;
}

interface LoginLog {
  loginId: number;
  loginTimestamp: string;
  loginStatus: "SUCCESS" | "FAILED" | "LOCKOUT";
  ipAddress: string;
}

interface ModerationLog {
  logId: number;
  admin: {
    userId: number;
    username: string;
    email: string;
  };
  contentType: string;
  contentId: number;
  actionType: string;
  reason: string;
  actionDate: string;
}

const UserProfile: React.FC = () => {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [profileExist, setProfileExist] = useState(false);
  const [actionLogs, setActionLogs] = useState<ActionLog[]>([]);
  const [loginLogs, setLoginLogs] = useState<LoginLog[]>([]);
  const [modLogs, setModLogs] = useState<ModerationLog[]>([]);
  const [allUsers, setAllUsers] = useState<UserDTO[]>([]);
  const [activeTab, setActiveTab] = useState<
    "profile" | "history" | "settings" | "moderation" | "users"
  >("profile");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const { isAdmin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    fetchUserData();
    fetchActionLogs();
    fetchLoginLog();
    if (isAdmin) {
      fetchAllUsers();
    }
    if (isAdmin) {
      fetchModerationLogs();
    }
  }, [isAdmin]);

  const getImageUrl = (imagePath: string | null) => {
    if (!imagePath) return null;
    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
      return imagePath;
    }
    return `http://localhost:8080/${imagePath.replace(/^\//, "")}`;
  };

  const fetchUserData = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/profile/user/${localStorage.getItem("userId")}`
      );
      setProfile(response.data);
    } catch (err) {
      setProfileExist(true);
    } finally {
      console.log(profile);
      console.log(profileExist);
    }
  };

  const fetchAllUsers = async () => {
    try {
      const token = localStorage.getItem("authToken");
      const response = await axios.get("http://localhost:8080/auth/getAll", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setAllUsers(response.data);
    } catch (err) {
      console.error("Error fetching users:", err);
    }
  };

  const handleDeleteUser = async (userId: number) => {
    try {
      const token = localStorage.getItem("authToken");
      await axios.delete(`http://localhost:8080/auth/delete/user/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setAllUsers((prevUsers) =>
        prevUsers.filter((user) => user.userId !== userId)
      );
      alert("User deleted successfully");
    } catch (err) {
      console.error("Error deleting user:", err);
      alert("Failed to delete user");
    }
  };

  const fetchActionLogs = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/user/activity/${localStorage.getItem("userId")}`
      );
      setActionLogs(response.data);
    } catch (err) {
      console.error("Error fetching action logs:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchLoginLog = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/auth/history/${localStorage.getItem("userId")}`
      );
      setLoginLogs(response.data);
    } catch (err) {
      console.error("Error fetching login logs:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchModerationLogs = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/modLog/getAll`);
      setModLogs(response.data);
    } catch (err) {
      console.error("Error fetching moderation logs:", err);
    }
  };

  const handleDeleteLogs = async () => {
    // setShowDeleteConfirm(true);
    try {
      const token = localStorage.getItem("authToken");
      await axios.delete(
        `http://localhost:8080/user/delete/${localStorage.getItem("userId")}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setActionLogs([]);
      setShowDeleteConfirm(false);
      alert("Action logs deleted successfully");
    } catch (err) {
      console.error("Error deleting logs:", err);
      alert("Failed to delete logs");
    }
  };

  const handleDeleteLoginLogs = async () => {
    try {
      const token = localStorage.getItem("authToken");
      await axios.delete(
        `http://localhost:8080/auth/delete/history/${localStorage.getItem(
          "userId"
        )}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setLoginLogs([]);
      setShowDeleteConfirm(false);
      alert("Action logs deleted successfully");
    } catch (err) {
      console.error("Error deleting logs:", err);
      alert("Failed to delete logs");
    }
  };

  const handleDeleteSingleLog = async (activityId: number) => {
    try {
      const token = localStorage.getItem("authToken");
      await axios.delete(
        `http://localhost:8080/user/deleteActivity/${activityId}/${localStorage.getItem(
          "userId"
        )}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setActionLogs((prevLogs) =>
        prevLogs.filter((log) => log.activityId !== activityId)
      );
    } catch (err) {
      console.error("Error deleting activity log:", err);
      alert("Failed to delete this log");
    }
  };

  const handlePermanentDelete = async () => {
    if (!profile) return;
    try {
      const token = localStorage.getItem("authToken");
      // 1. Delete profile
      await axios.delete(
        `http://localhost:8080/profile/userDeleted/${profile.profileId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      // 2. Delete user
      await axios.delete(
        `http://localhost:8080/auth/delete/user/${profile.userDTO.userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      // 3. Clean up and redirect
      localStorage.removeItem("authToken");
      localStorage.removeItem("userId");
      localStorage.removeItem("userName");
      navigate("/login");
      alert("Account permanently deleted");
    } catch (err) {
      console.error("Error deleting account:", err);
      alert("Failed to delete account");
    }
  };

  const getActionIcon = (type: string) => {
    switch (type) {
      case "LOGIN":
        return "🔐";
      case "POST_CREATED":
        return "📰";
      case "COMMENT":
        return "💬";
      case "RSVP":
        return "✅";
      case "FORUM_POST":
        return "📣";
      case "SURVEY_SUBMIT":
        return "📊";
      case "EVENT_CREATED":
        return "📅";
      case "SUCCESS":
        return "✅";
      case "FAILED":
        return "❌";
      case "LOCKOUT":
        return "🔒";
      default:
        return "📝";
    }
  };

  if (loading) return <div className="loading">Loading profile...</div>;

  if (error) {
    return (
      <div className="error">
        <h2>Error</h2>
        <p>{error}</p>
      </div>
    );
  }
  if (profileExist) {
    return (
      <div className="no-profile">
        <h2>No Profile Found</h2>
        <p>You haven't created a profile yet.</p>
        <button onClick={() => navigate("/create-profile")}>
          Create Profile
        </button>
      </div>
    );
  }

  if (!profile) return <div>Profile Failed to Load</div>;
  return (
    <div className="user-profile-page">
      <div className="profile-container">
        {/* Sidebar */}
        <div className="profile-sidebar">
          <div className="sidebar-menu">
            <div
              className={`menu-item ${activeTab === "profile" ? "active" : ""}`}
              onClick={() => setActiveTab("profile")}
            >
              👤 Profile
            </div>
            <div
              className={`menu-item ${activeTab === "history" ? "active" : ""}`}
              onClick={() => setActiveTab("history")}
            >
              📋 History
            </div>
            <div
              className={`menu-item ${activeTab === "settings" ? "active" : ""
                }`}
              onClick={() => setActiveTab("settings")}
            >
              ⚙️ Settings
            </div>
            {isAdmin && (
              <div
                className={`menu-item ${activeTab === "moderation" ? "active" : ""}`}
                onClick={() => setActiveTab("moderation")}
              >
                🛡️ Moderation Log
              </div>
            )}
            {isAdmin && (
              <div
                className={`menu-item ${activeTab === "users" ? "active" : ""}`}
                onClick={() => setActiveTab("users")}
              >
                👥 Users
              </div>
            )}
          </div>
        </div>

        {/* Main Content */}
        <div className="profile-content">

          {activeTab === "profile" && (
            <div className="profile-info">
              <div className="profile-header-enhanced">
                <div className="profile-details-left">
                  <div className="profile-name-section">
                    <h1 className="profile-display-name">{profile.displayName}</h1>
                    <p className="profile-username">@{profile.userDTO.username}</p>
                    <div className="profile-status-badge">
                      <span className={`status-indicator ${profile.userDTO.accountStatus.toLowerCase()}`}>
                        {profile.userDTO.accountStatus}
                      </span>
                      {profile.userDTO.isAdmin && (
                        <span className="admin-badge">Admin</span>
                      )}
                    </div>
                  </div>

                  <div className="profile-info-grid">
                    <div className="info-item">
                      <span className="info-label">Email</span>
                      <span className="info-value">{profile.userDTO.email}</span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">Location</span>
                      <span className="info-value">{profile.location || 'Not specified'}</span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">Member Since</span>
                      <span className="info-value">
                        {new Date(profile.userDTO.registrationDate).toLocaleDateString('en-US', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric'
                        })}
                      </span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">Last Active</span>
                      <span className="info-value">
                        {new Date(profile.userDTO.lastLogin).toLocaleDateString('en-US', {
                          year: 'numeric',
                          month: 'short',
                          day: 'numeric'
                        })}
                      </span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">Profile Visibility</span>
                      <span className="info-value">{profile.profileVisibility}</span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">Contact Preference</span>
                      <span className="info-value">{profile.contactType}</span>
                    </div>
                  </div>

                  {profile.bio && (
                    <div className="profile-bio-section">
                      <h3 className="bio-title">About</h3>
                      <p className="bio-text">{profile.bio}</p>
                    </div>
                  )}
                </div>

                <div className="profile-avatar-section">
                  <div className="profile-avatar-enhanced">
                    {profile.profilePicture ? (
                      <img
                        src={
                          getImageUrl(profile.profilePicture) ||
                          `https://via.placeholder.com/200x200?text=${encodeURIComponent(
                            profile.displayName
                          )}`
                        }
                        alt={profile.displayName}
                        className="profile-image"
                        onError={(e) => {
                          const target = e.target as HTMLImageElement;
                          target.onerror = null;
                          target.src = `https://via.placeholder.com/200x200?text=${encodeURIComponent(
                            profile.displayName
                          )}`;
                        }}
                      />
                    ) : (
                      <div className="avatar-placeholder-enhanced">
                        {profile.displayName.charAt(0).toUpperCase()}
                      </div>
                    )}
                  </div>
                </div>
              </div>

              <div className="profile-stats-enhanced">
                <div className="stats-grid">
                  <div className="stat-card-enhanced">
                    <div className="stat-icon">📰</div>
                    <div className="stat-content">
                      <div className="stat-number">
                        {actionLogs.filter(log => log.activityType === "POST_CREATED").length}
                      </div>
                      <div className="stat-label">News Posts</div>
                    </div>
                  </div>
                  <div className="stat-card-enhanced">
                    <div className="stat-icon">📅</div>
                    <div className="stat-content">
                      <div className="stat-number">
                        {actionLogs.filter(log => log.activityType === "EVENT_CREATED").length}
                      </div>
                      <div className="stat-label">Events Created</div>
                    </div>
                  </div>
                  <div className="stat-card-enhanced">
                    <div className="stat-icon">✅</div>
                    <div className="stat-content">
                      <div className="stat-number">
                        {actionLogs.filter(log => log.activityType === "RSVP").length}
                      </div>
                      <div className="stat-label">Events Attended</div>
                    </div>
                  </div>
                  <div className="stat-card-enhanced">
                    <div className="stat-icon">📊</div>
                    <div className="stat-content">
                      <div className="stat-number">
                        {actionLogs.filter(log => log.activityType === "SURVEY_SUBMIT").length}
                      </div>
                      <div className="stat-label">Surveys</div>
                    </div>
                  </div>
                  <div className="stat-card-enhanced">
                    <div className="stat-icon">📣</div>
                    <div className="stat-content">
                      <div className="stat-number">
                        {actionLogs.filter(log => log.activityType === "FORUM_POST").length}
                      </div>
                      <div className="stat-label">Forum Posts</div>
                    </div>
                  </div>
                  <div className="stat-card-enhanced">
                    <div className="stat-icon">💬</div>
                    <div className="stat-content">
                      <div className="stat-number">
                        {actionLogs.filter(log => log.activityType === "COMMENT").length}
                      </div>
                      <div className="stat-label">Comments</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === "history" && (
            <div>
              <div className="action-history">
                <div className="history-header">
                  <h2>Action History</h2>
                  <button
                    className="delete-logs-btn"
                    onClick={() => handleDeleteLogs()}
                    disabled={actionLogs.length === 0}
                  >
                    Clear History
                  </button>
                </div>

                <div className="action-logs">
                  {actionLogs.length === 0 ? (
                    <div className="no-logs">No action history available</div>
                  ) : (
                    actionLogs.map((log) => (
                      <div key={log.activityId} className="log-item">
                        <div className="log-icon">
                          {getActionIcon(log.activityType)}
                        </div>
                        <div className="log-content">
                          <div className="log-action">{log.entityType}</div>
                          <div className="log-details">{log.entityType}</div>
                          <div className="log-timestamp">
                            {new Date(log.occurredAt).toLocaleString()}
                          </div>
                        </div>
                        <div
                          className="log-delete-icon"
                          title="Delete this log"
                          onClick={() => handleDeleteSingleLog(log.activityId)}
                          style={{ cursor: "pointer", marginLeft: "auto" }}
                        >
                          🗑️
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
              <br />
              <div className="action-history">
                <div className="history-header">
                  <h2>Login History</h2>
                  <button
                    className="delete-logs-btn"
                    onClick={() => handleDeleteLoginLogs()}
                    disabled={actionLogs.length === 0}
                  >
                    Clear History
                  </button>
                </div>

                <div className="action-logs">
                  {actionLogs.length === 0 ? (
                    <div className="no-logs">No login history available</div>
                  ) : (
                    loginLogs.map((log) => (
                      <div key={log.loginId} className="log-item">
                        <div className="log-icon">
                          {getActionIcon(log.loginStatus)}
                        </div>
                        <div className="log-content">
                          <div className="log-action">{log.loginStatus}</div>
                          <div className="log-details">{log.ipAddress}</div>
                          <div className="log-timestamp">
                            {new Date(log.loginTimestamp).toLocaleString()}
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>
          )}

          {activeTab === "settings" && (
            <div className="profile-settings">
              <h2>Account Settings</h2>

              <div className="settings-section">
                <h3>Privacy & Security</h3>
                <div className="setting-item">
                  <label>Change Password</label>
                  <Link to="/user/changepassword">
                    <button className="setting-btn">Update Password</button>
                  </Link>
                </div>
              </div>

              <div className="settings-section danger-zone">
                <h3>Danger Zone</h3>
                <div className="setting-item">
                  <label >Permanently Delete Account</label>
                  <p className="warning-text">
                    This action cannot be undone. All your data will be
                    permanently deleted.
                  </p>
                  <button
                    className="setting-btn danger"
                    onClick={handlePermanentDelete}
                  >
                    Delete Account
                  </button>
                </div>
              </div>
            </div>
          )}

          {activeTab === "moderation" && isAdmin && (
            <div className="moderation-log">
              <h2>Moderation Logs</h2>
              {modLogs.length === 0 ? (
                <p>No moderation logs available.</p>
              ) : (
                <table className="mod-log-table">
                  <thead>
                    <tr>
                      <th>Admin</th>
                      <th>Content Type</th>
                      <th>Content ID</th>
                      <th>Action</th>
                      <th>Reason</th>
                      <th>Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {modLogs.map((log) => (
                      <tr key={log.logId}>
                        <td>{log.admin.username}</td>
                        <td>{log.contentType}</td>
                        <td>{log.contentId}</td>
                        <td>{log.actionType}</td>
                        <td>{log.reason}</td>
                        <td>{new Date(log.actionDate).toLocaleString()}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          )}

          {activeTab === "users" && isAdmin && (
            <div className="users-admin-panel">
              <h2>All Registered Users</h2>
              {allUsers.length === 0 ? (
                <p>No users found.</p>
              ) : (
                <table className="users-table">
                  <thead>
                    <tr>
                      <th>User ID</th>
                      <th>Username</th>
                      <th>Email</th>
                      <th>Is Admin</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {allUsers
                      .filter((user) => user.accountStatus !== "DELETED")
                      .map((user) => (
                        <tr key={user.userId}>
                          <td>{user.userId}</td>
                          <td>{user.username}</td>
                          <td>{user.email}</td>
                          <td>{user.isAdmin ? "Yes" : "No"}</td>
                          <td>
                            <button
                              className="danger small"
                              onClick={() => handleDeleteUser(user.userId)}
                            >
                              🗑️ Delete
                            </button>
                          </td>
                        </tr>
                      ))}
                  </tbody>
                </table>
              )}
            </div>
          )}

        </div>
      </div>

      {/* Delete Logs Confirmation Modal */}
      {showDeleteConfirm && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Confirm Delete</h3>
            <p>
              Are you sure you want to delete all action logs? This action
              cannot be undone.
            </p>
            <div className="modal-actions">
              <button
                className="cancel-btn"
                onClick={() => setShowDeleteConfirm(false)}
              >
                Cancel
              </button>
              <button className="confirm-btn danger" onClick={handleDeleteLogs}>
                Delete Logs
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserProfile;