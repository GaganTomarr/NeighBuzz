package com.infy.lcp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.lcp.dto.ForumThreadCommentsDTO;
import com.infy.lcp.dto.ForumThreadsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ActionType;
import com.infy.lcp.entity.ActivityType;
import com.infy.lcp.entity.ContentType;
import com.infy.lcp.entity.EntityType;
import com.infy.lcp.entity.ForumThreadComments;
import com.infy.lcp.entity.ForumThreads;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.entity.Notifications;
import com.infy.lcp.entity.NotificationsType;
import com.infy.lcp.entity.RelatedEntityType;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ForumThreadCommentRepo;
import com.infy.lcp.repository.ForumThreadsRepo;
import com.infy.lcp.repository.ModerationLogRepo;
import com.infy.lcp.repository.NotificationsRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;

@Service
public class ForumThreadCommentServiceImpl implements ForumThreadCommentService{

	@Autowired
	ForumThreadCommentRepo commentRepo;

	@Autowired
	UsersRepo userRepo;

	@Autowired
	ForumThreadsRepo threadRepo;

	@Autowired
	UserActivityRepo userActivityRepo;

	@Autowired
	private NotificationsRepo notificationRepo;

	@Autowired
	ModerationLogRepo moderationLogRepo;

	@Override
	public Integer postComment(ForumThreadCommentsDTO comment, Integer userId, Integer threadId) throws LCP_Exception
	{
		Optional<Users> userOpt = userRepo.findById(userId);
		if(userOpt.isEmpty()) throw new LCP_Exception("service.USER_NOT_EXIST");

		Optional<ForumThreads> contentOpt = threadRepo.findById(threadId);
		if(contentOpt.isEmpty()) throw new LCP_Exception("service.COMMENT_POST");

		ForumThreadComments comm = new ForumThreadComments();
		comm.setCommentText(comment.getCommentText());
		comm.setCreatedAt(LocalDateTime.now());
		comm.setUpdatedAt(LocalDateTime.now());
		comm.setUser(userOpt.get());
		comm.setThread(contentOpt.get());
		commentRepo.save(comm);

		UserActivity userActivity = new UserActivity();
		userActivity.setActivityType(ActivityType.COMMENT);
		userActivity.setEntityType(EntityType.COMMENT);
		userActivity.setEntityId(comm.getFtCommentId());
		userActivity.setUser(userOpt.get());
		userActivity.setOccurredAt(LocalDateTime.now());
		userActivityRepo.save(userActivity);

		return comm.getFtCommentId();
	}

	@Override
	public List<ForumThreadCommentsDTO> getComments() throws LCP_Exception
	{
		List<ForumThreadComments> list = commentRepo.findAll();
		if(list.isEmpty()) throw new LCP_Exception("service.COMMENT_NOT_FOUND");

		List<ForumThreadCommentsDTO> dtoList = new ArrayList<ForumThreadCommentsDTO>();
		ModelMapper mapper = new ModelMapper();

		for(ForumThreadComments comment : list)
		{
			ForumThreadCommentsDTO comm = mapper.map(comment, ForumThreadCommentsDTO.class);
			UsersDTO user = mapper.map(comment.getUser(), UsersDTO.class);
			comm.setUsers(user);
			ForumThreadsDTO forum = mapper.map(comment.getThread(), ForumThreadsDTO.class);
			comm.setThread(forum);
			dtoList.add(comm);
		}
		return dtoList;
	}

	@Override
	public void deleteReplies(ForumThreadComments parentComment) {
		List<ForumThreadComments> replies = commentRepo.findByParentComment(parentComment);
		for (ForumThreadComments reply : replies) {
			
			deleteReplies(reply);

			
			reply.setThread(null);
			reply.setUser(null);
			reply.setParentComment(null);

			commentRepo.delete(reply);
		}
	}

