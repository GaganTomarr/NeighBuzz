package com.infy.lcp.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.infy.lcp.dto.QuestionStatsDTO;
import com.infy.lcp.dto.SurveyQuestionsDTO;
import com.infy.lcp.dto.SurveyResponseDTO;
import com.infy.lcp.dto.SurveyResultDTO;
import com.infy.lcp.dto.SurveysDTO;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.SurveyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;


@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping(value="/survey")
@Tag(name = "Survey", description = "APIs for managing surveys and survey questions")
public class SurveyController {
	
	@Autowired
	SurveyService surveyService;
	
	@GetMapping("/viewAll")
	@Operation(summary = "View all surveys", description = "Get a list of all surveys")
	@ApiResponse(responseCode = "200", description = "List of surveys retrieved successfully")
	public ResponseEntity<List<SurveysDTO>> viewAllSurveys() throws LCP_Exception {
	    List<SurveysDTO> surveyList = surveyService.viewAllSurveys();
	    return new ResponseEntity<>(surveyList, HttpStatus.OK);
	}
	
	@GetMapping("/viewExpired")
	@Operation(summary = "View expired surveys", description = "Get a list of all expired surveys")
	@ApiResponse(responseCode = "200", description = "List of expired surveys retrieved successfully")
	public ResponseEntity<List<SurveysDTO>> viewExpiredSurveys() throws LCP_Exception {
	    List<SurveysDTO> expiredSurveys = surveyService.viewExpiredSurveys();
	    return new ResponseEntity<>(expiredSurveys, HttpStatus.OK);
	}

	@GetMapping("/view/{surveyId}")
	@Operation(summary = "View survey by ID", description = "Get survey details by survey ID")
	@ApiResponse(responseCode = "200", description = "Survey details retrieved successfully")
	public ResponseEntity<SurveysDTO> viewSurveyById(@PathVariable Integer surveyId) throws LCP_Exception {
	    SurveysDTO survey = surveyService.viewSurveyById(surveyId);
	    return new ResponseEntity<>(survey, HttpStatus.OK);
	}
	
	@PostMapping(value="/adds")
	@Operation(summary = "Add a new survey", description = "Create a new survey")
	@ApiResponse(responseCode = "201", description = "Survey created successfully")
	public ResponseEntity<Integer> addSurvey(@RequestBody SurveysDTO dto) throws LCP_Exception{
		return new ResponseEntity<Integer>(surveyService.createSurvey(dto), HttpStatus.CREATED);
	}
	
	@PutMapping(value="/updates")
	@Operation(summary = "Update a survey", description = "Update existing survey details")
	@ApiResponse(responseCode = "200", description = "Survey updated successfully")
	public ResponseEntity<Integer> updateSurvey(@RequestBody SurveysDTO dto) throws LCP_Exception{
		return new ResponseEntity<Integer>(surveyService.updateSurvey(dto), HttpStatus.OK);
	}
	

	@Operation(summary = "Delete a survey", description = "Delete a survey")
	@ApiResponse(responseCode = "200", description = "Survey deleted successfully")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteSurvey(@PathVariable Integer id) throws LCP_Exception {
	    return new ResponseEntity<>(surveyService.deleteSurveyById(id), HttpStatus.OK);
	}

	@PostMapping(value="/addq")
	@Operation(summary = "Add a survey question", description = "Add a new question to a survey")
	@ApiResponse(responseCode = "201", description = "Survey question added successfully")
	public ResponseEntity<SurveyQuestionsDTO> addQuestion(@RequestBody SurveyQuestionsDTO dto, HttpServletRequest request) throws LCP_Exception, UnsupportedEncodingException{
		request.setCharacterEncoding("UTF-8");
		return new ResponseEntity<SurveyQuestionsDTO>(surveyService.addQuestion(dto), HttpStatus.CREATED);
	}
	
	@PutMapping(value="/editq")
	@Operation(summary = "Edit a survey question", description = "Edit an existing survey question")
	@ApiResponse(responseCode = "200", description = "Survey question updated successfully")
	public ResponseEntity<SurveyQuestionsDTO> editQuestion(@RequestBody SurveyQuestionsDTO dto) throws LCP_Exception{
		return new ResponseEntity<SurveyQuestionsDTO>(surveyService.editQuestions(dto), HttpStatus.OK);
	}
	
