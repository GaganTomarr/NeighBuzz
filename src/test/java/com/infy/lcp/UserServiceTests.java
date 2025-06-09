package com.infy.lcp;

import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.infy.lcp.dto.UsersPwdDTO;
import com.infy.lcp.entity.AccountStatus;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.LoginHistoryRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.security.JwtUtil;
import com.infy.lcp.service.UserServiceImpl;

@SpringBootTest
public class UserServiceTests {

	@Mock
	private UsersRepo usersRepo;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtUtil jwtUtil;
	@Mock
	private LoginHistoryRepo loginHistoryRepo;
	@InjectMocks
	private UserServiceImpl userService;


	private Users testUser;
	private UsersPwdDTO testDto;
	private String testIpAddress;

	@BeforeEach
	void setUp() {
		testUser = new Users();
		testUser.setEmail("test@example.com");
		testUser.setUsername("testuser");
		testUser.setPasswordHash("hashedpassword");
		testUser.setAccountStatus(AccountStatus.ACTIVE);
		testUser.setUserId(1);
		testDto = new UsersPwdDTO();
		testDto.setEmail("test@example.com");
		testDto.setUsername("testuser");
		testDto.setPassword("Password123");
		testIpAddress = "127.0.0.1";
	}



	@Test
	void registerNewUserAccount_Success() throws LCP_Exception {
		String email = "new@example.com";
		String username = "newuser";
		String password = "Password123";
		Boolean isAdmin=false;

		Mockito.when(usersRepo.existsByEmail(email)).thenReturn(false);
		Mockito.when(usersRepo.existsByUsername(username)).thenReturn(false);
		Mockito.when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

		userService.registerNewUserAccount(email, username, password, isAdmin);
	}



	@Test
	void registerNewUserAccount_EmailExists_ThrowsException() {
		String email = "existing@example.com";
		String username = "newuser";
		String password = "Password123";
		Boolean isAdmin=false;
		Mockito.when(usersRepo.existsByEmail(email)).thenReturn(true);
		LCP_Exception exception = Assertions.assertThrows(LCP_Exception.class, () -> {userService.registerNewUserAccount(email, username, password, isAdmin);});
		Assertions.assertEquals("service.USER_EMAIL", exception.getMessage());
	}



	@Test
	void registerNewUserAccount_UsernameExists_ThrowsException() {
		String email = "new@example.com";
		String username = "existinguser";
		String password = "Password123";
		Boolean isAdmin=false;
		Mockito.when(usersRepo.existsByEmail(email)).thenReturn(false);
		Mockito.when(usersRepo.existsByUsername(username)).thenReturn(true);
		LCP_Exception exception = Assertions.assertThrows(LCP_Exception.class, () -> {userService.registerNewUserAccount(email, username, password, isAdmin);});
		Assertions.assertEquals("service.USER_USERNAME", exception.getMessage());
	}


	@Test
	void loginUser_Success() throws LCP_Exception {
		testDto.setUsername("testuser");
		testDto.setEmail(null);
		testDto.setPassword("correctPassword");

		Mockito.when(usersRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
		Mockito.when(passwordEncoder.matches(testDto.getPassword(), testUser.getPasswordHash())).thenReturn(true);
		Mockito.when(jwtUtil.generateToken(testUser.getUsername(), testUser.getUserId())).thenReturn("jwt-token");

		String result = userService.loginUser(testDto, testIpAddress);
		Assertions.assertEquals("jwt-token", result);
	}

	@Test
	void loginUser_InvalidCredentials_ThrowsException() {
		testDto.setUsername("testuser");
		testDto.setEmail(null);
		testDto.setPassword("wrongPassword");

		Mockito.when(usersRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
		Mockito.when(passwordEncoder.matches(testDto.getPassword(), testUser.getPasswordHash())).thenReturn(false);

		LCP_Exception exception = Assertions.assertThrows(LCP_Exception.class, () -> {userService.loginUser(testDto, testIpAddress);});
		Assertions.assertEquals("service.CREDENTIALS_WRONG", exception.getMessage());
	}

	@Test
	void loginUser_SuspendedAccount_ThrowsException() {
		testDto.setUsername("testuser");
		testDto.setEmail(null);
		testDto.setPassword("correctPassword");
		testUser.setAccountStatus(AccountStatus.SUSPENDED);
		Mockito.when(usersRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
		LCP_Exception exception = Assertions.assertThrows(LCP_Exception.class, () -> {userService.loginUser(testDto, testIpAddress);});
		Assertions.assertEquals("service.USER_SUSPENDED", exception.getMessage());
	}


	@Test
	void loginUser_UserNotFound_ThrowsException() {
		testDto.setUsername("nonexistentuser");
		testDto.setEmail(null);

		Mockito.when(usersRepo.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

		LCP_Exception exception = Assertions.assertThrows(LCP_Exception.class, () -> {userService.loginUser(testDto, testIpAddress);});
		Assertions.assertEquals("service.USER_NOT_EXIST", exception.getMessage());
	}



	@Test
	void updatePassword_Success() throws LCP_Exception {
		UsersPwdDTO passwordDto = new UsersPwdDTO();
		passwordDto.setUsername("testuser");
		passwordDto.setNewPassword("NewPassword123");
		Mockito.when(usersRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
		Mockito.when(passwordEncoder.encode(passwordDto.getNewPassword())).thenReturn("newHashedPassword");
		Mockito.when(jwtUtil.generateToken(testUser.getUsername(), testUser.getUserId())).thenReturn("new-jwt-token");
		String result = userService.updatePassword(passwordDto);
		Assertions.assertEquals("new-jwt-token", result);
		verify(usersRepo).save(testUser);
		Assertions.assertEquals("newHashedPassword", testUser.getPasswordHash());
	}



	@Test
	void updatePassword_UserNotFound_ThrowsException() {
		UsersPwdDTO passwordDto = new UsersPwdDTO();
		passwordDto.setUsername("nonexistentuser");
		passwordDto.setNewPassword("NewPassword123");
		Mockito.when(usersRepo.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
		LCP_Exception exception = Assertions.assertThrows(LCP_Exception.class, () -> {userService.updatePassword(passwordDto);});
		Assertions.assertEquals("service.USER_NOT_EXIST", exception.getMessage());
	}



	@Test
	void updatePassword_NoIdentifier_ThrowsException() {
		UsersPwdDTO passwordDto = new UsersPwdDTO();
		LCP_Exception exception = Assertions.assertThrows(LCP_Exception.class, () -> {userService.updatePassword(passwordDto);});
		Assertions.assertEquals("service.LOGIN_ERROR", exception.getMessage());
	}

}