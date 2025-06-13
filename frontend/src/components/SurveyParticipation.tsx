

// import React, { useEffect, useState, useRef } from "react";
// import { useParams, useNavigate } from "react-router-dom";
// import axios from "axios";
// import "./SurveyParticipation.css";



// interface Question {
//   questionId: number;
//   questionText: string;
//   isRequired: number;
//   options: string[];
//   type: "MULTIPLE_CHOICE" | "DROPDOWN";

// }

// interface Answer {
//   questionId: number;
//   selectedOption: string;
// }

// interface User {
//   userId: number;
//   username: string;
//   email: string;
// }

// interface Survey {
//   surveyId: number;
//   title: string;
//   description: string;
//   startDate: string;
//   endDate: string;
//   isAnonymous: boolean;
//   isPublished: boolean;
//   category: string | null;
//   users: User;
//   questions: Question[];
//   timerSeconds?: number;
// }

// const SurveyParticipation: React.FC = () => {
//   const { id } = useParams<{ id: string }>();
//   const surveyId = Number(id);
//   const navigate = useNavigate();

//   const [survey, setSurvey] = useState<Survey | null>(null);
//   const [questions, setQuestions] = useState<Question[]>([]);
//   const [answers, setAnswers] = useState<Answer[]>([]);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState("");

//   const [currentStep, setCurrentStep] = useState(0);
//   const [showPreview, setShowPreview] = useState(false);
//   const [timer, setTimer] = useState<number | null>(null);
//   const timerRef = useRef<NodeJS.Timeout | null>(null);

//   useEffect(() => {
//     const fetchSurvey = async () => {
//       setLoading(true);
//       try {
//         const res = await axios.get(`http://localhost:8080/survey/participate/${id}`);
//         setSurvey(res.data);
//         setQuestions(res.data.questions || []);
//         if (res.data.timerSeconds && res.data.timerSeconds > 0) {
//           setTimer(res.data.timerSeconds);
//         }
//       } catch (error) {
//         setError("Could not load survey. Invalid survey ID or server error.");
//       } finally {
//         setLoading(false);
//       }
//     };

//     if (!isNaN(surveyId)) {
//       fetchSurvey();
//     }
//   }, [surveyId, id]);

//   useEffect(() => {
//     if (timer === null) return;

//     if (timer <= 0) {
//       alert("Time is up! The survey will be submitted automatically.");
//       handleSubmitAuto();
//       return;
//     }

//     timerRef.current = setTimeout(() => {
//       setTimer((prev) => (prev !== null ? prev - 1 : null));
//     }, 1000);

//     return () => {
//       if (timerRef.current) clearTimeout(timerRef.current);
//     };
//   }, [timer]);

//   const handleAnswerChange = (questionId: number, selectedOption: string) => {
//     setAnswers((prev) => {
//       const existing = prev.find((ans) => ans.questionId === questionId);
//       if (existing) {
//         return prev.map((ans) =>
//           ans.questionId === questionId ? { ...ans, selectedOption } : ans
//         );
//       } else {
//         return [...prev, { questionId, selectedOption }];
//       }
//     });
//   };

//   const validateCurrentStep = (): boolean => {
//     const q = questions[currentStep];
//     if (!q) return false;
//     const answer = answers.find((a) => a.questionId === q.questionId);
//     if (q.isRequired === 1 && (!answer || answer.selectedOption.trim() === "")) {
//       alert("Please answer this required question before continuing.");
//       return false;
//     }
//     return true;
//   };

//   const handleNext = () => {
//     if (!validateCurrentStep()) return;
//     setCurrentStep((prev) => Math.min(prev + 1, questions.length - 1));
//   };

//   const handlePrev = () => {
//     setCurrentStep((prev) => Math.max(prev - 1, 0));
//   };

//   const validateAllAnswers = (): boolean => {
//     for (const q of questions) {
//       const answer = answers.find((a) => a.questionId === q.questionId);
//       if (q.isRequired === 1 && (!answer || answer.selectedOption.trim() === "")) {
//         alert("Please answer all required questions.");
//         return false;
//       }
//     }
//     return true;
//   };

//   const submitAnswers = async () => {
//     try {
//       const userIdStr = localStorage.getItem("userId");
//       const token = localStorage.getItem("authToken");
  
//       if (!userIdStr || !token) {
//         alert("User not authenticated.");
//         return;
//       }
  
//       const userId = Number(userIdStr);
  
//       // Submit each answer separately
//       for (const ans of answers) {
//         const payload = {
//           responseId: null, // or omit if backend generates this
//           surveys: { surveyId },
//           users: { userId },
//           question: { questionId: ans.questionId },
//           submittedAt: new Date().toISOString(),
//           answer: ans.selectedOption,
//         };
  
