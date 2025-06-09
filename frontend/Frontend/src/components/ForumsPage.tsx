import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../AuthContext";
import axios from "axios";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./ForumsPage.css";

const apiClient = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

const forumCategories = [
  "GENERAL",
  "EDUCATIONAL",
  "TECHNOLOGY",
  "ENTERTAINMENT",
  "LIFESTYLE",
  "NEWS",
  "HELP_AND_SUPPORT",
  "FAITH",
  "ENVIRONMENT",
];

interface Forum {
  forumId: number;
  forumDescription: string;
  createdAt: string;
  updatedAt: string;
  forumCategory: typeof forumCategories[number];
  users: {
    userId: number;
    username: string;
    email: string;
  };
}

const ForumsPage: React.FC = () => {
  const [forums, setForums] = useState<Forum[]>([]);
  const [categoryFilter, setCategoryFilter] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [dateFilter, setDateFilter] = useState(""); // NEW: date filter state
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [currentPage, setCurrentPage] = useState(1);
  const [forumsPerPage] = useState(9);
  const { isAuthenticated, isAdmin } = useAuth();

  useEffect(() => {
    fetchForums();
  }, []);

  const fetchForums = async () => {
    try {
      setLoading(true);
      const response = await apiClient.get("/forums/getAll");
      setForums(response.data);
      setError(null);
    } catch (err: any) {
      console.error("Error fetching forums:", err);
      if (err.response) {
        setError(
          `Error ${err.response.status}: ${err.response.data.message || "Server error"}`
        );
      } else if (err.request) {
        setError("No response from server. Please check your connection.");
      } else {
        setError(`Error: ${err.message}`);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setCategoryFilter(e.target.value);
    setCurrentPage(1);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
    setCurrentPage(1);
  };

  // NEW: Handle date change
  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDateFilter(e.target.value);
    setCurrentPage(1);
  };

  // NEW: Filtering logic includes date
  const filteredForums = forums
    .filter((forum) => {
      const matchesCategory = categoryFilter ? forum.forumCategory === categoryFilter : true;
      const matchesSearch = searchQuery
        ? forum.forumDescription.toLowerCase().includes(searchQuery.toLowerCase())
        : true;
      const matchesDate = dateFilter
        ? new Date(forum.createdAt).getTime() >= new Date(dateFilter).setHours(0, 0, 0, 0)
        : true;
      return matchesCategory && matchesSearch && matchesDate;
    })
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

  const indexOfLastForum = currentPage * forumsPerPage;
  const indexOfFirstForum = indexOfLastForum - forumsPerPage;
  const currentForums = filteredForums.slice(indexOfFirstForum, indexOfLastForum);

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);
  const totalPages = Math.ceil(filteredForums.length / forumsPerPage);

  return (
    <div className="forums-page-background">
      <div className="forums-page-container">
        <div className="forums-controls">
          <div className="forums-title">FORUMS</div>

          <div className="filter-item">
            <select
              value={categoryFilter}
              onChange={handleCategoryChange}
              className="filter-select"
            >
              <option value="">All Categories</option>
              {forumCategories.map((cat) => (
                <option key={cat} value={cat}>
                  {cat.replace(/_/g, " ")}
                </option>
              ))}
            </select>
          </div>

          {/* NEW: Date filter input */}
          <div className="filter-item">
            <input
              type="date"
              value={dateFilter}
              onChange={handleDateChange}
              className="filter-date-input"
              max={new Date().toISOString().split("T")[0]}
            />
          </div>

          <div className="search-container">
            <input
              type="text"
              placeholder="Search forums..."
              className="search-input"
              onChange={handleSearchChange}
              value={searchQuery}
              spellCheck={false}
              autoComplete="off"
            />
            <span className="search-icon">
              <i className="bi bi-search"></i>
            </span>
          </div>

          {isAdmin && (
            <div className="add-new-forum-section">
              <Link to="/forums/create" className="add-new-forum-button">
                <i className="bi bi-plus-circle"></i> Add New Forum
              </Link>
            </div>
          )}
        </div>
        {loading ? (
          <div className="loading-message">
            <i className="bi bi-hourglass-split"></i>
            Loading forums...
          </div>
        ) : error ? (
          <div className="error-message">
            <i className="bi bi-exclamation-triangle"></i>
            {error}
          </div>
        ) : (
          <>
            <div className="forums-list-container">
              {currentForums.length > 0 ? (
                currentForums.map((forum) => (
                  <Link
                    key={forum.forumId}
                    to={`/forums/${forum.forumId}`}
                    className="forum-card"
                  >
                    <div className="forum-card-content">
                      <div className="forum-card-header">
                        <span className="forum-category">
                          {forum.forumCategory.replace(/_/g, " ")}
                        </span>
                        <span className="forum-date">
                          {new Date(forum.createdAt).toLocaleDateString()}
                        </span>
                      </div>
                      <div className="forum-description">
                        {forum.forumDescription}
                      </div>
                      <div className="forum-footer">
                        <span className="forum-author">
                          <i className="bi bi-person"></i>
                          {forum.users.username}
                        </span>
                        <span className="read-more-link">
                          View Discussion
                          <i className="bi bi-arrow-right"></i>
                        </span>
                      </div>
                    </div>
                  </Link>
                ))
              ) : (
                <div className="no-forums-message">
                  <i className="bi bi-chat-square-text"></i>
                  No forums found matching your criteria.
                </div>
              )}
            </div>

            {filteredForums.length > forumsPerPage && (
              <div className="pagination-container">
                <ul className="pagination">
                  <li className={`page-item ${currentPage === 1 ? "disabled" : ""}`}>
                    <button
                      onClick={() => currentPage > 1 && paginate(currentPage - 1)}
                      className="page-link"
                      disabled={currentPage === 1}
                    >
                      Previous
                    </button>
                  </li>
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map((number) => (
                    <li key={number} className="page-item">
                      <button
                        onClick={() => paginate(number)}
                        className={`page-link ${currentPage === number ? "active" : ""}`}
                      >
                        {number}
                      </button>
                    </li>
                  ))}
                  <li className={`page-item ${currentPage === totalPages ? "disabled" : ""}`}>
                    <button
                      onClick={() => currentPage < totalPages && paginate(currentPage + 1)}
                      className="page-link"
                      disabled={currentPage === totalPages}
                    >
                      Next
                    </button>
                  </li>
                </ul>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default ForumsPage;