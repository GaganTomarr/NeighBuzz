package com.infy.lcp.service;

import java.util.List;

import com.infy.lcp.dto.NewsPostCommentsDTO;
import com.infy.lcp.entity.NewsPostComments;
import com.infy.lcp.exception.LCP_Exception;

public interface NewsPostCommentService {

	Integer postComments(NewsPostCommentsDTO comment, Integer userId, Integer postId) throws LCP_Exception;

	List<NewsPostComments> findByContentId(Integer contentId) throws LCP_Exception;

	void deleteComment(Integer commentId, Integer userId) throws LCP_Exception;
	
	NewsPostComments updateComment(NewsPostCommentsDTO comment, Integer commentId, Integer userId) throws LCP_Exception ;
	
	Integer replyComment(NewsPostCommentsDTO comment, Integer parentCommentId, Integer userId, Integer postId) throws LCP_Exception;
	
	void deleteReplies(NewsPostComments parentComment);
}
