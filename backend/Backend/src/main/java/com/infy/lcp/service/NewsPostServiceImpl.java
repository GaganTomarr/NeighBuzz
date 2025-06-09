package com.infy.lcp.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.ModerationLogDTO;
import com.infy.lcp.dto.NewsPostsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ActionType;
import com.infy.lcp.entity.ActivityType;
import com.infy.lcp.entity.ContentType;
import com.infy.lcp.entity.EntityType;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.entity.NewsCategory;
import com.infy.lcp.entity.NewsPosts;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ModerationLogRepo;
import com.infy.lcp.repository.NewsPostRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;

import jakarta.transaction.Transactional;
@Service
@Transactional
public class NewsPostServiceImpl implements NewsPostService{
	ModelMapper mapper=new ModelMapper();

	@Autowired
	NewsPostRepo newsRepo;

	@Autowired
	UsersRepo usersRepository;

	@Autowired
	UserActivityRepo userActivityRepo;

	@Autowired
	UsersRepo usersRepo;

	@Autowired
	ModerationLogRepo moderationLogRepo;

	@Autowired
	FileStorageServiceImpl fileStorageServiceImpl;

	@Override
	public List<NewsPostsDTO> getNewsPosts(String newsCategory) throws LCP_Exception {
		NewsCategory cat = NewsCategory.valueOf(newsCategory);
		List<NewsPosts> lis = newsRepo.findByCategory(cat);
		ModelMapper mapper = new ModelMapper();
		if(lis.isEmpty()) {
			throw new LCP_Exception("service.NEWS_LIST");

		}
		List<NewsPostsDTO> nlist= new  ArrayList<NewsPostsDTO>();

		for(NewsPosts np: lis) {
			NewsPostsDTO npdto = new NewsPostsDTO();
			npdto.setTitle(np.getTitle());
			UsersDTO user = mapper.map(np.getAuthor(), UsersDTO.class);
			npdto.setAuthor(user);
			npdto.setPostId(np.getPostId());
			npdto.setPublishedAt(np.getPublishedAt());
			npdto.setContent(np.getContent());
			if (np.getPublishedAt() != null) {
				npdto.setPublishedAt(np.getPublishedAt());
			} else {
				npdto.setPublishedAt(np.getCreatedAt());
			}
			npdto.setFeaturedImage(np.getFeaturedImage());

			nlist.add(npdto);
		}




		return nlist;
	}

	@Override
	public List<NewsPostsDTO> getAllNewsPosts() throws LCP_Exception {
		Iterable<NewsPosts> customers = newsRepo.findAll();
		ModelMapper mapper = new ModelMapper();
		return mapper.map(customers, new TypeToken<List<NewsPostsDTO>>() {
		}.getType());
	}




	@Override
	public String addNews(NewsPostsDTO news, Integer userId, MultipartFile image) throws LCP_Exception{
		
		Optional<Users> user = usersRepo.findById(userId);
		if(user.isPresent()) {
			ModelMapper mapper = new ModelMapper();
			NewsPosts newsData = mapper.map(news, NewsPosts.class);
			newsData.setAuthor(user.get());
			newsData.setCreatedAt(LocalDateTime.now());
			newsData.setUpdatedAt(LocalDateTime.now());
			newsData.setIsApproved(false);

			String imageUrl = null;
			if(image != null && !image.isEmpty())
			{
				imageUrl = fileStorageServiceImpl.storeFile(image);
				newsData.setFeaturedImage(imageUrl);
			}

			newsRepo.save(newsData);

			UserActivity userActivity = new UserActivity();
			userActivity.setActivityType(ActivityType.POST_CREATED);
			userActivity.setEntityType(EntityType.POST);
			userActivity.setEntityId(newsData.getPostId());
			userActivity.setUser(user.get());
			userActivity.setOccurredAt(LocalDateTime.now());
			userActivityRepo.save(userActivity);

			return "Post Added " + newsData.getPostId();
		}
		else {
			throw new LCP_Exception("service.USER_NOT_EXIST");
		}

	}

	@Override
	public String updateNews(NewsPostsDTO news, Integer userID, MultipartFile image) throws LCP_Exception{
		
		Optional<Users> users = usersRepo.findById(userID);
		Optional<NewsPosts> newsData = newsRepo.findById(news.getPostId());
		if(users.isEmpty()) {
			throw new LCP_Exception("service.USER_NOT_EXIST");
		}
		else if(newsData.isPresent() && newsData.get().getAuthor().equals(users.get())) {
			newsData.get().setContent(news.getContent());
			newsData.get().setFeaturedImage(news.getFeaturedImage());
			newsData.get().setTitle(news.getTitle());
			newsData.get().setNewsCategory(news.getNewsCategory());
			newsData.get().setUpdatedAt(LocalDateTime.now());
			String imageUrl = null;
			if(image != null && !image.isEmpty())
			{
				imageUrl = fileStorageServiceImpl.storeFile(image);
				newsData.get().setFeaturedImage(imageUrl);
			}
			newsRepo.save(newsData.get());
			return "Updated";
		}

		else {
			throw new LCP_Exception("service.NEWS_POST");
		}

	}

