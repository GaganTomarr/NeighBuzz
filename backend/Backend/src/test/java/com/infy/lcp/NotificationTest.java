package com.infy.lcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.lcp.dto.NotificationsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.EventRegistrations;
import com.infy.lcp.entity.Notifications;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.EventRegistrationsRepo;
import com.infy.lcp.repository.NotificationsRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.NotificationsServiceImpl;

@SpringBootTest
public class NotificationTest {

    @Mock
    private UsersRepo usersRepository;

    @Mock
    private EventRegistrationsRepo eventRegRepository;

    @Mock
    private NotificationsRepo notificationsRepository;

    @Mock
    private org.modelmapper.ModelMapper mapper;

    @InjectMocks
    private NotificationsServiceImpl notificationService;

    private NotificationsDTO notificationDto;
    private UsersDTO usersDto;

    @BeforeEach
    void setUp() {
        usersDto = new UsersDTO();
        usersDto.setUserId(1);

        notificationDto = new NotificationsDTO();
        notificationDto.setUserDTO(usersDto);
        notificationDto.setRelatedEntityId(10);
    }

    @Test
    void pushNotification_shouldSaveNotificationSuccessfully() throws LCP_Exception {
        // Arrange
        Users user = new Users();
        user.setUserId(1);

        EventRegistrations eventReg = new EventRegistrations();
        eventReg.setRegistrationId(10);

        Notifications notification = new Notifications();
        notification.setNotificationId(100);

        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(eventRegRepository.findById(10)).thenReturn(Optional.of(eventReg));
        when(mapper.map(notificationDto, Notifications.class)).thenReturn(notification);
        when(mapper.map(usersDto, Users.class)).thenReturn(user);
        when(notificationsRepository.save(notification)).thenReturn(notification);

        // Act
        Integer result = notificationService.pushNotification(notificationDto);

        // Assert
        assertEquals(100, result);
        verify(usersRepository).findById(1);
        verify(eventRegRepository).findById(10);
        verify(notificationsRepository).save(notification);
    }

    @Test
    void pushNotification_shouldThrowExceptionWhenUserNotFound() {
        
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        
        LCP_Exception thrown = assertThrows(LCP_Exception.class, () -> {
            notificationService.pushNotification(notificationDto);
        });

        assertEquals("service.USER_NOT_EXIST", thrown.getMessage());
        verify(usersRepository).findById(1);
        verify(eventRegRepository, never()).findById(any());
        verify(notificationsRepository, never()).save(any());
    }
    
    @Test
    void fetchAllNotifactions_shouldReturnMappedDTOList() throws LCP_Exception {
      
        int userId = 1;
        Notifications notification = new Notifications();
        Users user = new Users();
        user.setUserId(userId);
        notification.setUser(user);
        notification.setNotificationId(200);

        NotificationsDTO mappedDto = new NotificationsDTO();
        UsersDTO mappedUserDto = new UsersDTO();

        when(notificationsRepository.allNotificationsOfUser(userId)).thenReturn(List.of(notification));
        when(mapper.map(notification, NotificationsDTO.class)).thenReturn(mappedDto);
        when(mapper.map(notification, UsersDTO.class)).thenReturn(mappedUserDto);

        
        List<NotificationsDTO> result = notificationService.fetchAllNotifactions(userId);

        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(mappedDto, result.get(0));
        verify(notificationsRepository).allNotificationsOfUser(userId);
        verify(mapper).map(notification, NotificationsDTO.class);
        verify(mapper).map(notification, UsersDTO.class);
    }

    @Test
    void fetchAllNotifactions_shouldThrowExceptionWhenNoNotificationsExist() {
        
        int userId = 1;
        when(notificationsRepository.allNotificationsOfUser(userId)).thenReturn(Collections.emptyList());

        
        LCP_Exception thrown = assertThrows(LCP_Exception.class, () -> {
            notificationService.fetchAllNotifactions(userId);
        });

        assertEquals("service.NOTIFICATIONS_EXIST", thrown.getMessage());
        verify(notificationsRepository).allNotificationsOfUser(userId);
        verifyNoInteractions(mapper);
    }
    
    @Test
    void removeNotification_shouldDeleteNotificationSuccessfully() throws LCP_Exception {
        
        int notificationId = 200;
        int userId = 1;

        NotificationsDTO dto = new NotificationsDTO();
        dto.setNotificationId(notificationId);

        UsersDTO usersDto = new UsersDTO();
        usersDto.setUserId(userId);
        dto.setUserDTO(usersDto);

        Users user = new Users();
        user.setUserId(userId);

        Notifications notification = new Notifications();
        notification.setNotificationId(notificationId);
        notification.setUser(user);
        notification.setRelatedEntityId(10);

        when(notificationsRepository.findById(notificationId)).thenReturn(Optional.of(notification));

       
        notificationService.removeNotification(dto);

        
        assertNull(notification.getUser());
        assertNull(notification.getRelatedEntityId());
        verify(notificationsRepository).findById(notificationId);
        verify(notificationsRepository).deleteById(notificationId);
    }

    @Test
    void removeNotification_shouldThrowExceptionIfUserIsNotOwner() {
        
        int notificationId = 200;

        NotificationsDTO dto = new NotificationsDTO();
        dto.setNotificationId(notificationId);

        UsersDTO usersDto = new UsersDTO();
        usersDto.setUserId(99); 
        dto.setUserDTO(usersDto);

        Users actualUser = new Users();
        actualUser.setUserId(1);

        Notifications notification = new Notifications();
        notification.setNotificationId(notificationId);
        notification.setUser(actualUser);

        when(notificationsRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        
        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> {
            notificationService.removeNotification(dto);
        });

        assertEquals("service.NOTIFICATIONS_DELETE", ex.getMessage());
        verify(notificationsRepository).findById(notificationId);
        verify(notificationsRepository, never()).deleteById(anyInt());
    }
    
    @Test
    void markNotificationAsRead_shouldSetIsReadToTrue() throws LCP_Exception {
        
        int notificationId = 1;
        Notifications notification = new Notifications();
        notification.setNotificationId(notificationId);
        notification.setIsRead(false);

        when(notificationsRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        
        notificationService.markNotificationAsRead(notificationId);

        
        assertTrue(notification.getIsRead());
        verify(notificationsRepository).findById(notificationId);
        verify(notificationsRepository).save(notification);
    }

    @Test
    void markNotificationAsRead_shouldThrowExceptionIfNotFound() {
        
        int notificationId = 1;
        when(notificationsRepository.findById(notificationId)).thenReturn(Optional.empty());

        
        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> {
            notificationService.markNotificationAsRead(notificationId);
        });

        assertEquals("service.NOTIFICATIONS_EXIST", ex.getMessage());
        verify(notificationsRepository).findById(notificationId);
        verify(notificationsRepository, never()).save(any());
    }
    
    @Test
    void markAllNotificationsAsRead_shouldInvokeRepositoryMethod() throws LCP_Exception {
       
        int userId = 1;
        Users user = new Users();
        user.setUserId(userId);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        
        notificationService.markAllNotificationsAsRead(userId);

        
        verify(usersRepository).findById(userId);
        verify(notificationsRepository).markAllAsRead(userId);
    }

    @Test
    void markAllNotificationsAsRead_shouldThrowExceptionIfUserNotFound() {
        
        int userId = 1;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

       
        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> {
            notificationService.markAllNotificationsAsRead(userId);
        });

        assertEquals("service.USER_NOT_EXIST", ex.getMessage());
        verify(usersRepository).findById(userId);
        verify(notificationsRepository, never()).markAllAsRead(anyInt());
    }

}
