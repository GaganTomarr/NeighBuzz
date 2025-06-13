package com.infy.lcp.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.Surveys;

public interface SurveyRepo extends JpaRepository<Surveys, Integer>{

	@Query("Select s from Surveys s where s.endDate>=CURRENT_DATE AND s.startDate<=CURRENT_DATE")
	public List<Surveys> findByEndDate();
}
