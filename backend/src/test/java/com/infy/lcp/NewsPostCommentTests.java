package com.infy.lcp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.lcp.dto.NewsPostCommentsDTO;
import com.infy.lcp.dto.NewsPostsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.AccountStatus;
import com.infy.lcp.entity.Location;
import com.infy.lcp.entity.NewsCategory;
import com.infy.lcp.entity.NewsPostComments;
import com.infy.lcp.entity.NewsPosts;
import com.infy.lcp.entity.Notifications;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.NewsPostCommentsRepo;
import com.infy.lcp.repository.NewsPostRepo;
import com.infy.lcp.repository.NotificationsRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.NewsPostCommentServiceImpl;

@SpringBootTest
public class NewsPostCommentTests {

	@Mock
	private UserActivityRepo userActivityRepo;
	
    @Mock
    private NewsPostCommentsRepo commentRepo;

    @Mock
    private NewsPostRepo newsPost;

    @Mock
    private UsersRepo userRepo;

    @Mock
    private NewsPostRepo newsRepo;

    @Mock
    private NotificationsRepo notificationRepo;
    
    @InjectMocks
    private NewsPostCommentServiceImpl newsPostCommentService;

    @Test
    public void postComments_userNotFoundTest() {
        
        NewsPostComments comments = new NewsPostComments();
        comments.setCommentText("This is a valid comment.");
        comments.setCommentId(null);  
        comments.setCreatedAt(null);
        comments.setParentComment(null);
        comments.setUpdatedAt(null);

        Users user = new Users();
        user.setUserId(1);
        user.setUsername("johndoe");
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEmail("johndoe@example.com");
        
        NewsPosts post = new NewsPosts();
        post.setPostId(101);  
        post.setAuthor(user);
        post.setContent("This is a sample post content.");
        post.setNewsCategory(NewsCategory.EDUCATIONAL);
        post.setTitle("Latest Tech Trends");
        post.setLocation(Location.BENGALURU);

       
        ModelMapper mapper = new ModelMapper();
        NewsPostCommentsDTO commentDto = mapper.map(comments, NewsPostCommentsDTO.class);
        NewsPostsDTO postDto = mapper.map(post, NewsPostsDTO.class);
        UsersDTO userDto = mapper.map(user, UsersDTO.class);

       
        commentDto.setUserDTO(userDto);
        commentDto.setNewsPosts(postDto);

        
        Mockito.when(userRepo.findById(user.getUserId())).thenReturn(Optional.empty());

        
        LCP_Exception exc = Assertions.assertThrows(LCP_Exception.class, () -> {
            newsPostCommentService.postComments(commentDto, user.getUserId(), post.getPostId());
        });
        Assertions.assertEquals("service.USER_NOT_EXIST", exc.getMessage());
    }

    @Test
    public void postComments_PostNotFoundTest() {
        
        NewsPostComments comments = new NewsPostComments();
        comments.setCommentText("This is a valid comment.");
        
        comments.setCommentId(null);  
        comments.setCreatedAt(null);
        comments.setParentComment(null);
        comments.setUpdatedAt(null);

        Users user = new Users();
        user.setUserId(1);  
        user.setUsername("janedoe");
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEmail("janedoe@example.com");
        
        NewsPosts post = new NewsPosts();
        post.setPostId(1);  
        post.setAuthor(user);
        post.setContent("Content of the post regarding something interesting.");
        post.setNewsCategory(NewsCategory.CULTURE);
        post.setTitle("Health Tips for 2025");
        post.setLocation(Location.BENGALURU);

        
        ModelMapper mapper = new ModelMapper();
        NewsPostCommentsDTO commentDto = mapper.map(comments, NewsPostCommentsDTO.class);
        NewsPostsDTO postDto = mapper.map(post, NewsPostsDTO.class);
        UsersDTO userDto = mapper.map(user, UsersDTO.class);

        
        commentDto.setUserDTO(userDto);
        commentDto.setNewsPosts(postDto);

        
        Mockito.when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        Mockito.when(newsRepo.findById(post.getPostId())).thenReturn(Optional.empty());

        
        LCP_Exception exc = Assertions.assertThrows(LCP_Exception.class, () -> {
            newsPostCommentService.postComments(commentDto, user.getUserId(), post.getPostId());
        });
        Assertions.assertEquals("service.COMMENT_POST", exc.getMessage());
    }
    
