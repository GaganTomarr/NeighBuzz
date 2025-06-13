package com.infy.lcp.service;


import java.util.List;

import com.infy.lcp.dto.ForumThreadCommentsDTO;
import com.infy.lcp.entity.ForumThreadComments;
import com.infy.lcp.exception.LCP_Exception;

public interface ForumThreadCommentService {

	Integer postComment(ForumThreadCommentsDTO comment, Integer userId, Integer ForumId) throws LCP_Exception;
	
	List<ForumThreadCommentsDTO> getComments() throws LCP_Exception;
	
	void deleteComment(Integer commentId, Integer userId) throws LCP_Exception;
	 
	public void updateComment(ForumThreadCommentsDTO comment, Integer commentId, Integer userId) throws LCP_Exception;
	 
	 Integer replyComment(ForumThreadCommentsDTO comment, Integer parentCommentId, Integer userId, Integer threadId) throws LCP_Exception;
	 
	 public void deleteReplies(ForumThreadComments parentComment);
}
