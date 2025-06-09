import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import { useAuth } from "../AuthContext";
import { Edit, LucideTrash2, Reply } from "lucide-react";
import toast from "react-hot-toast";

interface User {
  userId: number | null;
  username: string;
}

interface ForumThreadComment {
  ftCommentId: number;
  commentText: string;
  createdAt: string;
  parentComment?: { ftCommentId: number } | null;
  user?: User | null;
  users?: User | null;
  thread?: { threadId: number };
  replies: ForumThreadComment[];
}

const API_BASE = "http://localhost:8080/api/comments/forum";

const isAdmin = localStorage.getItem("isAdmin") === "true";

const ForumThreadComments: React.FC = () => {
  const { userId, token } = useAuth();
  const { threadId: threadIdParam } = useParams<{ threadId: string }>();
  const threadId = parseInt(threadIdParam || "0");

  const [comments, setComments] = useState<ForumThreadComment[]>([]);
  const [newComment, setNewComment] = useState("");
  const [replyText, setReplyText] = useState("");
  const [replyingTo, setReplyingTo] = useState<number | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editText, setEditText] = useState("");

  useEffect(() => {
    if (threadId) fetchComments();
  }, [threadId]);

  const fetchComments = async () => {
    try {
      const res = await axios.get(`${API_BASE}/get/comment`);
      const allComments: ForumThreadComment[] = res.data;

      const filteredComments = allComments.filter(
        (comment) => comment.thread?.threadId === threadId
      );

      const commentMap: { [key: number]: ForumThreadComment } = {};
      const rootComments: ForumThreadComment[] = [];

      filteredComments.forEach((comment) => {
        comment.replies = [];
        commentMap[comment.ftCommentId] = comment;
      });

      filteredComments.forEach((comment) => {
        if (
          comment.parentComment &&
          comment.parentComment.ftCommentId &&
          commentMap[comment.parentComment.ftCommentId]
        ) {
          commentMap[comment.parentComment.ftCommentId].replies.push(comment);
        } else {
          rootComments.push(comment);
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
        `${API_BASE}/post/comment/${userId}/${threadId}`,
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
        `${API_BASE}/post/reply/${parentId}/${userId}/${threadId}`,
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

  const deleteComment = async (commentId: number) => {
    if (!token) return;

    try {
      await axios.delete(`${API_BASE}/delete/comment/${commentId}/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success("Comment deleted successfully");
      // fetchComments();
      const removeCommentById = (
        comments: ForumThreadComment[],
        idToRemove: number
      ): ForumThreadComment[] => {
        return comments
          .filter((comment) => comment.ftCommentId !== idToRemove)
          .map((comment) => ({
            ...comment,
            replies: removeCommentById(comment.replies, idToRemove),
          }));
      };
  
      setComments((prevComments) => removeCommentById(prevComments, commentId));
    } catch (err) {
      toast.error("Error deleting comment");
    }
  };

  const updateComment = async (commentId: number) => {
    if (!userId || !token) return;

    console.log("Updating comment with text:", editText);
    console.log("User ID:", userId);
    console.log("Token present:", !!token);

    try {
      await axios.put(
        `${API_BASE}/update/${commentId}/${userId}`,
        {
          commentText: editText,
          user: { userId },
        },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setEditingId(null);
      setEditText("");
      fetchComments();
    } catch (err: any) {
      console.error("Error updating comment:", err.response?.data || err.message);
    }
  };

  const renderComment = (comment: ForumThreadComment) => {
    const currentUser = comment.user || comment.users;
    const isOwner = currentUser?.userId === userId;

    return (
      <div key={comment.ftCommentId} className="comment-card">
        <p className="comment-header">
          <span className="username">{currentUser?.username || "Unknown User"}</span>
          <span className="timestamp"> • {new Date(comment.createdAt).toLocaleString()}</span>
        </p>

        {editingId === comment.ftCommentId ? (
          <div className="input-group">
            <textarea
              value={editText}
              onChange={(e) => setEditText(e.target.value)}
              rows={2}
            />
            <button onClick={() => updateComment(comment.ftCommentId)}>Save</button>
          </div>
        ) : (
          <>
            <p className="comment-text">{comment.commentText}</p>
            <div className="comment-actions">
              <button
                className="reply-btn"
                onClick={() => setReplyingTo(comment.ftCommentId)}
              >
                <Reply size={16} /> Reply
              </button>
              {isOwner && (
                <>
                  <button
                    className="edit-btn"
                    onClick={() => {
                      setEditingId(comment.ftCommentId);
                      setEditText(comment.commentText);
                    }}
                  >
                    <Edit size={16} /> Edit
                  </button>
                </>
              )}
              {(isOwner || isAdmin) && (
                <>
                  <button
                    className="delete-btn"
                    onClick={() => deleteComment(comment.ftCommentId)}
                  >
                    <LucideTrash2 size={16} /> Delete
                  </button>
                </>
              )}
            </div>
          </>
        )}

        {replyingTo === comment.ftCommentId && (
          <div className="reply-input">
            <textarea
              value={replyText}
              onChange={(e) => setReplyText(e.target.value)}
              placeholder="Write a reply..."
              rows={3}
            />
            <button className="post-cmt" onClick={() => postReply(comment.ftCommentId)}>
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

  if (!userId || !token || !threadId) {
    return <div className="comments-container">You must be logged in to view Threads Discussions...</div>;
  }

  return (
    <div className="comments-container">
      <h3>Forum Threads Discussions ...</h3>
      <div className="input-group">
        <textarea
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          placeholder="Write your Thoughts..."
          rows={3}
        />
        <button onClick={postComment}>Post Thread discussion ...</button>
      </div>

      <div className="comment-list">{comments.map((comment) => renderComment(comment))}</div>
    </div>
  );
};

export default ForumThreadComments;