    @Test
    public void postCommentsValidTest() throws LCP_Exception {
        
        NewsPostComments comments = new NewsPostComments();
        comments.setCommentText("This is a valid comment.");
        comments.setCreatedAt(LocalDateTime.now());
        comments.setParentComment(null);
        comments.setUpdatedAt(LocalDateTime.now());
        comments.setIsApproved(true);

        Users user = new Users();
        user.setUserId(1);
        user.setUsername("janedoe");
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEmail("janedoe@example.com");
        user.setLastLogin(LocalDateTime.now());
        user.setRegistrationDate(LocalDateTime.now());
        user.setIsAdmin(false);
        user.setPasswordHash("reyufrfy");

        NewsPosts post = new NewsPosts();
        post.setPostId(1);
        post.setAuthor(user);
        post.setContent("Content of the post regarding something interesting.");
        post.setNewsCategory(NewsCategory.HEALTH);
        post.setTitle("Health Tips for 2025");
        post.setApprovalUser(user);
        post.setUpdatedAt(LocalDateTime.now());
        post.setCreatedAt(LocalDateTime.now());
        post.setExcerpt("juigced");
        post.setFeaturedImage("");
        post.setIsApproved(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setLocation(Location.BENGALURU);

        
        ModelMapper mapper = new ModelMapper();
        NewsPostCommentsDTO commentDto = mapper.map(comments, NewsPostCommentsDTO.class);
        NewsPostsDTO postDto = mapper.map(post, NewsPostsDTO.class);
        UsersDTO userDto = mapper.map(user, UsersDTO.class);

        
        commentDto.setUserDTO(userDto);
        commentDto.setNewsPosts(postDto);

        
        Mockito.when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        Mockito.when(newsRepo.findById(post.getPostId())).thenReturn(Optional.of(post));

        
        Mockito.when(commentRepo.save(Mockito.any(NewsPostComments.class))).thenAnswer(invocation -> {
            NewsPostComments arg = invocation.getArgument(0);
            arg.setCommentId(1);  
            return arg;
        });

        
        Mockito.when(userActivityRepo.save(Mockito.any(UserActivity.class))).thenReturn(null);

       
        Integer commentId = newsPostCommentService.postComments(commentDto, user.getUserId(), post.getPostId());

        
        Assertions.assertEquals(1, commentId);
        Mockito.verify(commentRepo, Mockito.times(1)).save(Mockito.any(NewsPostComments.class));
        Mockito.verify(userActivityRepo, Mockito.times(1)).save(Mockito.any(UserActivity.class));
    }

    
    @Test
    public void findByContentIdPostNotFoundTest() throws LCP_Exception {
    	
    	NewsPostComments comments = new NewsPostComments();
        comments.setCommentText("This is a valid comment.");
        
        comments.setCommentId(null);  
        comments.setCreatedAt(null);
        comments.setParentComment(null);
        comments.setUpdatedAt(null);
        comments.setCommentId(10);

        Users user = new Users();
        user.setUserId(1);  
        user.setUsername("janedoe");
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEmail("janedoe@example.com");
        
        NewsPosts post = new NewsPosts();
        post.setPostId(1);  
        post.setAuthor(user);
        post.setContent("Content of the post regarding something interesting.");
        post.setNewsCategory(NewsCategory.HEALTH);
        post.setTitle("Health Tips for 2025");
        post.setLocation(Location.BENGALURU);

        
        ModelMapper mapper = new ModelMapper();
        NewsPostCommentsDTO commentDto = mapper.map(comments, NewsPostCommentsDTO.class);
        NewsPostsDTO postDto = mapper.map(post, NewsPostsDTO.class);
        UsersDTO userDto = mapper.map(user, UsersDTO.class);

        
        commentDto.setUserDTO(userDto);
        commentDto.setNewsPosts(postDto);
        
        
        Mockito.when(newsPost.findById(comments.getCommentId())).thenReturn(Optional.empty());

        
        LCP_Exception exception = Assertions.assertThrows(LCP_Exception.class, () -> 
            newsPostCommentService.findByContentId(comments.getCommentId())
        );
        Assertions.assertEquals("service.COMMENT_POST", exception.getMessage());
    }
    
    @Test
    public void findByContentIdValidTest() throws LCP_Exception {
    	
        Integer contentId = 100;
        
        NewsPosts post = new NewsPosts();
        post.setPostId(contentId);
        post.setTitle("Sample Post");

        NewsPostComments comment1 = new NewsPostComments();
        comment1.setCommentText("First comment");
        
        NewsPostComments comment2 = new NewsPostComments();
        comment2.setCommentText("Second comment");

        List<NewsPostComments> commentsList = Arrays.asList(comment1, comment2);

        
        Mockito.when(newsPost.findById(contentId)).thenReturn(Optional.of(post));

        
        Mockito.when(commentRepo.findByContentId(contentId)).thenReturn(commentsList);

        
        List<NewsPostComments> result = newsPostCommentService.findByContentId(contentId);

        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size()); 
        Assertions.assertEquals("First comment", result.get(0).getCommentText());
        Assertions.assertEquals("Second comment", result.get(1).getCommentText());
        Mockito.verify(newsPost, Mockito.times(1)).findById(contentId);
        Mockito.verify(commentRepo, Mockito.times(1)).findByContentId(contentId);
    }
    
    @Test
    public void deleteComment_ValidTest() throws LCP_Exception {
        Integer commentId = 10;
        Integer userId = 1;

        
        Users user = new Users();
        user.setUserId(userId);
        user.setIsAdmin(false);  

        
        NewsPostComments comment = new NewsPostComments();
        comment.setCommentId(commentId);
        comment.setUser(user);
        comment.setNewsPosts(new NewsPosts());
        comment.setParentComment(null); 

        
        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(commentRepo.findById(commentId)).thenReturn(Optional.of(comment));

        
        newsPostCommentService.deleteComment(commentId, userId);

        
        Mockito.verify(userRepo, Mockito.times(1)).findById(userId);
        Mockito.verify(commentRepo, Mockito.times(1)).findById(commentId);
        Mockito.verify(commentRepo, Mockito.times(1)).delete(comment);
    }


