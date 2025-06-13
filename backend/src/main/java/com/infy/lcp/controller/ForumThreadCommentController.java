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
import org.springframework.web.bind.annotation.RestController;

import com.infy.lcp.dto.ForumThreadCommentsDTO;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.ForumThreadCommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/comments/forum")
@Tag(name = "Forum Thread Comments", description = "APIs for managing forum thread comments")
public class ForumThreadCommentController {

	@Autowired
	ForumThreadCommentService service;
	
	@PostMapping("/post/comment/{userId}/{threadId}")
	@Operation(summary = "Post a new comment", description = "Creates a new comment in a forum thread")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Comment created successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input")
	})
	public ResponseEntity<String> postComment(@RequestBody ForumThreadCommentsDTO comment, @PathVariable Integer userId, @PathVariable Integer threadId) throws LCP_Exception {
		Integer commentId = service.postComment(comment, userId, threadId);
		return new ResponseEntity<>("Forum Thread Comment created with Id: "+ commentId, HttpStatus.CREATED);
	}
	
	@GetMapping("/get/comment")
	@Operation(summary = "Get all comments", description = "Fetches all forum thread comments")
	@ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
	public ResponseEntity<List<ForumThreadCommentsDTO>> getComments() throws LCP_Exception {
		return new ResponseEntity<>(service.getComments(), HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/comment/{commentId}/{userId}")
	@Operation(summary = "Delete a comment", description = "Deletes a comment by its ID and user ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Comment not found")
	})
	public ResponseEntity<String> deleteComment(@PathVariable Integer commentId, @PathVariable Integer userId) throws LCP_Exception {
		service.deleteComment(commentId, userId);
		return new ResponseEntity<>("comment deleted successully!", HttpStatus.OK);
	}
	
	@PutMapping("/update/{commentId}/{userId}")
	@Operation(summary = "Update a comment", description = "Updates an existing forum thread comment")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Comment updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input"),
		@ApiResponse(responseCode = "404", description = "Comment not found")
	})
	public ResponseEntity<String> updateComment(@RequestBody ForumThreadCommentsDTO comment, @PathVariable Integer commentId, @PathVariable Integer userId) throws LCP_Exception {
		service.updateComment(comment, commentId, userId); 
		return new ResponseEntity<>("Forum thread comment Updated Successfully!", HttpStatus.OK);
	}
	
	@PostMapping("/post/reply/{parentCommentId}/{userId}/{threadId}")
	@Operation(summary = "Reply to a comment", description = "Creates a reply to an existing comment in a forum thread")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Reply created successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input")
	})
	public ResponseEntity<String> replyComment(@RequestBody ForumThreadCommentsDTO comment, @PathVariable Integer parentCommentId, @PathVariable Integer userId, @PathVariable Integer threadId) throws LCP_Exception {
		Integer commentId = service.replyComment(comment, parentCommentId, userId, threadId);
		return new ResponseEntity<>("replied to comment id "+ parentCommentId + " done, with the new created comment id " + commentId, HttpStatus.CREATED);
	}
}
