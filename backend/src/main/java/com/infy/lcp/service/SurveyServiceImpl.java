package com.infy.lcp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.lcp.dto.QuestionStatsDTO;
import com.infy.lcp.dto.SurveyQuestionsDTO;
import com.infy.lcp.dto.SurveyResponseDTO;
import com.infy.lcp.dto.SurveyResultDTO;
import com.infy.lcp.dto.SurveysDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ActionType;
import com.infy.lcp.entity.ContentType;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.entity.SurveyQuestions;
import com.infy.lcp.entity.SurveyResponses;
import com.infy.lcp.entity.SurveyResult;
import com.infy.lcp.entity.Surveys;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ModerationLogRepo;
import com.infy.lcp.repository.SurveyQuestionRepo;
import com.infy.lcp.repository.SurveyRepo;
import com.infy.lcp.repository.SurveyResponseRepo;
import com.infy.lcp.repository.SurveyResultRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.utility.StringListConverter;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SurveyServiceImpl implements SurveyService{
	@Autowired
	SurveyRepo surveyRepo;

	@Autowired
	SurveyQuestionRepo surveyQuestionRepo;

	@Autowired
	SurveyResponseRepo surveyResponseRepo;

	@Autowired
	UsersRepo usersRepo;

	@Autowired
	SurveyResultRepo surveyResultRepo;
	
	@Autowired
	StringListConverter stringListConverter;

	@Autowired
	ModerationLogRepo moderationLogRepo;

	private ModelMapper map = new ModelMapper();
	  
	@Override
	public Integer createSurvey(SurveysDTO dto) throws LCP_Exception{
		Users user = usersRepo.findById(dto.getUsers().getUserId()).orElseThrow(() -> new LCP_Exception("service.USER_NOT_EXIST"));
		if(!user.getIsAdmin()) {
			throw new LCP_Exception("service.SURVEY_AUTHORITY");
		}
		Surveys survey = map.map(dto, Surveys.class);
		survey.setUser(user);
		surveyRepo.save(survey);
		return survey.getSurveyId();
	}

	@Override
	public SurveysDTO viewSurveyById(Integer surveyId) throws LCP_Exception {
		Surveys survey = surveyRepo.findById(surveyId)
				.orElseThrow(() -> new LCP_Exception("service.SURVEY_PARTICIPATION_NO_SURVEY"));

		
		if (survey.getEndDate().isAfter(java.time.LocalDateTime.now())) {
			throw new LCP_Exception("service.SURVEY_ACTIVE");
		}

		List<SurveyQuestions> questions = surveyQuestionRepo.findBySurveyBySurveyId(surveyId);

		SurveysDTO surveyDTO = map.map(survey, SurveysDTO.class);

		List<SurveyQuestionsDTO> questionDTOs = new ArrayList<>();
		for (SurveyQuestions q : questions) {
			questionDTOs.add(map.map(q, SurveyQuestionsDTO.class));
		}

		surveyDTO.setQuestions(questionDTOs);
		return surveyDTO;
	}


	@Override
	public List<SurveysDTO> viewAllSurveys() throws LCP_Exception {
		List<Surveys> surveys = surveyRepo.findAll();
		List<SurveysDTO> surveyDTOs = new ArrayList<>();

		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		for (Surveys s : surveys) {
			if (s.getEndDate().isAfter(now)) {
				SurveysDTO dto = new SurveysDTO();
				dto.setSurveyId(s.getSurveyId());
				dto.setCategory(s.getCategory());
				dto.setDescription(s.getDescription());
				dto.setEndDate(s.getEndDate());
				dto.setIsAnonymous(s.getIsAnonymous());
				dto.setIsPublished(s.getIsPublished());
				dto.setStartDate(s.getStartDate());
				dto.setTitle(s.getTitle());
				dto.setUsers(map.map(usersRepo.findById(s.getUser().getUserId()).get(), UsersDTO.class));
				if (s.getQuestions() != null) {
					List<SurveyQuestionsDTO> questionDTOs = new ArrayList<>();
					for (SurveyQuestions q : s.getQuestions()) {
						SurveyQuestionsDTO qdto = new SurveyQuestionsDTO();
						qdto.setQuestionId(q.getQuestionId());
						qdto.setQuestionText(q.getQuestionText());
						qdto.setIsRequired(q.getIsRequired());
						qdto.setOptions(stringListConverter.stringToList(q.getOptions()));
						questionDTOs.add(qdto);
					}
					dto.setQuestions(questionDTOs);
				}
				surveyDTOs.add(dto);
			}
		}
		return surveyDTOs;
	}

	@Override
	public List<SurveysDTO> viewExpiredSurveys() throws LCP_Exception {
		List<Surveys> surveys = surveyRepo.findAll();
		List<SurveysDTO> expiredSurveyDTOs = new ArrayList<>();

	    LocalDateTime now = LocalDateTime.now();
	    for (Surveys s : surveys) {
	        if (s.getEndDate().isBefore(now)) { 
	            SurveysDTO dto = new SurveysDTO();

	            dto.setSurveyId(s.getSurveyId());
	            dto.setCategory(s.getCategory());
	            dto.setDescription(s.getDescription());
	            dto.setEndDate(s.getEndDate());
	            dto.setIsAnonymous(s.getIsAnonymous());
	            dto.setIsPublished(s.getIsPublished());
	            dto.setStartDate(s.getStartDate());
	            dto.setTitle(s.getTitle());

	            
	            dto.setUsers(map.map(usersRepo.findById(s.getUser().getUserId()).get(), UsersDTO.class));

	            
	            if (s.getQuestions() != null) {
	                List<SurveyQuestionsDTO> questionDTOs = new ArrayList<>();
	                for (SurveyQuestions q : s.getQuestions()) {
	                    SurveyQuestionsDTO qdto = new SurveyQuestionsDTO();
	                    qdto.setQuestionId(q.getQuestionId());
	                    qdto.setQuestionText(q.getQuestionText());
	                    qdto.setIsRequired(q.getIsRequired());
	                    qdto.setOptions(stringListConverter.stringToList(q.getOptions())); // Convert string to list
	                    questionDTOs.add(qdto);
	                }
	                dto.setQuestions(questionDTOs);
	            }

	            expiredSurveyDTOs.add(dto);
	        }
	    }

	    return expiredSurveyDTOs;
	}

	@Override
	public Integer updateSurvey(SurveysDTO dto) throws LCP_Exception{
		Users user = usersRepo.findById(dto.getUsers().getUserId()).orElseThrow(() -> new LCP_Exception("service.USER_NOT_EXIST"));
		if(!user.getIsAdmin()) {
			throw new LCP_Exception("service.SURVEY_EDIT");
		}
		Surveys survey = map.map(dto, Surveys.class);
		survey.setUser(user);
		surveyRepo.save(survey);
		return survey.getSurveyId();
	}

	
	@Override
	public String deleteSurveyById(Integer surveyId) throws LCP_Exception {
	    Surveys survey = surveyRepo.findById(surveyId)
	        .orElseThrow(() -> new LCP_Exception("service.SURVEY_PARTICIPATION_NO_SURVEY"));

	    Users user = survey.getUser();
	    if (user == null || !user.getIsAdmin()) {
	        throw new LCP_Exception("service.SURVEY_DELETE");
	    }

	    
	    ModerationLog modLog = new ModerationLog();
	    modLog.setActionDate(LocalDateTime.now());
	    modLog.setActionType(ActionType.DELETE);
	    modLog.setAdmin(user);
	    modLog.setContentId(surveyId);
	    modLog.setContentType(ContentType.SURVEY);
	    modLog.setReason("This survey was deleted by admin.");
	    moderationLogRepo.save(modLog);

	   
	    surveyRepo.deleteById(surveyId);

	    return "Survey deleted";
	}

	@Override
	public SurveyQuestionsDTO addQuestion(SurveyQuestionsDTO dto) throws LCP_Exception{
		Surveys survey = surveyRepo.findById(dto.getSurvey().getSurveyId()).orElseThrow(() -> new LCP_Exception("service.SURVEY_NOT_EXIST"));
		SurveyQuestions questions = new SurveyQuestions();
		questions.setSurvey(survey);
		questions.setQuestionText(dto.getQuestionText());
		questions.setIsRequired(dto.getIsRequired());
		questions.setOptions(stringListConverter.listToString(dto.getOptions()));
		questions.setType(dto.getType());
		surveyQuestionRepo.save(questions);
		SurveyQuestionsDTO output = new SurveyQuestionsDTO();
		output.setSurvey(dto.getSurvey());
		output.setIsRequired(questions.getIsRequired());
		output.setQuestionId(questions.getQuestionId());
		output.setQuestionText(questions.getQuestionText());
		output.setOptions(stringListConverter.stringToList(questions.getOptions()));
		output.setType(questions.getType()); 
		return output;
	}

	@Override
	public SurveyQuestionsDTO editQuestions(SurveyQuestionsDTO dto) throws LCP_Exception{
		SurveyQuestions questions = surveyQuestionRepo.findById(dto.getQuestionId()).orElseThrow(() -> new LCP_Exception("service.SURVEY_NO_QUESTION"));
		questions.setQuestionText(dto.getQuestionText());
		questions.setIsRequired(dto.getIsRequired());
		questions.setOptions(stringListConverter.listToString(dto.getOptions()));
		questions.setType(dto.getType());
		surveyQuestionRepo.save(questions);
		SurveyQuestionsDTO output = new SurveyQuestionsDTO();
		output.setSurvey(dto.getSurvey());
		output.setIsRequired(output.getIsRequired());
		output.setQuestionId(questions.getQuestionId());
		output.setQuestionText(questions.getQuestionText());
		output.setOptions(stringListConverter.stringToList(questions.getOptions()));
		output.setType(questions.getType()); 
		return output;
	}

	@Override
	public String deleteQuestions(SurveyQuestionsDTO dto) throws LCP_Exception{
		SurveyQuestions questions = surveyQuestionRepo.findById(dto.getQuestionId()).orElseThrow(() -> new LCP_Exception("service.SURVEY_NO_QUESTION"));
		questions.setSurvey(null);
		surveyQuestionRepo.deleteById(questions.getQuestionId());
		return "Question deleted";
	}
	
	@Override
	public SurveysDTO getActiveSurveyWithQuestions(Integer surveyId) throws LCP_Exception {
	    Surveys survey = surveyRepo.findById(surveyId)
	            .orElseThrow(() -> new LCP_Exception("Survey not found with ID: " + surveyId));

	    
	    if (survey.getEndDate().isBefore(LocalDateTime.now())) {
	        throw new LCP_Exception("service.SURVEY_EXPIRED");
	    }

	    SurveysDTO dto = new SurveysDTO();
	    dto.setSurveyId(survey.getSurveyId());
	    dto.setTitle(survey.getTitle());
	    dto.setDescription(survey.getDescription());
	    dto.setStartDate(survey.getStartDate());
	    dto.setEndDate(survey.getEndDate());
	    dto.setIsAnonymous(survey.getIsAnonymous());
	    dto.setIsPublished(survey.getIsPublished());
	    dto.setCategory(survey.getCategory());
	    dto.setUsers(map.map(survey.getUser(), UsersDTO.class));

	    List<SurveyQuestions> questionList = surveyQuestionRepo.findBySurveyBySurveyId(surveyId);
	    List<SurveyQuestionsDTO> questionDTOs = new ArrayList<>();

	    for (SurveyQuestions q : questionList) {
	        SurveyQuestionsDTO qdto = new SurveyQuestionsDTO();
	        qdto.setQuestionId(q.getQuestionId());
	        qdto.setQuestionText(q.getQuestionText());
	        qdto.setIsRequired(q.getIsRequired());
	        qdto.setOptions(stringListConverter.stringToList(q.getOptions()));
	        qdto.setType(q.getType());
	        questionDTOs.add(qdto);
	    }

	    dto.setQuestions(questionDTOs);
	    return dto;
	}
	
	@Override
	public Integer submitSurveyResponse(SurveyResponseDTO dto) throws LCP_Exception {
	    
	    Surveys survey = surveyRepo.findById(dto.getSurveys().getSurveyId())
	            .orElseThrow(() -> new LCP_Exception("service.SURVEY_PARTICIPATION_NO_SURVEY"));

	    
	    Users user = usersRepo.findById(dto.getUsers().getUserId())
	            .orElseThrow(() -> new LCP_Exception("service.USER_NOT_EXIST"));

	    
	    SurveyQuestions question = surveyQuestionRepo.findById(dto.getQuestion().getQuestionId())
	            .orElseThrow(() -> new LCP_Exception("service.SURVEY_NO_QUESTION_FOUND"));

	    
	    boolean alreadySubmitted = surveyResponseRepo.existsBySurveyAndQuestionAndUser(survey, question, user);
	    if (alreadySubmitted) {
	        throw new LCP_Exception("service.SURVEY_USER_SUBMITTED");
	    }

	    
	    SurveyResponses response = new SurveyResponses();
	    response.setSurvey(survey);
	    response.setUser(user);
	    response.setQuestion(question);
	    response.setSubmittedAt(LocalDateTime.now());
	    response.setAnswer(dto.getAnswer());

	    SurveyResponses saved = surveyResponseRepo.save(response);
	    return saved.getResponseId();
	}


	
	@Override
	public List<SurveyResultDTO> viewAllSurveyResult() throws LCP_Exception{
		List <SurveyResult> optionalResult=surveyResultRepo.findAll();
		if(optionalResult.isEmpty()) {
			throw new LCP_Exception("service.SURVEY_NO_RESULT");
		}
		List<SurveyResultDTO> surveyResultList=new ArrayList<>();
		for(SurveyResult res:optionalResult) {
			SurveyResultDTO list1=map.map(res, SurveyResultDTO.class);
			list1.setSurveys(map.map(res.getSurvey(), SurveysDTO.class));
			list1.setResult(stringListConverter.stringToList(res.getResult()));
			surveyResultList.add(list1);
		}
    return surveyResultList;
		
	}
	
	@Override
	public SurveyResultDTO viewSurveyResult(Integer surveyResultId) throws LCP_Exception{
		Optional <SurveyResult> optionalResult=surveyResultRepo.findById(surveyResultId);
		SurveyResult surveyResult=optionalResult.orElseThrow(()-> new LCP_Exception("service.NO_RESULT"));
		SurveyResultDTO surveyResultList=map.map(surveyResult, SurveyResultDTO.class);
		surveyResultList.setResult(stringListConverter.stringToList(surveyResult.getResult()));
		return surveyResultList;
	}
		
	
	@Override
	public List<QuestionStatsDTO> getSurveyStatistics(Integer surveyId) throws LCP_Exception {
	    if (!surveyRepo.existsById(surveyId)) {
	        throw new LCP_Exception("service.SURVEY_NO_SURVEY " + surveyId);
	    }

	    
	    List<SurveyQuestions> questions = surveyQuestionRepo.findBySurveyBySurveyId(surveyId);

	    
	    List<Object[]> statsRaw = surveyResponseRepo.getAnswerCountsBySurveyId(surveyId);

	    
	    Map<Integer, Map<String, Long>> countsMap = new HashMap<>();
	    for (Object[] row : statsRaw) {
	        Integer questionId = (Integer) row[0];
	        String option = (String) row[1];
	        Long count = (Long) row[2];

	        countsMap.computeIfAbsent(questionId, k -> new HashMap<>()).put(option, count);
	    }

	    
	    Map<Integer, Long> totalUsersPerQuestion = new HashMap<>();
	    for (SurveyQuestions question : questions) {
	        Long totalUsers = surveyResponseRepo.countDistinctUsersBySurveyIdAndQuestionId(surveyId, question.getQuestionId());
	        totalUsersPerQuestion.put(question.getQuestionId(), totalUsers != null ? totalUsers : 0L);
	    }

	    List<QuestionStatsDTO> statsList = new ArrayList<>();

	    
	    for (SurveyQuestions question : questions) {
	        Integer questionId = question.getQuestionId();
	        String optionsString = question.getOptions(); 
	        if (optionsString == null || optionsString.trim().isEmpty()) {
	            continue; 
	        }

	        String[] options = optionsString.split("\\|");
	        Map<String, Long> optionCounts = countsMap.getOrDefault(questionId, new HashMap<>());
	        Long totalUsers = totalUsersPerQuestion.getOrDefault(questionId, 1L);

	        for (String opt : options) {
	            Long count = optionCounts.getOrDefault(opt, 0L);
	            double percentage = totalUsers > 0 ? (count * 100.0) / totalUsers : 0.0;

	            QuestionStatsDTO dto = new QuestionStatsDTO();
	            dto.setQuestionId(questionId);
	            dto.setAnswerOption(opt);
	            dto.setCount(count);
	            dto.setPercentage(percentage);

	            statsList.add(dto);
	        }
	    }

	    return statsList;
	}




}