package com.infy.lcp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.infy.lcp.dto.ForumThreadsDTO;
import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.entity.ForumThreads;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ForumThreadsRepo;
import com.infy.lcp.service.ForumThreadsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/threads")
@Tag(name = "Forum Threads", description = "APIs for managing forum threads")
public class ForumThreadsController {

	@Autowired
	private ForumThreadsServiceImpl forumThreadsService;

	@Autowired
	private ForumThreadsRepo forumThreadsRepo;

	@PostMapping(value="/post", consumes= {"multipart/form-data"})
	@Operation(summary = "Create a new forum thread", description = "Creates a new forum thread with optional image upload")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Forum Thread created successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input")
	})
	public ResponseEntity<String> createThread(@RequestParam("forumThreadsDto") String forumThreadDtoJson, @RequestPart(value="image", required=false) MultipartFile image) throws LCP_Exception, JsonMappingException, JsonProcessingException{
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		ForumThreadsDTO forumThreadsDto=objectMapper.readValue(forumThreadDtoJson, ForumThreadsDTO.class);

		Integer forumId=forumThreadsService.addForumThreads(forumThreadsDto, image);
		return new ResponseEntity<>("Forum Thread created with Id: "+forumId, HttpStatus.CREATED);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Delete a forum thread", description = "Deletes the forum thread identified by thread ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Forum Thread removed successfully"),
		@ApiResponse(responseCode = "404", description = "Thread not found")
	})
	public ResponseEntity<String> removeThread(@RequestParam("tId") Integer threadId) throws LCP_Exception{
		forumThreadsService.deleteForumThreads(threadId);
		return new ResponseEntity<>("Forum Thread Removed Successfully!", HttpStatus.OK);
	}

	@PutMapping(value="/update", consumes= {"multipart/form-data"})
	@Operation(summary = "Update forum thread details", description = "Updates details of an existing forum thread")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Forum thread details updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input"),
		@ApiResponse(responseCode = "404", description = "Thread not found")
	})
	public ResponseEntity<String> updateThreadDetails(@RequestParam("tId") Integer threadId, @RequestParam("forumThreadsDto") String forumThreadDtoJson, @RequestPart(value="image", required=false) MultipartFile image) throws LCP_Exception, JsonMappingException, JsonProcessingException{
		ObjectMapper objectMapper=new ObjectMapper();
		ForumThreadsDTO forumThreadsDto=objectMapper.readValue(forumThreadDtoJson, ForumThreadsDTO.class);

		forumThreadsService.updateForumThreads(threadId, forumThreadsDto, image);
		return new ResponseEntity<>("Forum thread details Updated Successfully!", HttpStatus.OK);
	}

	@GetMapping("/getAll")
	@Operation(summary = "Get all forum threads", description = "Retrieves all forum threads")
	@ApiResponse(responseCode = "200", description = "List of forum threads returned successfully")
	public ResponseEntity<List<ForumThreadsDTO>> getAllThreads() throws LCP_Exception{
		return new ResponseEntity<>(forumThreadsService.getAllForumsThreads(), HttpStatus.OK);
	}

	@GetMapping("/getByCategory/{forumCategory}")
	@Operation(summary = "Get forum threads by category", description = "Fetches forum threads filtered by category")
	@ApiResponse(responseCode = "200", description = "List of forum threads by category returned")
	public ResponseEntity<List<ForumThreadsDTO>> getForumThreadsByCategory(@PathVariable(value="forumCategory") ForumCategory forumCategory) throws LCP_Exception{
		return new ResponseEntity<>(forumThreadsService.getForumThreadsByCategory(forumCategory),HttpStatus.OK);
	}

	@GetMapping("/getByDate/{createdAt}")
	@Operation(summary = "Get forum threads by creation date", description = "Fetches forum threads created at a specific date/time")
	@ApiResponse(responseCode = "200", description = "List of forum threads by creation date returned")
	public ResponseEntity<List<ForumThreadsDTO>> getForumThreadsByDate(@PathVariable(value="createdAt") LocalDateTime createdAt) throws LCP_Exception{
		return new ResponseEntity<>(forumThreadsService.getForumThreadsByDate(createdAt),HttpStatus.OK);
	}

	@GetMapping("/getByPopularity")
	@Operation(summary = "Get popular forum threads", description = "Retrieves forum threads sorted by popularity")
	@ApiResponse(responseCode = "200", description = "List of popular forum threads returned")
	public ResponseEntity<List<ForumThreadsDTO>> getForumThreadsByPopularity() throws LCP_Exception{
		return new ResponseEntity<>(forumThreadsService.getForumThreadsByPopularity(),HttpStatus.OK);
	}

	@GetMapping("/search/byKeyword")
	@Operation(summary = "Search forum threads by keyword", description = "Searches forum threads matching the keyword")
	@ApiResponse(responseCode = "200", description = "List of forum threads matching keyword returned")
	public ResponseEntity<List<ForumThreadsDTO>> searchThread(@RequestParam String keyword) throws LCP_Exception{
		return new ResponseEntity<>(forumThreadsService.searchThreadByKeyword(keyword),HttpStatus.OK);
	}

	@GetMapping("/getByThreadId/{threadId}")
	@Operation(summary = "Get forum thread by ID", description = "Retrieves a specific forum thread by its ID")
	@ApiResponse(responseCode = "200", description = "Forum thread returned successfully")
	public ResponseEntity<ForumThreadsDTO> getThreadById(@PathVariable Integer threadId) throws LCP_Exception
	{
		return new ResponseEntity<>(forumThreadsService.getThreadById(threadId),HttpStatus.OK);
	}

	@PutMapping("/approve/{forumThreadId}")
	@Operation(summary = "Approve forum thread", description = "Approves and publishes a forum thread")
	@ApiResponse(responseCode = "200", description = "Forum thread approved and published successfully")
	public ResponseEntity<String> approveForumThread(@PathVariable Integer forumThreadId) throws LCP_Exception {
		Optional<ForumThreads> forumThreadOpt = forumThreadsRepo.findById(forumThreadId);
		ForumThreads forumThreads = forumThreadOpt.orElseThrow(() -> new LCP_Exception("Thread not found"));
		forumThreads.setIsLocked(false);
		forumThreads.setUpdatedAt(LocalDateTime.now());
		forumThreadsRepo.save(forumThreads);
		return ResponseEntity.ok("Forum Thread approved and published.");
	}

	@PostMapping("/modLogApprove/{forumThreadId}/{userId}")
	@Operation(summary = "Moderator approval log", description = "Logs moderator approval for a forum thread")
	@ApiResponse(responseCode = "202", description = "Moderator approval logged successfully")
	public ResponseEntity<String> addModLogApprove(@PathVariable("forumThreadId") Integer forumThreadId, @PathVariable("userId") Integer userId) throws LCP_Exception{
		forumThreadsService.approveForumThread(forumThreadId, userId);
		return new ResponseEntity<>("Forum Thread approved", HttpStatus.ACCEPTED);
	}

	@PostMapping("/modLogReject/{forumThreadId}/{userId}")
	@Operation(summary = "Moderator rejection log", description = "Logs moderator rejection for a forum thread")
	@ApiResponse(responseCode = "202", description = "Moderator rejection logged successfully")
	public ResponseEntity<String> addModLogReject(@PathVariable("forumThreadId") Integer forumThreadId, @PathVariable("userId") Integer userId) throws LCP_Exception{
		forumThreadsService.rejectForumThread(forumThreadId, userId);
		return new ResponseEntity<>("Forum Thread rejected", HttpStatus.ACCEPTED);
	}
}
