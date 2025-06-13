package com.infy.lcp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.lcp.entity.EventRegistrations;
import com.infy.lcp.entity.Events;
import com.infy.lcp.entity.Users;

public interface EventRegistrationsRepo extends JpaRepository<EventRegistrations, Integer> {

	boolean existsByEventAndUser(Events event, Users user);

}
