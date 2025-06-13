
// import React, { useEffect, useState } from "react";
// import axios from "axios";
// import "./SurveyList.css";
// import { useNavigate } from "react-router-dom";
// import { useAuth } from "../AuthContext";

// interface SurveyDTO {
//   surveyId: number;
//   title: string;
//   description: string;
//   startDate: string;
//   endDate: string;
//   surveyType?: string;
//   category?: string;
//   isTaken?: boolean;
// }

// const SurveyList: React.FC = () => {
//   const [surveys, setSurveys] = useState<SurveyDTO[]>([]);
//   const [searchTerm, setSearchTerm] = useState("");
//   const [selectedCategory, setSelectedCategory] = useState("");
//   const [selectedDate, setSelectedDate] = useState("");
//   const [error, setError] = useState<string>("");
//   const [loading, setLoading] = useState<boolean>(false);
//   const [isViewingResults, setIsViewingResults] = useState(false);

//   const { isAdmin, token } = useAuth();
//   const navigate = useNavigate();

//   const surveyCategories = [
//     "NEWS",
//     "DISCUSSION",
//     "FEEDBACK",
//     "EVENTS",
//     "CONTENT",
//   ];

//   useEffect(() => {
//     fetchActiveSurveys();
//   }, []);

//   const fetchActiveSurveys = () => {
//     setLoading(true);
//     setError("");
//     setIsViewingResults(false);
//     axios
//       .get<SurveyDTO[]>("http://localhost:8080/survey/viewAll", {
//         headers: {
//           "Content-Type": "application/json",
//           Accept: "application/json",
//         },
//       })
//       .then((response) => {
//         const normalized = response.data.map((survey) => ({
//           ...survey,
//           category: survey.surveyType ?? survey.category,
//         }));
//         setSurveys(normalized);
//         setLoading(false);
//       })
//       .catch((err) => {
//         setError("Failed to fetch active surveys");
//         setLoading(false);
//         console.error(err);
//       });
//   };

//   const fetchExpiredSurveys = () => {
//     setLoading(true);
//     setError("");
//     setIsViewingResults(true);
//     axios
//       .get<SurveyDTO[]>("http://localhost:8080/survey/viewExpired", {
//         headers: {
//           "Content-Type": "application/json",
//           Accept: "application/json",
//         },
//       })
//       .then((response) => {
//         const normalized = response.data.map((survey) => ({
//           ...survey,
//           category: survey.surveyType ?? survey.category,
//         }));
//         setSurveys(normalized);
//         setLoading(false);
//       })
//       .catch((err) => {
//         setError("Failed to fetch expired surveys");
//         setLoading(false);
//         console.error(err);
//       });
//   };

//   const handleDeleteSurvey = (surveyId: number) => {
//     if (!window.confirm("Are you sure you want to delete this survey?")) return;

//     axios
//       .delete(`http://localhost:8080/survey/delete/${surveyId}`, {
//         headers: {
//           Authorization: `Bearer ${token}`,
//           "Content-Type": "application/json",
//         },
//       })
//       .then(() => {
//         setSurveys((prev) => prev.filter((s) => s.surveyId !== surveyId));
//       })
//       .catch((err) => {
//         console.error("Delete failed", err);
//         alert("Failed to delete survey.");
//       });
//   };

//   const handleViewSurveyClick = () => {
//     fetchActiveSurveys();
//     setIsViewingResults(false);
//   };

//   const handleViewResultClick = () => {
//     fetchExpiredSurveys();
//     setIsViewingResults(true);
//   };

//   const handleCardButtonClick = (surveyId: number) => {
//     if (isViewingResults) {
//       navigate(`/survey/results/${surveyId}`);
//     } else {
//       navigate(`/survey/participation/${surveyId}`);
//     }
//   };

//   const filteredSurveys = surveys.filter((survey) => {
//     const matchesSearch = survey.title
//       .toLowerCase()
//       .includes(searchTerm.toLowerCase());
//     const matchesCategory = selectedCategory
//       ? survey.category === selectedCategory
//       : true;
//     const matchesDate = selectedDate
//       ? new Date(survey.endDate) <= new Date(selectedDate)
//       : true;
//     return matchesSearch && matchesCategory && matchesDate;
//   });

//   return (
//     <div className="survey-page-background">
//       <div className="survey-page-container">
//         <div className="survey-controls">
//           <span className="survey-label">SURVEYS</span>
//           <div className="survey-filters">
//             <input
//               type="text"
//               placeholder="Search..."
//               value={searchTerm}
//               onChange={(e) => setSearchTerm(e.target.value)}
//               className="search-bar"
//             />
//             <select
//               value={selectedCategory}
//               onChange={(e) => setSelectedCategory(e.target.value)}
//               className="category-filter"
//             >
//               <option value="">All Categories</option>
//               {surveyCategories.map((category) => (
//                 <option key={category} value={category}>
//                   {category}
//                 </option>
//               ))}
//             </select>
//             <input
//               type="date"
//               value={selectedDate}
//               onChange={(e) => setSelectedDate(e.target.value)}
//               className="date-filter"
//             />
//           </div>
//         </div>

