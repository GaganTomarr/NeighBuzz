package com.infy.lcp.dto;



import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.infy.lcp.entity.QuestionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class SurveyQuestionsDTO {	
	@NotNull(message="{questions.questionId.notNull}")
	private Integer questionId;

	@JsonBackReference
    @NotNull(message = "{questions.survey.notNull}")
    @Valid
    private SurveysDTO survey;

    @NotBlank(message = "{questions.questionText.notBlank}")
    @Size(max = 1000, message = "{questions.questionText.size}")
    private String questionText;

    @NotNull(message = "{questions.isRequired.notNull}")
    private Boolean isRequired;

    @NotNull(message = "{questions.options.notNull}")
    private List<String> options;
    
    @NotNull(message = "Question type must not be null")
    private QuestionType type;
}