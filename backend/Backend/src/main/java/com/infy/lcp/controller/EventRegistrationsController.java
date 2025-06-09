package com.infy.lcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.lcp.dto.EventRegistrationsDTO;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.EventRegistrationsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/eventRegistrations")
@Tag(name = "Event Registrations", description = "APIs for managing event registrations")
public class EventRegistrationsController {
    @Autowired
    private EventRegistrationsServiceImpl eventRegService;
     
    @PostMapping("/post")
    @Operation(summary = "Register an event", description = "Creates a new event registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<String> register(@RequestBody EventRegistrationsDTO eventRegDto) throws LCP_Exception{
        Integer registrationId=eventRegService.makeRegistrations(eventRegDto);
        return new ResponseEntity<>("Registration added with Id: "+registrationId, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Cancel registration", description = "Deletes an existing event registration by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration removed successfully"),
        @ApiResponse(responseCode = "404", description = "Registration not found"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<String> cancelRegistration(@RequestParam("rId") Integer registrationId) throws LCP_Exception{
        eventRegService.cancelRegistrations(registrationId);
        return new ResponseEntity<>("Registration Removed Successfully!", HttpStatus.OK);
    }
    
    @GetMapping("/getAll")
    @Operation(summary = "Get all registrations", description = "Retrieves all event registrations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of all registrations retrieved"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<List<EventRegistrationsDTO>> showAllRegistrations() throws LCP_Exception{
        return new ResponseEntity<>(eventRegService.fetchAllRegistrations(),HttpStatus.OK);
    }
}

