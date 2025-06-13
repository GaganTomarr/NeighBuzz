package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.infy.lcp.entity.SurveyQuestions;
import com.infy.lcp.entity.SurveyResponses;
import com.infy.lcp.entity.Surveys;
import com.infy.lcp.entity.Users;

public interface SurveyResponseRepo extends JpaRepository<SurveyResponses, Integer>{
	boolean existsBySurveyAndQuestionAndUser(Surveys survey, SurveyQuestions question, Users user);
	
	
	@Query("SELECT r.question.questionId, r.answer, COUNT(r) " +
		       "FROM SurveyResponses r " +
		       "WHERE r.survey.surveyId = :surveyId " +
		       "GROUP BY r.question.questionId, r.answer")
	

		List<Object[]> getAnswerCountsBySurveyId(@Param("surveyId") Integer surveyId);
		
		@Query("SELECT COUNT(DISTINCT sr.user.userId) " +
		           "FROM SurveyResponses sr " +
		           "WHERE sr.survey.surveyId = :surveyId " +
		           "AND sr.question.questionId = :questionId")
		    Long countDistinctUsersBySurveyIdAndQuestionId(@Param("surveyId") Integer surveyId, 
		                                                   @Param("questionId") Integer questionId);
}
