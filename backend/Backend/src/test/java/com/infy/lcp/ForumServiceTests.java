package com.infy.lcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.lcp.dto.ForumDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.Forum;
import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ForumRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.ForumServiceImpl;

@SpringBootTest
public class ForumServiceTests {

	@InjectMocks
	private ForumServiceImpl forumService;

	@Mock
	private ForumRepo forumRepository;

	@Mock
	private UsersRepo usersRepository;

	private ModelMapper mapper = new ModelMapper();

	private ForumDTO forumDto;
	private Users user;
	private Forum forum;

	@BeforeEach
	public void setup() {
		user = new Users();
		user.setUserId(1);
		UsersDTO userDto = new UsersDTO();
		userDto.setUserId(1);
		forumDto = new ForumDTO();
		forumDto.setForumId(100);
		forumDto.setForumDescription("Test Forum");
		forumDto.setUsers(userDto);
		forum = mapper.map(forumDto, Forum.class);
		forum.setUser(user);
		forum.setForumId(100);
	}

	
	@Test
	void testAddForum_Valid() throws Exception {
		when(usersRepository.findById(1)).thenReturn(Optional.of(user));
		when(forumRepository.save(any(Forum.class))).thenAnswer(i -> {
			Forum saved = i.getArgument(0);
			saved.setForumId(100);
			return saved;
		});
		Integer id = forumService.addForum(forumDto);
		assertEquals(100, id);
	}

	@Test
	void testAddForum_InvalidUser() {
		when(usersRepository.findById(1)).thenReturn(Optional.empty());
		LCP_Exception ex = assertThrows(LCP_Exception.class, () -> forumService.addForum(forumDto));
		assertEquals("service.USER_NOT_EXIST", ex.getMessage());
	}

	
	@Test
	void testUpdateForum_Valid() throws Exception {
		forum.setUser(user);
		when(forumRepository.findById(100)).thenReturn(Optional.of(forum));
		forumService.updateForum(100, forumDto);
		verify(forumRepository).findById(100);
	}

	@Test
	void testUpdateForum_Invalid_NotCreator() {
		user.setUserId(2);
		forum.setUser(user);
		when(forumRepository.findById(100)).thenReturn(Optional.of(forum));
		LCP_Exception ex = assertThrows(LCP_Exception.class, () -> forumService.updateForum(100, forumDto));
		assertEquals("service.FORUM_CREATER", ex.getMessage());
	}

	
	@Test
	void testDeleteForum_Valid() throws Exception {
		forum.setUser(user);
		when(forumRepository.findById(100)).thenReturn(Optional.of(forum));
		forumService.deleteForum(100);
		verify(forumRepository).deleteById(100);
	}

	@Test
	void testDeleteForum_Invalid_NotExist() {
		when(forumRepository.findById(999)).thenReturn(Optional.empty());
		LCP_Exception ex = assertThrows(LCP_Exception.class, () -> forumService.deleteForum(999));
		assertEquals("service.FORUM_EXIST", ex.getMessage());
	}

	
	@Test
	void testGetAllForums_Valid() throws Exception {
		forum.setUser(user);
		List<Forum> forumList = List.of(forum);
		when(forumRepository.findAll()).thenReturn(forumList);
		List<ForumDTO> result = forumService.getAllForums();
		assertEquals(1, result.size());
	}

	@Test
	void testGetAllForums_Empty() {
		when(forumRepository.findAll()).thenReturn(Collections.emptyList());
		LCP_Exception ex = assertThrows(LCP_Exception.class, () -> forumService.getAllForums());
		assertEquals("service.FORUM_EXIST", ex.getMessage());
	}

	
	@Test
	void testGetForumByCategory_Valid() throws Exception {
		ForumCategory category = ForumCategory.EDUCATIONAL;
		forum.setForumCategory(category);
		when(forumRepository.findByCategory(category)).thenReturn(List.of(forum));
		List<ForumDTO> result = forumService.getForumByCategory(category);
		assertEquals(1, result.size());
	}

	@Test
	void testGetForumByCategory_Empty() {
		ForumCategory category = ForumCategory.EDUCATIONAL;
		when(forumRepository.findByCategory(category)).thenReturn(Collections.emptyList());
		LCP_Exception ex = assertThrows(LCP_Exception.class, () -> forumService.getForumByCategory(category));
		assertEquals("service.FORUM_NOT_FOUND", ex.getMessage());
	}
}