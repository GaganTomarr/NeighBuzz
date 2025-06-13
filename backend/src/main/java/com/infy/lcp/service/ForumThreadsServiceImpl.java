package com.infy.lcp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.ForumDTO;
import com.infy.lcp.dto.ForumThreadsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ActionType;
import com.infy.lcp.entity.ActivityType;
import com.infy.lcp.entity.ContentType;
import com.infy.lcp.entity.EntityType;
import com.infy.lcp.entity.Forum;
import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.entity.ForumThreads;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ForumThreadsRepo;
import com.infy.lcp.repository.ModerationLogRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;

import jakarta.transaction.Transactional;

@Service(value="forumThreadsService")
@Transactional
public class ForumThreadsServiceImpl implements ForumThreadsService {

	ModelMapper mapper=new ModelMapper();

	@Autowired
	ForumThreadsRepo forumThreadsRepository;

	@Autowired
	UsersRepo usersRepository;

	@Autowired
	private FileStorageServiceImpl fileStorageServiceImpl;

	@Autowired
	UserActivityRepo userActivityRepo;

	@Autowired
	ModerationLogRepo moderationLogRepo;

	@Override
	public Integer addForumThreads(ForumThreadsDTO forumThreadsDto, MultipartFile image) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(forumThreadsDto.getUsers().getUserId());
		Users users=userOpt.orElseThrow(()-> new LCP_Exception("service.USER_NOT_EXIST"));

		ForumThreads forumThreads=mapper.map(forumThreadsDto, ForumThreads.class);
		forumThreads.setForum(mapper.map(forumThreadsDto.getForum(), Forum.class));
		forumThreads.setCreator(users);
		forumThreads.setCreatedAt(LocalDateTime.now());
		forumThreads.setUpdatedAt(LocalDateTime.now());
		forumThreads.setIsLocked(true);

		String imageUrl=null;
		if(image!=null && !image.isEmpty()) {
			imageUrl=fileStorageServiceImpl.storeFile(image);
			forumThreads.setFeaturedImage(imageUrl);
		}

		forumThreads=forumThreadsRepository.save(forumThreads);

		UserActivity userActivity = new UserActivity();
		userActivity.setActivityType(ActivityType.FORUM_POST);
		userActivity.setEntityType(EntityType.THREAD);
		userActivity.setEntityId(forumThreads.getThreadId());
		userActivity.setUser(userOpt.get());
		userActivity.setOccurredAt(LocalDateTime.now());
		userActivityRepo.save(userActivity);

