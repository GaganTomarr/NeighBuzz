package com.infy.lcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.lcp.dto.QuestionStatsDTO;
import com.infy.lcp.dto.SurveyQuestionsDTO;
import com.infy.lcp.dto.SurveysDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.AccountStatus;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.entity.SurveyQuestions;
import com.infy.lcp.entity.Surveys;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ModerationLogRepo;
import com.infy.lcp.repository.SurveyQuestionRepo;
import com.infy.lcp.repository.SurveyRepo;
import com.infy.lcp.repository.SurveyResponseRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.SurveyServiceImpl;
import com.infy.lcp.utility.StringListConverter;

@SpringBootTest
public class SurveyServiceTests {

    @InjectMocks
    private SurveyServiceImpl surveyService;

    @Mock
    private StringListConverter stringListConverter;
    
    @Mock
    private SurveyRepo surveyRepo;

    @Mock
    private SurveyResponseRepo surveyResponseRepo;
    
    @Mock
    private SurveyQuestionRepo surveyQuestionRepo;

    @Mock
    private UsersRepo usersRepo;

    @Mock
    private ModerationLogRepo moderationLogRepo; 
    
    private Users adminUser;
    private SurveysDTO surveyDto;
    private Surveys surveyEntity;
    private SurveyQuestionsDTO questionDto;
    private SurveyQuestions questionEntity;

    @BeforeEach
    public void setUp() {
        adminUser = new Users();
        adminUser.setUserId(1);
        adminUser.setIsAdmin(true);

        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setUserId(1);
        usersDTO.setIsAdmin(true);
        usersDTO.setEmail("admin@test.com");
        usersDTO.setUsername("admin");
        usersDTO.setAccountStatus(AccountStatus.ACTIVE);
        usersDTO.setRegistrationDate(LocalDateTime.now());

        surveyDto = new SurveysDTO();
        surveyDto.setSurveyId(1);
        surveyDto.setUsers(usersDTO);
        surveyDto.setTitle("Survey Title");
        surveyDto.setDescription("Survey Description");
        surveyDto.setStartDate(LocalDateTime.now());
        surveyDto.setEndDate(LocalDateTime.now().plusDays(5));
        surveyDto.setIsAnonymous(true);
        surveyDto.setIsPublished(false);

        surveyEntity = new ModelMapper().map(surveyDto, Surveys.class);
        surveyEntity.setUser(adminUser);

        questionDto = new SurveyQuestionsDTO();
        questionDto.setQuestionId(1);
        questionDto.setQuestionText("What is your name?");
        questionDto.setIsRequired(true);
        questionDto.setOptions(List.of("Option1", "Option2"));
        questionDto.setSurvey(surveyDto);

        questionEntity = new ModelMapper().map(questionDto, SurveyQuestions.class);
        questionEntity.setSurvey(surveyEntity);
    }


    @Test
    public void testCreateSurvey_Valid() throws LCP_Exception {
        when(usersRepo.findById(1)).thenReturn(Optional.of(adminUser));
        when(surveyRepo.save(any(Surveys.class))).thenReturn(surveyEntity);

        Integer result = surveyService.createSurvey(surveyDto);

        assertEquals(1, result);
        verify(surveyRepo, times(1)).save(any(Surveys.class));
    }

