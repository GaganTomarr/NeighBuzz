package com.infy.lcp.service;

import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.UserProfilesDTO;
import com.infy.lcp.exception.LCP_Exception;

public interface UserProfileService {
	public UserProfilesDTO getUserProfile(Integer userId) throws LCP_Exception;
	public UserProfilesDTO saveUserProfile(Integer userId, UserProfilesDTO profileDto, MultipartFile image) throws LCP_Exception;
	public UserProfilesDTO updateUserProfile(Integer userId, UserProfilesDTO profileDto, MultipartFile image)throws LCP_Exception;
	public void deleteUserProfile(Integer profileId) throws LCP_Exception;
}
