package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.LoginHistory;

public interface LoginHistoryRepo extends JpaRepository<LoginHistory, Integer>{
	@Query("select l from LoginHistory l where l.user.userId = ?1")
	public List<LoginHistory> getByUser(Integer userId);
}
