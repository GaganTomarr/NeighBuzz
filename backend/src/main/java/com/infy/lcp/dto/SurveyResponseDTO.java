package com.infy.lcp.dto;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SurveyResponseDTO {
	
	@NotNull(message = "{responses.responseId.notNull}")
    private Integer responseId;

    @NotNull(message = "{responses.surveys.notNull}")
    @Valid
    private SurveysDTO surveys;
    
    @NotNull(message = "{responses.users.notNull}")
    @Valid
    private UsersDTO users;

    @NotNull(message = "{responses.question.notNull}")
    @Valid
    private SurveyQuestionsDTO question;

    @NotNull(message = "{responses.submittedAt.notNull}")
    private LocalDateTime submittedAt;

    @NotNull(message = "{responses.answer.notNull}")
    private String answer;
}