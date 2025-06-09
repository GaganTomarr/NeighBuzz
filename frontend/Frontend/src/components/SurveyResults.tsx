import React, { useEffect, useState } from "react";
import axios from "axios";
import "./SurveyResults.css";

interface OptionResult {
  optionText: string;
  voteCount: number;
  percentage?: number;
}

interface QuestionResult {
  questionId: number;
  questionText: string;
  options: OptionResult[];
}

interface QuestionStatsDTO {
  questionId: number;
  answerOption: string;
  count: number;
  percentage: number;
}

interface SurveyResultProps {
  surveyId: number;
}

interface SurveysDTO {
  surveyId: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  isAnonymous: boolean;
  isPublished: boolean;
  category: string;
  users: UsersDTO;
  questions: SurveyQuestionsDTO[];
}

interface SurveyQuestionsDTO {
  questionId: number;
  questionText: string;
  isRequired: boolean;
  options: string[];
}

interface UsersDTO {
  userId: number;
  name: string;
  email: string;
}

const SurveyResults: React.FC<SurveyResultProps> = ({ surveyId }) => {
  const [results, setResults] = useState<QuestionResult[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        const [statsResponse, surveyResponse] = await Promise.all([
          axios.get<QuestionStatsDTO[]>(`http://localhost:8080/survey/${surveyId}/statistics`),
          axios.get<SurveysDTO>(`http://localhost:8080/survey/view/${surveyId}`)
        ]);

        const stats = statsResponse.data;
        const surveyData = surveyResponse.data;

        const questionTextMap: Record<number, string> = {};

        if (Array.isArray(surveyData.questions)) {
          surveyData.questions.forEach((q) => {
            questionTextMap[q.questionId] = q.questionText;
          });
        } else {
          throw new Error("Questions array is missing from the survey data.");
        }

        const grouped: Record<number, QuestionResult> = {};

        stats.forEach((item) => {
          const { questionId, answerOption, count, percentage } = item;
          if (!grouped[questionId]) {
            grouped[questionId] = {
              questionId,
              questionText: questionTextMap[questionId] || `Question ${questionId}`,
              options: [],
            };
          }

          grouped[questionId].options.push({
            optionText: answerOption,
            voteCount: count,
            percentage: percentage,
          });
        });

        setResults(Object.values(grouped));
        setError("");
      } catch (err) {
        console.error(err);
        setError("Failed to load survey results");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [surveyId]);

  if (loading) return <div className="loading-message">Loading results...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (results.length === 0) return <div>No results found for this survey.</div>;

  return (
    <div className="survey-results-container">
      <h2>Survey Results</h2>
      {results.map((question) => (
        <div key={question.questionId} className="question-result-card">
          <h3>{question.questionText}</h3>
          <div className="option-bars">
            {question.options.map((option, index) => (
              <div key={index} className="option-bar">
                <div className="option-label">{option.optionText}</div>
                <div className="bar-container">
                  <div
                    className="bar-fill"
                    style={{
                      width: `${option.percentage ? option.percentage : 0}%`,
                      backgroundColor: option.percentage
                        ? "rgba(255, 204, 102, 0.6)"
                        : "transparent",
                    }}
                  >
                    {option.percentage ? (
                      <span className="bar-text">{option.percentage.toFixed(1)}%</span>
                    ) : null}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
};

export default SurveyResults;
