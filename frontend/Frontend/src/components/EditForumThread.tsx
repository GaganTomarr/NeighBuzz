import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import './EditForumThread.css';

interface ForumThreadForm {
  title: string;
  description: string;
  featuredImage?: File | null;
}

const getImageUrl = (imagePath: string | null | undefined): string | null => {
  if (!imagePath) return null;
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }
  return `http://localhost:8080/${imagePath.replace(/^\//, '')}`;
};

const EditForumThread: React.FC = () => {
  const { id } = useParams<{ id : string }>();
  console.log(id);
  const threadId = id;
  const navigate = useNavigate();

  const [formData, setFormData] = useState<ForumThreadForm>({
    title: '',
    description: '',
    featuredImage: null,
  });

  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchThread = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/threads/getByThreadId/${threadId}`);
        console.log(res.data);
        
          setFormData({
            title: res.data.title,
            description: res.data.description,
            featuredImage: null,
      });

          setImagePreview(getImageUrl(res.data.featuredImage));
        
      } catch (err) {
        setError('Failed to fetch thread data');
      } finally {
        setFetching(false);
      }
    };

    fetchThread();
  }, [threadId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setFormData((prev) => ({ ...prev, featuredImage: file }));
      setImagePreview(URL.createObjectURL(file));
    }
  };

  const handleCancel = () => {
    navigate(-1); // Navigate back
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const forumThreadsDto = {
        title: formData.title,
        description: formData.description,
        forum: { forumId: 6 },
        users: { userId: localStorage.getItem("userId") },
      };

      const data = new FormData();
      data.append('forumThreadsDto', JSON.stringify(forumThreadsDto));
      if (formData.featuredImage) {
        data.append('image', formData.featuredImage);
      }

      await axios.put('http://localhost:8080/threads/update', data, {
        params: {
          tId: threadId,
        },
        headers: {
          'Content-Type': 'multipart/form-data',
          Authorization: `Bearer ${localStorage.getItem('authToken')}`,
        },
      });

      // navigate(`/forumThread/${threadId}`);
      navigate(-1);
    } catch (err: any) {
      setError(err.response?.data || 'Failed to update thread');
    } finally {
      setLoading(false);
    }
  };

  if (fetching) return <div>Loading thread data...</div>;
  // if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="thread-form-container">
      <h2>Edit Forum Thread</h2>

      <form className="thread-form" onSubmit={handleSubmit}>
        <label>
          Title
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Description
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Image
          <input
            type="file"
            accept="featuredImage/*"
            onChange={handleFileChange}
            ref={fileInputRef}
          />
        </label>

        {imagePreview && (
          <div className="image-preview">
            <img src={imagePreview} alt="Preview" />
          </div>
        )}

        <div className="form-buttons">
          <button type="submit" disabled={loading}>
            {loading ? 'Updating...' : 'Update Thread'}
          </button>
          <button type="button" onClick={handleCancel}>
            Cancel
          </button>
        </div>

        {/* {error && <p className="error-message">{error}</p>} */}
      </form>
    </div>
  );
};

export default EditForumThread;
