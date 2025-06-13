package com.infy.lcp.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infy.lcp.dto.EventsDTO;
import com.infy.lcp.entity.Events;
import com.infy.lcp.entity.EventsCategory;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.EventsRepo;
import com.infy.lcp.service.EventsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "APIs for managing events")
public class EventsController {

	@Autowired
	private EventsServiceImpl eventsService;


	@Autowired
	private EventsRepo eventsRepository;

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("http://localhost:5173");
		config.addAllowedOrigin("http://localhost:3000");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@PostMapping(value="/post", consumes= {"multipart/form-data"})
	@Operation(summary = "Create a new event", description = "Creates an event with optional image upload")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Event created successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input data")
	})
	public ResponseEntity<String> postEvent(
			@RequestParam("eventDto") String eventDtoJson,
			@RequestPart(value="image", required=false) MultipartFile image
	) throws LCP_Exception, JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		EventsDTO eventsDto=objectMapper.readValue(eventDtoJson, EventsDTO.class);

		Integer eventId=eventsService.addEvent(eventsDto, image);
		return new ResponseEntity<>("Event added with Id: "+eventId, HttpStatus.CREATED);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Delete an event by ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Event deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Event not found")
	})
	public ResponseEntity<String> removeEvent(@RequestParam("eId") Integer eventId) throws LCP_Exception{
		eventsService.deleteEvent(eventId);
		return new ResponseEntity<>("Event Deleted Successfully!", HttpStatus.OK);
	}

	@PutMapping(value="/update", consumes= {"multipart/form-data"})
	@Operation(summary = "Update event details", description = "Update event info with optional image")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Event updated successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid input data"),
		@ApiResponse(responseCode = "404", description = "Event not found")
	})
	public ResponseEntity<String> updateEventDetails(
			@RequestParam("eId") Integer eventId,
			@RequestPart("eventDto") String eventDtoJson,
			@RequestPart(value="image", required=false) MultipartFile image
	) throws LCP_Exception, JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		EventsDTO eventsDto=objectMapper.readValue(eventDtoJson, EventsDTO.class);

		eventsService.updateEvent(eventId, eventsDto, image);
		return new ResponseEntity<>("Event Details Updated Successfully!", HttpStatus.OK);
	}

	@GetMapping("/getAll")
	@Operation(summary = "Get all events visible to a user")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "List of events returned")
	})
	public ResponseEntity<List<EventsDTO>> getAllEvents(
			@RequestParam Integer userId,
			@RequestParam boolean isAdmin
	) throws LCP_Exception {
		List<EventsDTO> eventList = eventsService.getVisibleEventsForUser(userId, isAdmin);
		return new ResponseEntity<>(eventList, HttpStatus.OK);
	}

	@GetMapping("/getById/{eventId}")
	@Operation(summary = "Get event by ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Event found"),
		@ApiResponse(responseCode = "404", description = "Event not found")
	})
	public ResponseEntity<EventsDTO> getEventById(@PathVariable(value="eventId") Integer eventId) throws LCP_Exception
	{
		EventsDTO eventDto=eventsService.getEventById(eventId);
		return new ResponseEntity<> (eventDto,HttpStatus.OK);
	}

	@GetMapping("/getByCategory/{eventCategory}")
	@Operation(summary = "Get events by category")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "List of events returned")
	})
	public ResponseEntity<List<EventsDTO>> getEventByCategory(@PathVariable(value="eventCategory") EventsCategory eventCategory) throws LCP_Exception
	{
		List<EventsDTO> eventList=eventsService.getEventByCategory(eventCategory);
		return new ResponseEntity<> (eventList,HttpStatus.OK);
	}

	@GetMapping("/getByEventDate/{eventDate}")
	@Operation(summary = "Get events by event date")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "List of events returned")
	})
	public ResponseEntity<List<EventsDTO>> getEventByEventDate(@PathVariable(value="eventDate") LocalDate eventDate) throws LCP_Exception
	{
		List<EventsDTO> eventList=eventsService.getEventByDate(eventDate);
		return new ResponseEntity<> (eventList,HttpStatus.OK);
	}

	@PutMapping("/approve/{eventId}")
	@Operation(summary = "Approve and publish an event")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Event approved and published"),
		@ApiResponse(responseCode = "404", description = "Event not found")
	})
	public ResponseEntity<String> approveEvent(@PathVariable Integer eventId) throws LCP_Exception {
		Optional<Events> eventOpt = eventsRepository.findById(eventId);
		Events event = eventOpt.orElseThrow(() -> new LCP_Exception("Event not found"));
		event.setIsPublished(true);
		event.setUpdatedAt(LocalDateTime.now());
		eventsRepository.save(event);
		return ResponseEntity.ok("Event approved and published.");
	}

	@PostMapping("/modLogApprove/{eventId}/{userId}")
	@Operation(summary = "Add moderator approval log")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "202", description = "Event approved"),
		@ApiResponse(responseCode = "404", description = "Event or user not found")
	})
	public ResponseEntity<String> addModLogApprove(@PathVariable("eventId") Integer eventId, @PathVariable("userId") Integer userId) throws LCP_Exception{
		eventsService.approveEvent(eventId, userId);
		return new ResponseEntity<>("Event approved", HttpStatus.ACCEPTED);
	}

	@PostMapping("/modLogReject/{eventId}/{userId}")
	@Operation(summary = "Add moderator rejection log")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "202", description = "Event rejected"),
		@ApiResponse(responseCode = "404", description = "Event or user not found")
	})
	public ResponseEntity<String> addModLogReject(@PathVariable("eventId") Integer eventId, @PathVariable("userId") Integer userId) throws LCP_Exception{
		eventsService.rejectEvent(eventId, userId);
		return new ResponseEntity<>("Event rejected", HttpStatus.ACCEPTED);
	}
}
