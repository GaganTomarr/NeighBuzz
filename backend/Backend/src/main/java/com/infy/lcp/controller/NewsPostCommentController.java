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

import com.infy.lcp.dto.NewsPostCommentsDTO;
import com.infy.lcp.entity.NewsPostComments;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.NewsPostCommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "News Post Comments", description = "APIs for managing comments on news posts")
public class NewsPostCommentController {

	@Autowired
	private NewsPostCommentService commentService;

	@PostMapping("/post-comment/{userId}/{postId}")
	@Operation(summary = "Post a comment", description = "Create a comment on a news post by a user")
	@ApiResponse(responseCode = "201", description = "Comment created successfully")
	public ResponseEntity<String> postComment(@RequestBody NewsPostCommentsDTO comment, @PathVariable Integer userId, @PathVariable Integer postId) throws LCP_Exception {
		Integer commentId =  commentService.postComments(comment, userId, postId);
		return new ResponseEntity<>("Forum Thread Comment created with Id: "+ commentId, HttpStatus.CREATED);
	}

	@GetMapping("/get-comment/{contentId}")
	@Operation(summary = "Get comments by content ID", description = "Retrieve all comments for a specific content ID")
	@ApiResponse(responseCode = "200", description = "Comments fetched successfully")
	public ResponseEntity<List<NewsPostComments>> findByContentId(@PathVariable Integer contentId) throws LCP_Exception {
		return new ResponseEntity<>(commentService.findByContentId(contentId), HttpStatus.OK);
	}

	@DeleteMapping("/delete/{commentId}/{userId}")
	@Operation(summary = "Delete a comment", description = "Delete a comment by comment ID and user ID")
	@ApiResponse(responseCode = "200", description = "Comment deleted successfully")
	public ResponseEntity<String> deleteComment(@PathVariable Integer commentId, @PathVariable Integer userId) throws LCP_Exception {
		commentService.deleteComment(commentId, userId);
		return new ResponseEntity<>("comment deleted successully!", HttpStatus.OK);
	}

	@PutMapping("/update/{commentId}/{userId}")
	@Operation(summary = "Update a comment", description = "Update a comment by comment ID and user ID")
	@ApiResponse(responseCode = "200", description = "Comment updated successfully")
	public ResponseEntity<String> updateComment(@RequestBody NewsPostCommentsDTO comment, @PathVariable Integer commentId, @PathVariable Integer userId) throws LCP_Exception {
		commentService.updateComment(comment, commentId, userId);
		return new ResponseEntity<>("Forum thread comment Updated Successfully!", HttpStatus.OK);
	}

	@PostMapping("/reply/{parentCommentId}/{userId}/{postId}")
	@Operation(summary = "Reply to a comment", description = "Reply to an existing comment")
	@ApiResponse(responseCode = "201", description = "Reply created successfully")
	public ResponseEntity<String> replyComment(@RequestBody NewsPostCommentsDTO comment, @PathVariable Integer parentCommentId, @PathVariable Integer userId, @PathVariable Integer postId) throws LCP_Exception {
		Integer commentId = commentService.replyComment(comment, parentCommentId, userId, postId);
		return new ResponseEntity<>("replied to comment id "+ parentCommentId + " done, with the new created comment id " + commentId, HttpStatus.CREATED);
	} 
}

