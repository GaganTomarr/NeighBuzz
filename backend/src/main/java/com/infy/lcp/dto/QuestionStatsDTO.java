package com.infy.lcp.dto;

import lombok.Data;

@Data
public class QuestionStatsDTO {
	
    private Integer questionId;
    
    private String answerOption;
    
    private Long count;
    
    private Double percentage;
}
