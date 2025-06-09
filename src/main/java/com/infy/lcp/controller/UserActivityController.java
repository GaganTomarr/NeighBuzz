package com.infy.lcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.UserActivityServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping(value = "user")
@Tag(name = "User Activity", description = "APIs for managing user activities")
public class UserActivityController {

    @Autowired
    private UserActivityServiceImpl userActivityService;

    @GetMapping(value = "/activity/{userId}")
    @Operation(summary = "Get user activity", description = "Retrieve the activity list of a given user by userId")
    @ApiResponse(responseCode = "200", description = "User activity retrieved successfully")
    public ResponseEntity<List<UserActivity>> getUserActivity(@PathVariable Integer userId) throws LCP_Exception {
        List<UserActivity> activity = userActivityService.getUserActivity(userId);
        return ResponseEntity.ok(activity);
    }

    @DeleteMapping(value = "/deleteActivity/{activityId}/{userId}")
    @Operation(summary = "Delete specific user activity", description = "Delete a specific activity by activityId for a given userId")
    @ApiResponse(responseCode = "204", description = "User activity deleted successfully")
    public ResponseEntity<Void> deleteUserActivity(@PathVariable Integer userId, @PathVariable Integer activityId) throws LCP_Exception {
        userActivityService.deleteUserActivity(userId, activityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/delete/{userId}")
    @Operation(summary = "Delete all activities for user", description = "Delete all activities for the given userId")
    @ApiResponse(responseCode = "200", description = "All user activities deleted successfully")
    public ResponseEntity<Void> delte(@PathVariable Integer userId) throws LCP_Exception {
        userActivityService.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}