package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.SurveyQuestions;

public interface SurveyQuestionRepo extends JpaRepository<SurveyQuestions, Integer>{

	@Query("select q from SurveyQuestions q where q.survey.surveyId=?1")
	List<SurveyQuestions> findBySurveyBySurveyId(Integer surveyId);
}
