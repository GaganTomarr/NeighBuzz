import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./CreateEventPage.css";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import axios from "axios";

interface FormData {
  title: string;
  description: string;
  eventDate: Date | null;
  startTime: string;
  endTime?: string;
  capacity?: number | "";
  registrationDeadline: Date | null;
  location: "GENERAL" | "MUMBAI" | "DELHI" | "BENGALURU" | "HYDERABAD" | "AHMEDABAD" | "CHENNAI" | "KOLKATA" | "SURAT" | "PUNE" | "JAIPUR" | "";
  featuredImage: string | null;
  imageFile: File | null;
  eventsCategory: string;
}

const CreateEventPage: React.FC = () => {
  const { token } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<FormData>({
    title: "",
    description: "",
    eventDate: null,
    startTime: "",
    endTime: "",
    capacity: "",
    registrationDeadline: null,
    location: "",
    featuredImage: null,
    imageFile: null,
    eventsCategory: "",
  });

  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [successMessage, setSuccessMessage] = useState<string>("");
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [mandatory, setMandatory] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [valid, setValid] = useState<boolean>(false);

  const categoryList: string[] = [
    "SEMINAR",
    "BOOKMEET",
    "HEALTHCAMP",
    "NETWORKING",
    "FESTIVAL",
    "ENTERTAINMENT",
    "WORKSHOPS",
    "EDUCATIONAL",
    "RECREATIONAL",
  ];

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

  const url = "http://localhost:8080/events/post";

  const Messages = {
    REGISTRATION_DATE_ERROR: "Please enter a date before the event date",
    SUCCESS: "Successfully created an event",
    ERROR: "Failed to create event. Please try again.",
    MANDATORY: "Enter all mandatory fields",
  };

  useEffect(() => {
    const requiredFieldsValid =
      formData.title.trim() &&
      formData.description.trim() &&
      formData.eventDate &&
      formData.startTime.trim() &&
      formData.location.trim() &&
      formData.eventsCategory.trim();

    const noErrors = Object.values(errors).every((error) => !error);
    setValid(!!requiredFieldsValid && noErrors);
  }, [formData, errors]);

  const validateRegistrationDate = (
    registrationDate: Date | null,
    eventDate: Date | null
  ): boolean => {
    if (!registrationDate || !eventDate) return true;
    return registrationDate < eventDate;
  };

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    validateField(name, value);
  };

  const validateField = (name: string, value: any) => {
    let newErrors = { ...errors };

    switch (name) {
      case "title":
        newErrors.title = value.trim() ? "" : "Title is required";
        break;
      case "description":
        newErrors.description = value.trim() ? "" : "Description is required";
        break;
      case "location":
        newErrors.location = value.trim() ? "" : "Location is required";
        break;
      case "eventsCategory":
        newErrors.eventsCategory = value.trim() ? "" : "Category is required";
        break;
      case "registrationDeadline":
        newErrors.registrationDeadline = validateRegistrationDate(
          formData.registrationDeadline,
          formData.eventDate
        )
          ? ""
          : Messages.REGISTRATION_DATE_ERROR;
        break;
    }

    setErrors(newErrors);
  };

  const handleDateChange = (name: keyof FormData, date: Date | null) => {
    setFormData((prev) => ({ ...prev, [name]: date }));
    if (name === "registrationDeadline") {
      validateField(name, date);
    }
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setFormData((prev) => ({
      ...prev,
      imageFile: file,
      featuredImage: file ? file.name : null,
    }));
  };

  const resetForm = () => {
    setFormData({
      title: "",
      description: "",
      eventDate: null,
      startTime: "",
      endTime: "",
      capacity: "",
      registrationDeadline: null,
      location: "",
      featuredImage: null,
      imageFile: null,
      eventsCategory: "",
    });
    (document.getElementById("image") as HTMLInputElement).value = "";
    setErrors({});
    setSuccessMessage("");
    setErrorMessage("");
    setMandatory("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !formData.title ||
      !formData.description ||
      !formData.eventDate ||
      !formData.startTime ||
      !formData.location ||
      !formData.eventsCategory
    ) {
      setMandatory(Messages.MANDATORY);
      return;
    }

    setIsSubmitting(true);
    setMandatory("");

    const eventToSubmit = {
      title: formData.title,
      description: formData.description,
      eventDate: formData.eventDate?.toISOString().split("T")[0] || null,
      startTime: formData.startTime,
      endTime: formData.endTime || null,
      capacity: formData.capacity || null,
      registrationDeadline: formData.registrationDeadline
        ? formData.registrationDeadline.toISOString()
        : null,
      location: formData.location,
      eventsCategory: formData.eventsCategory,
      isPublished: true,
      organizer: {
        userId: localStorage.getItem("userId"),
      },
    };

    const data = new FormData();
    data.append("eventDto", JSON.stringify(eventToSubmit));
    if (formData.imageFile) {
      data.append("image", formData.imageFile);
    }

    try {
      const response = await axios.post(url, data, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setSuccessMessage(`${Messages.SUCCESS}: ${response.data}`);
      resetForm();

      setTimeout(() => {
        navigate("/events");
      }, 1000);
    } catch (error: any) {
      console.error("Error creating event:", error);
      setErrorMessage(error?.response?.data || Messages.ERROR);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="create-event-background">
      <div className="create-event-page">
        <div className="create-event-card">
          <div className="create-event-header">
            <h2>
              <i className="bi bi-calendar-plus"></i>
              Create New Event
            </h2>
            <p>Share your upcoming event with the community</p>
          </div>
          <Link to="/events" className="back-link">
            <i className="bi bi-arrow-left"></i> Back to Events
          </Link>
          <form onSubmit={handleSubmit} className="create-event-form">
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="title">Title</label>
                <input
                  type="text"
                  id="title"
                  name="title"
                  className="form-control"
                  value={formData.title}
                  onChange={handleChange}
                  placeholder="Enter the Event Title"
                />
                {errors.title && (
                  <span className="error-message">{errors.title}</span>
                )}
              </div>
              <div className="form-group">
                <label htmlFor="eventsCategory">Category</label>
                <select
                  name="eventsCategory"
                  id="eventsCategory"
                  className="form-control"
                  onChange={handleChange}
                  value={formData.eventsCategory}
                >
                  <option value="" disabled>
                    --Select Category--
                  </option>
                  {categoryList.map((cat) => (
                    <option key={cat} value={cat}>
                      {cat}
                    </option>
                  ))}
                </select>
                {errors.eventsCategory && (
                  <span className="error-message">{errors.eventsCategory}</span>
                )}
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="description">Description</label>
              <textarea
                id="description"
                name="description"
                className="form-control"
                value={formData.description}
                onChange={handleChange}
                placeholder="Enter description"
                rows={4}
              />
              {errors.description && (
                <span className="error-message">{errors.description}</span>
              )}
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="eventDate">Event Date</label>
                <DatePicker
                  selected={formData.eventDate}
                  onChange={(date) => handleDateChange("eventDate", date)}
                  dateFormat="yyyy-MM-dd"
                  className="form-control"
                  placeholderText="Select event date"
                />
              </div>
              <div className="form-group">
                <label htmlFor="registrationDeadline">Registration Deadline</label>
                <DatePicker
                  selected={formData.registrationDeadline}
                  onChange={(date) =>
                    handleDateChange("registrationDeadline", date)
                  }
                  dateFormat="yyyy-MM-dd"
                  className="form-control"
                  placeholderText="Select deadline"
                />
                {errors.registrationDeadline && (
                  <span className="error-message">
                    {errors.registrationDeadline}
                  </span>
                )}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="startTime">Start Time</label>
                <input
                  type="time"
                  id="startTime"
                  name="startTime"
                  className="form-control"
                  value={formData.startTime}
                  onChange={handleChange}
                />
              </div>
              <div className="form-group">
                <label htmlFor="endTime">End Time</label>
                <input
                  type="time"
                  id="endTime"
                  name="endTime"
                  className="form-control"
                  value={formData.endTime}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="capacity">Capacity</label>
                <input
                  type="number"
                  id="capacity"
                  name="capacity"
                  className="form-control"
                  value={formData.capacity}
                  onChange={handleChange}
                  placeholder="Enter capacity"
                />
              </div>
              <div className="form-group">
                <label htmlFor="location">Location</label>
                <select
                  name="location"
                  id="location"
                  className="form-control"
                  onChange={handleChange}
                  value={formData.location}
                >
                  <option value="" disabled>
                    --Select Location--
                  </option>
                  {locationOptions.map((location) => (
                    <option key={location} value={location}>
                      {getLocationDisplayName(location)}
                    </option>
                  ))}
                </select>
                {errors.location && (
                  <span className="error-message">{errors.location}</span>
                )}
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="image">Upload Image</label>
              <input
                type="file"
                id="image"
                name="image"
                className="form-control"
                onChange={handleImageChange}
              />
            </div>

            <div className="form-group text-center">
              <button
                type="submit"
                className="submit-button"
                disabled={!valid || isSubmitting}
              >
                {isSubmitting ? "Creating..." : "Create Event"}
              </button>
            </div>

            <div className="form-messages text-center">
              {mandatory && <p className="text-danger">{mandatory}</p>}
              {successMessage && <p className="text-success">{successMessage}</p>}
              {errorMessage && <p className="text-danger">{errorMessage}</p>}
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CreateEventPage;