	@Override
	public void deleteComment(Integer commentId, Integer userId) throws LCP_Exception
	{
		Optional<Users> userOpt=userRepo.findById(userId);
		Users users=userOpt.orElseThrow(()-> new LCP_Exception("service.USER_NOT_EXIST"));

		Optional<ForumThreadComments> comment = commentRepo.findById(commentId);
		if(comment.isEmpty()) throw new LCP_Exception("service.COMMENT_NOT_FOUND");

		
		Integer commentOwnerId = comment.get().getUser().getUserId();
		if (!commentOwnerId.equals(userId) && users.getIsAdmin()!=true) {
			throw new LCP_Exception("service.COMMENT_OWNER_DELETE");
		}

		if(users.getIsAdmin()==true) {
			ModerationLog modLog=new ModerationLog();
			modLog.setActionDate(LocalDateTime.now());
			modLog.setActionType(ActionType.DELETE);
			modLog.setAdmin(users);
			modLog.setContentId(commentId);
			modLog.setContentType(ContentType.COMMENT);
			modLog.setReason("This comment is deleted.");

			moderationLogRepo.save(modLog);
		}

		
		deleteReplies(comment.get());

		comment.get().setUser(null);
		comment.get().setThread(null);
		comment.get().setParentComment(null);


		commentRepo.delete(comment.get());
	}

	@Override
	public void updateComment(ForumThreadCommentsDTO comment, Integer commentId, Integer userId) throws LCP_Exception
	{
		Optional<ForumThreadComments> commOpt = commentRepo.findById(commentId);
		if(commOpt.isEmpty()) throw new LCP_Exception("service.COMMENT_NOT_FOUND");

		Integer commentOwnerId = commOpt.get().getUser().getUserId();
		System.out.println("owner id" + commentId);
		

		if(!commentOwnerId.equals(userId))
			throw new LCP_Exception("service.COMMENT_OWNER_EDIT");

		commOpt.get().setCommentText(comment.getCommentText());
		commOpt.get().setUpdatedAt(LocalDateTime.now());
		commentRepo.save(commOpt.get());
	}

	@Override
	public Integer replyComment(ForumThreadCommentsDTO comment, Integer parentCommentId, Integer userId, Integer threadId) throws LCP_Exception
	{
		Optional<ForumThreadComments> parentOpt = commentRepo.findById(parentCommentId);
		if(parentOpt.isEmpty()) throw new LCP_Exception("service.PARENT_COMMENT");

		Optional<Users> userOpt = userRepo.findById(userId);
		if(userOpt.isEmpty()) throw new LCP_Exception("service.USER_NOT_EXIST");

		Optional<ForumThreads> contentOpt = threadRepo.findById(threadId);
		if(contentOpt.isEmpty()) throw new LCP_Exception("service.COMMENT_POST");

		if(parentOpt.get().getThread().getThreadId() != threadId)
			throw new LCP_Exception("service.PARENT_ID");


		ModelMapper mapper = new ModelMapper();
		ForumThreadComments reply = mapper.map(comment, ForumThreadComments.class);
		reply.setCreatedAt(LocalDateTime.now());
		reply.setUpdatedAt(LocalDateTime.now());
		reply.setParentComment(parentOpt.get());
		reply.setThread(contentOpt.get());
		reply.setUser(userOpt.get());
		commentRepo.save(reply);

		UserActivity userActivity = new UserActivity();
		userActivity.setActivityType(ActivityType.COMMENT);
		userActivity.setEntityType(EntityType.COMMENT);
		userActivity.setEntityId(reply.getFtCommentId());
		userActivity.setUser(userOpt.get());
		userActivity.setOccurredAt(LocalDateTime.now());
		userActivityRepo.save(userActivity);

		Users ParentCommentUser = parentOpt.get().getUser();
		Notifications notification = new Notifications();
		notification.setCreatedAt(LocalDateTime.now());
		notification.setNotificationsType(NotificationsType.FORUM_THREAD_DISCUSSION);
		notification.setContent(userOpt.get().getUsername() + " replied to thread chat " + comment.getCommentText());
		notification.setUser(ParentCommentUser);
		notification.setRelatedEntityType(RelatedEntityType.FORUM_THREAD_DISCUSSION);
		notification.setRelatedEntityId(parentCommentId);
		notification.setPostId(threadId);
		notificationRepo.save(notification);

		return reply.getFtCommentId();
	}

}
