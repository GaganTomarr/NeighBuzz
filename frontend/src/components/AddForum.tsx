import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AddForum.css';

interface ForumForm {
  description: string;
  category: string;
}

enum ForumCategoryEnum {
  GENERAL = 'GENERAL',
  EDUCATIONAL = 'EDUCATIONAL',
  TECHNOLOGY = 'TECHNOLOGY',
  ENTERTAINMENT = 'ENTERTAINMENT',
  LIFESTYLE = 'LIFESTYLE',
  NEWS = 'NEWS',
  HELP_AND_SUPPORT = 'HELP_AND_SUPPORT',
  FAITH = 'FAITH',
  ENVIRONMENT = 'ENVIRONMENT'
}

const AddForum: React.FC = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState<ForumForm>({
    description: '',
    category: ForumCategoryEnum.GENERAL,
  });

  const [categories, setCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setCategories(Object.values(ForumCategoryEnum));
  }, []);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCancel = () => {
    navigate('/forums');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const forumDto = {
        forumDescription: formData.description,
        forumCategory: formData.category,
        users: { userId: localStorage.getItem("userId") }, // Replace with actual user logic
      };

      await axios.post('http://localhost:8080/forums/post', forumDto, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('authToken')}`,
        },
      });

      navigate('/forums');
    } catch (err: any) {
      const backendError = err.response?.data;
      if (backendError?.errorMessage) {
        setError(backendError.errorMessage);
      } else if (typeof backendError === 'string') {
        setError(backendError);
      } else {
        setError('Failed to create forum. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="add-forum-background">
      <div className="add-forum-container">
        <div className="add-forum-card">
          <div className="add-forum-header">
            <h2>
              <i className="bi bi-chat-square-plus"></i>
              Create Forum
            </h2>
            <p>Start a new discussion and connect with the community</p>
          </div>
          <form className="add-forum-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="description">
                <i className="bi bi-card-text"></i>
                Forum Description
              </label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                required
                className="form-control"
                placeholder="Describe your forum topic..."
                rows={5}
              />
            </div>

            <div className="form-group">
              <label htmlFor="category">
                <i className="bi bi-tag"></i>
                Category
              </label>
              <select
                id="category"
                name="category"
                value={formData.category}
                onChange={handleChange}
                required
                className="form-control"
              >
                {categories.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase())}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? (
                  <>
                    <i className="bi bi-hourglass-split"></i>
                    Creating...
                  </>
                ) : (
                  <>
                    <i className="bi bi-send"></i>
                    Create Forum
                  </>
                )}
              </button>
              <button type="button" className="btn btn-secondary" onClick={handleCancel}>
                <i className="bi bi-x-circle"></i>
                Cancel
              </button>
            </div>
            {error && <div className="error-message"><i className="bi bi-exclamation-triangle"></i> {error}</div>}
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddForum;
