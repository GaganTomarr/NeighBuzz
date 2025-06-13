package com.infy.lcp.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.ModerationLogDTO;
import com.infy.lcp.dto.NewsPostsDTO;
import com.infy.lcp.exception.LCP_Exception;
public interface NewsPostService {

	public List<NewsPostsDTO> getNewsPosts(String newsCategory) throws LCP_Exception;
	List<NewsPostsDTO> getAllNewsPosts() throws LCP_Exception;

	public String addNews(NewsPostsDTO news, Integer userId, MultipartFile image) throws LCP_Exception;
	public String updateNews(NewsPostsDTO news, Integer userID, MultipartFile image) throws LCP_Exception;
	public String deleteNews(Integer postId) throws LCP_Exception;
	public String approveNews(ModerationLogDTO log) throws LCP_Exception;
	public NewsPostsDTO getPostById(Integer postId) throws LCP_Exception ;

	public List<NewsPostsDTO> getVisibleNewsPostsForUser(Integer userId, boolean isAdmin);
	public void approveNewsPost(Integer postId, Integer userId) throws LCP_Exception;
	public void rejectNewsPost(Integer postId, Integer userId) throws LCP_Exception;
}