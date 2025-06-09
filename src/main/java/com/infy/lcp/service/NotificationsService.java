package com.infy.lcp.service;

import java.util.List;

import com.infy.lcp.dto.NotificationsDTO;
import com.infy.lcp.exception.LCP_Exception;

public interface NotificationsService {
	public Integer pushNotification (NotificationsDTO notificationDto) throws LCP_Exception;
	public List<NotificationsDTO> fetchAllNotifactions (Integer userId) throws LCP_Exception;
	public void removeNotification(NotificationsDTO notificationDto) throws LCP_Exception;
	public void markNotificationAsRead(Integer notificationId) throws LCP_Exception;
	void markAllNotificationsAsRead(Integer userId) throws LCP_Exception;

}