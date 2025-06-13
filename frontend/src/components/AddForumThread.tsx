import React, { useState, useRef } from 'react';
import axios from 'axios';
import './AddForumThread.css';
import { useParams, useNavigate } from 'react-router-dom';

interface ForumThreadForm {
  title: string;
  description: string;
  image?: File | null;
}

const AddForumThread: React.FC = () => {
  const { forumId } = useParams<{ forumId: string }>();
  const navigate = useNavigate();
  const userId = Number(localStorage.getItem('userId'));

  const [formData, setFormData] = useState<ForumThreadForm>({
    title: '',
    description: '',
    image: null,
  });

  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setFormData((prev) => ({ ...prev, image: file }));
      setImagePreview(URL.createObjectURL(file));
    }
  };

  const handleCancel = () => {
    navigate(-1);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      if (!forumId || isNaN(Number(forumId))) {
        throw new Error('Invalid forum ID');
      }

      if (!userId || isNaN(userId)) {
        throw new Error('User not logged in or invalid user ID');
      }

      const forumThreadsDto = {
        title: formData.title,
        description: formData.description,
        forum: { forumId: Number(forumId) },
        users: { userId },
      };

      const data = new FormData();
      data.append('forumThreadsDto', JSON.stringify(forumThreadsDto));
      if (formData.image) {
        data.append('image', formData.image);
      }

      await axios.post('http://localhost:8080/threads/post', data, {
        headers: {
          'Content-Type': 'multipart/form-data',
          Authorization: `Bearer ${localStorage.getItem('authToken')}`,
        },
      });

      navigate(`/forums/${forumId}`);
    } catch (err: any) {
      setError(err.response?.data || err.message || 'Failed to create thread');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="add-thread-background">
      <div className="add-thread-container">
        <div className="add-thread-card">
          <div className="add-thread-header">
            <h1>
              <i className="fas fa-plus-circle"></i>
              Create Forum Thread
            </h1>
            <p>Share your thoughts and start a discussion</p>
          </div>

          <form className="add-thread-form" onSubmit={handleSubmit}>
            {error && (
              <div className="error-message">
                <i className="fas fa-exclamation-triangle"></i>
                {error}
              </div>
            )}

            {successMessage && (
              <div className="success-message">
                <i className="fas fa-check-circle"></i>
                {successMessage}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="title">
                <i className="fas fa-heading"></i>
                Thread Title
              </label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleChange}
                className="form-control"
                placeholder="Enter thread title..."
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">
                <i className="fas fa-align-left"></i>
                Description
              </label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                className="form-control"
                placeholder="Enter thread description..."
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="image">
                <i className="fas fa-image"></i>
                Featured Image
              </label>
              <input
                type="file"
                id="image"
                accept="image/*"
                onChange={handleFileChange}
                ref={fileInputRef}
                className="form-control"
              />
            </div>

            {imagePreview && (
              <div className="form-group">
                <label>Image Preview</label>
                <div className="image-preview">
                  <img src={imagePreview} alt="Thread preview" />
                  <button
                    type="button"
                    className="remove-image-btn"
                    onClick={() => {
                      setImagePreview(null);
                      setFormData(prev => ({ ...prev, image: null }));
                      if (fileInputRef.current) {
                        fileInputRef.current.value = '';
                      }
                    }}
                  >
                    <i className="fas fa-times"></i>
                  </button>
                </div>
              </div>
            )}

            <div className="form-actions">
              <button
                type="button"
                onClick={handleCancel}
                className="btn btn-secondary"
                disabled={loading}
              >
                <i className="fas fa-times"></i>
                Cancel
              </button>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <i className="fas fa-spinner fa-spin"></i>
                    Posting...
                  </>
                ) : (
                  <>
                    <i className="fas fa-paper-plane"></i>
                    Post Thread
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

export default AddForumThread;
