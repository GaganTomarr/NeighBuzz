package com.infy.lcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.infy.lcp.dto.ModerationLogDTO;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.service.ModerationLogServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/modLog")
@Tag(name = "Moderation Logs", description = "APIs for managing moderation logs")
public class ModerationLogController {

    @Autowired
    private ModerationLogServiceImpl modLogService;

    @GetMapping("/getAll")
    @Operation(summary = "Get all moderation logs", description = "Retrieves all moderation log entries")
    @ApiResponse(responseCode = "200", description = "List of moderation logs returned successfully")
    public ResponseEntity<List<ModerationLogDTO>> getAllModLogs() throws LCP_Exception {
        return new ResponseEntity<>(modLogService.getAllModLogs(), HttpStatus.OK);
    }
}