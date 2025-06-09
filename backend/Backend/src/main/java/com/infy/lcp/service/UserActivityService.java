package com.infy.lcp.service;

import java.util.List;

import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.exception.LCP_Exception;

public interface UserActivityService {
	
	public List<UserActivity> getUserActivity(Integer userId) throws LCP_Exception;
	public void deleteUserActivity(Integer userId,Integer activityId) throws LCP_Exception;
}
