package com.infy.lcp.service;

import java.util.List;

import com.infy.lcp.dto.ForumDTO;
import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.exception.LCP_Exception;

public interface ForumService {
	public Integer addForum(ForumDTO forumDto) throws LCP_Exception;
	public void updateForum(Integer forumId, ForumDTO forumDto) throws LCP_Exception;
	public void deleteForum(Integer forumId) throws LCP_Exception;
	public List<ForumDTO> getAllForums() throws LCP_Exception;
	public List<ForumDTO> getForumByCategory(ForumCategory forumCategory) throws LCP_Exception;
}