//         {isAdmin && (
//           <div className="admin-controls">
//             <button
//               className="create-survey-button"
//               onClick={() => navigate("/survey/create")}
//             >
//               Create Survey
//             </button>
//           </div>
//         )}

//         <div className="button-toggle-container">
//           <button
//             className={`view-survey-button ${!isViewingResults ? "active" : ""}`}
//             onClick={handleViewSurveyClick}
//           >
//             View Survey
//           </button>
//           <button
//             className={`view-result-button ${isViewingResults ? "active" : ""}`}
//             onClick={handleViewResultClick}
//           >
//             Survey Result
//           </button>
//         </div>

//         {error && <p className="error-message">{error}</p>}
//         {loading && <p className="loading-message">Loading surveys...</p>}

//         <div className="survey-list-wrapper">
//           <div className="survey-list-container">
//             {filteredSurveys.length > 0 ? (
//               filteredSurveys.map((survey) => {
//                 const end = new Date(survey.endDate);
//                 const now = new Date();
//                 const remainingDays = Math.ceil(
//                   (end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)
//                 );
//                 const daysText =
//                   remainingDays > 0 ? `${remainingDays} day(s) left` : "Expired";

//                 return (
//                   <div key={survey.surveyId} className="survey-card">
//                     <div className="survey-card-header">
//                       <span className="survey-category-label">{survey.category}</span>
//                       <span className="survey-days-left">
//                         {isViewingResults ? "Completed" : daysText}
//                       </span>
//                     </div>
//                     <h3 className="survey-card-title">{survey.title}</h3>

//                     {!isViewingResults && (
//                       <div className="survey-description-row">
//                         <p className="survey-description-preview">{survey.description}</p>
//                         <div className="survey-status">
//                           {survey.isTaken ? (
//                             <span className="status-label taken">Taken</span>
//                           ) : (() => {
//                             const now = new Date();
//                             const start = new Date(survey.startDate);
//                             const end = new Date(survey.endDate);
//                             const isWithinDateRange = now >= start && now <= end;

//                             return (
//                               <button
//                                 className="take-survey-button"
//                                 onClick={() => handleCardButtonClick(survey.surveyId)}
//                                 disabled={!isWithinDateRange}
//                                 title={
//                                   !isWithinDateRange ? "Survey is not active right now" : ""
//                                 }
//                               >
//                                 Take Survey
//                               </button>
//                             );
//                           })()}
//                         </div>
//                       </div>
//                     )}

//                     {isViewingResults && (
//                       <div className="survey-status" style={{ textAlign: "right" }}>
//                         <button
//                           className="take-survey-button"
//                           onClick={() => handleCardButtonClick(survey.surveyId)}
//                         >
//                           View Result
//                         </button>
//                       </div>
//                     )}

//                     {isAdmin && (
//                       <div className="survey-delete-wrapper">
//                         <button
//                           className="delete-survey-button"
//                           onClick={() => handleDeleteSurvey(survey.surveyId)}
//                         >
//                           Delete
//                         </button>
//                       </div>
//                     )}
//                   </div>
//                 );
//               })
//             ) : (
//               <p className="no-surveys-message">No surveys match the criteria.</p>
//             )}
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default SurveyList;




import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";
import "./SurveyList.css";

interface SurveyDTO {
  surveyId: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  surveyType?: string;
  category?: string;
  isTaken?: boolean;
}

