import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../AuthContext";
import "./ForumThreadsPage.css";

interface User {
  userId: number;
  username: string | null;
  email: string | null;
}

interface Forum {
  forumId: number;
}

interface ForumThread {
  threadId: number;
  title: string;
  description: string;
  createdAt: string;
  updatedAt: string;
  forum: Forum;
  users: User;
  featuredImage?: string | null;
  isLocked: boolean;
}

const apiClient = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

const getImageUrl = (imagePath: string | null | undefined): string | null => {
  if (!imagePath) return null;
  if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
    return imagePath;
  }
  return `http://localhost:8080/${imagePath.replace(/^\//, "")}`;
};

const ForumThreadsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [threads, setThreads] = useState<ForumThread[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const { userId, isAdmin, token } = useAuth();

  useEffect(() => {
    fetchThreads();
  }, [id, userId, isAdmin]);

  const fetchThreads = async () => {
    try {
      setLoading(true);
      const response = await apiClient.get("/threads/getAll");
      const allThreads: ForumThread[] = response.data;

      const filtered = allThreads.filter((thread) => {
        const inForum = thread.forum.forumId === Number(id);
        const isVisibleToEveryone = !thread.isLocked;
        const isCreator = userId && thread.users.userId === userId;
        return inForum && (isVisibleToEveryone || isCreator || isAdmin);
      });

      setThreads(filtered);
      setError(null);
    } catch (err: any) {
      console.error("Error fetching threads:", err);
      setError("Failed to load forum threads. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (threadId: number) => {
    if (!userId) return;

    try {
      await apiClient.put(`/threads/approve/${threadId}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });

      await apiClient.post(`/threads/modLogApprove/${threadId}/${userId}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert("Thread approved and published.");
      fetchThreads();
    } catch (err) {
      console.error("Approval failed:", err);
      alert("Failed to approve thread.");
    }
  };

  const handleReject = async (threadId: number) => {
    if (!userId) return;

    const confirmReject = window.confirm("Rejecting will permanently delete this thread. Continue?");
    if (!confirmReject) return;

    try {
      await apiClient.delete("/threads/delete", {
        params: { tId: threadId },
        headers: { Authorization: `Bearer ${token}` },
      });

      await apiClient.post(`/threads/modLogReject/${threadId}/${userId}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert("Thread rejected and deleted.");
      fetchThreads();
    } catch (err) {
      console.error("Rejection failed:", err);
      alert("Failed to reject thread.");
    }
  };

  const filteredThreads = threads.filter(
    (thread) =>
      thread.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      thread.description.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="forum-threads-page-background">
      <div className="forum-threads-header-container">
        <h1 className="forum-threads-header-title">FORUM THREADS</h1>
        <div className="search-container">
          <input
            type="text"
            placeholder="Search threads..."
            className="search-input"
            onChange={(e) => setSearchQuery(e.target.value)}
            value={searchQuery}
          />
        </div>
        <div className="header-buttons">
          <Link to="/forums" className="back-to-forums">
            &larr; Back to Forums
          </Link>
          <Link to={`/forumThread/create/${id}`} className="create-thread-btn">
            + Create Thread
          </Link>
        </div>
      </div>

      <div className="forum-threads-page-container">
        {loading ? (
          <p className="loading-text">Loading threads...</p>
        ) : error ? (
          <p className="error-message">{error}</p>
        ) : filteredThreads.length > 0 ? (
          <div className="threads-list">
            {filteredThreads.map((thread) => (
              <div key={thread.threadId} className="thread-card">
                {thread.featuredImage && (
                  <img
                    src={getImageUrl(thread.featuredImage) || `https://via.placeholder.com/300x180?text=Thread`}
                    alt={thread.title}
                    className="thread-image"
                    onError={(e) => {
                      const target = e.target as HTMLImageElement;
                      target.onerror = null;
                      target.src = `https://via.placeholder.com/300x180?text=Thread`;
                    }}
                  />
                )}
                
                <div className="thread-content">
                  <h3 className="thread-title">{thread.title}</h3>
                  <p className="thread-description">{thread.description}</p>
                  <p className="thread-meta">
                    Created by <strong>{thread.users.username || "Unknown"}</strong> on{" "}
                    {new Date(thread.createdAt).toLocaleDateString()}
                  </p>
                  {thread.isLocked && (
                    <p className="text-yellow-600 font-semibold">Pending Approval</p>
                  )}
                  <Link
                    to={`/forums/${thread.forum.forumId}/${thread.threadId}`}
                    className="view-thread-details"
                  >
                    View Thread Details
                  </Link>

                  {isAdmin && thread.isLocked && (
                    <div className="admin-actions mt-2">
                      <button
                        className="approve-btn bg-green-500 text-white px-3 py-1 rounded mr-2"
                        onClick={() => handleApprove(thread.threadId)}
                      >
                        Approve
                      </button>
                      <button
                        className="reject-btn bg-red-500 text-white px-3 py-1 rounded"
                        onClick={() => handleReject(thread.threadId)}
                      >
                        Reject
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="no-threads-message">No threads found for this forum.</p>
        )}
      </div>
    </div>
  );
};

export default ForumThreadsPage;