	@DeleteMapping(value="/deleteq")
	@Operation(summary = "Delete a survey question", description = "Delete a question from a survey")
	@ApiResponse(responseCode = "200", description = "Survey question deleted successfully")
	public ResponseEntity<String> deleteQuestion(@RequestBody SurveyQuestionsDTO dto) throws LCP_Exception, UnsupportedEncodingException{
		return new ResponseEntity<String>(surveyService.deleteQuestions(dto), HttpStatus.OK);
	}
	
	@GetMapping("/participate/{surveyId}")
	@Operation(summary = "Get survey for participation", description = "Fetch survey with questions for user participation")
	@ApiResponse(responseCode = "200", description = "Survey fetched successfully")
	@ApiResponse(responseCode = "404", description = "Survey not found")
	public ResponseEntity<SurveysDTO> getSurveyForParticipation(@PathVariable Integer surveyId) throws LCP_Exception {
	    SurveysDTO survey = surveyService.getActiveSurveyWithQuestions(surveyId);
	    if (survey == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	    return new ResponseEntity<>(survey, HttpStatus.OK);
	}

	@PostMapping("/submitResponse")
	@Operation(summary = "Submit survey response", description = "Submit answers for a survey")
	@ApiResponse(responseCode = "201", description = "Survey response submitted successfully")
	@ApiResponse(responseCode = "400", description = "Invalid survey response data")
	@ApiResponse(responseCode = "500", description = "Internal server error")
	public ResponseEntity<String> submitSurveyResponse(@RequestBody SurveyResponseDTO responseDTO) {
	    try {
	        Integer responseId = surveyService.submitSurveyResponse(responseDTO);
	        return new ResponseEntity<>("Response submitted successfully with ID: " + responseId, HttpStatus.CREATED);
	    } catch (LCP_Exception e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	
//	@GetMapping("/getSurveyResult")
//	public ResponseEntity<List<SurveyResultDTO>> viewAllSurveyResult() throws LCP_Exception
//	{
//		List<SurveyResultDTO> surveyList=surveyService.viewAllSurveyResult();
//		return new ResponseEntity<> (surveyList,HttpStatus.OK);
//	}
//	
//	@GetMapping("/getSurveyResultById/{surveyResultId}")
//	public ResponseEntity<SurveyResultDTO> viewSurveyResult(@PathVariable Integer surveyResultId) throws LCP_Exception
//	{
//		return new ResponseEntity<> (surveyService.viewSurveyResult(surveyResultId),HttpStatus.OK);
//	}
//
//	
//	@GetMapping("/{surveyId}/statistics")
//	public ResponseEntity<List<QuestionStatsDTO>> getSurveyStatistics(@PathVariable Integer surveyId) {
//	    try {
//	        List<QuestionStatsDTO> stats = surveyService.getSurveyStatistics(surveyId);
//	        return ResponseEntity.ok(stats);
//	    } catch (LCP_Exception e) {
//	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//	    } catch (Exception e) {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//	    }
//	}
	@Operation(summary = "View all survey results", description = "Returns a list of all submitted survey results")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved survey results"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/getSurveyResult")
    public ResponseEntity<List<SurveyResultDTO>> viewAllSurveyResult() throws LCP_Exception {
        List<SurveyResultDTO> surveyList = surveyService.viewAllSurveyResult();
        return new ResponseEntity<>(surveyList, HttpStatus.OK);
    }

    @Operation(summary = "View specific survey result by ID", description = "Fetch a single survey result using its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Survey result found"),
        @ApiResponse(responseCode = "404", description = "Survey result not found")
    })
    @GetMapping("/getSurveyResultById/{surveyResultId}")
    public ResponseEntity<SurveyResultDTO> viewSurveyResult(
        @Parameter(description = "ID of the survey result to fetch") @PathVariable Integer surveyResultId) throws LCP_Exception {
        return new ResponseEntity<>(surveyService.viewSurveyResult(surveyResultId), HttpStatus.OK);
    }

    @Operation(summary = "Get survey statistics", description = "Returns aggregated statistics for a survey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Survey not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    
    @GetMapping("/{surveyId}/statistics")
    public ResponseEntity<List<QuestionStatsDTO>> getSurveyStatistics(
        @Parameter(description = "ID of the survey") @PathVariable Integer surveyId) {
        try {
            List<QuestionStatsDTO> stats = surveyService.getSurveyStatistics(surveyId);
            return ResponseEntity.ok(stats);
        } catch (LCP_Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


	
}