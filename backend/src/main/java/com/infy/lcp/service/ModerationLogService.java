package com.infy.lcp.service;

import java.util.List;

import com.infy.lcp.dto.ModerationLogDTO;
import com.infy.lcp.exception.LCP_Exception;

public interface ModerationLogService{
	public List<ModerationLogDTO> getAllModLogs() throws LCP_Exception;
}