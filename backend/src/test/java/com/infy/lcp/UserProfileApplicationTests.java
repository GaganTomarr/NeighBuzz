package com.infy.lcp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.UserProfilesDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.UserProfiles;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.UsersProfileRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.UserProfileServiceImpl;

@SpringBootTest

public class UserProfileApplicationTests {

    @Mock

    private UsersProfileRepo profileRepo;

    @Mock

    private UsersRepo userRepo;

    @Mock

    private ModelMapper mapper;

    @InjectMocks

    private UserProfileServiceImpl service;

    @Test
    void getProfile_existingId_returnsDTO() throws LCP_Exception {
        Integer userId = 1;

        UserProfiles profile = new UserProfiles();
        Users user = new Users();

        UsersDTO userDTO = new UsersDTO();
        UserProfilesDTO profileDTO = new UserProfilesDTO();

        when(profileRepo.findByUser(userId)).thenReturn(Optional.of(profile));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.map(user, UsersDTO.class)).thenReturn(userDTO);
        when(mapper.map(profile, UserProfilesDTO.class)).thenReturn(profileDTO);

        UserProfilesDTO result = service.getUserProfile(userId);

        assertEquals(profileDTO, result);
        assertEquals(userDTO, result.getUserDTO());  
        verify(profileRepo).findByUser(userId);
        verify(userRepo).findById(userId);
        verify(mapper).map(user, UsersDTO.class);
        verify(mapper).map(profile, UserProfilesDTO.class);
    }


    @Test
    void getProfile_nonExistingId_throwsNotFound() {
        Integer userId = 1;

       
        when(profileRepo.findByUser(userId)).thenReturn(Optional.empty());

        
        LCP_Exception exception = assertThrows(LCP_Exception.class, () -> service.getUserProfile(userId));
        assertTrue(exception.getMessage().contains("service.USER_PROFILE_NOT_FOUND"));

        verify(profileRepo).findByUser(userId);
        verifyNoInteractions(userRepo);
        verifyNoInteractions(mapper);
    }


    @Test
    void saveProfile_validInput_returnsSavedDTO() throws LCP_Exception {
        Integer userId = 1;

        
        UserProfilesDTO inputDTO = new UserProfilesDTO();

      
        Users user = new Users();
        user.setUserId(userId);

        
        UserProfiles profileToSave = new UserProfiles();
        profileToSave.setUser(user); 

        UserProfiles savedProfile = new UserProfiles();
        UserProfilesDTO savedDTO = new UserProfilesDTO();

        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(true); 
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.map(inputDTO, UserProfiles.class)).thenReturn(profileToSave);
        when(profileRepo.save(profileToSave)).thenReturn(savedProfile);
        when(mapper.map(savedProfile, UserProfilesDTO.class)).thenReturn(savedDTO);

        
        UserProfilesDTO result = service.saveUserProfile(userId, inputDTO, image);

       
        assertEquals(savedDTO, result);

        
        verify(userRepo).findById(userId);
        verify(mapper).map(inputDTO, UserProfiles.class);
        verify(profileRepo).save(profileToSave);
        verify(mapper).map(savedProfile, UserProfilesDTO.class);
    }


    @Test

    void saveProfile_userNotFound_throwsNotFound() {

        Integer userId = 1;

        UserProfilesDTO inputDTO = new UserProfilesDTO();

        when(userRepo.findById(userId)).thenReturn(Optional.empty());
     
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(true); 


        assertThrows(LCP_Exception.class, () -> service.saveUserProfile(userId, inputDTO, image));

        verify(userRepo).findById(userId);

        verifyNoInteractions(mapper, profileRepo);

    }

    @Test

    void updateProfile_existingId_returnsUpdatedDTO() throws LCP_Exception {

        Integer profileId = 1;

        UserProfilesDTO updateDTO = new UserProfilesDTO();

        UserProfiles existingProfile = new UserProfiles();

        UserProfiles updatedProfile = new UserProfiles();

        UserProfilesDTO updatedDTO = new UserProfilesDTO();
     
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(true); 


        when(profileRepo.findById(profileId)).thenReturn(Optional.of(existingProfile));

        when(profileRepo.save(existingProfile)).thenReturn(updatedProfile);

        when(mapper.map(updatedProfile, UserProfilesDTO.class)).thenReturn(updatedDTO);

        UserProfilesDTO result = service.updateUserProfile(profileId, updateDTO, image);

        assertEquals(updatedDTO, result);

        verify(profileRepo).findById(profileId);


        verify(profileRepo).save(existingProfile);

        verify(mapper).map(updatedProfile, UserProfilesDTO.class);

    }

    @Test

    void updateProfile_nonExistingId_throwsNotFound() {

        Integer profileId = 1;

        UserProfilesDTO updateDTO = new UserProfilesDTO();

     
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(true); 

        when(profileRepo.findById(profileId)).thenReturn(Optional.empty());

        assertThrows(LCP_Exception.class, () -> service.updateUserProfile(profileId, updateDTO, image));

        verify(profileRepo).findById(profileId);

        verifyNoInteractions(mapper, userRepo);

    }


    @Test

    void deleteProfile_nonExistingId_throwsNotFound() {

        Integer profileId = 1;

        when(profileRepo.existsById(profileId)).thenReturn(false);

        LCP_Exception exception= assertThrows(LCP_Exception.class, () ->
        service.deleteUserProfile(profileId));


        Assertions.assertEquals("service.USER_PROFILE_DELETED"+profileId, exception.getMessage());
    }

}

