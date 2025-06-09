import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../AuthContext";
import "./NewsPage.css";

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
  newsCategory: NewsCategory;
  location: "GENERAL" | "MUMBAI" | "DELHI" | "BENGALURU" | "HYDERABAD" | "AHMEDABAD" | "CHENNAI" | "KOLKATA" | "SURAT" | "PUNE" | "JAIPUR";
  isApproved: boolean;
}

const newsCategories = [
  "EDUCATIONAL", "CRIME", "GEOPOLITICS", "NATIONAL", "SPORTS", "HEALTH",
  "LIFESTYLE", "EDITORIALS", "ENTERTAINMENT", "TECHNOLOGY", "BUSINESS",
  "SPIRITUAL", "CULTURE"
] as const;

type NewsCategory = typeof newsCategories[number];

const locationOptions = [
  "GENERAL",
  "MUMBAI",
  "DELHI",
  "BENGALURU",
  "HYDERABAD",
  "AHMEDABAD",
  "CHENNAI",
  "KOLKATA",
  "SURAT",
  "PUNE",
  "JAIPUR",
];

const NEWS_API_BASE_URL = "http://localhost:8080";

const NewsPage: React.FC = () => {
  const [newsItems, setNewsItems] = useState<NewsPost[]>([]);
  const [filteredNews, setFilteredNews] = useState<NewsPost[]>([]);
  const [categoryFilter, setCategoryFilter] = useState<string>("");
  const [locationFilter, setLocationFilter] = useState<string>("");
  const [dateFilter, setDateFilter] = useState<string>("");
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Pagination state
  const [currentPage, setCurrentPage] = useState<number>(1);
  const newsPerPage = 6;

  const { isAuthenticated, userId, isAdmin, token } = useAuth();

  // Function to map location enum to display-friendly names
  const getLocationDisplayName = (location: string) => {
    const locationMap: { [key: string]: string } = {
      GENERAL: "General",
      MUMBAI: "Mumbai",
      DELHI: "Delhi",
      BENGALURU: "Bengaluru",
      HYDERABAD: "Hyderabad",
      AHMEDABAD: "Ahmedabad",
      CHENNAI: "Chennai",
      KOLKATA: "Kolkata",
      SURAT: "Surat",
      PUNE: "Pune",
      JAIPUR: "Jaipur",
    };
    return locationMap[location] || location;
  };

  useEffect(() => {
    const fetchNews = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`${NEWS_API_BASE_URL}/news/newsPost`, {
          params: { userId: userId ?? 0, isAdmin: isAdmin ?? false },
          headers: token ? { Authorization: `Bearer ${token}` } : {}
        });
        setNewsItems(response.data);
      } catch (err) {
        setError("Failed to load news articles. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchNews();
  }, [userId, isAdmin]);

  useEffect(() => {
    let processed = [...newsItems];

    if (categoryFilter) {
      processed = processed.filter((item) => item.newsCategory === categoryFilter);
    }

    if (locationFilter) {
      processed = processed.filter((item) => item.location === locationFilter);
    }

    if (dateFilter) {
      const filterDate = new Date(dateFilter);
      processed = processed.filter((item) => {
        const postDate = new Date(item.publishedAt);
        postDate.setHours(0, 0, 0, 0);
        return postDate >= filterDate;
      });
    }

    if (searchQuery) {
      processed = processed.filter((item) =>
        item.title.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    // Filter by approval status
    processed = processed.filter((item) =>
      item.isApproved || isAdmin || item.author?.userId === userId
    );

    processed.sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime());
    setFilteredNews(processed);
    setCurrentPage(1); // Reset to first page on filter change
  }, [newsItems, categoryFilter, locationFilter, dateFilter, searchQuery, isAdmin, userId]);

  // Pagination logic
  const totalPages = Math.ceil(filteredNews.length / newsPerPage);
  const indexOfLastNews = currentPage * newsPerPage;
  const indexOfFirstNews = indexOfLastNews - newsPerPage;
  const currentNews = filteredNews.slice(indexOfFirstNews, indexOfLastNews);

  const handlePageChange = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  const handleApprove = async (postId: number) => {
    try {
      await axios.put(`${NEWS_API_BASE_URL}/news/approve/${postId}`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      await axios.post(`${NEWS_API_BASE_URL}/news/modLogApprove/${postId}/${userId}`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      alert("News post approved and published.");
      window.location.reload();
    } catch (err) {
      console.error("Error approving news:", err);
      alert("Failed to approve post.");
    }
  };

  const handleReject = async (postId: number) => {
    const confirmed = window.confirm("Are you sure you want to reject and delete this news post?");
    if (!confirmed) return;

    try {
      await axios.delete(`${NEWS_API_BASE_URL}/news/newsPost/delete/${postId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      await axios.post(`${NEWS_API_BASE_URL}/news/modLogReject/${postId}/${userId}`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
      alert("News post rejected and deleted.");
      window.location.reload();
    } catch (err) {
      console.error("Error rejecting news:", err);
      alert("Failed to reject post.");
    }
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  const formatCategoryLabel = (category: string): string => {
    return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
  };

  const getImageUrl = (imagePath?: string | null): string | null => {
    if (!imagePath) return null;
    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
      return imagePath;
    }
    return `${NEWS_API_BASE_URL}/${imagePath.replace(/^\//, "")}`;
  };

  if (loading) return <div className="news-page-container">Loading news...</div>;
  if (error) return <div className="news-page-container error-message">{error}</div>;

  return (
    <div className="news-page-background">
      <div className="news-page-container">
        <div className="news-toolbar">
          <h1 className="news-title">LATEST NEWS</h1>

          <div className="filter-container">
            <select
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value)}
              className="category-filter"
            >
              <option value="">All Categories</option>
              {newsCategories.map((cat) => (
                <option key={cat} value={cat}>
                  {formatCategoryLabel(cat)}
                </option>
              ))}
            </select>

            <select
              value={locationFilter}
              onChange={(e) => setLocationFilter(e.target.value)}
              className="location-filter"
            >
              <option value="">All Locations</option>
              {locationOptions.map((location) => (
                <option key={location} value={location}>
                  {getLocationDisplayName(location)}
                </option>
              ))}
            </select>

            <input
              type="date"
              value={dateFilter}
              onChange={(e) => setDateFilter(e.target.value)}
              className="date-filter"
            />

            <div className="search-container">
              <input
                type="text"
                placeholder="Search news..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="search-filter-input"
              />
            </div>

            {isAuthenticated && (
              <Link to="/news/add" className="add-news-button">
                <i className="bi bi-plus-circle"></i> Add News
              </Link>
            )}
          </div>
        </div>

        <div className="news-grid">
          {currentNews.length > 0 ? (
            currentNews.map((newsItem) => (
              <div key={newsItem.postId} className="news-card">
                {newsItem.featuredImage && (
                  <div className="news-card-image-container">
                    <img
                      src={
                        getImageUrl(newsItem.featuredImage) ||
                        `https://via.placeholder.com/300x200?text=${encodeURIComponent(newsItem.title)}`
                      }
                      alt={newsItem.title}
                      className="news-card-image"
                    />
                  </div>
                )}

                <div className="news-card-content">
                  <h2 className="news-card-title">{newsItem.title}</h2>
                  <p className="news-card-meta">
                    <span className="news-card-category">
                      {formatCategoryLabel(newsItem.newsCategory)}
                    </span>
                    <span className="news-card-location">
                      <i className="bi bi-geo-alt-fill"></i> {getLocationDisplayName(newsItem.location)}
                    </span>
                    <span className="news-card-author">
                      By: {newsItem.author?.username || "Unknown Author"}
                    </span>
                    <span className="news-card-date">{formatDate(newsItem.publishedAt)}</span>
                  </p>
                  {!newsItem.isApproved && (
                    <p className="text-yellow-600 font-semibold">Pending Approval</p>
                  )}
                  <p className="news-card-summary">
                    {newsItem.excerpt || newsItem.content.substring(0, 150) + "..."}
                  </p>
                  <Link to={`/news/${newsItem.postId}`} className="read-more-link">
                    Read More
                  </Link>

                  {isAdmin && !newsItem.isApproved && (
                    <div className="admin-actions mt-2">
                      <button className="approve-btn bg-green-600 text-white px-3 py-1 rounded mr-2"
                        onClick={() => handleApprove(newsItem.postId)}>
                        Approve
                      </button>
                      <button className="reject-btn bg-red-600 text-white px-3 py-1 rounded"
                        onClick={() => handleReject(newsItem.postId)}>
                        Reject
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))
          ) : (
            <p className="no-news-message">No news articles found matching your criteria.</p>
          )}
        </div>

        {/* Pagination controls */}
        {totalPages > 1 && (
          <div className="pagination-container">
            <ul className="pagination">
              <li className={`page-item ${currentPage === 1 ? "disabled" : ""}`}>
                <button
                  onClick={() => currentPage > 1 && handlePageChange(currentPage - 1)}
                  className="page-link"
                  disabled={currentPage === 1}
                >
                  Previous
                </button>
              </li>
              {Array.from({ length: totalPages }, (_, i) => i + 1).map((number) => (
                <li key={number} className="page-item">
                  <button
                    onClick={() => handlePageChange(number)}
                    className={`page-link ${currentPage === number ? "active" : ""}`}
                  >
                    {number}
                  </button>
                </li>
              ))}
              <li className={`page-item ${currentPage === totalPages ? "disabled" : ""}`}>
                <button
                  onClick={() => currentPage < totalPages && handlePageChange(currentPage + 1)}
                  className="page-link"
                  disabled={currentPage === totalPages}
                >
                  Next
                </button>
              </li>
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default NewsPage;