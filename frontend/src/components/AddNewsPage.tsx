import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";
import "./AddNewsPage.css";
import toast from "react-hot-toast";

// Enum values from backend
const NEWS_CATEGORIES = [
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
] as const;

type NewsCategory = (typeof NEWS_CATEGORIES)[number];

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

const AddNewsPage: React.FC = () => {
  const [formData, setFormData] = useState({
    title: "",
    newsCategory: "",
    location: "",
    excerpt: "",
    content: "",
  });

  const [imageFile, setImageFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const { token, userId: contextUserId } = useAuth();
  const userId = contextUserId ?? Number(localStorage.getItem("userId"));

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

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.currentTarget.files?.[0];
    if (file) {
      setImageFile(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.title || !formData.content || !formData.newsCategory || !formData.location) {
      setError("Please fill in all required fields");
      return;
    }

    if (!userId) {
      setError("User ID not found. Please log in again.");
      return;
    }

    setIsSubmitting(true);
    setError(null);

    const now = new Date().toISOString();

    const newsDto = {
      title: formData.title,
      content: formData.content,
      excerpt: formData.excerpt || "",
      newsCategory: formData.newsCategory,
      location: formData.location,
      author: { userId },
      featuredImage: "",
      createdAt: now,
      updatedAt: now,
      publishedAt: now,
      isApproved: false,
      approvalUser: null,
    };

    const form = new FormData();
    form.append("newsDto", JSON.stringify(newsDto));
    if (imageFile) {
      form.append("image", imageFile);
    }

    try {
      const response = await fetch(
        `http://localhost:8080/news/newsPost/add/${userId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body: form,
        }
      );

      if (response.ok) {
        toast.success("News article posted successfully!");
        navigate("/news");
      } else {
        const errorText = await response.text();
        setError(errorText || "Failed to submit news post");
      }
    } catch (err) {
      console.error("Error submitting news post:", err);
      setError("An unexpected error occurred");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="add-news-background">
      <div className="add-news-container">
        <div className="add-news-card">
          <div className="add-news-header">
            <h1>
              <i className="bi bi-plus-circle"></i>
              Add News Article
            </h1>
            <p>Share important news with your community</p>
          </div>

          {error && (
            <div className="error-message">
              <i className="bi bi-exclamation-triangle"></i>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="add-news-form">
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="title">
                  <i className="bi bi-type"></i>
                  Article Title *
                </label>
                <input
                  type="text"
                  id="title"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  placeholder="Enter a compelling title"
                  required
                  className="form-control"
                />
              </div>
              <div className="form-group">
                <label htmlFor="newsCategory">
                  <i className="bi bi-tag"></i>
                  Category *
                </label>
                <select
                  id="newsCategory"
                  name="newsCategory"
                  value={formData.newsCategory}
                  onChange={handleChange}
                  required
                  className="form-control"
                >
                  <option value="">Select a category</option>
                  {NEWS_CATEGORIES.map((category) => (
                    <option key={category} value={category}>
                      {category.replace(/_/g, " ")}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="location">
                  <i className="bi bi-geo-alt"></i>
                  Location *
                </label>
                <select
                  id="location"
                  name="location"
                  value={formData.location}
                  onChange={handleChange}
                  required
                  className="form-control"
                >
                  <option value="">Select a location</option>
                  {locationOptions.map((location) => (
                    <option key={location} value={location}>
                      {getLocationDisplayName(location)}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="excerpt">
                  <i className="bi bi-card-text"></i>
                  Article Summary
                </label>
                <textarea
                  id="excerpt"
                  name="excerpt"
                  value={formData.excerpt}
                  onChange={handleChange}
                  placeholder="Write a brief summary of your article (optional)"
                  className="form-control"
                  rows={3}
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="content">
                <i className="bi bi-file-text"></i>
                Article Content *
              </label>
              <textarea
                id="content"
                name="content"
                value={formData.content}
                onChange={handleChange}
                placeholder="Write your full article content here..."
                required
                className="form-control content-textarea"
                rows={12}
              />
            </div>

            <div className="form-group">
              <label htmlFor="image">
                <i className="bi bi-image"></i>
                Featured Image
              </label>
              <div className="file-upload-container">
                <input
                  type="file"
                  id="image"
                  name="image"
                  onChange={handleImageChange}
                  accept="image/*"
                  className="file-input"
                />
                <label htmlFor="image" className="file-upload-label">
                  <i className="bi bi-cloud-upload"></i>
                  Choose Image
                </label>
              </div>
              {previewUrl && (
                <div className="image-preview">
                  <img src={previewUrl} alt="Preview" />
                  <button
                    type="button"
                    className="remove-image"
                    onClick={() => {
                      setImageFile(null);
                      setPreviewUrl(null);
                    }}
                  >
                    <i className="bi bi-x"></i>
                  </button>
                </div>
              )}
            </div>

            <div className="form-actions">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navigate("/news")}
              >
                <i className="bi bi-x-circle"></i>
                Cancel
              </button>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <i className="bi bi-hourglass-split"></i>
                    Publishing...
                  </>
                ) : (
                  <>
                    <i className="bi bi-send"></i>
                    Publish Article
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddNewsPage;