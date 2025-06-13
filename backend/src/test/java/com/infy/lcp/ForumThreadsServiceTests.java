package com.infy.lcp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.ForumThreadsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.Forum;
import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.entity.ForumThreads;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ForumThreadsRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.FileStorageServiceImpl;
import com.infy.lcp.service.ForumThreadsServiceImpl;

@SpringBootTest
class ForumThreadsServiceTests {

	@InjectMocks
	private ForumThreadsServiceImpl service;

	@Mock
	private ForumThreadsRepo forumThreadsRepo;

	@Mock
	private UsersRepo usersRepo;

	@Mock
	private FileStorageServiceImpl fileStorageServiceImpl;

	@Mock
	private MultipartFile file;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	
	
	@Test
	void testAddForumThreads_userNotFound() {
		ForumThreadsDTO dto = new ForumThreadsDTO();
		UsersDTO usersDTO = new UsersDTO();
		usersDTO.setUserId(99);
		dto.setUsers(usersDTO);

		when(usersRepo.findById(99)).thenReturn(Optional.empty());

		assertThrows(LCP_Exception.class, () -> service.addForumThreads(dto, null));
	}

	
	@Test
	void testUpdateForumThreads_success() throws Exception {
		ForumThreads thread = new ForumThreads();
		thread.setCreator(new Users());
		thread.getCreator().setUserId(1);

		ForumThreadsDTO dto = new ForumThreadsDTO();
		dto.setUsers(new UsersDTO());
		dto.getUsers().setUserId(1);
		dto.setThreadId(100);

		when(forumThreadsRepo.findById(100)).thenReturn(Optional.of(thread));
		when(file.isEmpty()).thenReturn(true);

		assertDoesNotThrow(() -> service.updateForumThreads(100, dto, file));
	}

	@Test
	void testUpdateForumThreads_unauthorizedUser() {
		ForumThreads thread = new ForumThreads();
		thread.setCreator(new Users());
		thread.getCreator().setUserId(1);

		ForumThreadsDTO dto = new ForumThreadsDTO();
		dto.setUsers(new UsersDTO());
		dto.getUsers().setUserId(2);
		dto.setThreadId(100);

		when(forumThreadsRepo.findById(100)).thenReturn(Optional.of(thread));

		assertThrows(LCP_Exception.class, () -> service.updateForumThreads(100, dto, file));
	}

	@Test
	void testDeleteForumThreads_success() throws Exception {
		ForumThreads thread = new ForumThreads();
		thread.setCreator(new Users());
		thread.setForum(new Forum());

		when(forumThreadsRepo.findById(1)).thenReturn(Optional.of(thread));

		assertDoesNotThrow(() -> service.deleteForumThreads(1));
		verify(forumThreadsRepo, times(1)).deleteById(1);
	}

	@Test
	void testDeleteForumThreads_notFound() {
		when(forumThreadsRepo.findById(2)).thenReturn(Optional.empty());
		assertThrows(LCP_Exception.class, () -> service.deleteForumThreads(2));
	}

	@Test
	void testGetAllForumThreads_success() throws Exception {
		ForumThreads thread = new ForumThreads();
		thread.setForum(new Forum());
		thread.setCreator(new Users());

		when(forumThreadsRepo.findAll()).thenReturn(List.of(thread));
		List<ForumThreadsDTO> result = service.getAllForumsThreads();

		assertFalse(result.isEmpty());
	}

	@Test
	void testGetAllForumThreads_emptyList() {
		when(forumThreadsRepo.findAll()).thenReturn(Collections.emptyList());
		assertThrows(LCP_Exception.class, () -> service.getAllForumsThreads());
	}

	@Test
	void testGetThreadsByCategory_success() throws Exception {
		ForumCategory category = ForumCategory.EDUCATIONAL;
		ForumThreads thread = new ForumThreads();

		when(forumThreadsRepo.findByCategory(category)).thenReturn(List.of(thread));
		List<ForumThreadsDTO> result = service.getForumThreadsByCategory(category);

		assertEquals(1, result.size());
	}

	@Test
	void testGetThreadsByCategory_empty() {
		ForumCategory category = ForumCategory.EDUCATIONAL;

		when(forumThreadsRepo.findByCategory(category)).thenReturn(Collections.emptyList());
		assertThrows(LCP_Exception.class, () -> service.getForumThreadsByCategory(category));
	}

	@Test
	void testGetThreadsByDate_success() throws Exception {
		LocalDateTime date = LocalDateTime.now();
		ForumThreads thread = new ForumThreads();

		when(forumThreadsRepo.findByDate(date)).thenReturn(List.of(thread));
		assertFalse(service.getForumThreadsByDate(date).isEmpty());
	}

	@Test
	void testGetThreadsByDate_empty() {
		LocalDateTime date = LocalDateTime.now();

		when(forumThreadsRepo.findByDate(date)).thenReturn(Collections.emptyList());
		assertThrows(LCP_Exception.class, () -> service.getForumThreadsByDate(date));
	}

	@Test
	void testGetThreadsByPopularity_success() throws Exception {
		ForumThreads t1 = new ForumThreads();
		ForumThreads t2 = new ForumThreads();

		t1.setViewCount(10);
		t2.setViewCount(20);

		when(forumThreadsRepo.findAll()).thenReturn(List.of(t1, t2));
		List<ForumThreadsDTO> result = service.getForumThreadsByPopularity();

		assertTrue(result.get(0).getViewCount() >= result.get(1).getViewCount());
	}

	@Test
	void testGetThreadsByPopularity_empty() {
		when(forumThreadsRepo.findAll()).thenReturn(Collections.emptyList());
		assertThrows(LCP_Exception.class, () -> service.getForumThreadsByPopularity());
	}

	@Test
	void testSearchByKeyword_success() throws Exception {
		ForumThreads thread = new ForumThreads();
		thread.setCreator(new Users());
		thread.setForum(new Forum());

		when(forumThreadsRepo.findByTitleContainingIgnoreCase("java")).thenReturn(List.of(thread));
		assertFalse(service.searchThreadByKeyword("java").isEmpty());
	}

	@Test
	void testSearchByKeyword_empty() {
		when(forumThreadsRepo.findByTitleContainingIgnoreCase("none")).thenReturn(Collections.emptyList());
		assertThrows(LCP_Exception.class, () -> service.searchThreadByKeyword("none"));
	}

	@Test
	void testGetThreadById_success() throws Exception {
		ForumThreads thread = new ForumThreads();
		thread.setCreator(new Users());
		thread.setForum(new Forum());

		when(forumThreadsRepo.findById(1)).thenReturn(Optional.of(thread));
		ForumThreadsDTO result = service.getThreadById(1);
		assertNotNull(result);
	}

	@Test
	void testGetThreadById_notFound() {
		when(forumThreadsRepo.findById(10)).thenReturn(Optional.empty());
		assertThrows(LCP_Exception.class, () -> service.getThreadById(10));
	}
}