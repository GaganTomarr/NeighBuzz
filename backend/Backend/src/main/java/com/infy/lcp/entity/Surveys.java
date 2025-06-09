package com.infy.lcp.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="surveys")
public class Surveys {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="survey_id")
	private Integer surveyId;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="creator_id", nullable=false)
	private Users user;
	@Column(nullable=false)
	private String title;
	@Column(columnDefinition="TEXT")
	private String description;//text
	@Column(nullable=false)
	private LocalDateTime startDate;
	@Column(nullable=false)
	private LocalDateTime endDate;
	private Boolean isAnonymous;
	private Boolean isPublished;
	
	@Column(name="survey_type" ,nullable=false)
	@Enumerated(EnumType.STRING)
	private SurveyCategory category;
	
	@OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyQuestions> questions;
}
