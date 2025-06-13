package com.infy.lcp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserActivityServiceImpl {
	@Autowired
private UserActivityRepo userActivityRepo ;
	@Autowired
	private UsersRepo usersRepo;
	
	public List<UserActivity> getUserActivity(Integer userId) throws LCP_Exception{
		Optional<Users> usersOp=usersRepo.findById(userId);
		if(usersOp.isPresent()) {
			return userActivityRepo.findByUserOrderByOccurredAtDesc(usersOp.get());
		}
		else {
			throw new LCP_Exception("service.USER_ACTIVITY_NO_PROFILE"+userId);
		}
	}
	
	public void deleteUserActivity(Integer userId,Integer activityId) throws LCP_Exception {
		Optional<UserActivity> act = userActivityRepo.findById(activityId);
		if(act.isEmpty()) {
			throw new LCP_Exception("service.USER_ACTIVIY_DELETE");
		}
		UserActivity activity = act.get();
		activity.setUser(null);
		userActivityRepo.deleteById(activityId);
	}
	
	public void delete(Integer userId) throws LCP_Exception{
		userActivityRepo.deleteByUser(userId);
	}
}
