package com.infy.lcp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infy.lcp.dto.ModerationLogDTO;
import com.infy.lcp.dto.NewsPostsDTO;
import com.infy.lcp.entity.NewsPosts;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.NewsPostRepo;
import com.infy.lcp.service.NewsPostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping(value="/news")
@Validated
@Tag(name = "News Posts", description = "APIs for managing news posts")
public class NewsPostsController {
	@Autowired
	NewsPostRepo newsPostRepo;
	@Autowired
	NewsPostService newsService;
	@Autowired
	Environment environment;

	@GetMapping(value="/newsPost/{newsCategory}")
	@Operation(summary = "Get news posts by category", description = "Retrieve list of news posts filtered by category")
	@ApiResponse(responseCode = "200", description = "List of news posts retrieved successfully")
	public ResponseEntity<List<NewsPostsDTO>> getNewsPosts(@PathVariable String newsCategory) throws LCP_Exception {
		List<NewsPostsDTO> lis= newsService.getNewsPosts(newsCategory);
		return new ResponseEntity<>(lis,HttpStatus.OK);
	}

	@GetMapping(value="/post/{postId}")
	@Operation(summary = "Get news post by ID", description = "Retrieve a news post by its ID")
	@ApiResponse(responseCode = "200", description = "News post retrieved successfully")
	public ResponseEntity<NewsPostsDTO> getPostById(@PathVariable Integer postId) throws LCP_Exception {
		NewsPostsDTO post = newsService.getPostById(postId);
		return new ResponseEntity<>(post, HttpStatus.OK);
	}

	@GetMapping(value="/newsPost")
	@Operation(summary = "Get all news posts", description = "Retrieve all news posts")
	@ApiResponse(responseCode = "200", description = "List of all news posts retrieved successfully")
	public ResponseEntity<List<NewsPostsDTO>> getAllNewsPosts() throws LCP_Exception {
		List<NewsPostsDTO> lis= newsService.getAllNewsPosts();
		return new ResponseEntity<>(lis,HttpStatus.OK);
	}

	@PostMapping(value="/newsPost/add/{userId}", consumes = {"multipart/form-data"})
	@Operation(summary = "Add a new news post", description = "Create a new news post with optional image upload")
	@ApiResponse(responseCode = "201", description = "News post created successfully")
	public ResponseEntity<String> addPost(@RequestParam("newsDto") String newsDtoJson,
			@PathVariable Integer userId, @RequestPart(value = "image", required = false) MultipartFile image)
					throws LCP_Exception, JsonMappingException, JsonProcessingException {
		ObjectMapper obj = new ObjectMapper();
		obj.registerModule(new JavaTimeModule());
		obj.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		NewsPostsDTO dto = obj.readValue(newsDtoJson, NewsPostsDTO.class);
		String response = newsService.addNews(dto, userId, image);
		return new ResponseEntity<String>(response, HttpStatus.CREATED);
	}

	@DeleteMapping(value="/newsPost/delete/{postId}")
	@Operation(summary = "Delete a news post", description = "Delete a news post by its ID")
	@ApiResponse(responseCode = "200", description = "News post deleted successfully")
	public ResponseEntity<String> deletePost(@PathVariable Integer postId) throws LCP_Exception {
		String response = newsService.deleteNews(postId);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@PutMapping(value="/newsPost/update/{userId}", consumes = {"multipart/form-data"})
	@Operation(summary = "Update a news post", description = "Update a news post with optional image")
	@ApiResponse(responseCode = "202", description = "News post updated successfully")
	public ResponseEntity<String> updatePost(@PathVariable Integer userId, @RequestPart("newsDto") String dtoJson,
			@RequestPart(value = "image", required = false) MultipartFile image)
					throws LCP_Exception, JsonMappingException, JsonProcessingException {
		ObjectMapper obj = new ObjectMapper();
		obj.registerModule(new JavaTimeModule());
		obj.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		NewsPostsDTO dto = obj.readValue(dtoJson, NewsPostsDTO.class);
		String response = newsService.updateNews(dto, userId, image);
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}

	@PutMapping(value="/newsPost/approval")
	@Operation(summary = "Approve a news post via moderation log", description = "Approve a news post by providing moderation log details")
	@ApiResponse(responseCode = "202", description = "News post approved successfully")
	public ResponseEntity<String> approve(@RequestBody ModerationLogDTO log) throws LCP_Exception {
		String response = newsService.approveNews(log);
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}

	@PutMapping("/approve/{postId}")
	@Operation(summary = "Approve and publish a news post", description = "Mark a news post as approved and published by ID")
	@ApiResponse(responseCode = "200", description = "News post approved and published successfully")
	public ResponseEntity<String> approveEvent(@PathVariable Integer postId) throws LCP_Exception {
		Optional<NewsPosts> newsPostOpt = newsPostRepo.findById(postId);
		NewsPosts post = newsPostOpt.orElseThrow(() -> new LCP_Exception("Post not found"));
		post.setIsApproved(true);
		post.setUpdatedAt(LocalDateTime.now());
		newsPostRepo.save(post);
		return ResponseEntity.ok("Post approved and published.");
	}

	@PostMapping("/modLogApprove/{postId}/{userId}")
	@Operation(summary = "Add moderation log entry for approval", description = "Record approval of news post by moderator")
	@ApiResponse(responseCode = "202", description = "Moderation log for approval recorded")
	public ResponseEntity<String> addModLogApprove(@PathVariable("postId") Integer postId, @PathVariable("userId") Integer userId) throws LCP_Exception {
		newsService.approveNewsPost(postId, userId);
		return new ResponseEntity<>("Post approved", HttpStatus.ACCEPTED);
	}

	@PostMapping("/modLogReject/{postId}/{userId}")
	@Operation(summary = "Add moderation log entry for rejection", description = "Record rejection of news post by moderator")
	@ApiResponse(responseCode = "202", description = "Moderation log for rejection recorded")
	public ResponseEntity<String> addModLogReject(@PathVariable("postId") Integer postId, @PathVariable("userId") Integer userId) throws LCP_Exception {
		newsService.rejectNewsPost(postId, userId);
		return new ResponseEntity<>("Post rejected", HttpStatus.ACCEPTED);
	}
}