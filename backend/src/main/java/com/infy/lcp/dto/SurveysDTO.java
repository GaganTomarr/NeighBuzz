package com.infy.lcp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.infy.lcp.entity.SurveyCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class SurveysDTO {


	 @NotNull(message = "{surveys.surveyId.notNull}")
	    private Integer surveyId;

	    @NotNull(message = "{surveys.users.notNull}")
	    @Valid
	    private UsersDTO users;

	    @NotBlank(message = "{surveys.title.notBlank}")
	    @Size(max = 200, message = "{surveys.title.size}")
	    private String title;

	    @NotBlank(message = "{surveys.description.notBlank}")
	    private String description;

	    @NotNull(message = "{surveys.startDate.notNull}")
	    private LocalDateTime startDate;

	    @NotNull(message = "{surveys.endDate.notNull}")
	    private LocalDateTime endDate;

	    private Boolean isAnonymous;

	    private Boolean isPublished;

	    @NotNull(message = "{surveys.category.notNull}")
	    private SurveyCategory category;
	    
	    @JsonManagedReference
	    private List<SurveyQuestionsDTO> questions;
}
