package com.infy.lcp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.lcp.entity.ModerationLog;

public interface ModerationLogRepo extends JpaRepository<ModerationLog, Integer>{
	
}
