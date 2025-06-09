package com.infy.lcp.service;

import java.util.List;

import com.infy.lcp.dto.EventRegistrationsDTO;
import com.infy.lcp.exception.LCP_Exception;

public interface EventRegistrationsService {
	public Integer makeRegistrations(EventRegistrationsDTO eventRegistrationDto) throws LCP_Exception;
	public List<EventRegistrationsDTO> fetchAllRegistrations() throws LCP_Exception;
	public void cancelRegistrations(Integer registrationId) throws LCP_Exception;
}