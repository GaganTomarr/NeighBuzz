package com.infy.lcp.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
//import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.UserProfilesDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.UserProfiles;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.UsersProfileRepo;
import com.infy.lcp.repository.UsersRepo;

import jakarta.transaction.Transactional;

@Service
public class UserProfileServiceImpl implements UserProfileService{
	@Autowired
	private UsersProfileRepo  usersProfileRepo;

	@Autowired
	private FileStorageServiceImpl fileStorageServiceImpl;

	@Autowired
	private UsersRepo usersRepo;

	private ModelMapper modelMapper = new ModelMapper();

	@Override
	public UserProfilesDTO getUserProfile(Integer userId) throws LCP_Exception {
		UserProfiles profile = usersProfileRepo.findByUser(userId).orElseThrow(()->new LCP_Exception("service.USER_PROFILE_NOT_FOUND"+userId));
		Users user = usersRepo.findById(userId).orElseThrow(() -> new LCP_Exception("service.USER_NOT_EXIST"));
		UsersDTO dto  = modelMapper.map(user, UsersDTO.class);
		UserProfilesDTO pdto = modelMapper.map(profile, UserProfilesDTO.class);
		pdto.setUserDTO(dto);
		return pdto;
	}

	@Override
	@Transactional
	public UserProfilesDTO saveUserProfile(Integer userId, UserProfilesDTO profileDto, MultipartFile image) throws LCP_Exception{
		Users user=usersRepo.findById(userId).orElseThrow(()->new  LCP_Exception("service.USER_NOT_EXIST"+userId) );

		UserProfiles profile= modelMapper.map(profileDto,UserProfiles.class );
		profile.setUser(user);

		String imageUrl=null;
		if(image!=null && !image.isEmpty()) {
			imageUrl=fileStorageServiceImpl.storeFile(image);
			profile.setProfilePicture(imageUrl);
		}

		UserProfiles savedprofile= usersProfileRepo.save(profile);

		return modelMapper.map(savedprofile,UserProfilesDTO.class);
	}

	@Override
	public UserProfilesDTO updateUserProfile(Integer profileId,UserProfilesDTO profileDto, MultipartFile image)throws LCP_Exception{
		UserProfiles existingProfile=usersProfileRepo.findById(profileId).orElseThrow(()-> new LCP_Exception("service.USER_PROFILE_NOT_FOUND"+profileId));
		existingProfile.setDisplayName(profileDto.getDisplayName());
		existingProfile.setBio(profileDto.getBio());
		existingProfile.setLocation(profileDto.getLocation());
		existingProfile.setProfilePicture(profileDto.getProfilePicture());
		existingProfile.setProfileVisibility(profileDto.getProfileVisibility());
		String imageUrl=null;
		if(image!=null && !image.isEmpty()) {
			imageUrl=fileStorageServiceImpl.storeFile(image);
			existingProfile.setProfilePicture(imageUrl);
		}
		UserProfiles updateProfile=usersProfileRepo.save(existingProfile);
		return modelMapper.map(updateProfile, UserProfilesDTO.class);
	}

	@Override
	public void deleteUserProfile(Integer profileId) throws LCP_Exception{
		Optional<UserProfiles> existingProfile=usersProfileRepo.findById(profileId);
		if(existingProfile.isPresent()) {

			usersProfileRepo.deleteById(profileId);
		}else {
			throw new LCP_Exception("service.USER_PROFILE_DELETED"+profileId);
		}
	}
}