const SurveyList: React.FC = () => {
  const [surveys, setSurveys] = useState<SurveyDTO[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [isViewingResults, setIsViewingResults] = useState(false);

  const { isAdmin, token } = useAuth();
  const navigate = useNavigate();

  const surveyCategories = ["NEWS", "DISCUSSION", "FEEDBACK", "EVENTS", "CONTENT"];

  useEffect(() => {
    fetchActiveSurveys();
  }, []);

  const fetchActiveSurveys = () => {
    setLoading(true);
    setError("");
    setIsViewingResults(false);
    axios
      .get<SurveyDTO[]>("http://localhost:8080/survey/viewAll")
      .then((response) => {
        const normalized = response.data.map((survey) => ({
          ...survey,
          category: survey.surveyType ?? survey.category,
        }));
        setSurveys(normalized);
        setLoading(false);
      })
      .catch((err) => {
        setError("Failed to fetch active surveys");
        setLoading(false);
        console.error(err);
      });
  };

  const fetchExpiredSurveys = () => {
    setLoading(true);
    setError("");
    setIsViewingResults(true);
    axios
      .get<SurveyDTO[]>("http://localhost:8080/survey/viewExpired")
      .then((response) => {
        const normalized = response.data.map((survey) => ({
          ...survey,
          category: survey.surveyType ?? survey.category,
        }));
        setSurveys(normalized);
        setLoading(false);
      })
      .catch((err) => {
        setError("Failed to fetch expired surveys");
        setLoading(false);
        console.error(err);
      });
  };

  const handleDeleteSurvey = (surveyId: number) => {
    if (!window.confirm("Are you sure you want to delete this survey?")) return;

    axios
      .delete(`http://localhost:8080/survey/delete/${surveyId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => {
        setSurveys((prev) => prev.filter((s) => s.surveyId !== surveyId));
      })
      .catch((err) => {
        console.error("Delete failed", err);
        alert("Failed to delete survey.");
      });
  };

  const handleCardButtonClick = (surveyId: number) => {
    navigate(isViewingResults ? `/survey/results/${surveyId}` : `/survey/participation/${surveyId}`);
  };

  const filteredSurveys = surveys.filter((survey) => {
    const matchesSearch = survey.title.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory ? survey.category === selectedCategory : true;
    const matchesDate = selectedDate ? new Date(survey.endDate) <= new Date(selectedDate) : true;
    return matchesSearch && matchesCategory && matchesDate;
  });

  return (
    <div className="survey-page-background">
      <div className="survey-page-container">
        <div className="survey-controls">
          <span className="survey-label">SURVEYS</span>
          <div className="survey-filter-row">
            <div className="survey-filters-with-button">
              <div className="survey-filters">
                <input
                  type="text"
                  placeholder="Search..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="search-bar"
                />
                <select
                  value={selectedCategory}
                  onChange={(e) => setSelectedCategory(e.target.value)}
                  className="category-filter"
                >
                  <option value="">All Categories</option>
                  {surveyCategories.map((category) => (
                    <option key={category} value={category}>
                      {category}
                    </option>
                  ))}
                </select>
                <input
                  type="date"
                  value={selectedDate}
                  onChange={(e) => setSelectedDate(e.target.value)}
                  className="date-filter"
                />
              </div>
              {isAdmin && (
                <button className="create-survey-button" onClick={() => navigate("/survey/create")}>
                  Create Survey
                </button>
              )}
            </div>
          </div>
        </div>

        <div className="button-toggle-container">
          <button
            className={`view-survey-button ${!isViewingResults ? "active" : ""}`}
            onClick={fetchActiveSurveys}
          >
            View Survey
          </button>
          <button
            className={`view-result-button ${isViewingResults ? "active" : ""}`}
            onClick={fetchExpiredSurveys}
          >
            Survey Result
          </button>
        </div>

        {error && <p className="error-message">{error}</p>}
        {loading && <p className="loading-message">Loading surveys...</p>}

        <div className="survey-list-wrapper">
          <div className="survey-list-container">
            {filteredSurveys.length > 0 ? (
              filteredSurveys.map((survey) => {
                const end = new Date(survey.endDate);
                const now = new Date();
                const remainingDays = Math.ceil((end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
                const daysText = remainingDays > 0 ? `${remainingDays} day(s) left` : "Expired";

                return (
                  <div key={survey.surveyId} className="survey-card">
                    <div className="survey-card-header">
                      <span className="survey-category-label">{survey.category}</span>
                      <span className="survey-days-left">
                        {isViewingResults ? "Completed" : daysText}
                      </span>
                    </div>
                    <h3 className="survey-card-title">{survey.title}</h3>
                    <p className="survey-description-preview">{survey.description}</p>

                    <div className="survey-card-actions-row">
                      <div className="survey-card-actions">
                        {!isViewingResults ? (
                          survey.isTaken ? (
                            <span className="status-label taken">Already Taken</span>
                          ) : (
                            <button
                              className="take-survey-button"
                              onClick={() => handleCardButtonClick(survey.surveyId)}
                              disabled={new Date(survey.startDate) > now || new Date(survey.endDate) < now}
                            >
                              Take Survey
                            </button>
                          )
                        ) : (
                          <button
                            className="take-survey-button"
                            onClick={() => handleCardButtonClick(survey.surveyId)}
                          >
                            View Result
                          </button>
                        )}
                      </div>

                      {isAdmin && (
                        <button
                          className="delete-survey-button"
                          onClick={() => handleDeleteSurvey(survey.surveyId)}
                        >
                          Delete
                        </button>
                      )}
                    </div>
                  </div>
                );
              })
            ) : (
              <p className="no-surveys-message">No surveys match the selected filters.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SurveyList;
