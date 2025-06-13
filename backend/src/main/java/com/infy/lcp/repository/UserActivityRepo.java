package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;

import jakarta.transaction.Transactional;

public interface UserActivityRepo extends JpaRepository<UserActivity, Integer>{

	List<UserActivity> findByUserOrderByOccurredAtDesc(Users user);
	
	void deleteByUserAndActivityId(Users user,Integer activityId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM UserActivity a WHERE a.user.userId = ?1")
	void deleteByUser(Integer userId);

}
