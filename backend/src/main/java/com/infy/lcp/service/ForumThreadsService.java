package com.infy.lcp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.ForumThreadsDTO;
import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.exception.LCP_Exception;

public interface ForumThreadsService {
	public Integer addForumThreads(ForumThreadsDTO forumThreadsDto, MultipartFile image) throws LCP_Exception;
	public void updateForumThreads(Integer threadId, ForumThreadsDTO forumThreadsDto, MultipartFile image) throws LCP_Exception;
	public void deleteForumThreads(Integer threadId) throws LCP_Exception;
	public List<ForumThreadsDTO> getAllForumsThreads() throws LCP_Exception;
	public List<ForumThreadsDTO> getForumThreadsByCategory(ForumCategory forumCategory) throws LCP_Exception;
	public List<ForumThreadsDTO> getForumThreadsByDate(LocalDateTime createdAt) throws LCP_Exception;
	public List<ForumThreadsDTO> getForumThreadsByPopularity() throws LCP_Exception;
	public List<ForumThreadsDTO> searchThreadByKeyword(String keyword) throws LCP_Exception;
	public ForumThreadsDTO getThreadById(Integer threadId) throws LCP_Exception;

	public List<ForumThreadsDTO> getVisibleForumThreadsForUser(Integer userId, boolean isAdmin);
	public void approveForumThread(Integer forumThreadId, Integer userId) throws LCP_Exception;
	public void rejectForumThread(Integer forumThreadId, Integer userId) throws LCP_Exception;
}