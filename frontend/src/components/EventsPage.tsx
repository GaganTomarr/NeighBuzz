import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../AuthContext";
import axios from "axios";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./EventsPage.css";

const apiClient = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

interface Event {
  eventId: number;
  title: string;
  description: string;
  location: "GENERAL" | "MUMBAI" | "DELHI" | "BENGALURU" | "HYDERABAD" | "AHMEDABAD" | "CHENNAI" | "KOLKATA" | "SURAT" | "PUNE" | "JAIPUR";
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
  organizer: {
    userId: number;
    username: string;
    email: string;
  };
}

const EventsPage: React.FC = () => {
  const [events, setEvents] = useState<Event[]>([]);
  const [categoryFilter, setCategoryFilter] = useState("");
  const [locationFilter, setLocationFilter] = useState("");
  const [dateFilter, setDateFilter] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [currentPage, setCurrentPage] = useState(1);
  const [eventsPerPage] = useState(6);
  const { token } = useAuth();
  const { isAuthenticated, userId, isAdmin } = useAuth();

  const eventCategories = [
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

  useEffect(() => {
    fetchEvents();
  }, [userId, isAdmin]);

  const fetchEvents = async () => {
    if (userId === null) {
      await fetchEventsForParams(0, false);
    } else {
      await fetchEventsForParams(userId, isAdmin);
    }
  };

  const fetchEventsForParams = async (uid: number, adminFlag: boolean) => {
    try {
      setLoading(true);
      const response = await apiClient.get("/events/getAll", {
        params: {
          userId: uid,
          isAdmin: adminFlag,
        },
      });
      setEvents(response.data);
      setError(null);
    } catch (err: any) {
      if (err.response) {
        setError(
          `Error ${err.response.status}: ${err.response.data.message || "Server error"}`
        );
      } else if (err.request) {
        setError("No response from server. CORS issue may be blocking the request.");
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

  const handleLocationChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setLocationFilter(e.target.value);
    setCurrentPage(1);
  };

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDateFilter(e.target.value);
    setCurrentPage(1);
  };

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
    setCurrentPage(1);
  };

  const getImageUrl = (imagePath: string | null) => {
    if (!imagePath) return null;
    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
      return imagePath;
    }
    return `http://localhost:8080/${imagePath.replace(/^\//, "")}`;
  };

  const filteredEvents = events
    .filter((event) => {
      const matchesCategory = categoryFilter
        ? event.eventsCategory === categoryFilter
        : true;

      const matchesLocation = locationFilter
        ? event.location === locationFilter
        : true;

      const matchesDate = dateFilter
        ? new Date(event.eventDate) >= new Date(dateFilter)
        : true;

      const matchesSearch = searchQuery
        ? event.title.toLowerCase().includes(searchQuery.toLowerCase())
        : true;

      return matchesCategory && matchesLocation && matchesDate && matchesSearch;
    })
    .sort((a, b) => {
      return new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime();
    });

  const indexOfLastEvent = currentPage * eventsPerPage;
  const indexOfFirstEvent = indexOfLastEvent - eventsPerPage;
  const currentEvents = filteredEvents.slice(indexOfFirstEvent, indexOfLastEvent);

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);
  const totalPages = Math.ceil(filteredEvents.length / eventsPerPage);

  // Approve event and add moderation log
  const handleApprove = async (eventId: number) => {
    if (!userId) {
      alert("User ID missing. Cannot approve.");
      return;
    }
    try {
      // 1. Approve event
      await apiClient.put(
        `/events/approve/${eventId}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      // 2. Add mod log approve
      await apiClient.post(
        `/events/modLogApprove/${eventId}/${userId}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      alert("Event approved and logged successfully.");
      fetchEvents();
    } catch (err) {
      console.error("Failed to approve event or log moderation:", err);
      alert("Failed to approve event or log moderation. Please try again.");
    }
  };

  // Reject event and add moderation log
  const handleReject = async (eventId: number) => {
    if (!userId) {
      alert("User ID missing. Cannot reject.");
      return;
    }

    const confirmDelete = window.confirm(
      "Are you sure you want to reject and delete this event?"
    );
    if (!confirmDelete) return;

    try {
      // 1. Reject/delete event
      await apiClient.delete(`/events/delete`, {
        params: { eId: eventId },
        headers: { Authorization: `Bearer ${token}` },
      });

      // 2. Add mod log reject
      await apiClient.post(
        `/events/modLogReject/${eventId}/${userId}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      alert("Event rejected, deleted, and logged successfully.");
      fetchEvents();
    } catch (err) {
      console.error("Failed to reject event or log moderation:", err);
      alert("Failed to reject event or log moderation. Please try again.");
    }
  };

  return (
    <div className="events-page-background">
      <div className="events-page-container">
        {/* <div className="events-controls flex flex-wrap items-center gap-4 mb-6">
          <div className="events-title font-bold text-xl">
            EVENTS
          </div>

          <div className="filter-item">
            <select
              value={categoryFilter}
              onChange={handleCategoryChange}
              className="filter-select"
            >
              <option value="">All Categories</option>
              {eventCategories.map((category) => (
                <option key={category} value={category}>
                  {category}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-item">
            <select
              value={locationFilter}
              onChange={handleLocationChange}
              className="filter-select"
            >
              <option value="">All Locations</option>
              {locationOptions.map((location) => (
                <option key={location} value={location}>
                  {getLocationDisplayName(location)}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-item">
            <input
              type="date"
              value={dateFilter}
              onChange={handleDateChange}
              className="filter-date-input"
            />
          </div>

          <div className="search-container relative">
            <input
              type="text"
              placeholder="Search events..."
              className="search-input pl-10 pr-3 py-2 border border-gray-300 rounded-full"
              onChange={handleSearch}
              value={searchQuery}
            />
          </div>

          {isAuthenticated && (
            <div className="add-new-event-section">
              <Link to="/events/create" className="add-new-event-button">
                <i className="bi bi-calendar-plus"></i> Add New Event
              </Link>
            </div>
          )}
        </div> */}
        <div className="events-controls">
          <div className="events-title">
            EVENTS
          </div>

          <div className="filter-controls">
            <div className="filter-item">
              <select
                value={categoryFilter}
                onChange={handleCategoryChange}
                className="filter-select"
              >
                <option value="">All Categories</option>
                {eventCategories.map((category) => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-item">
              <select
                value={locationFilter}
                onChange={handleLocationChange}
                className="filter-select"
              >
                <option value="">All Locations</option>
                {locationOptions.map((location) => (
                  <option key={location} value={location}>
                    {getLocationDisplayName(location)}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-item">
              <input
                type="date"
                value={dateFilter}
                onChange={handleDateChange}
                className="filter-date-input"
              />
            </div>

            <div className="search-container">
              <input
                type="text"
                placeholder="Search events..."
                className="search-input"
                onChange={handleSearch}
                value={searchQuery}
              />
            </div>

            {isAuthenticated && (
              <div className="add-new-event-section">
                <Link to="/events/create" className="add-new-event-button">
                  <i className="bi bi-calendar-plus"></i> Add New Event
                </Link>
              </div>
            )}
          </div>
        </div>


        {loading ? (
          <p>Loading events...</p>
        ) : error ? (
          <p className="error-message">{error}</p>
        ) : (
          <>
            <div className="events-list-container">
              {currentEvents.length > 0 ? (
                currentEvents.map((event) => (
                  <div
                    key={event.eventId}
                    className={`event-card ${!event.isPublished ? "event-unpublished" : ""
                      }`}
                  >
                    <img
                      src={
                        getImageUrl(event.featuredImage) ||
                        `https://via.placeholder.com/300x200?text=${encodeURIComponent(
                          event.eventsCategory
                        )}`
                      }
                      alt={event.title}
                      className="event-card-image"
                      onError={(e) => {
                        const target = e.target as HTMLImageElement;
                        target.onerror = null;
                        target.src = `https://via.placeholder.com/300x200?text=${encodeURIComponent(
                          event.eventsCategory
                        )}`;
                      }}
                    />

                    {/* <div className="event-card-content">
                      <h3>{event.title}</h3>

                      <p className="event-datetime">
                        <i className="bi bi-calendar-event"></i>{" "}
                        {new Date(event.eventDate).toLocaleDateString()} |{" "}
                        <i className="bi bi-clock"></i> {event.startTime}
                        {event.endTime && ` - ${event.endTime}`}
                      </p>

                      {!event.isPublished && (
                        <p className="text-yellow-600 font-semibold mb-2">
                          Pending Approval
                        </p>
                      )}

                      <p className="event-description-preview">{event.description}</p>

                      <p className="event-location">
                        <i className="bi bi-geo-alt-fill"></i> {getLocationDisplayName(event.location)}
                      </p>

                      <Link
                        to={`/events/${event.eventId}`}
                        className="view-details-button"
                      >
                        View Details
                      </Link>

                      {isAdmin && !event.isPublished && (
                        <div className="admin-actions mt-2">
                          <button
                            className="approve-btn mr-2 bg-green-500 text-white px-3 py-1 rounded"
                            onClick={() => handleApprove(event.eventId)}
                          >
                            Approve
                          </button>
                          <button
                            className="reject-btn bg-red-500 text-white px-3 py-1 rounded"
                            onClick={() => handleReject(event.eventId)}
                          >
                            Reject
                          </button>
                        </div>
                      )}
                    </div> */}
                    <div className="event-card-content">
                      <h3>{event.title}</h3>

                      {!event.isPublished && (
                        <p className="text-yellow-600 font-semibold mb-2">
                          Pending Approval
                        </p>
                      )}

                      <div className="event-meta-line">
                        <div className="event-datetime">
                          <i className="bi bi-calendar-event"></i>
                          {new Date(event.eventDate).toLocaleDateString()}
                        </div>
                        <div className="event-datetime">
                          <i className="bi bi-clock"></i>
                          {event.startTime}{event.endTime && ` - ${event.endTime}`}
                        </div>
                        <div className="event-location">
                          <i className="bi bi-geo-alt-fill"></i>
                          {getLocationDisplayName(event.location)}
                        </div>
                        <div className="event-category">
                          {event.eventsCategory}
                        </div>
                      </div>

                      <p className="event-description-preview">{event.description}</p>

                      <Link
                        to={`/events/${event.eventId}`}
                        className="view-details-button"
                      >
                        View Details
                      </Link>

                      {isAdmin && !event.isPublished && (
                        <div className="admin-actions">
                          <button
                            className="approve-btn"
                            onClick={() => handleApprove(event.eventId)}
                          >
                            Approve
                          </button>
                          <button
                            className="reject-btn"
                            onClick={() => handleReject(event.eventId)}
                          >
                            Reject
                          </button>
                        </div>
                      )}
                    </div>

                  </div>
                ))
              ) : (
                <p className="no-events-message">
                  No events found matching your criteria.
                </p>
              )}
            </div>

            {filteredEvents.length > eventsPerPage && (
              <div className="pagination-container mt-6 flex justify-center">
                <ul className="pagination flex gap-2">
                  <li className={`page-item ${currentPage === 1 ? "disabled" : ""}`}>
                    <button
                      onClick={() => currentPage > 1 && paginate(currentPage - 1)}
                      className={`page-link px-3 py-1 rounded ${currentPage === 1
                        ? "bg-gray-200 text-gray-500 cursor-not-allowed"
                        : "bg-gray-200 hover:bg-gray-300"
                        }`}
                      disabled={currentPage === 1}
                    >
                      Previous
                    </button>
                  </li>

                  {Array.from({ length: totalPages }, (_, i) => i + 1).map(
                    (number) => (
                      <li key={number} className="page-item">
                        <button
                          onClick={() => paginate(number)}
                          className={`page-link px-3 py-1 rounded ${currentPage === number
                            ? "bg-blue-600 text-white"
                            : "bg-gray-200 hover:bg-gray-300"
                            }`}
                        >
                          {number}
                        </button>
                      </li>
                    )
                  )}

                  <li
                    className={`page-item ${currentPage === totalPages ? "disabled" : ""
                      }`}
                  >
                    <button
                      onClick={() =>
                        currentPage < totalPages && paginate(currentPage + 1)
                      }
                      className={`page-link px-3 py-1 rounded ${currentPage === totalPages
                        ? "bg-gray-200 text-gray-500 cursor-not-allowed"
                        : "bg-gray-200 hover:bg-gray-300"
                        }`}
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

export default EventsPage;