    @Test
    public void testCreateSurvey_NonAdmin() {
        adminUser.setIsAdmin(false);
        when(usersRepo.findById(1)).thenReturn(Optional.of(adminUser));

        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> surveyService.createSurvey(surveyDto));
        assertEquals("service.SURVEY_AUTHORITY", ex.getMessage());
    }


    @Test
    public void testUpdateSurvey_Valid() throws LCP_Exception {
        when(usersRepo.findById(1)).thenReturn(Optional.of(adminUser));
        when(surveyRepo.save(any(Surveys.class))).thenReturn(surveyEntity);

        Integer result = surveyService.updateSurvey(surveyDto);

        assertEquals(1, result);
    }

    @Test
    public void testUpdateSurvey_NonAdmin() {
        adminUser.setIsAdmin(false);
        when(usersRepo.findById(1)).thenReturn(Optional.of(adminUser));

        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> surveyService.updateSurvey(surveyDto));
        assertEquals("service.SURVEY_EDIT", ex.getMessage());
    }


    @Test
    void testDeleteSurveyById_AdminUser_Success() throws LCP_Exception {
       
        int surveyId = 100;

        Users adminUser = new Users();
        adminUser.setUserId(1);
        adminUser.setIsAdmin(true);

        Surveys survey = new Surveys();
        survey.setSurveyId(surveyId);
        survey.setUser(adminUser);

        when(surveyRepo.findById(surveyId)).thenReturn(Optional.of(survey));
        when(moderationLogRepo.save(any(ModerationLog.class))).thenReturn(new ModerationLog());

        
        String result = surveyService.deleteSurveyById(surveyId);

        
        assertEquals("Survey deleted", result);
        verify(surveyRepo).findById(surveyId);
        verify(moderationLogRepo).save(any(ModerationLog.class));
        verify(surveyRepo).deleteById(surveyId);
    }



    @Test
    void testDeleteSurveyById_NonAdminUser_ThrowsException() {
        // Arrange
        int surveyId = 100;

        Users normalUser = new Users();
        normalUser.setUserId(2);
        normalUser.setIsAdmin(false);

        Surveys survey = new Surveys();
        survey.setSurveyId(surveyId);
        survey.setUser(normalUser);

        when(surveyRepo.findById(surveyId)).thenReturn(Optional.of(survey));

        
        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> {
            surveyService.deleteSurveyById(surveyId);
        });

        assertEquals("service.SURVEY_DELETE", ex.getMessage());
        verify(surveyRepo, never()).deleteById(anyInt());
        verify(moderationLogRepo, never()).save(any());
    }
    
    @Test
    void testDeleteSurveyById_SurveyNotFound_ThrowsException() {
       
        int surveyId = 999;
        when(surveyRepo.findById(surveyId)).thenReturn(Optional.empty());

        
        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> {
            surveyService.deleteSurveyById(surveyId);
        });

        assertEquals("service.SURVEY_PARTICIPATION_NO_SURVEY", ex.getMessage());
        verify(surveyRepo).findById(surveyId);
        verify(surveyRepo, never()).deleteById(anyInt());
        verify(moderationLogRepo, never()).save(any());
    }



    @Test
    public void testAddQuestion_Valid() throws LCP_Exception {
        
        Surveys surveyEntity = new Surveys();
        surveyEntity.setSurveyId(1);
        surveyEntity.setTitle("Customer Feedback");

        
        SurveyQuestionsDTO questionDto = new SurveyQuestionsDTO();
        SurveysDTO surveyDto = new SurveysDTO();
        surveyDto.setSurveyId(1);
        questionDto.setSurvey(surveyDto);
        questionDto.setQuestionText("What is your name?");
        questionDto.setIsRequired(true);
        questionDto.setOptions(List.of("Option1", "Option2"));

        
        SurveyQuestions questionEntity = new SurveyQuestions();
        questionEntity.setQuestionId(101);
        questionEntity.setSurvey(surveyEntity);
        questionEntity.setQuestionText("What is your name?");
        questionEntity.setIsRequired(true);
        questionEntity.setOptions("Option1,Option2"); 

       
        when(surveyRepo.findById(1)).thenReturn(Optional.of(surveyEntity));
        when(stringListConverter.listToString(List.of("Option1", "Option2"))).thenReturn("Option1,Option2");
        when(stringListConverter.stringToList("Option1,Option2")).thenReturn(List.of("Option1", "Option2"));
        when(surveyQuestionRepo.save(any(SurveyQuestions.class))).thenReturn(questionEntity);

      
        SurveyQuestionsDTO result = surveyService.addQuestion(questionDto);

        
        assertNotNull(result);
        assertEquals("What is your name?", result.getQuestionText());
        assertTrue(result.getIsRequired());
        assertEquals(List.of("Option1", "Option2"), result.getOptions());

       
        verify(surveyRepo).findById(1);
        verify(stringListConverter).listToString(List.of("Option1", "Option2"));
        verify(surveyQuestionRepo).save(any(SurveyQuestions.class));
    }


    @Test
    public void testAddQuestion_NoSurvey() {
        when(surveyRepo.findById(1)).thenReturn(Optional.empty());

        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> surveyService.addQuestion(questionDto));
        assertEquals("service.SURVEY_NOT_EXIST", ex.getMessage());
    }


    @Test
    public void testEditQuestion_Valid() throws LCP_Exception {
       
        SurveyQuestionsDTO questionDto = new SurveyQuestionsDTO();
        questionDto.setQuestionId(1);
        questionDto.setQuestionText("What is your name?");
        questionDto.setIsRequired(true);
        questionDto.setOptions(Arrays.asList("Option 1", "Option 2"));
        
        
        SurveysDTO surveyDto = new SurveysDTO();
        surveyDto.setSurveyId(10);
        questionDto.setSurvey(surveyDto);

       
        SurveyQuestions questionEntity = new SurveyQuestions();
        questionEntity.setQuestionId(1);
        questionEntity.setQuestionText("Old Question?");
        questionEntity.setIsRequired(false);
        questionEntity.setOptions("Old Option");

        
        when(surveyQuestionRepo.findById(1)).thenReturn(Optional.of(questionEntity));
        when(surveyQuestionRepo.save(any(SurveyQuestions.class))).thenAnswer(inv -> {
            SurveyQuestions q = inv.getArgument(0);
            q.setQuestionId(1); 
            return q;
        });

       
        SurveyQuestionsDTO result = surveyService.editQuestions(questionDto);

        
        assertEquals("What is your name?", result.getQuestionText());
        assertEquals(1, result.getQuestionId());
        verify(surveyQuestionRepo).save(any(SurveyQuestions.class));
    }


    @Test
    public void testEditQuestion_NotFound() {
        when(surveyQuestionRepo.findById(1)).thenReturn(Optional.empty());

        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> surveyService.editQuestions(questionDto));
        assertEquals("service.SURVEY_NO_QUESTION", ex.getMessage());
    }


    @Test
    public void testDeleteQuestion_Valid() throws LCP_Exception {
        when(surveyQuestionRepo.findById(1)).thenReturn(Optional.of(questionEntity));

        String result = surveyService.deleteQuestions(questionDto);

        assertEquals("Question deleted", result);
        verify(surveyQuestionRepo).deleteById(1);
    }

    @Test
    public void testDeleteQuestion_NotFound() {
        when(surveyQuestionRepo.findById(1)).thenReturn(Optional.empty());

        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> surveyService.deleteQuestions(questionDto));
        assertEquals("service.SURVEY_NO_QUESTION", ex.getMessage());
    }
    
    @Test
    void testGetSurveyStatistics_validSurvey_returnsStats() throws LCP_Exception {
        Integer surveyId = 1;

       
        when(surveyRepo.existsById(surveyId)).thenReturn(true);

        
        List<SurveyQuestions> surveyQuestions = new ArrayList<>();

        SurveyQuestions q1 = new SurveyQuestions();
        q1.setQuestionId(101);
        q1.setOptions("Yes|No");

        SurveyQuestions q2 = new SurveyQuestions();
        q2.setQuestionId(102);
        q2.setOptions("Maybe");

        surveyQuestions.add(q1);
        surveyQuestions.add(q2);

        when(surveyQuestionRepo.findBySurveyBySurveyId(surveyId)).thenReturn(surveyQuestions);

        List<Object[]> statsRaw = new ArrayList<>();
        statsRaw.add(new Object[]{101, "Yes", 6L});
        statsRaw.add(new Object[]{101, "No", 4L});
        statsRaw.add(new Object[]{102, "Maybe", 3L});
        when(surveyResponseRepo.getAnswerCountsBySurveyId(surveyId)).thenReturn(statsRaw);

        
        when(surveyResponseRepo.countDistinctUsersBySurveyIdAndQuestionId(surveyId, 101)).thenReturn(10L);
        when(surveyResponseRepo.countDistinctUsersBySurveyIdAndQuestionId(surveyId, 102)).thenReturn(3L);

        List<QuestionStatsDTO> result = surveyService.getSurveyStatistics(surveyId);

        assertEquals(3, result.size());

        QuestionStatsDTO first = result.get(0);
        assertEquals(101, first.getQuestionId());
        assertEquals("Yes", first.getAnswerOption());
        assertEquals(6L, first.getCount());
        assertEquals(60.0, first.getPercentage());

        QuestionStatsDTO second = result.get(1);
        assertEquals(101, second.getQuestionId());
        assertEquals("No", second.getAnswerOption());
        assertEquals(4L, second.getCount());
        assertEquals(40.0, second.getPercentage());

        QuestionStatsDTO third = result.get(2);
        assertEquals(102, third.getQuestionId());
        assertEquals("Maybe", third.getAnswerOption());
        assertEquals(3L, third.getCount());
        assertEquals(100.0, third.getPercentage());
    }


    @Test
    void testGetSurveyStatistics_invalidSurvey_throwsException() {
        Integer surveyId = 999;

        when(surveyRepo.existsById(surveyId)).thenReturn(false);

        LCP_Exception exception = assertThrows(LCP_Exception.class, () ->
            surveyService.getSurveyStatistics(surveyId)
        );

        assertEquals("service.SURVEY_NO_SURVEY" +  " 999", exception.getMessage());
    }

    @Test
    void testGetSurveyStatistics_emptyStats_returnsEmptyList() throws LCP_Exception {
        Integer surveyId = 1;

        when(surveyRepo.existsById(surveyId)).thenReturn(true);
        when(surveyResponseRepo.getAnswerCountsBySurveyId(surveyId)).thenReturn(Collections.emptyList());

        List<QuestionStatsDTO> result = surveyService.getSurveyStatistics(surveyId);

        assertTrue(result.isEmpty());
    }
}




