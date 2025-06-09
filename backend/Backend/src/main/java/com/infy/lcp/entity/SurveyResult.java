package com.infy.lcp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="survey_result")
public class SurveyResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="survey_result_id")
	private Integer surveyResultId;
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(unique=true, name="survey_id")
	private Surveys survey;
	private String result;
}
