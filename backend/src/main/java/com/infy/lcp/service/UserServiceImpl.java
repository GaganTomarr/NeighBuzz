package com.infy.lcp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.dto.UsersPwdDTO;
import com.infy.lcp.entity.AccountStatus;
import com.infy.lcp.entity.LoginHistory;
import com.infy.lcp.entity.LoginStatus;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.LoginHistoryRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{
	private final UsersRepo usersRepo;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil;

	private final LoginHistoryRepo loginHistoryRepo;
	@Autowired
	public UserServiceImpl(UsersRepo usersRepo,PasswordEncoder passwordEncoder, JwtUtil jwtUtil, LoginHistoryRepo loginHistoryRepo) {
		this.usersRepo=usersRepo;
		this.passwordEncoder=passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.loginHistoryRepo = loginHistoryRepo;
	}

	public void registerNewUserAccount(String email,String username,String password) throws LCP_Exception {
		if(usersRepo.existsByEmail(email)) {
			throw new LCP_Exception("service.USER_EMAIL");
		}
		if(usersRepo.existsByUsername(username)) {
			throw new LCP_Exception("service.USER_USERNAME");
		}
		if(password==null|| password.length()<8||!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")) {
			throw new LCP_Exception("service.USER_PASSWORD");
		}
		Users newUser= new Users();
		newUser.setEmail(email);
		newUser.setUsername(username);
		newUser.setPasswordHash(passwordEncoder.encode(password));
		newUser.setAccountStatus(AccountStatus.ACTIVE);
		newUser.setRegistrationDate(LocalDateTime.now());
		newUser.setLastLogin(LocalDateTime.now());
		newUser.setIsAdmin(false);
		usersRepo.save(newUser);
	}

	@Override
	public void registerNewUserAccount(String email,String username,String password, Boolean isAdmin) throws LCP_Exception {
		if(usersRepo.existsByEmail(email)) {
			throw new LCP_Exception("service.USER_EMAIL");
		}
		if(usersRepo.existsByUsername(username)) {
			throw new LCP_Exception("service.USER_USERNAME");
		}
		if(password==null|| password.length()<8||!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")) {
			throw new LCP_Exception("service.USER_PASSWORD");
		}
		Users newUser= new Users();
		newUser.setEmail(email);
		newUser.setUsername(username);
		newUser.setPasswordHash(passwordEncoder.encode(password));
		newUser.setAccountStatus(AccountStatus.ACTIVE);
		newUser.setRegistrationDate(LocalDateTime.now());
		newUser.setLastLogin(LocalDateTime.now());
		newUser.setIsAdmin(isAdmin);
		usersRepo.save(newUser);
	}

	@Override
	public String loginUser(UsersPwdDTO dto, String ipAdress) throws LCP_Exception{
		LoginHistory history = new LoginHistory();
		Optional<Users> user;
		if(dto.getUsername() != null && !dto.getUsername().isEmpty()) {
			user = usersRepo.findByUsername(dto.getUsername());
		}
		else if(dto.getEmail() != null && !dto.getEmail().isEmpty()) {
			user = usersRepo.findByEmail(dto.getEmail());
		}
		else {
			throw new LCP_Exception("service.LOGIN_ERROR");
		}
		Users users = user.orElseThrow(() -> new LCP_Exception("service.USER_NOT_EXIST"));

		history.setIpAddress(ipAdress);
		history.setUser(users);
		history.setLoginTimestamp(LocalDateTime.now());

		if(users.getAccountStatus().equals(AccountStatus.SUSPENDED)) {
			history.setLoginStatus(LoginStatus.LOCKOUT);
			loginHistoryRepo.save(history);
			throw new LCP_Exception("service.USER_SUSPENDED");
		}
		if(!passwordEncoder.matches(dto.getPassword(), users.getPasswordHash())) {
			history.setLoginStatus(LoginStatus.FAILED);
			loginHistoryRepo.save(history);
			throw new LCP_Exception("service.CREDENTIALS_WRONG");
		}

		history.setLoginStatus(LoginStatus.SUCCESS);
		loginHistoryRepo.save(history);
		return jwtUtil.generateToken(users.getUsername(), users.getUserId());
	}

	@Override
	public String updatePassword(UsersPwdDTO dto) throws LCP_Exception{
		Optional<Users> user;
		if(dto.getUsername() != null && !dto.getUsername().isEmpty()) {
			user = usersRepo.findByUsername(dto.getUsername());
		}
		else if(dto.getEmail() != null && !dto.getEmail().isEmpty()) {
			user = usersRepo.findByEmail(dto.getEmail());
		}
		else {
			throw new LCP_Exception("service.LOGIN_ERROR");
		}
		Users users = user.orElseThrow(() -> new LCP_Exception("service.USER_NOT_EXIST"));
		users.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
		usersRepo.save(users);
		return jwtUtil.generateToken(users.getUsername(), users.getUserId());
	}

	@Override
	public void deleteHistory(Integer userId) throws LCP_Exception{
		List<LoginHistory> history = loginHistoryRepo.getByUser(userId);
		loginHistoryRepo.deleteAllInBatch(history);
	}

	@Override
	@Transactional
	public void deleteUserData(Integer userId) throws LCP_Exception {
		Users user = usersRepo.findById(userId)
				.orElseThrow(() -> new LCP_Exception("service.USER_NOT_EXIST"));
		

		if (user.getAccountStatus() == AccountStatus.DELETED) {
			throw new LCP_Exception("service.USER_DELETED");
		}

		user.setAccountStatus(AccountStatus.DELETED);

		
		user.setEmail("deleted_" + user.getUserId() + "@anonymized.local");
		user.setPasswordHash("DELETED_USER"); 
		user.setUsername("DeletedUser_" + (user.getUserId() * 17));

		usersRepo.save(user);
	}

	@Override
	@Transactional
	public List<UsersDTO> getAllUsers() throws LCP_Exception {
		ModelMapper mapper=new ModelMapper();

		List<Users> usersList=usersRepo.findAll();
		if(usersList.isEmpty()) {
			throw new LCP_Exception("service.USER_NOT_EXIST");
		}

		List<UsersDTO> usersDTOList= new ArrayList<>();
		usersList.stream().forEach(obj->{
			UsersDTO userDto=mapper.map(obj, UsersDTO.class);
			usersDTOList.add(userDto);
		});

		return usersDTOList;
	}
}