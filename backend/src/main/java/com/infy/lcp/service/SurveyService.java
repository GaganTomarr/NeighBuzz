package com.infy.lcp.service;

import java.util.List;

import com.infy.lcp.dto.QuestionStatsDTO;
import com.infy.lcp.dto.SurveyQuestionsDTO;
import com.infy.lcp.dto.SurveyResponseDTO;
import com.infy.lcp.dto.SurveyResultDTO;
import com.infy.lcp.dto.SurveysDTO;
import com.infy.lcp.exception.LCP_Exception;

public interface SurveyService {
	public Integer createSurvey(SurveysDTO dto) throws LCP_Exception;
	public Integer updateSurvey(SurveysDTO dto) throws LCP_Exception;
	//public String deleteSurvey(SurveysDTO dto) throws LCP_Exception;
	public SurveyQuestionsDTO addQuestion(SurveyQuestionsDTO dto) throws LCP_Exception;
	public SurveyQuestionsDTO editQuestions(SurveyQuestionsDTO dto) throws LCP_Exception;
	public String deleteQuestions(SurveyQuestionsDTO dto) throws LCP_Exception;
	public String deleteSurveyById(Integer surveyId) throws LCP_Exception;
	public SurveysDTO viewSurveyById(Integer surveyId) throws LCP_Exception;
	public List<SurveysDTO> viewAllSurveys() throws LCP_Exception;
	List<SurveysDTO> viewExpiredSurveys() throws LCP_Exception;
	public SurveysDTO getActiveSurveyWithQuestions(Integer surveyId) throws LCP_Exception;
	public Integer submitSurveyResponse(SurveyResponseDTO dto) throws LCP_Exception;
	public List<SurveyResultDTO> viewAllSurveyResult() throws LCP_Exception;
	public SurveyResultDTO viewSurveyResult(Integer surveyResultId) throws LCP_Exception;
	public List<QuestionStatsDTO> getSurveyStatistics(Integer surveyId) throws LCP_Exception;
}