import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../AuthContext";
import "./NewsDetailPage.css";
import NewsPostComments from "./NewsPostComments";
import { confirmAlert } from 'react-confirm-alert';
import 'react-confirm-alert/src/react-confirm-alert.css';
import toast from "react-hot-toast";

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
  publishedAt: string;
  featuredImage?: string;
  newsCategory: "EDUCATIONAL" | "CRIME" | "GENERAL";
  source?: string;
}

const NEWS_API_BASE_URL = "http://localhost:8080";

const NewsDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [newsItem, setNewsItem] = useState<NewsPost | null>(null);
  const [relatedNews, setRelatedNews] = useState<NewsPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { isAuthenticated, userId, username, isAdmin } = useAuth();

  const getImageUrl = (imagePath?: string | null): string | null => {
    if (!imagePath) return null;
    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
      return imagePath;
    }
    return `${NEWS_API_BASE_URL}/${imagePath.replace(/^\//, "")}`;
  };

  useEffect(() => {
    if (!id) return;
    const fetchNewsDetail = async () => {
      try {
        const response = await axios.get<NewsPost>(
          `${NEWS_API_BASE_URL}/news/post/${id}`
        );
        setNewsItem(response.data);
        setLoading(false);
      } catch (err) {
        console.error("Failed to fetch news item:", err);
        setError("Article not found or failed to load.");
        setLoading(false);
      }
    };
    fetchNewsDetail();
  }, [id]);

  useEffect(() => {
    if (!newsItem) return;
    const fetchRelatedNews = async () => {
      try {
        const response = await axios.get<NewsPost[]>(
          `${NEWS_API_BASE_URL}/news/newsPost/${newsItem.newsCategory}`
        );
        const filtered = response.data.filter(
          (post) => post.postId !== newsItem.postId
        );
        setRelatedNews(filtered.slice(0, 5)); // Limit to 5 related articles
      } catch (err) {
        console.error("Failed to fetch related news:", err);
      }
    };
    fetchRelatedNews();
  }, [newsItem]);

  const handleDelete = () => {
    if (!newsItem || !userId) return;

    confirmAlert({
      title: "Confirm Delete",
      message: "Are you sure you want to delete this news post?",
      buttons: [
        {
          label: "Yes",
          onClick: async () => {
            try {
              await axios.delete(
                `${NEWS_API_BASE_URL}/news/newsPost/delete/${newsItem.postId}`,
                {
                  headers: {
                    Authorization: `Bearer ${localStorage.getItem("authToken")}`,
                  },
                }
              );
              toast.success('News post deleted successfully');
              navigate("/news");
            } catch (error) {
              console.error("Failed to delete the news post:", error);
              toast.error("Failed to delete the news post.");
            }
          },
        },
        {
          label: "No",
          onClick: () => {},
        },
      ],
    });
  };

  if (loading)
    return (
      <div className="news-detail-background">
        <div className="news-detail-container">
          <div className="news-detail-loading">
            <i className="bi bi-hourglass-split"></i>
            Loading article...
          </div>
        </div>
      </div>
    );

  if (error)
    return (
      <div className="news-detail-background">
        <div className="news-detail-container">
          <div className="news-detail-error">
            <i className="bi bi-exclamation-triangle"></i>
            {error}
          </div>
        </div>
      </div>
    );

  if (!newsItem)
    return (
      <div className="news-detail-background">
        <div className="news-detail-container">
          <div className="news-detail-error">
            <i className="bi bi-search"></i>
            Article not found.
          </div>
        </div>
      </div>
    );

  return (
    <div className="news-detail-background">
      <div className="news-detail-container">
        {/* === Main News Section === */}
        <div className="news-detail-card">
          <Link to="/news" className="back-link">
            <i className="bi bi-arrow-left"></i> Back to News
          </Link>

          <div className="news-detail-header">
            <h1 className="news-detail-title">{newsItem.title}</h1>
            <div className="news-detail-meta">
              <span className="news-detail-category">
                {newsItem.newsCategory.charAt(0) +
                  newsItem.newsCategory.slice(1).toLowerCase()}
              </span>
              <span className="news-detail-date">
                <i className="bi bi-calendar3"></i>
                {new Date(newsItem.publishedAt).toLocaleDateString()}
              </span>
              <span className="news-detail-author">
                <i className="bi bi-person"></i>
                {newsItem.author?.username || "Unknown Author"}
              </span>
              {newsItem.source && (
                <span className="news-detail-source">
                  <i className="bi bi-link-45deg"></i>
                  {newsItem.source}
                </span>
              )}
            </div>
          </div>

          {/* Featured Image */}
          {newsItem.featuredImage && (
            <div className="news-detail-image-container">
              <img
                src={
                  getImageUrl(newsItem.featuredImage) ||
                  "https://via.placeholder.com/800x400?text=Image+Not+Available"
                }
                alt={newsItem.title}
                className="news-detail-image"
              />
            </div>
          )}

          {/* Excerpt */}
          {newsItem.excerpt && (
            <div className="news-detail-summary">
              <strong>{newsItem.excerpt}</strong>
            </div>
          )}

          {/* Content */}
          <div className="news-detail-content">
            {newsItem.content.split("\n\n").map((paragraph, index) => (
              <p key={index}>{paragraph}</p>
            ))}
          </div>

          {/* Admin Edit/Delete Actions */}
          {isAuthenticated && (newsItem.author?.userId === userId || isAdmin === true) && (
            <div className="news-detail-admin-actions">
              <Link
                to={`/news/edit/${newsItem.postId}`}
                className="btn btn-secondary"
              >
                <i className="bi bi-pencil"></i> Edit Article
              </Link>
              <button onClick={handleDelete} className="btn btn-danger">
                <i className="bi bi-trash"></i> Delete Article
              </button>
            </div>
          )}
        </div>

        {/* === Related News Sidebar === */}
        <aside className="news-detail-related">
          <h2>
            <i className="bi bi-newspaper"></i>
            Related News
          </h2>
          <div className="related-news-list">
            {relatedNews.length > 0 ? (
              relatedNews.map((post) => (
                <Link
                  key={post.postId}
                  to={`/news/${post.postId}`}
                  className="related-news-card"
                >
                  {post.featuredImage && (
                    <div className="related-news-image-container">
                      <img
                        src={
                          getImageUrl(post.featuredImage) ||
                          "https://via.placeholder.com/150x80?text=No+Image"
                        }
                        alt={post.title}
                        className="related-news-image"
                      />
                    </div>
                  )}
                  <div className="related-news-content">
                    <h4>{post.title}</h4>
                    <p>
                      {post.excerpt?.slice(0, 80) ||
                        post.content.slice(0, 80)}
                      ...
                    </p>
                    <span className="related-news-date">
                      {new Date(post.publishedAt).toLocaleDateString()}
                    </span>
                  </div>
                </Link>
              ))
            ) : (
              <div className="no-related-news">
                <i className="bi bi-info-circle"></i>
                No related news available.
              </div>
            )}
          </div>
        </aside>
      </div>
      <NewsPostComments />
    </div>
  );
};

export default NewsDetailPage;
