package com.infy.lcp;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.UserActivityServiceImpl;



@SpringBootTest
public class UserActivityTests {
	

	
	

	    @InjectMocks
	    private UserActivityServiceImpl userActivityService;

	    @Mock
	    private UserActivityRepo userActivityRepo;

	    @Mock
	    private UsersRepo usersRepo;

	    private Users testUser;
	    private UserActivity testActivity;

	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.openMocks(this);

	        testUser = new Users();
	        testUser.setUserId(null);

	        
	        testActivity = new UserActivity();
	        testActivity.setActivityId(100);
	        testActivity.setUser(testUser);

	    }

	    @Test
	    public void testGetUserActivity_UserExists_ReturnsActivityList() throws LCP_Exception {
	       
	        when(usersRepo.findById(1)).thenReturn(Optional.of(testUser));
	        
	        when(userActivityRepo.findByUserOrderByOccurredAtDesc(testUser)).thenReturn(Collections.singletonList(testActivity));

	        
	        List<UserActivity> result = userActivityService.getUserActivity(1);

	        
	        assertNotNull(result, "Result list should not be null");
	        assertEquals(1, result.size(), "Result list size should be 1");
	        assertEquals(testActivity, result.get(0), "Returned activity should match testActivity");

	        
	        verify(usersRepo, times(1)).findById(1);
	        verify(userActivityRepo, times(1)).findByUserOrderByOccurredAtDesc(testUser);
	    }

	    @Test
	    public void testGetUserActivity_UserDoesNotExist_ThrowsException() {
	        
	        when(usersRepo.findById(1)).thenReturn(Optional.empty());

	  
	        LCP_Exception thrown = assertThrows(LCP_Exception.class, () -> userActivityService.getUserActivity(1));
	        assertTrue(thrown.getMessage().contains("service.USER_ACTIVITY_NO_PROFILE"));

	       
	        verify(usersRepo, times(1)).findById(1);
	        verify(userActivityRepo, never()).findByUserOrderByOccurredAtDesc(any());
	    }

	    @Test
	    public void testDeleteUserActivity_UserExists_DeletesActivity() throws LCP_Exception {
	        
	        UserActivity activity = new UserActivity();
	        activity.setActivityId(100);
	        activity.setUser(testUser);

	       
	        when(userActivityRepo.findById(100)).thenReturn(Optional.of(activity));

	       
	        userActivityService.deleteUserActivity(1, 100);

	        verify(userActivityRepo).findById(100);
	        verify(userActivityRepo).deleteById(100);

	        assertNull(activity.getUser());
	    }


	    @Test
	    public void testDeleteUserActivity_ActivityDoesNotExist_ThrowsException() {
	        
	        when(userActivityRepo.findById(100)).thenReturn(Optional.empty());

	        
	        LCP_Exception thrown = assertThrows(LCP_Exception.class, () -> 
	            userActivityService.deleteUserActivity(1, 100)
	        );

	        assertEquals("service.USER_ACTIVIY_DELETE", thrown.getMessage());

	        verify(userActivityRepo, times(1)).findById(100);
	        verify(userActivityRepo, never()).deleteById(anyInt());
	    }

	}


