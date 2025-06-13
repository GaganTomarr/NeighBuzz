package com.infy.lcp.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.EventsDTO;
import com.infy.lcp.entity.EventsCategory;
import com.infy.lcp.exception.LCP_Exception;

public interface EventsService {
	public Integer addEvent(EventsDTO eventDto, MultipartFile image) throws LCP_Exception;
	public void updateEvent(Integer eventId, EventsDTO eventDto, MultipartFile image) throws LCP_Exception;
	public void deleteEvent(Integer eventId) throws LCP_Exception;
	public List<EventsDTO> getAllEvents() throws LCP_Exception;

	public EventsDTO getEventById(Integer eventId) throws LCP_Exception;
	public List<EventsDTO> getEventByCategory(EventsCategory eventCategory) throws LCP_Exception;
	public List<EventsDTO> getEventByDate(LocalDate eventDate) throws LCP_Exception;

	public List<EventsDTO> getVisibleEventsForUser(Integer userId, boolean isAdmin);
	public void approveEvent(Integer eventId, Integer userId) throws LCP_Exception;
	public void rejectEvent(Integer eventId, Integer userId) throws LCP_Exception;
}
