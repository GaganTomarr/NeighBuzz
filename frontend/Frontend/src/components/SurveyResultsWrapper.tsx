import React from 'react';
import { useParams } from 'react-router-dom';
import SurveyResults from './SurveyResults';

const SurveyResultsWrapper: React.FC = () => {
  const { surveyId } = useParams<{ surveyId: string }>();

  if (!surveyId) {
    return <div>Invalid survey ID</div>;
  }

  return <SurveyResults surveyId={parseInt(surveyId, 10)} />;
};

export default SurveyResultsWrapper;
