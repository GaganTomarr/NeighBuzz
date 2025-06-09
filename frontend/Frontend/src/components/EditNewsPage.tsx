import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../AuthContext";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./EditNewsPage.css";

interface User {
  userId: number;
  username: string;
  displayName?: string;
}

interface NewsPost {
  postId: number;
  title: string;
  content: string;
  excerpt?: string;
  author: User;
  featuredImage?: string;
  newsCategory:
    | "EDUCATIONAL"
    | "CRIME"
    | "GEOPOLITICS"
    | "NATIONAL"
    | "SPORTS"
    | "HEALTH"
    | "LIFESTYLE"
    | "EDITORIALS"
    | "ENTERTAINMENT"
    | "TECHNOLOGY"
    | "BUSINESS"
    | "SPIRITUAL"
    | "CULTURE";
  createdAt: string;
  updatedAt: string;
  publishedAt: string;
  isApproved: boolean;
  approvalUser: null;
}

const NEWS_API_BASE_URL = "http://localhost:8080";

const CATEGORY_OPTIONS = [
  "EDUCATIONAL",
  "CRIME",
  "GEOPOLITICS",
  "NATIONAL",
  "SPORTS",
  "HEALTH",
  "LIFESTYLE",
  "EDITORIALS",
  "ENTERTAINMENT",
  "TECHNOLOGY",
  "BUSINESS",
  "SPIRITUAL",
  "CULTURE",
];

const EditNewsPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { userId } = useAuth();
  const [newsItem, setNewsItem] = useState<NewsPost | null>(null);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const token = localStorage.getItem("authToken");

  useEffect(() => {
    const fetchNewsItem = async () => {
      try {
        const response = await axios.get<NewsPost>(
          `${NEWS_API_BASE_URL}/news/post/${id}`
        );
        setNewsItem(response.data);
      } catch (error) {
        setErrorMsg("Error fetching news.");
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchNewsItem();
  }, [id]);

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    if (!newsItem) return;
    const { name, value } = e.target;
    setNewsItem((prev) => (prev ? { ...prev, [name]: value } : null));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newsItem || !userId) {
      setErrorMsg("Missing user or post information");
      return;
    }
    setErrorMsg(null);
    setSuccessMsg(null);

    const form = new FormData();
    form.append("newsDto", JSON.stringify(newsItem));

    try {
      await axios.put(
        `${NEWS_API_BASE_URL}/news/newsPost/update/${userId}`,
        form,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setSuccessMsg("News updated successfully!");
      setTimeout(() => {
        navigate(`/news/${newsItem.postId}`);
      }, 1200);
    } catch (error: any) {
      if (error.response?.status === 403) {
        setErrorMsg("You are not authorized to edit this post.");
      } else {
        setErrorMsg("Failed to update news. Please try again.");
      }
    }
  };

  if (loading)
    return (
      <div className="edit-news-background">
        <div className="edit-news-container">
          <div className="edit-news-loading">
            <i className="bi bi-hourglass-split"></i> Loading...
          </div>
        </div>
      </div>
    );
  if (!newsItem)
    return (
      <div className="edit-news-background">
        <div className="edit-news-container">
          <div className="edit-news-error">
            <i className="bi bi-exclamation-triangle"></i> News not found.
          </div>
        </div>
      </div>
    );

  return (
    <div className="edit-news-background">
      <div className="edit-news-container">
        <div className="edit-news-card">
          <div className="edit-news-header">
            <h1>
              <i className="bi bi-pencil-square"></i>
              Edit News Article
            </h1>
            <p>Update your news article details below</p>
          </div>
          <form onSubmit={handleSubmit} className="edit-news-form">
            <div className="form-group">
              <label htmlFor="title">
                <i className="bi bi-type"></i>
                Title
              </label>
              <input
                type="text"
                name="title"
                id="title"
                value={newsItem.title}
                onChange={handleChange}
                className="form-control"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="excerpt">
                <i className="bi bi-card-text"></i>
                Excerpt
              </label>
              <input
                type="text"
                name="excerpt"
                id="excerpt"
                value={newsItem.excerpt || ""}
                onChange={handleChange}
                className="form-control"
                placeholder="Short summary (optional)"
              />
            </div>

            <div className="form-group">
              <label htmlFor="content">
                <i className="bi bi-file-text"></i>
                Content
              </label>
              <textarea
                name="content"
                id="content"
                value={newsItem.content}
                onChange={handleChange}
                className="form-control"
                required
                rows={10}
              />
            </div>

            <div className="form-group">
              <label htmlFor="featuredImage">
                <i className="bi bi-image"></i>
                Featured Image URL
              </label>
              <input
                type="text"
                name="featuredImage"
                id="featuredImage"
                value={newsItem.featuredImage || ""}
                onChange={handleChange}
                className="form-control"
                placeholder="Paste image URL"
              />
            </div>

            <div className="form-group">
              <label htmlFor="newsCategory">
                <i className="bi bi-tag"></i>
                Category
              </label>
              <select
                name="newsCategory"
                id="newsCategory"
                value={newsItem.newsCategory}
                onChange={handleChange}
                className="form-control"
                required
              >
                {CATEGORY_OPTIONS.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat.charAt(0) + cat.slice(1).toLowerCase()}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary">
                <i className="bi bi-save"></i>
                Save Changes
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navigate(`/news/${newsItem.postId}`)}
              >
                <i className="bi bi-x-circle"></i>
                Cancel
              </button>
            </div>
            {errorMsg && (
              <div className="error-message">
                <i className="bi bi-exclamation-triangle"></i> {errorMsg}
              </div>
            )}
            {successMsg && (
              <div className="success-message">
                <i className="bi bi-check-circle"></i> {successMsg}
              </div>
            )}
          </form>
        </div>
      </div>
    </div>
  );
};

export default EditNewsPage;
