import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";
import { useNotification } from "./NotificationContext";
import "react-toastify/dist/ReactToastify.css";
import "./EventDetails.css";
import axios from "axios";
import { toast } from "react-hot-toast";


const apiClient = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

interface Organizer {
  userId: number;
  username: string;
  email: string;
}

interface Event {
  eventId: number;
  title: string;
  description: string;
  location: string;
  eventDate: string;
  startTime: string;
  endTime: string | null;
  capacity: number | null;
  registrationDeadline: string | null;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
  featuredImage: string | null;
  eventsCategory:
  | "SEMINAR"
  | "BOOKMEET"
  | "HEALTHCAMP"
  | "NETWORKING"
  | "FESTIVAL"
  | "ENTERTAINMENT"
  | "WORKSHOPS"
  | "EDUCATIONAL"
  | "RECREATIONAL";
  organizer: Organizer;
}

const EventDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [event, setEvent] = useState<Event | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isRegistered, setIsRegistered] = useState<boolean>(false);
  const [registrationId, setRegistrationId] = useState<number | null>(null);
  const [seatsLeft, setSeatsLeft] = useState<number | null>(null);
  const [totalRegistrations, setTotalRegistrations] = useState<number>(0);

  const { isAuthenticated, userId, username, token, isAdmin } = useAuth();
  const { refreshNotifications } = useNotification();

  const getImageUrl = (imagePath: string | null) => {
    if (!imagePath) return null;
    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
      return imagePath;
    }
    return `http://localhost:8080/${imagePath.replace(/^\//, "")}`;
  };

  useEffect(() => {
    const fetchRegistrations = async () => {
      try {
        const response = await apiClient.get("/eventRegistrations/getAll");
        const registrations = response.data.filter(
          (reg: any) => reg.events.eventId === Number(id)
        );
        setTotalRegistrations(registrations.length);
        if (event && event.capacity !== null) {
          setSeatsLeft(event.capacity - registrations.length);
        }
      } catch (err) {
        setSeatsLeft(event?.capacity ?? null);
      }
    };

    if (event) {
      fetchRegistrations();
    }
  }, [event, id]);

  useEffect(() => {
    const fetchEventDetails = async () => {
      try {
        setLoading(true);
        const response = await apiClient.get(`/events/getById/${id}`);
        setEvent(response.data);
        setError(null);
      } catch (err: any) {
        if (err.response) {
          setError(
            `Error ${err.response.status}: ${err.response.data.message || "Server error"
            }`
          );
        } else if (err.request) {
          setError(
            "No response from server. CORS issue may be blocking the request."
          );
        } else {
          setError(`Error: ${err.message}`);
        }
      } finally {
        setLoading(false);
      }
    };

    fetchEventDetails();
  }, [id]);

  useEffect(() => {
    const fetchRegistrationStatus = async () => {
      try {
        const response = await apiClient.get("/eventRegistrations/getAll");
        const registrations = response.data;

        const userRegistration = registrations.find(
          (reg: any) =>
            reg.events.eventId === Number(id) && reg.users.userId === userId
        );

        if (userRegistration) {
          setIsRegistered(true);
          setRegistrationId(userRegistration.registrationId);
        }
      } catch (err) {
        console.error("Failed to fetch registration status", err);
      }
    };

    if (isAuthenticated && userId !== null) {
      fetchRegistrationStatus();
    }
  }, [id, isAuthenticated, userId]);

  const handleRegister = async () => {
    if (!event || userId === null || !token) {
      toast.error("You must be logged in to register.");
      return;
    }

    const payload = {
      users: { userId },
      events: { eventId: event.eventId },
      registrationStatus: "REGISTERED",
      registrationDate: new Date().toISOString(),
      cancellationDate: null,
    };

    try {
      const response = await apiClient.post(
        "/eventRegistrations/post",
        payload,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const idMatch = response.data.match(/Id:\s*(\d+)/);
      const newId = idMatch ? parseInt(idMatch[1]) : null;
      setRegistrationId(newId);
      setIsRegistered(true);
      setSeatsLeft((prev) => (prev !== null ? prev - 1 : prev));

      // 🟢 Refresh notifications and show success toast
      refreshNotifications();
      toast.success("🎉 Successfully registered for the event!");
    } catch (error: any) {
      console.error("Registration failed:", error);
      toast.error(
        error.response?.data || "Failed to register. Please try again."
      );
    }
  };

  const handleCancel = async () => {
    if (!registrationId || !token) return;

    try {
      await apiClient.delete(
        `/eventRegistrations/delete?rId=${registrationId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setIsRegistered(false);
      setSeatsLeft((prev) => (prev !== null ? prev + 1 : prev));
      setRegistrationId(null);
      toast.success("Registration cancelled.");
    } catch (error) {
      console.error("Cancellation failed:", error);
      toast.error("Failed to cancel registration.");
    }
  };

  const handleDelete = async () => {
    if (!token) return;

    try {
      await apiClient.delete(`/events/delete?eId=${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      navigate(`/events`);
      toast.success("Event deleted.");
    } catch (error) {
      console.error("Deletion failed:", error);
      toast.error("Failed to delete event.");
    }
  };

  if (loading)
    return (
      <div className="event-details-loading">Loading event details...</div>
    );
  if (error) return <div className="event-details-error">{error}</div>;
  if (!event) return <div className="event-details-error">Event not found</div>;

  const startDate = new Date(event.eventDate);
  const startTime = new Date(`1970-01-01T${event.startTime}`);
  const endTime = event.endTime
    ? new Date(`1970-01-01T${event.endTime}`)
    : null;

  return (
    <div className="event-details-container">
      <div className="event-details-card">
        <Link to="/events" className="back-link">
          <i className="bi bi-arrow-left"></i> Back to Events
        </Link>
        <br />
        <br />

        <div className="event-header">
          <div className="event-image-container">
            <img
              src={
                getImageUrl(event.featuredImage) ||
                `https://via.placeholder.com/300x200?text=${encodeURIComponent(
                  event.eventsCategory
                )}`
              }
              alt={event.title}
              className="event-image"
            />
          </div>

          <h1 className="event-title">{event.title}</h1>
          <div className="event-meta">
            <div className="event-meta-item">
              <i className="bi bi-calendar-event"></i>
              <span>{startDate.toLocaleDateString()}</span>
            </div>
            <div className="event-meta-item">
              <i className="bi bi-clock"></i>
              <span>
                {startTime.toLocaleTimeString([], {
                  hour: "2-digit",
                  minute: "2-digit",
                })}
                {endTime &&
                  ` - ${endTime.toLocaleTimeString([], {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}`}
              </span>
            </div>
            <div className="event-meta-item">
              <i className="bi bi-geo-alt-fill"></i>
              <span>{event.location}</span>
            </div>
          </div>
        </div>

        <div className="event-section">
          <h2>Description</h2>
          <p className="event-description">{event.description}</p>
        </div>

        <div className="event-details-grid">
          <div className="event-details-item">
            <h2>Category</h2>
            <p>{event.eventsCategory}</p>
          </div>
          {event.capacity !== null && (
            <div className="event-details-item">
              <h2>Capacity</h2>
              <p>{event.capacity}</p>
            </div>
          )}
          {event.capacity !== null && (
            <div className="event-details-item">
              <h2>Seats Left</h2>
              <p className={seatsLeft === 0 ? "seats-left-zero" : ""}>
                {seatsLeft !== null ? seatsLeft : "Loading..."}
              </p>
            </div>
          )}
          {event.registrationDeadline && (
            <div className="event-details-item">
              <h2>Registration Deadline</h2>
              <p>{new Date(event.registrationDeadline).toLocaleDateString()}</p>
            </div>
          )}
          <div className="event-details-item">
            <h2>Organizer</h2>
            <p>
              <strong>{event.organizer.username}</strong>
            </p>
            <p>
              <i className="bi bi-envelope"></i> {event.organizer.email}
            </p>
          </div>
        </div>

        <div className="event-registration d-flex justify-content-between flex-wrap align-items-center mb-2">
          <div className="d-flex gap-2">
            {isAuthenticated && (userId === event.organizer.userId || isAdmin === true) && (
              <>
                <Link to={`/events/edit/${event.eventId}`} className="btn btn-secondary text-dark">
                  <i className="bi bi-pencil"></i> Edit
                </Link>
                <button onClick={handleDelete} className="btn btn-danger text-dark">
                  <i className="bi bi-trash"></i> Delete
                </button>
              </>
            )}
          </div>
          <div className="d-flex gap-2">
            {isAuthenticated ? (
              isRegistered ? (
                <>
                  <button className="btn btn-secondary text-dark" disabled>
                    Registered
                  </button>
                  <button className="btn btn-danger text-dark" onClick={handleCancel}>
                    Cancel
                  </button>
                </>
              ) : (
                <button
                  className={`btn btn-primary text-light${seatsLeft === 0 && !isRegistered ? " btn-disabled" : ""}`}
                  onClick={seatsLeft === 0 && !isRegistered ? undefined : handleRegister}>
                  Register Now
                </button>
              )
            ) : (
              <p className="mb-0">
                <Link to="/login">Login</Link> to register for this event.
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default EventDetails;