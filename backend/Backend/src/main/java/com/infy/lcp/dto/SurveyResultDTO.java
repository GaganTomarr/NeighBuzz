package com.infy.lcp.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SurveyResultDTO {

	@NotNull(message = "{surveyResults.surveyResultId.notNull}")
    private Integer surveyResultId;

    @NotNull(message = "{surveyResults.surveys.notNull}")
    @Valid
    private SurveysDTO surveys;

    @NotNull(message = "{surveyResults.result.notNull}")
    @Size(min = 1, message = "{surveyResults.result.size}")
    private List<String> result;
}
