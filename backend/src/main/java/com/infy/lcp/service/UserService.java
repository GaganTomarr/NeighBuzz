package com.infy.lcp.service;

import java.util.List;

import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.dto.UsersPwdDTO;
import com.infy.lcp.exception.LCP_Exception;

public interface UserService {
	public void registerNewUserAccount(String email,String username,String password, Boolean isAdmin) throws LCP_Exception;
	public String loginUser(UsersPwdDTO dto, String ipAdress) throws LCP_Exception;
	public String updatePassword(UsersPwdDTO dto) throws LCP_Exception;
	public void deleteHistory(Integer userId) throws LCP_Exception;
	public void deleteUserData(Integer userId) throws LCP_Exception;
	public List<UsersDTO> getAllUsers() throws LCP_Exception;
}