	@Override
	public String deleteNews(Integer postId) throws LCP_Exception{
		Optional<NewsPosts> posts = newsRepo.findById(postId);
		posts.get().setAuthor(null);
		newsRepo.deleteById(postId);
		return "Deleted the post " + postId;
	}

	@Override
	public String approveNews(ModerationLogDTO log) throws LCP_Exception {
		
		Optional<Users> admin = usersRepo.findById(log.getAdmin().getUserId());
		if(admin.isEmpty()) {
			throw new LCP_Exception("service.USER_NOT_EXIST");
		}
		else if(admin.isPresent() && admin.get().getIsAdmin()) {
			Optional<NewsPosts> newsData = newsRepo.findById(log.getContentId());
			if(newsData.isPresent()) {
				newsData.get().setApprovalUser(admin.get());
				ModerationLog mod = new ModerationLog();
				mod.setAdmin(admin.get());
				mod.setContentType(ContentType.POST);
				mod.setContentId(newsData.get().getPostId());
				mod.setActionDate(LocalDateTime.now());
				mod.setReason(log.getReason());

				if(log.getActionType().equals(ActionType.APPROVE)) {
					newsData.get().setIsApproved(true);
					newsData.get().setPublishedAt(LocalDateTime.now());
					mod.setActionType(ActionType.APPROVE);
					newsRepo.save(newsData.get());
				}

				else if(log.getActionType().equals(ActionType.REJECT)){
					newsData.get().setIsApproved(false);
					newsData.get().setPublishedAt(null);
					mod.setActionType(ActionType.REJECT);
					newsRepo.save(newsData.get());
				}

				else if(log.getActionType().equals(ActionType.DELETE)) {
					mod.setContentId(null);
					deleteNews(newsData.get().getPostId());
				}

				else if(log.getActionType().equals(ActionType.SUSPEND)) {
					newsData.get().setIsApproved(false);
					mod.setActionType(ActionType.SUSPEND);
					newsRepo.save(newsData.get());
				}

				else {
					newsData.get().setIsApproved(true);
					mod.setActionType(ActionType.RESTORE);
					newsRepo.save(newsData.get());
				}
				moderationLogRepo.save(mod);
				return "Moderation log done";

			}

			else {
				throw new LCP_Exception("service.NEWS_POST");
			}
		}
		else {
			throw new LCP_Exception("service.NEWS_AUTHORITY");
		}
	}

	@Override
	public NewsPostsDTO getPostById(Integer postId) throws LCP_Exception {
		Optional<NewsPosts> newsOpt = newsRepo.findById(postId);

		if (newsOpt.isEmpty()) {
			throw new LCP_Exception("service.NEWS_NOT_FOUND" + postId);
		}

		NewsPosts news = newsOpt.get();
		ModelMapper mapper = new ModelMapper();

		NewsPostsDTO dto = mapper.map(news, NewsPostsDTO.class);
		dto.setAuthor(mapper.map(news.getAuthor(), UsersDTO.class));

		if (news.getPublishedAt() != null) {
			dto.setPublishedAt(news.getPublishedAt());
		} else {
			dto.setPublishedAt(news.getCreatedAt());
		}

		return dto;
	}

	@Override
	public List<NewsPostsDTO> getVisibleNewsPostsForUser(Integer userId, boolean isAdmin) {
		List<NewsPosts> newsPostsList = newsRepo.findAll();
		List<NewsPostsDTO> visiblePosts = new ArrayList<>();

		for (NewsPosts newsPost : newsPostsList) {
			if (Boolean.TRUE.equals(newsPost.getIsApproved()) || 
					(newsPost.getAuthor().getUserId().equals(userId)) || 
					isAdmin) { 
				NewsPostsDTO dto = mapper.map(newsPost, NewsPostsDTO.class);
				dto.setAuthor(mapper.map(newsPost.getAuthor(), UsersDTO.class));
				visiblePosts.add(dto);
			}
		}
		return visiblePosts;
	}

	@Override
	public void approveNewsPost(Integer postId, Integer userId) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(userId);
		Users user=userOpt.orElseThrow(()->new LCP_Exception("service.USER_NOT_EXIST"));

		ModerationLog modLog=new ModerationLog();
		modLog.setActionDate(LocalDateTime.now());
		modLog.setActionType(ActionType.APPROVE);
		modLog.setAdmin(user);
		modLog.setContentId(postId);
		modLog.setContentType(ContentType.POST);
		modLog.setReason("This post is approved.");

		moderationLogRepo.save(modLog);
	}

	@Override
	public void rejectNewsPost(Integer postId, Integer userId) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(userId);
		Users user=userOpt.orElseThrow(()->new LCP_Exception("service.USER_NOT_EXIST"));

		ModerationLog modLog=new ModerationLog();
		modLog.setActionDate(LocalDateTime.now());
		modLog.setActionType(ActionType.REJECT);
		modLog.setAdmin(user);
		modLog.setContentId(postId);
		modLog.setContentType(ContentType.POST);
		modLog.setReason("This post is rejected.");

		moderationLogRepo.save(modLog);
	}
}