		return forumThreads.getThreadId();
	}

	@Override
	public void updateForumThreads(Integer threadId, ForumThreadsDTO forumThreadsDto, MultipartFile image) throws LCP_Exception {
		Optional<ForumThreads> forumThreadOpt = forumThreadsRepository.findById(threadId);
		ForumThreads forumThread = forumThreadOpt.orElseThrow(() -> new LCP_Exception("service.THREAD"));

		
		if (forumThread.getCreator().getUserId() != forumThreadsDto.getUsers().getUserId()) {
			throw new LCP_Exception("service.THREAD_OWNER");
		}

		
		forumThread.setTitle(forumThreadsDto.getTitle());
		forumThread.setDescription(forumThreadsDto.getDescription());
		forumThread.setUpdatedAt(LocalDateTime.now());


		String imageUrl=null;
		if(image!=null && !image.isEmpty()) {
			imageUrl=fileStorageServiceImpl.storeFile(image);
			forumThread.setFeaturedImage(imageUrl);
		}

		
		forumThreadsRepository.save(forumThread);
	}


	@Override
	public void deleteForumThreads(Integer threadId) throws LCP_Exception {
		Optional<ForumThreads> forumThreadOpt=forumThreadsRepository.findById(threadId);
		ForumThreads forumThread=forumThreadOpt.orElseThrow(()->new LCP_Exception("service.THREAD"));

		if(forumThread.getCreator()!=null || forumThread.getForum()!=null) {
			forumThread.setCreator(null);
			forumThread.setForum(null);
		}

		forumThreadsRepository.deleteById(threadId);
	}

	@Override
	public List<ForumThreadsDTO> getAllForumsThreads() throws LCP_Exception {
		List<ForumThreads> forumThreadsInList=forumThreadsRepository.findAll();
		if(forumThreadsInList.isEmpty()) {
			throw new LCP_Exception("service.THREAD");
		}

		List<ForumThreadsDTO> forumThreadsOutList=new ArrayList<>();
		forumThreadsInList.stream().forEach(obj->{
			ForumThreadsDTO forumThreadsDTO=mapper.map(obj, ForumThreadsDTO.class);
			forumThreadsDTO.setForum(mapper.map(obj.getForum(), ForumDTO.class));
			forumThreadsDTO.setUsers(mapper.map(obj.getCreator(), UsersDTO.class));
			forumThreadsOutList.add(forumThreadsDTO);
		});

		return forumThreadsOutList;
	}

	@Override
	public List<ForumThreadsDTO> getForumThreadsByCategory(ForumCategory forumCategory) throws LCP_Exception{
		List<ForumThreads> forumThreadsList=forumThreadsRepository.findByCategory(forumCategory);
		if(forumThreadsList.isEmpty()) {
			throw new LCP_Exception("service.THREAD");
		}
		List<ForumThreadsDTO> forumThreadsDTOList= new ArrayList<>();
		for(ForumThreads forumThreads: forumThreadsList) {
			ForumThreadsDTO forumT1=mapper.map(forumThreads,ForumThreadsDTO.class);
			forumThreadsDTOList.add(forumT1);
		}
		return forumThreadsDTOList;
	}

	@Override
	public List<ForumThreadsDTO> getForumThreadsByDate(LocalDateTime createdAt) throws LCP_Exception{
		List<ForumThreads> forumThreadsList=forumThreadsRepository.findByDate(createdAt);
		if(forumThreadsList.isEmpty()) {
			throw new LCP_Exception("service.THREAD");
		}
		List<ForumThreadsDTO> forumThreadsDTOList= new ArrayList<>();
		for(ForumThreads forumThreads: forumThreadsList) {
			ForumThreadsDTO forumT1=mapper.map(forumThreads,ForumThreadsDTO.class);
			forumThreadsDTOList.add(forumT1);
		}
		return forumThreadsDTOList;
	}

	@Override
	public List<ForumThreadsDTO> getForumThreadsByPopularity() throws LCP_Exception{
		List<ForumThreads> forumThreadsList=forumThreadsRepository.findAll();
		if(forumThreadsList.isEmpty()) {
			throw new LCP_Exception("service.THREAD");
		}
		List<ForumThreadsDTO> forumThreadsDTOList= new ArrayList<>();
		for(ForumThreads forumThreads: forumThreadsList) {
			ForumThreadsDTO forumT1=mapper.map(forumThreads,ForumThreadsDTO.class);
			forumThreadsDTOList.add(forumT1);
		}
		return forumThreadsDTOList.stream().sorted((p1,p2)->p2.getViewCount().compareTo(p1.getViewCount())).toList();
	}

	@Override
	public List<ForumThreadsDTO> searchThreadByKeyword(String keyword) throws LCP_Exception
	{
		List<ForumThreads> list = forumThreadsRepository.findByTitleContainingIgnoreCase(keyword);
		if(list.isEmpty()) {
			throw new LCP_Exception("service.THREAD");
		}
		List<ForumThreadsDTO> dtoList = new ArrayList<ForumThreadsDTO>();
		ModelMapper mapper = new ModelMapper();

		for(ForumThreads thread : list)
		{
			ForumThreadsDTO th = mapper.map(thread, ForumThreadsDTO.class);
			UsersDTO user = mapper.map(thread.getCreator(), UsersDTO.class);
			th.setUsers(user);
			ForumDTO forum = mapper.map(thread.getForum(), ForumDTO.class);
			th.setForum(forum);
			dtoList.add(th);
		}
		return dtoList;
	}

	@Override
	public ForumThreadsDTO getThreadById(Integer threadId) throws LCP_Exception
	{
		Optional<ForumThreads> optThread = forumThreadsRepository.findById(threadId);

		if(optThread.isEmpty()) throw new LCP_Exception("service.THREAD");

		ModelMapper mapper = new ModelMapper();
		ForumThreadsDTO threadDto = mapper.map(optThread.get(), ForumThreadsDTO.class);
		UsersDTO userDto = mapper.map(optThread.get().getCreator(), UsersDTO.class);
		threadDto.setUsers(userDto);
		ForumDTO forumDto = mapper.map(optThread.get().getForum(), ForumDTO.class);
		threadDto.setForum(forumDto);

		return threadDto;
	}

	@Override
	public List<ForumThreadsDTO> getVisibleForumThreadsForUser(Integer userId, boolean isAdmin) {
		List<ForumThreads> forumThreadList=forumThreadsRepository.findAll();
		List<ForumThreadsDTO> visibleForumThreads=new ArrayList<>();

		for(ForumThreads forumThread: forumThreadList) {
			if (Boolean.FALSE.equals(forumThread.getIsLocked()) || // Visible to everyone if published
					(forumThread.getCreator().getUserId().equals(userId)) || // Visible to creator
					isAdmin) { // Visible to admin
				ForumThreadsDTO dto = mapper.map(forumThread, ForumThreadsDTO.class);
				dto.setUsers(mapper.map(forumThread.getCreator(), UsersDTO.class));
				visibleForumThreads.add(dto);
			}
		}
		return visibleForumThreads;
	}

	@Override
	public void approveForumThread(Integer forumThreadId, Integer userId) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(userId);
		Users user=userOpt.orElseThrow(()->new LCP_Exception("service.USER_NOT_EXIST"));

		ModerationLog modLog=new ModerationLog();
		modLog.setActionDate(LocalDateTime.now());
		modLog.setActionType(ActionType.APPROVE);
		modLog.setAdmin(user);
		modLog.setContentId(forumThreadId);
		modLog.setContentType(ContentType.THREAD);
		modLog.setReason("This thread is approved.");

		moderationLogRepo.save(modLog);
	}

	@Override
	public void rejectForumThread(Integer forumThreadId, Integer userId) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(userId);
		Users user=userOpt.orElseThrow(()->new LCP_Exception("service.USER_NOT_EXIST"));

		ModerationLog modLog=new ModerationLog();
		modLog.setActionDate(LocalDateTime.now());
		modLog.setActionType(ActionType.REJECT);
		modLog.setAdmin(user);
		modLog.setContentId(forumThreadId);
		modLog.setContentType(ContentType.THREAD);
		modLog.setReason("This thread is rejected.");

		moderationLogRepo.save(modLog);
	}
}