//         await axios.post("http://localhost:8080/survey/submitResponse", payload, {
//           headers: {
//             Authorization: `Bearer ${token}`,
//             "Content-Type": "application/json",
//           },
//         });
//       }
  
//       alert("Thank you for submitting the survey!");
//       navigate("/survey");
//     } catch (error) {
//       alert("Failed to submit survey.");
//       console.error(error);
//     }
//   };
  

//   const handleSubmitAuto = async () => {
//     if (!validateAllAnswers()) return;
//     await submitAnswers();
//   };

//   const handleSubmit = (e: React.FormEvent) => {
//     e.preventDefault();
//     if (!validateAllAnswers()) return;
//     setShowPreview(true);
//   };

//   const confirmSubmit = async () => {
//     setShowPreview(false);
//     await submitAnswers();
//   };

//   if (loading) return <p>Loading survey...</p>;
//   if (error) return <p>{error}</p>;
//   if (!survey) return null;

//   const currentQuestion = questions[currentStep];

//   return (
//     <div className="container">
//       <h1>{survey.title}</h1>
//       <p>{survey.description}</p>
//       <p>
//         Start: {new Date(survey.startDate).toLocaleDateString()} | End:{" "}
//         {new Date(survey.endDate).toLocaleDateString()}
//       </p>
//       <p>Created by: {survey.users.username}</p>

//       {timer !== null && (
//         <div className="timer">
//           Time remaining: {Math.floor(timer / 60).toString().padStart(2, "0")}:
//           {(timer % 60).toString().padStart(2, "0")}
//         </div>
//       )}

//       <h2>
//         Question {currentStep + 1} of {questions.length}
//       </h2>

//       <form onSubmit={handleSubmit}>
//         {currentQuestion ? (
//           <div className="question-block">
//             <label>
//               <strong>
//                 {currentStep + 1}. {currentQuestion.questionText}
//                 {currentQuestion.isRequired === 1 && (
//                   <span className="required-star">*</span>
//                 )}
//               </strong>
//             </label>

//             <div>
//   {currentQuestion.type === "MULTIPLE_CHOICE"  ? (
//     currentQuestion.options.map((opt, idx) => (
//       <label key={idx} className="option-label">
//         <input
//           type="radio"
//           name={`question-${currentQuestion.questionId}`}
//           value={opt}
//           checked={
//             answers.find((a) => a.questionId === currentQuestion.questionId)
//               ?.selectedOption === opt
//           }
//           onChange={() =>
//             handleAnswerChange(currentQuestion.questionId, opt)
//           }
//           required={currentQuestion.isRequired === 1}
//         />
//         {opt}
//       </label>
//     ))
//   ) : (
//     <select
//       value={
//         answers.find((a) => a.questionId === currentQuestion.questionId)
//           ?.selectedOption || ""
//       }
//       onChange={(e) =>
//         handleAnswerChange(currentQuestion.questionId, e.target.value)
//       }
//       required={currentQuestion.isRequired === 1}
//     >
//       <option value="" disabled>
//         -- Select an option --
//       </option>
//       {currentQuestion.options.map((opt, idx) => (
//         <option key={idx} value={opt}>
//           {opt}
//         </option>
//       ))}
//     </select>
//   )}
// </div>

//           </div>
//         ) : (
//           <p>No questions available.</p>
//         )}

//         <div className="navigation-buttons">
//           {currentStep > 0 && (
//             <button type="button" onClick={handlePrev} className="nav-btn">
//               Previous
//             </button>
//           )}
//           {currentStep < questions.length - 1 && (
//             <button type="button" onClick={handleNext} className="nav-btn">
//               Next
//             </button>
//           )}
//           {currentStep === questions.length - 1 && (
//             <button type="submit" className="submit-btn">
//               Preview Survey
//             </button>
//           )}
//         </div>
//       </form>

