package com.infy.lcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.ModerationLogDTO;
import com.infy.lcp.dto.NewsPostsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ActionType;
import com.infy.lcp.entity.Location;
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
import com.infy.lcp.service.FileStorageServiceImpl;
import com.infy.lcp.service.NewsPostServiceImpl;

@SpringBootTest
public class NewsPostTests {

	@Mock
	private NewsPostRepo newsRepo;

	@Mock
	private UserActivityRepo userActivityRepo;

	@Mock
	private UsersRepo usersRepo;

	@Mock
	private ModerationLogRepo moderationLogRepo;
	
	@Mock
	FileStorageServiceImpl fileStorageServiceImpl;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private NewsPostServiceImpl newsService;

	@Test
	void getNewsPosts_existingCategory_returnsDTOList() throws LCP_Exception {

		String categoryStr = "CRIME";

		NewsCategory category = NewsCategory.valueOf(categoryStr);

		List<NewsPosts> newsList = new ArrayList<>();

		NewsPosts newsPost1 = new NewsPosts();

		newsPost1.setTitle("Crime News 1");

		Users author1 = new Users();

		author1.setUserId(1);

		newsPost1.setAuthor(author1);

		newsPost1.setPublishedAt(LocalDateTime.now());

		newsPost1.setContent("Content 1");
		
		newsPost1.setLocation(Location.BENGALURU);

		newsList.add(newsPost1);

		NewsPostsDTO dto1 = new NewsPostsDTO();

		dto1.setTitle("Crime News 1");

		UsersDTO authorDTO1 = new UsersDTO();

		authorDTO1.setUserId(1);

		dto1.setAuthor(authorDTO1);

		dto1.setPublishedAt(LocalDateTime.now());

		dto1.setContent("Content 1");

		

		when(newsRepo.findByCategory(category)).thenReturn(newsList);

		when(modelMapper.map(author1, UsersDTO.class)).thenReturn(authorDTO1);

		List<NewsPostsDTO> result = newsService.getNewsPosts(categoryStr);

		assertFalse(result.isEmpty());

		assertEquals(dto1.getTitle(), result.get(0).getTitle());

		verify(newsRepo).findByCategory(category);

		

	}

	@Test
	void getNewsPosts_emptyList_throwsException() {

		String categoryStr = "GENERAL";

		NewsCategory category = NewsCategory.valueOf(categoryStr);

		

		when(newsRepo.findByCategory(category)).thenReturn(new ArrayList<>());

		assertThrows(LCP_Exception.class, () -> newsService.getNewsPosts(categoryStr));

		verify(newsRepo).findByCategory(category);

	}

	@Test
	void getAllNewsPosts_returnsAllDTOs() throws LCP_Exception {

		List<NewsPosts> allNews = new ArrayList<>();

		allNews.add(new NewsPosts());

		allNews.add(new NewsPosts());

		List<NewsPostsDTO> allDTOs = new ArrayList<>();

		allDTOs.add(new NewsPostsDTO());

		allDTOs.add(new NewsPostsDTO());

		when(newsRepo.findAll()).thenReturn(allNews);

		when(modelMapper.map(allNews, new TypeToken<List<NewsPostsDTO>>() {}.getType())).thenReturn(allDTOs);

		List<NewsPostsDTO> result = newsService.getAllNewsPosts();

		assertEquals(2, result.size());

		verify(newsRepo).findAll();

		

	}

	@Test
	void addNews_userExists_addsAndReturnsPostId() throws LCP_Exception {

		Integer userId = 1;

		NewsPostsDTO newsDTO = new NewsPostsDTO();

		newsDTO.setTitle("New Post");

		Users user = new Users();

		user.setUserId(userId);

		NewsPosts newsPost = new NewsPosts();

		newsPost.setPostId(10);
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

		when(usersRepo.findById(userId)).thenReturn(Optional.of(user));

		when(modelMapper.map(newsDTO, NewsPosts.class)).thenReturn(newsPost);

		when(newsRepo.save(any(NewsPosts.class))).thenReturn(newsPost);
		
		when(fileStorageServiceImpl.storeFile(image)).thenReturn("/images/test.jpg");

		String result = newsService.addNews(newsDTO, userId, image);

		assertEquals("Post Added null", result);

		verify(usersRepo).findById(userId);

		

		verify(newsRepo).save(any(NewsPosts.class));

		verify(userActivityRepo).save(any(UserActivity.class));

	}