    @Test
    public void deleteComment_InvalidTest() {
       Integer commentId = 99;
       Integer userId = 1;

       
       Users user = new Users();
       user.setUserId(userId);
       Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(user));

       
       Mockito.when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

       LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
           newsPostCommentService.deleteComment(commentId, userId);
       });

       Assertions.assertEquals("service.COMMENT_NOT", ex.getMessage());
    }



    @Test
    public void updateComment_ValidTest() throws LCP_Exception {
        Integer commentId = 1;
        Integer userId = 100;

        
        NewsPostComments existingComment = new NewsPostComments();
        existingComment.setCommentId(commentId);
        
        Users user = new Users();
        user.setUserId(userId);
        existingComment.setUser(user); 
        
        NewsPostCommentsDTO dto = new NewsPostCommentsDTO();
        dto.setCommentId(commentId);
        dto.setCommentText("Updated comment text");

       
        Mockito.when(commentRepo.findById(commentId)).thenReturn(Optional.of(existingComment));
        Mockito.when(commentRepo.save(Mockito.any(NewsPostComments.class))).thenAnswer(invocation -> invocation.getArgument(0));

   
        NewsPostComments result = newsPostCommentService.updateComment(dto, commentId, userId);

      
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated comment text", result.getCommentText());
        Mockito.verify(commentRepo, Mockito.times(1)).save(Mockito.any(NewsPostComments.class));
    }


    @Test
    public void updateComment_InvalidTest() {
       Integer commentId = 99;
       Integer userId=1;
       NewsPostCommentsDTO dto = new NewsPostCommentsDTO();
       dto.setCommentText("New Text");

       Mockito.when(commentRepo.findById(commentId)).thenReturn(Optional.empty());

       LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
           newsPostCommentService.updateComment(dto, commentId, userId);
       });

       Assertions.assertEquals("service.COMMENT_NOT_FOUND", ex.getMessage());
    }


    @Test
    public void replyToComment_ValidTest() throws LCP_Exception {
        Integer parentCommentId = 10;
        Integer userId = 1;          
        Integer postId = 100;

        
        NewsPosts post = new NewsPosts();
        post.setPostId(postId);
        post.setTitle("Some news");

        
        Users parentCommentUser = new Users();
        parentCommentUser.setUserId(2);
        parentCommentUser.setUsername("parentUser");
        parentCommentUser.setEmail("parent@example.com");
        parentCommentUser.setAccountStatus(AccountStatus.ACTIVE);

        
        NewsPostComments parentComment = new NewsPostComments();
        parentComment.setCommentId(parentCommentId);
        parentComment.setCommentText("Parent comment");
        parentComment.setNewsPosts(post);
        parentComment.setUser(parentCommentUser);

        
        Users replyUser = new Users();
        replyUser.setUserId(userId);
        replyUser.setUsername("replyUser");
        replyUser.setEmail("reply@example.com");
        replyUser.setAccountStatus(AccountStatus.ACTIVE);

        
        NewsPostCommentsDTO replyDto = new NewsPostCommentsDTO();
        replyDto.setCommentText("This is a reply");

       
        Mockito.when(commentRepo.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(replyUser));  
        Mockito.when(newsRepo.findById(postId)).thenReturn(Optional.of(post));

        
        Mockito.when(commentRepo.save(Mockito.any(NewsPostComments.class))).thenAnswer(invocation -> {
            NewsPostComments reply = invocation.getArgument(0);
            reply.setCommentId(999);  
            return reply;
        });

        
        Integer returnedId = newsPostCommentService.replyComment(replyDto, parentCommentId, userId, postId);

        
        Assertions.assertNotNull(returnedId);
        Assertions.assertEquals(999, returnedId);

        
        Mockito.verify(commentRepo, Mockito.times(1)).save(Mockito.any(NewsPostComments.class));
        Mockito.verify(notificationRepo, Mockito.times(1)).save(Mockito.any(Notifications.class));
        Mockito.verify(userActivityRepo, Mockito.times(1)).save(Mockito.any(UserActivity.class));
    }


    @Test
    public void replyToComment_InvalidTest() {
       Integer userId = 1, postId = 100, parentCommentId = 200;

       Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(new Users()));
       Mockito.when(newsRepo.findById(postId)).thenReturn(Optional.of(new NewsPosts()));
       Mockito.when(commentRepo.findById(parentCommentId)).thenReturn(Optional.empty());

       NewsPostCommentsDTO dto = new NewsPostCommentsDTO();
       dto.setCommentText("Invalid reply");

       LCP_Exception ex = Assertions.assertThrows(LCP_Exception.class, () -> {
           newsPostCommentService.replyComment(dto, userId, postId, parentCommentId);
       });

       Assertions.assertEquals("service.PARENT_COMMENT", ex.getMessage());
    }
    
}