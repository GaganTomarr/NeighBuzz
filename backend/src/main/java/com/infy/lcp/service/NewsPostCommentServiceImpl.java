package com.infy.lcp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.lcp.dto.NewsPostCommentsDTO;
import com.infy.lcp.entity.ActionType;
import com.infy.lcp.entity.ActivityType;
import com.infy.lcp.entity.ContentType;
import com.infy.lcp.entity.EntityType;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.entity.NewsPostComments;
import com.infy.lcp.entity.NewsPosts;
import com.infy.lcp.entity.Notifications;
import com.infy.lcp.entity.NotificationsType;
import com.infy.lcp.entity.RelatedEntityType;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ModerationLogRepo;
import com.infy.lcp.repository.NewsPostCommentsRepo;
import com.infy.lcp.repository.NewsPostRepo;
import com.infy.lcp.repository.NotificationsRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;

@Service
public class NewsPostCommentServiceImpl implements NewsPostCommentService{

	@Autowired
	private NewsPostCommentsRepo commentRepo;

	@Autowired
	private NewsPostRepo newsPost;

	@Autowired
	UserActivityRepo userActivityRepo;

	@Autowired
	private UsersRepo userRepo;

	@Autowired
	private NewsPostRepo newsRepo;

	@Autowired
	private NotificationsRepo notificationRepo;

	@Autowired
	private NotificationsService notificationsService;

	@Autowired
	ModerationLogRepo moderationLogRepo;

	@Override
	public Integer postComments(NewsPostCommentsDTO comment, Integer userId, Integer postId) throws LCP_Exception
	{
		Optional<Users> userOpt = userRepo.findById(userId);
		if(userOpt.isEmpty()) throw new LCP_Exception("service.USER_NOT_EXIST");

		Optional<NewsPosts> contentOpt = newsRepo.findById(postId);
		if(contentOpt.isEmpty()) throw new LCP_Exception("service.COMMENT_POST");

		NewsPostComments comm = new NewsPostComments();
		
		comm.setCommentText(comment.getCommentText());
		comm.setNewsPosts(contentOpt.get());
		comm.setUser(userOpt.get());
		comm.setCreatedAt(LocalDateTime.now());
		comm.setUpdatedAt(LocalDateTime.now());
		commentRepo.save(comm);

		UserActivity userActivity = new UserActivity();
		userActivity.setActivityType(ActivityType.COMMENT);
		userActivity.setEntityType(EntityType.COMMENT);
		userActivity.setEntityId(comm.getCommentId());
		userActivity.setUser(userOpt.get());
		userActivity.setOccurredAt(LocalDateTime.now());
		userActivityRepo.save(userActivity);

		return comm.getCommentId();
	}

	@Override
	public List<NewsPostComments> findByContentId(Integer contentId) throws LCP_Exception
	{
		Optional<NewsPosts> newsPostOpt = newsPost.findById(contentId);
		NewsPosts newsPost=newsPostOpt.orElseThrow(()->new LCP_Exception("service.COMMENT_POST"));

		List<NewsPostComments> postCommentsList = new ArrayList<NewsPostComments>();
		postCommentsList = commentRepo.findByContentId(contentId);
		return postCommentsList;
	}

	@Override
	public void deleteReplies(NewsPostComments parentComment) {
		List<NewsPostComments> replies = commentRepo.findByParentComment(parentComment);
		for (NewsPostComments reply : replies) {
			
			deleteReplies(reply);

			
			reply.setNewsPosts(null);
			reply.setUser(null);
			reply.setParentComment(null);

			commentRepo.delete(reply);
		}
	}


	@Override
	public void deleteComment(Integer commentId, Integer userId) throws LCP_Exception {
		Optional<Users> userOpt=userRepo.findById(userId);
		Users users=userOpt.orElseThrow(()-> new LCP_Exception("service.USER_NOT_EXIST"));

		
		Optional<NewsPostComments> newsPostOpt = commentRepo.findById(commentId);
		NewsPostComments newsPostComment = newsPostOpt.orElseThrow(() -> new LCP_Exception("service.COMMENT_NOT"));

		
		Integer commentOwnerId = newsPostComment.getUser().getUserId();
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

		
		deleteReplies(newsPostComment);

		
		newsPostComment.setNewsPosts(null);
		newsPostComment.setUser(null);
		newsPostComment.setParentComment(null);

		
		commentRepo.delete(newsPostComment);
	}


	@Override
	public NewsPostComments updateComment(NewsPostCommentsDTO comment, Integer commentId, Integer userId) throws LCP_Exception {
		Optional<NewsPostComments> commOpt = commentRepo.findById(commentId);
		if (commOpt.isEmpty()) {
			throw new LCP_Exception("service.COMMENT_NOT_FOUND");
		}

		NewsPostComments existingComment = commOpt.get();
		Integer commentOwnerId = existingComment.getUser().getUserId();

		
		if (!commentOwnerId.equals(userId)) {
			throw new LCP_Exception("service.COMMENT_OWNER_EDIT");
		}

		
		existingComment.setCommentText(comment.getCommentText());
		existingComment.setUpdatedAt(LocalDateTime.now());

		
		return commentRepo.save(existingComment);
	}


	@Override
	public Integer replyComment(NewsPostCommentsDTO comment, Integer parentCommentId, Integer userId, Integer postId) throws LCP_Exception
	{
		Optional<NewsPostComments> parentOpt = commentRepo.findById(parentCommentId);
		if(parentOpt.isEmpty()) throw new LCP_Exception("service.PARENT_COMMENT");

		Optional<Users> userOpt = userRepo.findById(userId);
		if(userOpt.isEmpty()) throw new LCP_Exception("service.USER_NOT_EXIST");

		Optional<NewsPosts> contentOpt = newsRepo.findById(postId);
		if(contentOpt.isEmpty()) throw new LCP_Exception("service.COMMENT_POST");

		if(parentOpt.get().getNewsPosts().getPostId() != postId)
			throw new LCP_Exception("service.PARENT_ID");


		ModelMapper mapper = new ModelMapper();
		NewsPostComments reply = mapper.map(comment, NewsPostComments.class);

		
		reply.setCreatedAt(LocalDateTime.now());
		reply.setUpdatedAt(LocalDateTime.now());
		reply.setParentComment(parentOpt.get());
		reply.setNewsPosts(contentOpt.get());
		reply.setUser(userOpt.get());
		commentRepo.save(reply);

		UserActivity userActivity = new UserActivity();
		userActivity.setActivityType(ActivityType.COMMENT);
		userActivity.setEntityType(EntityType.COMMENT);
		userActivity.setEntityId(reply.getCommentId());
		userActivity.setUser(userOpt.get());
		userActivity.setOccurredAt(LocalDateTime.now());
		userActivityRepo.save(userActivity);

		Users ParentCommentUser = parentOpt.get().getUser();
		Notifications notification = new Notifications();
		notification.setCreatedAt(LocalDateTime.now());
		notification.setNotificationsType(NotificationsType.COMMENT_REPLY);
		notification.setContent(userOpt.get().getUsername() + " replied to your comment " + comment.getCommentText());
		notification.setUser(ParentCommentUser);
		notification.setRelatedEntityType(RelatedEntityType.NEWS_POST_COMMENT_REPLY);
		notification.setRelatedEntityId(parentCommentId);
		notification.setPostId(postId);
		notificationRepo.save(notification);

		return reply.getCommentId();
	}


}