	@Test
	void addNews_userDoesNotExist_throwsException() {

		Integer userId = 1;

		NewsPostsDTO newsDTO = new NewsPostsDTO();
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

		when(usersRepo.findById(userId)).thenReturn(Optional.empty());

		assertThrows(LCP_Exception.class, () -> newsService.addNews(newsDTO, userId, image));

		verify(usersRepo).findById(userId);

		verifyNoInteractions(modelMapper, newsRepo, userActivityRepo);

	}

	@Test
	void updateNews_userAndPostExistAndMatch_updatesAndReturnsUpdated() throws LCP_Exception {

		Integer userId = 1;

		NewsPostsDTO newsDTO = new NewsPostsDTO();

		newsDTO.setPostId(10);

		newsDTO.setContent("Updated Content");

		Users author = new Users();

		author.setUserId(userId);

		NewsPosts existingPost = new NewsPosts();

		existingPost.setPostId(10);

		existingPost.setAuthor(author);
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

		when(usersRepo.findById(userId)).thenReturn(Optional.of(author));

		when(newsRepo.findById(10)).thenReturn(Optional.of(existingPost));

		when(newsRepo.save(existingPost)).thenReturn(existingPost);
		
		when(fileStorageServiceImpl.storeFile(image)).thenReturn("/images/test.jpg");

		String result = newsService.updateNews(newsDTO, userId, image);

		assertEquals("Updated", result);

		assertEquals("Updated Content", existingPost.getContent());

		verify(usersRepo).findById(userId);

		verify(newsRepo).findById(10);

		verify(newsRepo).save(existingPost);

	}

	@Test
	void updateNews_userDoesNotExist_throwsException() {

		Integer userId = 1;

		NewsPostsDTO newsDTO = new NewsPostsDTO();
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

		when(usersRepo.findById(userId)).thenReturn(Optional.empty());

		assertThrows(LCP_Exception.class, () -> newsService.updateNews(newsDTO, userId, image));

		verify(usersRepo).findById(userId);

		

	}

	@Test
	void updateNews_postDoesNotExist_throwsException() {

		Integer userId = 1;

		NewsPostsDTO newsDTO = new NewsPostsDTO();

		newsDTO.setPostId(10);

		Users author = new Users();
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

		author.setUserId(userId);

		when(usersRepo.findById(userId)).thenReturn(Optional.of(author));

		when(newsRepo.findById(10)).thenReturn(Optional.empty());

		assertThrows(LCP_Exception.class, () -> newsService.updateNews(newsDTO, userId, image));

		verify(usersRepo).findById(userId);

		verify(newsRepo).findById(10);

	}

	@Test
	void updateNews_userNotAuthor_throwsException() {

		Integer userId = 2;

		NewsPostsDTO newsDTO = new NewsPostsDTO();

		newsDTO.setPostId(10);

		Users author = new Users();

		author.setUserId(1);

		Users loggedInUser = new Users();

		loggedInUser.setUserId(userId);

		NewsPosts existingPost = new NewsPosts();

		existingPost.setPostId(10);

		existingPost.setAuthor(author);
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

		when(usersRepo.findById(userId)).thenReturn(Optional.of(loggedInUser));

		when(newsRepo.findById(10)).thenReturn(Optional.of(existingPost));

		assertThrows(LCP_Exception.class, () -> newsService.updateNews(newsDTO, userId, image));

		verify(usersRepo).findById(userId);

		verify(newsRepo).findById(10);

	}

