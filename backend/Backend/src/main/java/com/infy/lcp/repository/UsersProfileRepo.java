package com.infy.lcp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.UserProfiles;

public interface UsersProfileRepo extends JpaRepository<UserProfiles,Integer> {
	@Query("select u from UserProfiles u where u.user.userId = ?1")
	Optional<UserProfiles> findByUser(Integer userId);
}
