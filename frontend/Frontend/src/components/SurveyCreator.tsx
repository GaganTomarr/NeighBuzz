

// import React, { useState } from "react";
// import { useNavigate } from "react-router-dom";
// import axios from "axios";
// import "./SurveyCreator.css";

// const categories = [
//   "NEWS",
//   "DISCUSSION",
//   "FEEDBACK",
//   "EVENTS",
//   "CONTENT"
// ];

// const SurveyCreator: React.FC = () => {
//   const [title, setTitle] = useState("");
//   const [description, setDescription] = useState("");
//   const [startDate, setStartDate] = useState("");
//   const [endDate, setEndDate] = useState("");
//   const [isAnonymous, setIsAnonymous] = useState(false);
//   const [category, setCategory] = useState(categories[0]);

//   const [loading, setLoading] = useState(false);
//   const [surveyCreated, setSurveyCreated] = useState(false);

//   const navigate = useNavigate();

//   const handleMetadataSubmit = async (e: React.FormEvent) => {
//     e.preventDefault();

//     if (surveyCreated || loading) return;

//     setLoading(true);

//     try {
//       const userId = localStorage.getItem("userId");
//       const token = localStorage.getItem("authToken");

//       if (!userId || !token) {
//         alert("User not authenticated.");
//         return;
//       }

//       const surveyDTO = {
//         users: { userId },
//         title,
//         description,
//         startDate: new Date(startDate).toISOString(),
//         endDate: new Date(endDate).toISOString(),
//         isAnonymous,
//         isPublished: false,
//         surveyType: category,  // <-- added category here
//       };

//       const res = await axios.post("http://localhost:8080/survey/adds", surveyDTO, {
//         headers: {
//           "Content-Type": "application/json",
//           Authorization: `Bearer ${token}`,
//         },
//       });

//       const surveyId = res.data;

//       localStorage.setItem("surveyMetadata", JSON.stringify(surveyDTO));
//       localStorage.setItem("surveyId", surveyId);

//       setSurveyCreated(true);

//       alert("Survey metadata saved. Redirecting to question builder...");
//       navigate(`/survey/questions/${surveyId}`);
//     } catch (error) {
//       console.error("Failed to create survey metadata:", error);
//       alert("Error saving survey metadata.");
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="survey-creator-container">
//       <h2>Create Survey</h2>
//       <form onSubmit={handleMetadataSubmit}>
//         <label>
//           Title:
//           <input
//             type="text"
//             value={title}
//             onChange={(e) => setTitle(e.target.value)}
//             required
//           />
//         </label>

//         <label>
//           Description:
//           <textarea
//             value={description}
//             onChange={(e) => setDescription(e.target.value)}
//           />
//         </label>

//         <label>
//           Category:
//           <select
//             value={category}
//             onChange={(e) => setCategory(e.target.value)}
//             required
//           >
//             {categories.map((cat) => (
//               <option key={cat} value={cat}>{cat}</option>
//             ))}
//           </select>
//         </label>

//         <label>
//           Start Date:
//           <input
//             type="date"
//             value={startDate}
//             onChange={(e) => setStartDate(e.target.value)}
//             required
//           />
//         </label>

//         <label>
//           End Date:
//           <input
//             type="date"
//             value={endDate}
//             onChange={(e) => setEndDate(e.target.value)}
//             required
//           />
//         </label>

//         <label>
//           <input
//             type="checkbox"
//             checked={isAnonymous}
//             onChange={(e) => setIsAnonymous(e.target.checked)}
//           />
//           Anonymous Survey
//         </label>

//         <button type="submit" disabled={loading || surveyCreated}>
//           {loading ? "Submitting..." : "Continue to Questions"}
//         </button>
//       </form>
//     </div>
//   );
// };

// export default SurveyCreator;


import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./SurveyCreator.css";

const categories = [
  "NEWS",
  "DISCUSSION",
  "FEEDBACK",
  "EVENTS",
  "CONTENT"
];

const SurveyCreator: React.FC = () => {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [isAnonymous, setIsAnonymous] = useState(false);
  const [category, setCategory] = useState(categories[0]);

  const [loading, setLoading] = useState(false);
  const [surveyCreated, setSurveyCreated] = useState(false);

  const navigate = useNavigate();

  const handleMetadataSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (surveyCreated || loading) return;

    setLoading(true);

    try {
      const userId = localStorage.getItem("userId");
      const token = localStorage.getItem("authToken");

      if (!userId || !token) {
        alert("User not authenticated.");
        return;
      }

      const surveyDTO = {
        users: { userId },
        title,
        description,
        startDate: new Date(startDate).toISOString(),
        endDate: new Date(endDate).toISOString(),
        isAnonymous,
        isPublished: false,
        category: category
      };

      const res = await axios.post("http://localhost:8080/survey/adds", surveyDTO, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      const surveyId = res.data;

      localStorage.setItem("surveyMetadata", JSON.stringify(surveyDTO));
      localStorage.setItem("surveyId", surveyId);

      setSurveyCreated(true);

      alert("Survey metadata saved. Redirecting to question builder...");
      navigate(`/survey/questions/${surveyId}`);
    } catch (error) {
      console.error("Failed to create survey metadata:", error);
      alert("Error saving survey metadata.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="survey-creator-container">
      <h2>Create Survey</h2>
      <form onSubmit={handleMetadataSubmit}>
        <label>
          Title:
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </label>

        <label>
          Description:
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </label>

        <label>
          Category:
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            required
          >
            {categories.map((cat) => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </select>
        </label>

        <label>
          Start Date:
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            required
          />
        </label>

        <label>
          End Date:
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            required
          />
        </label>

        {/* <label className="checkbox-label">
          <input
            type="checkbox"
            checked={isAnonymous}
            onChange={(e) => setIsAnonymous(e.target.checked)}
          />
          Anonymous Survey
        </label> */}

        <button type="submit" disabled={loading || surveyCreated}>
          {loading ? "Submitting..." : "Continue to Questions"}
        </button>
      </form>
    </div>
  );
};

export default SurveyCreator;
