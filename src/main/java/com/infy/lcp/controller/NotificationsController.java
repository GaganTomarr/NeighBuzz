package com.infy.lcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.lcp.dto.NotificationsDTO;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.NotificationsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "APIs for managing notifications")
public class NotificationsController {
	@Autowired
	private NotificationsServiceImpl notificationsService;

	@PostMapping("/post")
	@Operation(summary = "Send a notification", description = "Push a new notification")
	@ApiResponse(responseCode = "201", description = "Notification sent successfully")
	public ResponseEntity<String> postNotification(@RequestBody NotificationsDTO notificationsDto) throws LCP_Exception {
		Integer notificationId = notificationsService.pushNotification(notificationsDto);
		return new ResponseEntity<>("Notification sent with Id: " + notificationId, HttpStatus.CREATED);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Remove a notification", description = "Delete a notification")
	@ApiResponse(responseCode = "200", description = "Notification removed successfully")
	public ResponseEntity<String> deleteNotification(@RequestBody NotificationsDTO notificationsDto) throws LCP_Exception {
		notificationsService.removeNotification(notificationsDto);
		return new ResponseEntity<>("Notification Removed Successfully!", HttpStatus.OK);
	}

	@GetMapping("/getAll")
	@Operation(summary = "Get all notifications for a user", description = "Fetch all notifications for a given user by userId")
	@ApiResponse(responseCode = "200", description = "List of notifications retrieved successfully")
	public ResponseEntity<List<NotificationsDTO>> getAllNotifications(@RequestParam("uId") Integer userId) throws LCP_Exception {
		return new ResponseEntity<>(notificationsService.fetchAllNotifactions(userId), HttpStatus.OK);
	}

	@PutMapping("/mark-as-read/{id}")
	@Operation(summary = "Mark a notification as read", description = "Mark a single notification as read by ID")
	@ApiResponse(responseCode = "200", description = "Notification marked as read")
	public ResponseEntity<String> markAsRead(@PathVariable Integer id) throws LCP_Exception {
		notificationsService.markNotificationAsRead(id);
		return new ResponseEntity<>("Notification marked as read", HttpStatus.OK);
	}

	@PutMapping("/mark-all-as-read")
	@Operation(summary = "Mark all notifications as read", description = "Mark all notifications for a user as read")
	@ApiResponse(responseCode = "200", description = "All notifications marked as read")
	public ResponseEntity<String> markAllAsRead(@RequestParam("uId") Integer userId) throws LCP_Exception {
		notificationsService.markAllNotificationsAsRead(userId);
		return new ResponseEntity<>("All notifications marked as read", HttpStatus.OK);
	}
}