	@Test
	void deleteNews_postExists_deletesAndReturnsMessage() throws LCP_Exception {

		Integer postId = 10;

		NewsPosts existingPost = new NewsPosts();

		existingPost.setPostId(postId);

		when(newsRepo.findById(postId)).thenReturn(Optional.of(existingPost));

		doNothing().when(newsRepo).deleteById(postId);

		String result = newsService.deleteNews(postId);

		assertEquals("Deleted the post 10", result);

		assertNull(existingPost.getAuthor());

		verify(newsRepo).findById(postId);

		verify(newsRepo).deleteById(postId);

	}

	@Test
	void approveNews_adminExistsAndIsAdmin_approvesAndLogs() throws LCP_Exception {

		ModerationLogDTO logDTO = new ModerationLogDTO();

		UsersDTO adminDTO = new UsersDTO();

		adminDTO.setUserId(1);

		logDTO.setAdmin(adminDTO);

		logDTO.setContentId(10);

		logDTO.setActionType(ActionType.APPROVE);

		logDTO.setReason("Good content");

		Users adminUser = new Users();

		adminUser.setUserId(1);

		adminUser.setIsAdmin(true);

		NewsPosts newsPost = new NewsPosts();

		newsPost.setPostId(10);

		when(usersRepo.findById(1)).thenReturn(Optional.of(adminUser));

		when(newsRepo.findById(10)).thenReturn(Optional.of(newsPost));

		when(moderationLogRepo.save(any(ModerationLog.class))).thenReturn(new ModerationLog());

		when(newsRepo.save(newsPost)).thenReturn(newsPost);

		String result = newsService.approveNews(logDTO);

		assertEquals("Moderation log done", result);

		assertTrue(newsPost.getIsApproved());

		assertNotNull(newsPost.getPublishedAt());

		verify(usersRepo).findById(1);

		verify(newsRepo).findById(10);

		verify(moderationLogRepo).save(any(ModerationLog.class));

		verify(newsRepo).save(newsPost);

	}

	@Test
	void approveNews_adminDoesNotExist_throwsException() {

		ModerationLogDTO logDTO = new ModerationLogDTO();

		UsersDTO adminDTO = new UsersDTO();

		adminDTO.setUserId(1);

		logDTO.setAdmin(adminDTO);

		when(usersRepo.findById(1)).thenReturn(Optional.empty());

		assertThrows(LCP_Exception.class, () -> newsService.approveNews(logDTO));

		verify(usersRepo).findById(1);

		verifyNoInteractions(newsRepo, moderationLogRepo);

	}

	@Test
	void approveNews_adminNotAdmin_throwsException() {

		ModerationLogDTO logDTO = new ModerationLogDTO();

		UsersDTO adminDTO = new UsersDTO();

		adminDTO.setUserId(1);

		logDTO.setAdmin(adminDTO);

		Users adminUser = new Users();

		adminUser.setUserId(1);

		adminUser.setIsAdmin(false);

		when(usersRepo.findById(1)).thenReturn(Optional.of(adminUser));

		assertThrows(LCP_Exception.class, () -> newsService.approveNews(logDTO));

		verify(usersRepo).findById(1);

		verifyNoInteractions(newsRepo, moderationLogRepo);

	}

	@Test
	void approveNews_postDoesNotExist_throwsException() {

		ModerationLogDTO logDTO = new ModerationLogDTO();

		UsersDTO adminDTO = new UsersDTO();

		adminDTO.setUserId(1);

		logDTO.setAdmin(adminDTO);

		logDTO.setContentId(10);

		Users adminUser = new Users();

		adminUser.setUserId(1);

		adminUser.setIsAdmin(true);

		when(usersRepo.findById(1)).thenReturn(Optional.of(adminUser));

		when(newsRepo.findById(10)).thenReturn(Optional.empty());

		assertThrows(LCP_Exception.class, () -> newsService.approveNews(logDTO));

		verify(usersRepo).findById(1);

		verify(newsRepo).findById(10);

		verifyNoInteractions(moderationLogRepo);

	}

}