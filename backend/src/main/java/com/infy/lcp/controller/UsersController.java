package com.infy.lcp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
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

import com.infy.lcp.dto.LoginHistoryDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.dto.UsersPwdDTO;
import com.infy.lcp.entity.LoginHistory;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.LoginHistoryRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.security.JwtUtil;
import com.infy.lcp.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "auth")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Tag(name = "User Authentication", description = "Endpoints for user registration, login, management, and login history")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginHistoryRepo loginHistoryRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersRepo usersRepository;

    @PostMapping(value = "/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    public ResponseEntity<String> registerUser(@RequestBody UsersPwdDTO usersdto) {
        if (usersdto.getIsAdmin() == null) {
            usersdto.setIsAdmin(false);
        }
        try {
            userService.registerNewUserAccount(usersdto.getEmail(), usersdto.getUsername(), usersdto.getPassword(), usersdto.getIsAdmin());
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    public ResponseEntity<?> login(@RequestBody UsersPwdDTO dto, HttpServletRequest request) throws LCP_Exception {
        String ipAddress = request.getRemoteAddr();
        String token = userService.loginUser(dto, ipAddress);

        Integer userId = jwtUtil.extractUserId(token); // Get userId from token

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", userId);
        response.put("username", dto.getUsername());

        Optional<Users> userOpt = usersRepository.findById(userId);
        response.put("isAdmin", userOpt.get().getIsAdmin());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    @Operation(summary = "Update user password", description = "Update password for logged-in user")
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    public ResponseEntity<String> update(@RequestBody UsersPwdDTO dto) throws LCP_Exception {
        String token = userService.updatePassword(dto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @GetMapping(value = "/history/{userId}")
    @Operation(summary = "Get login history", description = "Retrieve login history of a user")
    @ApiResponse(responseCode = "200", description = "Login history retrieved successfully")
    public ResponseEntity<List<LoginHistoryDTO>> history(@PathVariable Integer userId) throws LCP_Exception {
        List<LoginHistory> login = loginHistoryRepo.getByUser(userId);
        List<LoginHistoryDTO> loginDTO = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        login.stream().forEach(log -> {
            LoginHistoryDTO dto = mapper.map(log, LoginHistoryDTO.class);
            loginDTO.add(dto);
        });
        return new ResponseEntity<>(loginDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/history/{userId}")
    @Operation(summary = "Delete user login history", description = "Remove login history of a user")
    @ApiResponse(responseCode = "200", description = "Login history deleted successfully")
    public ResponseEntity<String> deleteHitory(@PathVariable Integer userId) throws LCP_Exception {
        userService.deleteHistory(userId);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/user/{userId}")
    @Operation(summary = "Delete user account", description = "Delete user data and account")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) throws LCP_Exception {
        userService.deleteUserData(userId);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get all users", description = "Retrieve list of all registered users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<List<UsersDTO>> getAllUsers() throws LCP_Exception {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}