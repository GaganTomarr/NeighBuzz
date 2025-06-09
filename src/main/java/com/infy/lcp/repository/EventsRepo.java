package com.infy.lcp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.Events;
import com.infy.lcp.entity.EventsCategory;

public interface EventsRepo extends JpaRepository<Events, Integer> {
	
@Query("Select e from Events e where e.eventsCategory=?1")
public List<Events> findByCategory(EventsCategory eventsCategory);

@Query("Select e from Events e where e.eventDate=?1")
public List<Events> findByEventDate(LocalDate eventDate);

}
