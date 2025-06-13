import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

interface ProfileFormData {
  displayName: string;
  bio: string;
  location: string;
  profileVisibility: "PUBLIC" | "PRIVATE";
  contactType: "EMAIL" | "SMS" | "MOBILENO" | "NONE";
  imageFile: File | null;
}

const CreateProfile: React.FC = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState<ProfileFormData>({
    displayName: "",
    bio: "",
    location: "",
    profileVisibility: "PUBLIC",
    contactType: "NONE",
    imageFile: null,
  });

  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [successMessage, setSuccessMessage] = useState<string>("");
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [mandatory, setMandatory] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [valid, setValid] = useState<boolean>(false);

  useEffect(() => {
    const requiredFieldsValid = formData.displayName.trim() !== "";
    const noErrors = Object.values(errors).every((e) => !e);
    setValid(requiredFieldsValid && noErrors);
  }, [formData, errors]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    validateField(name, value);
  };

  const validateField = (name: string, value: string) => {
    let newErrors = { ...errors };

    switch (name) {
      case "displayName":
        newErrors.displayName = value.trim() ? "" : "Display name is required";
        break;
    }

    setErrors(newErrors);
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setFormData((prev) => ({
      ...prev,
      imageFile: file,
    }));
    setPreviewUrl(file ? URL.createObjectURL(file) : null);
  };

  const resetForm = () => {
    setFormData({
      displayName: "",
      bio: "",
      location: "",
      profileVisibility: "PUBLIC",
      contactType: "NONE",
      imageFile: null,
    });
    setPreviewUrl(null);
    (document.getElementById("profilePicture") as HTMLInputElement).value = "";
    setErrors({});
    setSuccessMessage("");
    setErrorMessage("");
    setMandatory("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.displayName.trim()) {
      setMandatory("Display name is required");
      return;
    }

    setIsSubmitting(true);
    setMandatory("");

    const profileDto = {
      displayName: formData.displayName,
      bio: formData.bio,
      location: formData.location,
      profileVisibility: formData.profileVisibility,
      contactType: formData.contactType,
    };

    const data = new FormData();
    data.append("profileDto", JSON.stringify(profileDto));
    if (formData.imageFile) {
      data.append("image", formData.imageFile);
    }

    const token = localStorage.getItem("authToken");
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.post(
        `http://localhost:8080/profile/added/${userId}`,
        data,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data",
          },
        }
      );

      setSuccessMessage("Profile created successfully.");
      resetForm();

      setTimeout(() => {
        navigate("/profile");
      }, 1000);
    } catch (error: any) {
      console.error("Error creating profile:", error);
      setErrorMessage(error?.response?.data || "Failed to create profile.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="create-profile-form">
      <h2>Create Your Profile</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="displayName">Display Name</label>
          <input
            type="text"
            id="displayName"
            name="displayName"
            className="form-control"
            value={formData.displayName}
            onChange={handleChange}
            required
            placeholder="Enter your display name"
          />
          {errors.displayName && (
            <span className="error-message">{errors.displayName}</span>
          )}
        </div>

        <div className="form-group">
          <label htmlFor="location">Location</label>
          <input
            type="text"
            id="location"
            name="location"
            className="form-control"
            value={formData.location}
            onChange={handleChange}
            placeholder="Enter your location"
          />
        </div>

        <div className="form-group">
          <label htmlFor="bio">Bio</label>
          <textarea
            id="bio"
            name="bio"
            className="form-control"
            value={formData.bio}
            onChange={handleChange}
            placeholder="Write a short bio"
          />
        </div>

        <div className="form-group">
          <label htmlFor="profileVisibility">Profile Visibility</label>
          <select
            id="profileVisibility"
            name="profileVisibility"
            className="form-control"
            value={formData.profileVisibility}
            onChange={handleChange}
          >
            <option value="PUBLIC">Public</option>
            <option value="PRIVATE">Private</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="contactType">Preferred Contact</label>
          <select
            id="contactType"
            name="contactType"
            className="form-control"
            value={formData.contactType}
            onChange={handleChange}
          >
            <option value="NONE">None</option>
            <option value="EMAIL">Email</option>
            <option value="SMS">SMS</option>
            <option value="MOBILENO">Mobile No</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="profilePicture">Upload Profile Picture</label>
          <input
            type="file"
            id="profilePicture"
            name="profilePicture"
            className="form-control"
            accept="image/*"
            onChange={handleImageChange}
          />
        </div>

        {previewUrl && (
          <div className="image-preview">
            <img
              src={previewUrl}
              alt="Preview"
              style={{
                maxWidth: "200px",
                maxHeight: "200px",
                objectFit: "cover",
                marginTop: "10px",
              }}
            />
          </div>
        )}

        <div className="form-group text-center">
          <button
            type="submit"
            className="submit-button"
            disabled={!valid || isSubmitting}
          >
            {isSubmitting ? "Creating..." : "Create Profile"}
          </button>
        </div>

        <div className="form-messages text-center">
          {mandatory && <p className="text-danger">{mandatory}</p>}
          {successMessage && <p className="text-success">{successMessage}</p>}
          {errorMessage && <p className="text-danger">{errorMessage}</p>}
        </div>
      </form>
    </div>
  );
};

export default CreateProfile;