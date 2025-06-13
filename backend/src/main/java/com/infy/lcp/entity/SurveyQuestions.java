package com.infy.lcp.entity;
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
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name="survey_questions")
public class SurveyQuestions {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="question_id")
	private Integer questionId;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="survey_id",nullable=false)
	private Surveys survey;
	@Column(columnDefinition="TEXT", nullable=false)
	private String questionText;
	private Boolean isRequired;
	@Column(columnDefinition="TEXT", nullable=false)
	private String options;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;
}

