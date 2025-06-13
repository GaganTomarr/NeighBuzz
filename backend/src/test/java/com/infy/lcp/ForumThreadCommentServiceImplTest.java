package com.infy.lcp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.lcp.dto.ForumThreadCommentsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ForumThreadComments;
import com.infy.lcp.entity.ForumThreads;
import com.infy.lcp.entity.Notifications;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ForumThreadCommentRepo;
import com.infy.lcp.repository.ForumThreadsRepo;
import com.infy.lcp.repository.NotificationsRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.ForumThreadCommentServiceImpl;

@SpringBootTest
public class ForumThreadCommentServiceImplTest {

    @Mock
    private ForumThreadCommentRepo commentRepo;

    @Mock
    private UsersRepo userRepo;

    @Mock
    private ForumThreadsRepo threadRepo;

    @InjectMocks
    private ForumThreadCommentServiceImpl forumThreadCommentService;

    @Mock
    private UserActivityRepo userActivityRepo;

    @Mock
    private NotificationsRepo notificationRepo; 
    
    

    @Test
    public void postComment_UserNotFoundTest() {
        Integer userId = 1;
        Integer threadId = 10;

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        dto.setCommentText("Test comment");

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.postComment(dto, userId, threadId);
        });

        Assertions.assertEquals("service.USER_NOT_EXIST", ex.getMessage());
    }

    @Test
    public void postComment_ThreadNotFoundTest() {
        Integer userId = 1;
        Integer threadId = 10;

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        dto.setCommentText("Test comment");

        Users user = new Users();
        user.setUserId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(threadRepo.findById(threadId)).thenReturn(Optional.empty());

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.postComment(dto, userId, threadId);
        });

        Assertions.assertEquals("service.COMMENT_POST", ex.getMessage());
    }

    @Test
    public void postComment_ValidTest() throws LCP_Exception {
        Integer userId = 1;
        Integer threadId = 10;

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        dto.setCommentText("Valid comment");

        Users user = new Users();
        user.setUserId(userId);

        ForumThreads thread = new ForumThreads();
        thread.setThreadId(threadId);

        
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(threadRepo.findById(threadId)).thenReturn(Optional.of(thread));
        when(commentRepo.save(any(ForumThreadComments.class))).thenAnswer(invocation -> {
            ForumThreadComments comment = invocation.getArgument(0);
            comment.setFtCommentId(100);
            return comment;
        });

        Integer resultId = forumThreadCommentService.postComment(dto, userId, threadId);

        Assertions.assertEquals(100, resultId);
        verify(commentRepo, times(1)).save(any(ForumThreadComments.class));
    }

    

    @Test
    public void getComments_EmptyListTest() {
        when(commentRepo.findAll()).thenReturn(new ArrayList<>());

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.getComments();
        });

        Assertions.assertEquals("service.COMMENT_NOT_FOUND", ex.getMessage());
    }

    @Test
    public void getComments_ValidTest() throws LCP_Exception {
        Users user = new Users();
        user.setUserId(1);

        ForumThreads thread = new ForumThreads();
        thread.setThreadId(1);

        ForumThreadComments comment = new ForumThreadComments();
        comment.setFtCommentId(10);
        comment.setCommentText("Sample comment");
        comment.setUser(user);
        comment.setThread(thread);

        List<ForumThreadComments> list = List.of(comment);

        when(commentRepo.findAll()).thenReturn(list);

        List<ForumThreadCommentsDTO> result = forumThreadCommentService.getComments();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Sample comment", result.get(0).getCommentText());
        Assertions.assertEquals(user.getUserId(), result.get(0).getUsers().getUserId());
        Assertions.assertEquals(thread.getThreadId(), result.get(0).getThread().getThreadId());
    }

    

    @Test
    public void deleteComment_CommentNotFoundTest() {
        Integer commentId = 1;
        Integer userId = 1;

        Users user = new Users();
        user.setUserId(userId);
        user.setIsAdmin(false);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.deleteComment(commentId, userId);
        });

        Assertions.assertEquals("service.COMMENT_NOT_FOUND", ex.getMessage());
    }


    @Test
    public void deleteComment_NotOwnerTest() {
        Integer commentId = 1;
        Integer userId = 1;

        
        Users loggedInUser = new Users();
        loggedInUser.setUserId(userId);
        loggedInUser.setIsAdmin(false);

        
        Users commentUser = new Users();
        commentUser.setUserId(2); 

        ForumThreadComments comment = new ForumThreadComments();
        comment.setFtCommentId(commentId);
        comment.setUser(commentUser);

        when(userRepo.findById(userId)).thenReturn(Optional.of(loggedInUser));
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.deleteComment(commentId, userId);
        });

        Assertions.assertEquals("service.COMMENT_OWNER_DELETE", ex.getMessage());
    }


    @Test
    public void deleteComment_ValidTest() throws LCP_Exception {
        Integer commentId = 1;
        Integer userId = 1;

        Users user = new Users();
        user.setUserId(userId);
        user.setIsAdmin(false); 

        ForumThreadComments comment = new ForumThreadComments();
        comment.setFtCommentId(commentId);
        comment.setUser(user);
        comment.setThread(new ForumThreads());

        
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        
        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepo.findByParentComment(comment)).thenReturn(new ArrayList<>());

        forumThreadCommentService.deleteComment(commentId, userId);

        verify(commentRepo, times(1)).delete(comment);
    }


    

    @Test
    public void updateComment_CommentNotFoundTest() {
        Integer commentId = 1;
        Integer userId = 1;

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        UsersDTO userDto = new UsersDTO();
        userDto.setUserId(userId);
        dto.setUsers(userDto);
        dto.setCommentText("Updated comment");

        when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.updateComment(dto, commentId, userId);
        });

        Assertions.assertEquals("service.COMMENT_NOT_FOUND", ex.getMessage());
    }

    @Test
    public void updateComment_NotOwnerTest() {
        Integer commentId = 1;
        Integer userId = 1;

        Users commentUser = new Users();
        commentUser.setUserId(2);

        ForumThreadComments comment = new ForumThreadComments();
        comment.setUser(commentUser);

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        UsersDTO userDto = new UsersDTO();
        userDto.setUserId(userId);
        dto.setUsers(userDto);
        dto.setCommentText("Updated comment");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.updateComment(dto, commentId, userId);
        });

        Assertions.assertEquals("service.COMMENT_OWNER_EDIT", ex.getMessage());
    }

    @Test
    public void updateComment_ValidTest() throws LCP_Exception {
        Integer commentId = 1;
        Integer userId = 1;

        Users commentUser = new Users();
        commentUser.setUserId(userId);

        ForumThreadComments comment = new ForumThreadComments();
        comment.setUser(commentUser);
        comment.setCommentText("Old comment");
        comment.setUpdatedAt(LocalDateTime.now());

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        UsersDTO userDto = new UsersDTO();
        userDto.setUserId(userId);
        dto.setUsers(userDto);
        dto.setCommentText("Updated comment");

        when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepo.save(any(ForumThreadComments.class))).thenReturn(comment);

        forumThreadCommentService.updateComment(dto, commentId, userId);

        verify(commentRepo, times(1)).save(any(ForumThreadComments.class));
        Assertions.assertEquals("Updated comment", comment.getCommentText());
    }

    

    @Test
    public void replyComment_ParentNotFoundTest() {
        Integer parentCommentId = 1;
        Integer userId = 1;
        Integer threadId = 1;

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        dto.setCommentText("Reply comment");

        when(commentRepo.findById(parentCommentId)).thenReturn(Optional.empty());

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.replyComment(dto, parentCommentId, userId, threadId);
        });

        Assertions.assertEquals("service.PARENT_COMMENT", ex.getMessage());
    }

    @Test
    public void replyComment_ThreadMismatchTest() {
        Integer parentCommentId = 1;
        Integer userId = 1;
        Integer threadId = 10;

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        dto.setCommentText("Reply comment");

        ForumThreadComments parentComment = new ForumThreadComments();
        ForumThreads parentThread = new ForumThreads();
        parentThread.setThreadId(99);  // Different threadId
        parentComment.setThread(parentThread);

        Users user = new Users();
        user.setUserId(userId);

        ForumThreads thread = new ForumThreads();
        thread.setThreadId(threadId);

        when(commentRepo.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(threadRepo.findById(threadId)).thenReturn(Optional.of(thread));

        LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
            forumThreadCommentService.replyComment(dto, parentCommentId, userId, threadId);
        });

        Assertions.assertEquals("service.PARENT_ID", ex.getMessage());
    }

    @Test
    public void replyComment_ValidTest() throws LCP_Exception {
        Integer parentCommentId = 1;
        Integer userId = 1;
        Integer threadId = 10;

        ForumThreadCommentsDTO dto = new ForumThreadCommentsDTO();
        dto.setCommentText("Reply comment");

        ForumThreadComments parentComment = new ForumThreadComments();
        ForumThreads parentThread = new ForumThreads();
        parentThread.setThreadId(threadId);
        parentComment.setThread(parentThread);

        Users user = new Users();
        user.setUserId(userId);
        user.setUsername("John");

        ForumThreads thread = new ForumThreads();
        thread.setThreadId(threadId);

        when(commentRepo.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(threadRepo.findById(threadId)).thenReturn(Optional.of(thread));
        when(commentRepo.save(any(ForumThreadComments.class))).thenAnswer(invocation -> {
            ForumThreadComments c = invocation.getArgument(0);
            c.setFtCommentId(200);
            return c;
        });

        when(userActivityRepo.save(any(UserActivity.class))).thenReturn(new UserActivity());
        when(notificationRepo.save(any(Notifications.class))).thenReturn(new Notifications());

        Integer resultId = forumThreadCommentService.replyComment(dto, parentCommentId, userId, threadId);

        Assertions.assertEquals(200, resultId);
        verify(commentRepo, times(1)).save(any(ForumThreadComments.class));
        verify(userActivityRepo, times(1)).save(any(UserActivity.class));
        verify(notificationRepo, times(1)).save(any(Notifications.class));
    }

}
