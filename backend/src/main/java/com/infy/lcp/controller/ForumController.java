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

import com.infy.lcp.dto.ForumDTO;
import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.ForumServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/forums")
@Tag(name = "Forum Management", description = "APIs for managing forums")
public class ForumController {
	
	@Autowired
	private ForumServiceImpl forumService;
	
	@PostMapping("/post")
	@Operation(summary = "Create a new forum", description = "Creates a new forum and returns the forum ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Forum created successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid forum data")
	})
	public ResponseEntity<String> createForum(@RequestBody ForumDTO forumDto) throws LCP_Exception {
		Integer forumId = forumService.addForum(forumDto);
		return new ResponseEntity<>("Forum created with Id: " + forumId, HttpStatus.CREATED);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Delete a forum", description = "Deletes a forum by its ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Forum removed successfully"),
		@ApiResponse(responseCode = "404", description = "Forum not found")
	})
	public ResponseEntity<String> removeForum(@RequestParam("fId") Integer forumId) throws LCP_Exception {
		forumService.deleteForum(forumId);
		return new ResponseEntity<>("Forum Removed Successfully!", HttpStatus.OK);
	}

	@PutMapping("/update")
	@Operation(summary = "Update forum details", description = "Updates the details of a forum by its ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Forum details updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid forum data"),
		@ApiResponse(responseCode = "404", description = "Forum not found")
	})
	public ResponseEntity<String> updateForumDetails(@RequestParam("fId") Integer forumId, @RequestBody ForumDTO forumDto) throws LCP_Exception {
		forumService.updateForum(forumId, forumDto);
		return new ResponseEntity<>("Event Details Updated Successfully!", HttpStatus.OK);
	}
	
	@GetMapping("/getAll")
	@Operation(summary = "Get all forums", description = "Returns a list of all forums")
	@ApiResponse(responseCode = "200", description = "List of forums retrieved successfully")
	public ResponseEntity<List<ForumDTO>> getAllForum() throws LCP_Exception {
		return new ResponseEntity<>(forumService.getAllForums(), HttpStatus.OK);
	}
	
	@GetMapping("/getByCategory/{forumCategory}")
	@Operation(summary = "Get forums by category", description = "Returns a list of forums filtered by category")
	@ApiResponse(responseCode = "200", description = "Forums retrieved successfully")
	public ResponseEntity<List<ForumDTO>> getForumByCategory(@PathVariable(value="forumCategory") ForumCategory forumCategory) throws LCP_Exception {
		return new ResponseEntity<>(forumService.getForumByCategory(forumCategory), HttpStatus.OK);
	}
}
