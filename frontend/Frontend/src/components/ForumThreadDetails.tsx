import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ForumThreadDetails.css';
import ForumThreadComments from './ForumThreadComment';
import { useAuth } from '../AuthContext';

interface User {
  userId: number;
  username: string;
  email: string;
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
}

const getImageUrl = (imagePath: string | null | undefined): string | null => {
  if (!imagePath) return null;
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }
  return `http://localhost:8080/${imagePath.replace(/^\//, '')}`;
};

const ForumThreadDetails: React.FC = () => {
  const { threadId } = useParams<{ threadId: string }>();
  const [thread, setThread] = useState<ForumThread | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { userId, token, isAdmin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchThread = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/threads/getAll`);
        const threadData = res.data.find((t: ForumThread) => t.threadId === Number(threadId));
        if (!threadData) {
          setError('Thread not found.');
        } else {
          setThread(threadData);
        }
      } catch (err) {
        console.error('Error fetching thread:', err);
        setError('Failed to load thread.');
      } finally {
        setLoading(false);
      }
    };

    fetchThread();
  }, [threadId]);

  const handleDelete = async () => {
    if (!token || !thread) return;

    const confirm = window.confirm("Are you sure you want to delete this thread?");
    if (!confirm) return;

    try {
      await axios.delete(`http://localhost:8080/threads/delete`, {
        params: { tId: thread.threadId },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      navigate(`/forums/${thread.forum.forumId}`);
    } catch (err) {
      console.error("Error deleting thread:", err);
      alert("Failed to delete the thread.");
    }
  };

  if (loading) return <div className="thread-details-container">Loading...</div>;
  if (error) return <div className="thread-details-container error">{error}</div>;


  return (
    <>
      <div className="thread-details-container">
        <Link to={-1 as any} className="back-button">&larr; Back</Link>
        {thread && (
          <div className="thread-detail-card">
            {thread.featuredImage && (
              <img
                className="thread-detail-image"
                src={getImageUrl(thread.featuredImage) || ''}
                alt={thread.title}
                onError={(e) => {
                  const target = e.target as HTMLImageElement;
                  target.onerror = null;
                  target.src = `https://via.placeholder.com/600x300?text=No+Image`;
                }}
              />
            )}

            <h1 className="thread-title">{thread.title}</h1>
            <p className="thread-full-description">{thread.description}</p>

            <div className="thread-meta">
              <p><strong>Created by:</strong> {thread.users.username}</p>
              <p><strong>Created on:</strong> {new Date(thread.createdAt).toLocaleString()}</p>
              <p><strong>Last updated:</strong> {new Date(thread.updatedAt).toLocaleString()}</p>
            </div>

            {(thread.users.userId === userId || isAdmin === true) && (
              <div className="thread-actions">
                <button
                  className="edit-btn"
                  onClick={() => navigate(`/forumThread/edit/${thread.threadId}`)}
                >
                  Edit
                </button>

                <button
                  className="delete-btn"
                  onClick={handleDelete}
                >
                  Delete
                </button>
              </div>
            )}
          </div>
        )}
      </div>
      <ForumThreadComments />
    </>
  );
};

export default ForumThreadDetails;
