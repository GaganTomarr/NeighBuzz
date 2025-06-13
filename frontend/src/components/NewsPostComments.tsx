import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import { useAuth } from "../AuthContext";
import "./NewsPostComments.css";
import { Edit, LucideTrash2, Reply } from "lucide-react";
import { confirmAlert } from "react-confirm-alert";
import toast, { Toaster } from "react-hot-toast";

interface User {
  userId: number | null;
  username: string;
}

interface Comment {
  commentId: number;
  commentText: string;
  createdAt: string;
  parentComment?: Comment | null;
  user: User;
  replies: Comment[];
}

const API_BASE = "http://localhost:8080/api/comments";

const isAdmin = localStorage.getItem("isAdmin") === "true";

const NewsPostComments: React.FC = () => {
  const { userId, token } = useAuth();
  const { id } = useParams<{ id: string }>();
  const postId = parseInt(id || "0");

  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState("");
  const [replyText, setReplyText] = useState("");
  const [replyingTo, setReplyingTo] = useState<number | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editText, setEditText] = useState("");

  useEffect(() => {
    if (postId) fetchComments();
  }, [postId]);

  const fetchComments = async () => {
    try {
      const res = await axios.get(`${API_BASE}/get-comment/${postId}`);
      const flatComments: Comment[] = res.data;

      const commentMap: { [key: number]: Comment } = {};
      const rootComments: Comment[] = [];

      flatComments.forEach((comment) => {
        commentMap[comment.commentId] = { ...comment, replies: [] };
      });

      flatComments.forEach((comment) => {
        if (comment.parentComment && comment.parentComment.commentId) {
          const parentId = comment.parentComment.commentId;
          commentMap[parentId]?.replies.push(commentMap[comment.commentId]);
        } else {
          rootComments.push(commentMap[comment.commentId]);
        }
      });

      setComments(rootComments);
    } catch (err) {
      console.error("Failed to fetch comments", err);
    }
  };

  const postComment = async () => {
    if (newComment.trim() === "" || !userId || !token) return;

    try {
      await axios.post(
        `${API_BASE}/post-comment/${userId}/${postId}`,
        { commentText: newComment },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setNewComment("");
      fetchComments();
    } catch (err) {
      console.error("Error posting comment", err);
    }
  };

  const postReply = async (parentId: number) => {
    if (replyText.trim() === "" || !userId || !token) return;

    try {
      await axios.post(
        `${API_BASE}/reply/${parentId}/${userId}/${postId}`,
        { commentText: replyText },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setReplyText("");
      setReplyingTo(null);
      fetchComments();
    } catch (err) {
      console.error("Error replying to comment", err);
    }
  };

  const deleteComment = (commentId: number) => {
    confirmAlert({
      customUI: ({ onClose }) => {
        return (
          <div className="custom-confirm-modal">
            <h2>Confirm Delete</h2>
            <p>Are you sure you want to delete this comment?</p>
            <div className="confirm-buttons">
              <button
                className="btn btn-danger"
                onClick={async () => {
                  try {
                    await axios.delete(`${API_BASE}/delete/${commentId}/${userId}`, {
                      headers: { Authorization: `Bearer ${token}` },
                    });
                    toast.success("Comment deleted successfully");
                    fetchComments();
                  } catch (err) {
                    toast.error("Failed to delete comment");
                    console.error(err);
                  }
                  onClose();
                }}
              >
                Yes
              </button>
              <button className="btn btn-secondary" onClick={onClose}>
                No
              </button>
            </div>
          </div>
        );
      },
    });
  };
  
  

  const updateComment = async (commentId: number) => {
    if (editText.trim() === "" || !userId || !token) return;

    try {
      await axios.put(
        `${API_BASE}/update/${commentId}/${userId}`,
        {
          commentText: editText,
          userDTO: { userId },
        },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setEditingId(null);
      fetchComments();
    } catch (err) {
      console.error("Error updating comment", err);
    }
  };

  const renderComment = (comment: Comment) => {
    const isOwner = comment.user?.userId === userId;
  
    return (
      <div key={comment.commentId} className="comment-card">
        <p className="comment-header">
          <span className="username">{comment.user?.username}</span>
          <span className="timestamp">
            {" "}
            • {new Date(comment.createdAt).toLocaleString()}
          </span>
        </p>
  
        {editingId === comment.commentId ? (
          <div className="input-group">
            <textarea
              value={editText}
              onChange={(e) => setEditText(e.target.value)}
              rows={2}
            />
            <button onClick={() => updateComment(comment.commentId)}>
              Save
            </button>
          </div>
        ) : (
          <>
            <p className="comment-text">{comment.commentText}</p>
            <div className="comment-actions">
              <button
                className="reply-btn"
                onClick={() => setReplyingTo(comment.commentId)}
              >
                <Reply size={16} /> Reply
              </button>
              {isOwner && (
                <button
                  className="edit-btn"
                  onClick={() => {
                    setEditingId(comment.commentId);
                    setEditText(comment.commentText);
                  }}
                >
                  <Edit size={16} /> Edit
                </button>
              )}
              {(isOwner || isAdmin) && (
                <button
                  className="delete-btn"
                  onClick={() => deleteComment(comment.commentId)}
                >
                  <LucideTrash2 size={16} /> Delete
                </button>
              )}
            </div>
          </>
        )}
  
        {replyingTo === comment.commentId && (
          <div className="reply-input">
            <textarea
              value={replyText}
              onChange={(e) => setReplyText(e.target.value)}
              placeholder="Write a reply..."
              rows={3}
            />
            <button
              className="post-cmt"
              onClick={() => postReply(comment.commentId)}
            >
              Post Reply
            </button>
          </div>
        )}
  
        <div className="replies">
          {comment.replies.map((reply) => renderComment(reply))}
        </div>
      </div>
    );
  };
  

  if (!userId || !token || !postId) {
    return (
      <div className="comments-container">
        You must be logged in to view comments.
      </div>
    );
  }

  return (
    <div className="comments-container">
      <h3>Comments</h3>
      <div className="input-group">
        <textarea
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          placeholder="Write your comment..."
          rows={3}
        />
        <button onClick={postComment}>Post Comment</button>
      </div>

      <div className="comment-list">
        {comments.map((comment) => renderComment(comment))}
      </div>
    </div>
  );
};

export default NewsPostComments;