//       <div className="progress-bar">
//         <div
//           className="progress-fill"
//           style={{ width: `${((currentStep + 1) / questions.length) * 100}%` }}
//         />
//       </div>
//           <div className="container">
//       {showPreview && (
//         <div className="preview-modal" 
//         onClick={() => setShowPreview(false)}>
//           <div className="preview-content"
//            onClick={(e) => e.stopPropagation()} >
//             <h3>Review Your Answers</h3>
//             <ul>
//               {questions.map((q) => {
//                 const ans = answers.find((a) => a.questionId === q.questionId);
//                 return (
//                   <li key={q.questionId}>
//                     <strong>{q.questionText}</strong>
//                     <br />
//                     Your answer: {ans ? ans.selectedOption : "No answer"}
//                   </li>
//                 );
//               })}
//             </ul>
//             <div className="preview-actions">
//               <button
//                 onClick={() => setShowPreview(false)}
//                 className="preview-btn cancel-btn"
//               >
//                 Edit
//               </button>
//               <button onClick={confirmSubmit} className="preview-btn confirm-btn">
//                 Confirm & Submit
//               </button>
//             </div>
//           </div>
//         </div>
//       )}</div>
//     </div>
//   );
// };

// export default SurveyParticipation;


import React, { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./SurveyParticipation.css";

interface Question {
  questionId: number;
  questionText: string;
  isRequired: number;
  options: string[];
  type: "MULTIPLE_CHOICE" | "DROPDOWN";
}

interface Answer {
  questionId: number;
  selectedOption: string;
}

interface User {
  userId: number;
  username: string;
  email: string;
}

interface Survey {
  surveyId: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  isAnonymous: boolean;
  isPublished: boolean;
  category: string | null;
  users: User;
  questions: Question[];
  timerSeconds?: number;
}

const SurveyParticipation: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const surveyId = Number(id);
  const navigate = useNavigate();

  const [survey, setSurvey] = useState<Survey | null>(null);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [answers, setAnswers] = useState<Answer[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [currentStep, setCurrentStep] = useState(0);
  const [showPreview, setShowPreview] = useState(false);
  const [timer, setTimer] = useState<number | null>(null);
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    const fetchSurvey = async () => {
      setLoading(true);
      try {
        const res = await axios.get(`http://localhost:8080/survey/participate/${id}`);
        setSurvey(res.data);
        setQuestions(res.data.questions || []);
        if (res.data.timerSeconds && res.data.timerSeconds > 0) {
          setTimer(res.data.timerSeconds);
        }
      } catch (error) {
        setError("Could not load survey. Invalid survey ID or server error.");
      } finally {
        setLoading(false);
      }
    };

    if (!isNaN(surveyId)) {
      fetchSurvey();
    }
  }, [surveyId, id]);

  useEffect(() => {
    if (timer === null) return;

    if (timer <= 0) {
      toast.info("Time is up! The survey will be submitted automatically.");
      handleSubmitAuto();
      return;
    }

    timerRef.current = setTimeout(() => {
      setTimer((prev) => (prev !== null ? prev - 1 : null));
    }, 1000);

    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, [timer]);

  const handleAnswerChange = (questionId: number, selectedOption: string) => {
    setAnswers((prev) => {
      const existing = prev.find((ans) => ans.questionId === questionId);
      if (existing) {
        return prev.map((ans) =>
          ans.questionId === questionId ? { ...ans, selectedOption } : ans
        );
      } else {
        return [...prev, { questionId, selectedOption }];
      }
    });
  };

  const validateCurrentStep = (): boolean => {
    const q = questions[currentStep];
    if (!q) return false;
    const answer = answers.find((a) => a.questionId === q.questionId);
    if (q.isRequired === 1 && (!answer || answer.selectedOption.trim() === "")) {
      toast.warning("Please answer this required question before continuing.");
      return false;
    }
    return true;
  };

  const handleNext = () => {
    if (!validateCurrentStep()) return;
    setCurrentStep((prev) => Math.min(prev + 1, questions.length - 1));
  };

  const handlePrev = () => {
    setCurrentStep((prev) => Math.max(prev - 1, 0));
  };

  const validateAllAnswers = (): boolean => {
    for (const q of questions) {
      const answer = answers.find((a) => a.questionId === q.questionId);
      if (q.isRequired === 1 && (!answer || answer.selectedOption.trim() === "")) {
        toast.warning("Please answer all required questions.");
        return false;
      }
    }
    return true;
  };

  const submitAnswers = async () => {
    try {
      const userIdStr = localStorage.getItem("userId");
      const token = localStorage.getItem("authToken");

      if (!userIdStr || !token) {
        toast.error("User not authenticated.");
        return;
      }

      const userId = Number(userIdStr);

      for (const ans of answers) {
        const payload = {
          responseId: null,
          surveys: { surveyId },
          users: { userId },
          question: { questionId: ans.questionId },
          submittedAt: new Date().toISOString(),
          answer: ans.selectedOption,
        };

        await axios.post("http://localhost:8080/survey/submitResponse", payload, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
      }

      toast.success("Thank you! Your survey has been submitted.");
      setTimeout(() => {
        navigate("/survey");
      }, 3000);
    } catch (error: any) {
      if (error.response && error.response.status === 409) {
        toast.error("You have already submitted the survey. Your response will not be recorded.");
        setTimeout(() => {
          navigate("/survey");
        }, 3000);
      } else {
        toast.error("You have already submitted the survey. Your response will not be recorded.");
        setTimeout(() => {
          navigate("/survey");
        }, 3000);
      }
      console.error(error);
    }
  };

  const handleSubmitAuto = async () => {
    if (!validateAllAnswers()) return;
    await submitAnswers();
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateAllAnswers()) return;
    setShowPreview(true);
  };

  const confirmSubmit = async () => {
    setShowPreview(false);
    await submitAnswers();
  };

  if (loading) return <p>Loading survey...</p>;
  if (error) return <p>{error}</p>;
  if (!survey) return null;

  const currentQuestion = questions[currentStep];

  return (
    <div className="container">
      <h1>{survey.title}</h1>
      <p>{survey.description}</p>
      <p>
        Start: {new Date(survey.startDate).toLocaleDateString()} | End:{" "}
        {new Date(survey.endDate).toLocaleDateString()}
      </p>
      <p>Created by: {survey.users.username}</p>

      {timer !== null && (
        <div className="timer">
          Time remaining: {Math.floor(timer / 60).toString().padStart(2, "0")}:
          {(timer % 60).toString().padStart(2, "0")}
        </div>
      )}

      <h2>
        Question {currentStep + 1} of {questions.length}
      </h2>

      <form onSubmit={handleSubmit}>
        {currentQuestion ? (
          <div className="question-block">
            <label>
              <strong>
                {currentStep + 1}. {currentQuestion.questionText}
                {currentQuestion.isRequired === 1 && (
                  <span className="required-star">*</span>
                )}
              </strong>
            </label>

            <div>
              {currentQuestion.type === "MULTIPLE_CHOICE" ? (
                currentQuestion.options.map((opt, idx) => (
                  <label key={idx} className="option-label">
                    <input
                      type="radio"
                      name={`question-${currentQuestion.questionId}`}
                      value={opt}
                      checked={
                        answers.find((a) => a.questionId === currentQuestion.questionId)
                          ?.selectedOption === opt
                      }
                      onChange={() =>
                        handleAnswerChange(currentQuestion.questionId, opt)
                      }
                      required={currentQuestion.isRequired === 1}
                    />
                    {opt}
                  </label>
                ))
              ) : (
                <select
                  value={
                    answers.find((a) => a.questionId === currentQuestion.questionId)
                      ?.selectedOption || ""
                  }
                  onChange={(e) =>
                    handleAnswerChange(currentQuestion.questionId, e.target.value)
                  }
                  required={currentQuestion.isRequired === 1}
                >
                  <option value="" disabled>
                    -- Select an option --
                  </option>
                  {currentQuestion.options.map((opt, idx) => (
                    <option key={idx} value={opt}>
                      {opt}
                    </option>
                  ))}
                </select>
              )}
            </div>
          </div>
        ) : (
          <p>No questions available.</p>
        )}

        <div className="navigation-buttons">
          {currentStep > 0 && (
            <button type="button" onClick={handlePrev} className="nav-btn">
              Previous
            </button>
          )}
          {currentStep < questions.length - 1 && (
            <button type="button" onClick={handleNext} className="nav-btn">
              Next
            </button>
          )}
          {currentStep === questions.length - 1 && (
            <button type="submit" className="submit-btn">
              Preview Survey
            </button>
          )}
        </div>
      </form>

      <div className="progress-bar">
        <div
          className="progress-fill"
          style={{ width: `${((currentStep + 1) / questions.length) * 100}%` }}
        />
      </div>

      {showPreview && (
        <div className="preview-modal" onClick={() => setShowPreview(false)}>
          <div className="preview-content" onClick={(e) => e.stopPropagation()}>
            <h3>Review Your Answers</h3>
            <ul>
              {questions.map((q) => {
                const ans = answers.find((a) => a.questionId === q.questionId);
                return (
                  <li key={q.questionId}>
                    <strong>{q.questionText}</strong>
                    <br />
                    Your answer: {ans ? ans.selectedOption : "No answer"}
                  </li>
                );
              })}
            </ul>
            <div className="preview-actions">
              <button onClick={() => setShowPreview(false)} className="preview-btn cancel-btn">
                Edit
              </button>
              <button onClick={confirmSubmit} className="preview-btn confirm-btn">
                Confirm & Submit
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Toast notifications */}
      <ToastContainer />
    </div>
  );
};

export default SurveyParticipation;
