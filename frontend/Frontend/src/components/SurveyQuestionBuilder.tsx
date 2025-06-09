import React, { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import "./SurveyQuestionBuilder.css";

interface Question {
  questionText: string;
  isRequired: boolean;
  options: string[];
  type: "MULTIPLE_CHOICE" | "DROPDOWN";

}

const SurveyQuestionBuilder: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const surveyId = Number(id);

  const [questions, setQuestions] = useState<Question[]>([
    { questionText: "", isRequired: true, options: [], type: "MULTIPLE_CHOICE" }

  ]);
  const [loading, setLoading] = useState(false);
  const [previewMode, setPreviewMode] = useState(false); // NEW: preview mode state

  const updateQuestionText = (index: number, text: string) => {
    setQuestions((prev) => {
      const updated = [...prev];
      updated[index].questionText = text;
      return updated;
    });
  };

  const updateOptionText = (qIdx: number, oIdx: number, text: string) => {
    setQuestions((prev) => {
      const updated = [...prev];
      updated[qIdx].options[oIdx] = text;
      return updated;
    });
  };

  const updateQuestionType = (index: number, type: Question["type"]) => {
    setQuestions((prev) => {
      const updated = [...prev];
      updated[index].type = type;
      return updated;
    });
  };

  const addOption = (qIdx: number) => {
    setQuestions((prev) => {
      const updated = [...prev];
      updated[qIdx] = {
        ...updated[qIdx],
        options: [...updated[qIdx].options, ""],
      };
      return updated;
    });
  };

  const removeOption = (qIdx: number, oIdx: number) => {
    setQuestions((prev) => {
      const updated = [...prev];
      if (updated[qIdx].options.length > 1) {
        const newOptions = [...updated[qIdx].options];
        newOptions.splice(oIdx, 1);
        updated[qIdx] = {
          ...updated[qIdx],
          options: newOptions,
        };
      }
      return updated;
    });
  };

  const addQuestion = () => {
    setQuestions((prev) => [
      ...prev,
      { questionText: "", isRequired: true, options: [], type: "MULTIPLE_CHOICE" }

    ]);
  };

  const removeQuestion = (index: number) => {
    setQuestions((prev) => (prev.length > 1 ? prev.filter((_, i) => i !== index) : prev));
  };

  const toggleIsRequired = (index: number) => {
    setQuestions((prev) => {
      const updated = [...prev];
      updated[index].isRequired = !updated[index].isRequired;
      return updated;
    });
  };

  const handleFinalSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (questions.some((q) => !q.questionText.trim())) {
      alert("Each question must have text.");
      return;
    }

    if (questions.some((q) => q.options.some((opt) => !opt.trim()))) {
      alert("All options must be filled.");
      return;
    }

    setLoading(true);

    try {
      const token = localStorage.getItem("authToken");

      if (!token || !surveyId) {
        alert("Missing authentication or survey ID.");
        return;
      }

      for (const q of questions) {
        await axios.post(
          "http://localhost:8080/survey/addq",
          {
            survey: { surveyId },
            questionText: q.questionText,
            isRequired: q.isRequired ? 1 : 0,
            type: q.type,
            options: q.options,
          },
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
      }

      alert("Questions submitted successfully.");
      navigate("/survey");
    } catch (error: any) {
      if (axios.isAxiosError(error)) {
        alert(
          `Submission failed: ${
            error.response?.data?.message || error.message || "Unknown error"
          }`
        );
      } else {
        alert("Submission failed due to an unexpected error.");
      }
    } finally {
      setLoading(false);
    }
  };

  if (isNaN(surveyId)) {
    return <div>Invalid survey ID in URL.</div>;
  }

  return (
    <div className="container">
      <h2>Add Questions to Survey (ID: {surveyId})</h2>

      {previewMode && (
  <div className="preview-modal">
    <div className="preview-content">
      <h3>Survey Preview</h3>
      <ul>
        {questions.map((q, i) => (
          <li key={i}>
            <strong>
              Q{i + 1}: {q.questionText} {q.isRequired ? "(Required)" : "(Optional)"}
            </strong>
            <br />
            <em>Type: {q.type}</em>
            <ul>
              {q.options.map((opt, idx) => (
                <li key={idx}>{opt}</li>
              ))}
              {q.options.length === 0 && <li><em>No options</em></li>}
            </ul>
          </li>
        ))}
      </ul>
      <div className="preview-actions">
        <button className="preview-btn cancel-btn" onClick={() => setPreviewMode(false)}>
          Close Preview
        </button>
      </div>
    </div>
  </div>
)}


      {previewMode ? (
        // PREVIEW MODE - readonly view of questions & options
        <div>
          {questions.map((q, i) => (
            <div
              key={i}
              style={{
                border: "1px solid #ccc",
                padding: 16,
                marginBottom: 16,
                backgroundColor: "#f9f9f9",
              }}
            >
              <h4>
                Question {i + 1} {q.isRequired ? "(Required)" : "(Optional)"}
              </h4>
              <p>{q.questionText}</p>
              <p>
  <strong>Type:</strong> {q.type === "MULTIPLE_CHOICE" ? "Multiple Choice" : "Dropdown"}
</p>


              <div>
                <strong>Options:</strong>
                <ul>
                  {q.options.length > 0 ? (
                    q.options.map((opt, idx) => <li key={idx}>{opt}</li>)
                  ) : (
                    <li><em>No options added</em></li>
                  )}
                </ul>
              </div>
            </div>
          ))}
        </div>
      ) : (
        // EDIT MODE (existing form UI)
        <form onSubmit={handleFinalSubmit}>
          {questions.map((question, qIdx) => (
            <div
              key={qIdx}
              style={{ border: "1px solid #ccc", padding: 16, marginBottom: 16 }}
            >
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <h4>Question {qIdx + 1}</h4>
                {questions.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeQuestion(qIdx)}
                    style={{ color: "red" }}
                    aria-label={`Remove question ${qIdx + 1}`}
                  >
                    &times;
                  </button>
                )}
              </div>

              <textarea
                placeholder="Enter question text"
                value={question.questionText}
                onChange={(e) => updateQuestionText(qIdx, e.target.value)}
                rows={3}
                style={{ width: "100%", marginBottom: 8 }}
                required
              />

              {/* <label>
                <input
                  type="checkbox"
                  checked={question.isRequired}
                  onChange={() => toggleIsRequired(qIdx)}
                />{" "}
                Required
              </label> */}

              <div style={{ marginTop: 12 }}>
                <label>
                  Question Type:{" "}
                  <select
  value={question.type}
  onChange={(e) =>
    updateQuestionType(qIdx, e.target.value as Question["type"])
  }
>
  <option value="MULTIPLE_CHOICE">Multiple Choice</option>
  <option value="DROPDOWN">Dropdown</option>
</select>

                </label>

                <div style={{ marginTop: 8 }}>
                  <strong>Options:</strong>
                  {question.options.map((opt, oIdx) => (
                    <div
                      key={oIdx}
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: 8,
                        marginTop: 4,
                      }}
                    >
                      <input
                        type="text"
                        value={opt}
                        onChange={(e) => updateOptionText(qIdx, oIdx, e.target.value)}
                        required
                        style={{ flexGrow: 1 }}
                      />
                      {question.options.length > 1 && (
                        <button
                          type="button"
                          onClick={() => removeOption(qIdx, oIdx)}
                          style={{ color: "red" }}
                          aria-label={`Remove option ${oIdx + 1}`}
                        >
                          &times;
                        </button>
                      )}
                    </div>
                  ))}

                  <button
                    type="button"
                    onClick={() => addOption(qIdx)}
                    style={{ marginTop: 8 }}
                  >
                    + Add Option
                  </button>
                </div>
              </div>
            </div>
          ))}

          <button type="button" onClick={addQuestion}>
            + Add Question
          </button>

          <div style={{ display: "flex", justifyContent: "flex-end", gap: "12px", marginTop: 20 }}>
  <button type="button" onClick={() => setPreviewMode(true)}>
    Preview
  </button>
  <button type="submit" disabled={loading}>
    {loading ? "Submitting..." : "Submit Questions"}
  </button>
</div>

        </form>
      )}
    </div>
  );
};

export default SurveyQuestionBuilder;
