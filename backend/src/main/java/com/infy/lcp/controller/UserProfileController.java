package com.infy.lcp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infy.lcp.dto.UserProfilesDTO;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("profile")
@Tag(name = "User Profile", description = "Operations related to user profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping(value = "/user/{userId}")
    @Operation(summary = "Get user profile", description = "Fetch the user profile using userId")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    public ResponseEntity<UserProfilesDTO> getUserProfile(@PathVariable Integer userId) throws LCP_Exception {
        UserProfilesDTO profile = userProfileService.getUserProfile(userId);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PostMapping(value = "/added/{userId}", consumes = {"multipart/form-data"})
    @Operation(summary = "Add user profile", description = "Create a new user profile with optional image")
    @ApiResponse(responseCode = "201", description = "Profile created successfully")
    public ResponseEntity<UserProfilesDTO> saveProfile(@PathVariable Integer userId,
                                                       @RequestParam("profileDto") String profileDtoJson,
                                                       @RequestPart(value = "image", required = false) MultipartFile image)
            throws LCP_Exception, JsonMappingException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        UserProfilesDTO userProfileDTO = objectMapper.readValue(profileDtoJson, UserProfilesDTO.class);
        UserProfilesDTO savedProfile = userProfileService.saveUserProfile(userId, userProfileDTO, image);
        return new ResponseEntity<>(savedProfile, HttpStatus.CREATED);
    }

    @PutMapping(value = "/userUpdated/{profileId}", consumes = {"multipart/form-data"})
    @Operation(summary = "Update user profile", description = "Update existing user profile data and optional image")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    public ResponseEntity<UserProfilesDTO> updateUserProfile(@PathVariable Integer profileId,
                                                             @RequestParam("profileDto") String profileDtoJson,
                                                             @RequestPart(value = "image", required = false) MultipartFile image)
            throws LCP_Exception, JsonMappingException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        UserProfilesDTO userProfileDTO = objectMapper.readValue(profileDtoJson, UserProfilesDTO.class);
        UserProfilesDTO updateProfile = userProfileService.updateUserProfile(profileId, userProfileDTO, image);
        return new ResponseEntity<>(updateProfile, HttpStatus.OK);
    }

    @DeleteMapping(value = "/userDeleted/{profileId}")
    @Operation(summary = "Delete user profile", description = "Remove a user profile by profileId")
    @ApiResponse(responseCode = "204", description = "Profile deleted successfully")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Integer profileId) {
        try {
            userProfileService.deleteUserProfile(profileId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (LCP_Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}