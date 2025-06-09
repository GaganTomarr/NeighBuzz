package com.infy.lcp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.lcp.dto.NotificationsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.EventRegistrations;
import com.infy.lcp.entity.Notifications;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.EventRegistrationsRepo;
import com.infy.lcp.repository.NotificationsRepo;
import com.infy.lcp.repository.UsersRepo;

import jakarta.transaction.Transactional;

@Service(value="notificationsService")
@Transactional
public class NotificationsServiceImpl implements NotificationsService{
	
	ModelMapper mapper=new ModelMapper();

	@Autowired
	EventRegistrationsRepo eventRegRepository;

	@Autowired
	UsersRepo usersRepository;
	
	@Autowired
	NotificationsRepo notificationsRepository;

	@Override
	public Integer pushNotification(NotificationsDTO notificationDto) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(notificationDto.getUserDTO().getUserId());
		Users user=userOpt.orElseThrow(()->new LCP_Exception("service.USER_NOT_EXIST"));
		
		Optional<EventRegistrations> eventRegOpt=eventRegRepository.findById(notificationDto.getRelatedEntityId());
		EventRegistrations eventReg=eventRegOpt.orElseThrow(()->new LCP_Exception("service.NOTIFICATION_REGISTRATION"));
		
		Notifications notification=mapper.map(notificationDto, Notifications.class);
		notification.setUser(mapper.map(notificationDto.getUserDTO(), Users.class));
		
		notificationsRepository.save(notification);
		return notification.getNotificationId();
	}

	@Override
	public List<NotificationsDTO> fetchAllNotifactions(Integer userId) throws LCP_Exception {
		List<Notifications> notificationsInList=notificationsRepository.allNotificationsOfUser(userId);
		if(notificationsInList.isEmpty()) {
			throw new LCP_Exception("service.NOTIFICATIONS_EXIST");
		}
		
		List<NotificationsDTO> notificationOutList=new ArrayList<>();
		notificationsInList.stream().forEach(obj->{
			NotificationsDTO notificationsDto=mapper.map(obj, NotificationsDTO.class);
			notificationsDto.setUserDTO(mapper.map(obj, UsersDTO.class));
			notificationOutList.add(notificationsDto);
		});
		
		return notificationOutList;
	}

	@Override
	public void removeNotification(NotificationsDTO notificationDto) throws LCP_Exception {
		Optional<Notifications> notificationsOpt=notificationsRepository.findById(notificationDto.getNotificationId());
		Notifications notifications=notificationsOpt.orElseThrow(()->new LCP_Exception("service.NOTIFICATIONS_EXIST"));
		
		if(notificationDto.getUserDTO().getUserId()!=notifications.getUser().getUserId()) {
			throw new LCP_Exception("service.NOTIFICATIONS_DELETE");
		}
		
		if(notifications.getUser()!=null || notifications.getRelatedEntityId()!=null) {
			notifications.setUser(null);
			notifications.setRelatedEntityId(null);
		}
		
		notificationsRepository.deleteById(notificationDto.getNotificationId());
	}
	
	@Override
	public void markNotificationAsRead(Integer notificationId) throws LCP_Exception {
	    Optional<Notifications> notificationOpt = notificationsRepository.findById(notificationId);
	    if (notificationOpt.isEmpty()) {
	        throw new LCP_Exception("service.NOTIFICATIONS_EXIST");
	    }
	    Notifications notification = notificationOpt.get();
	    notification.setIsRead(true); 
	    notificationsRepository.save(notification);
	}
	
	@Override
	public void markAllNotificationsAsRead(Integer userId) throws LCP_Exception {
	    Optional<Users> userOpt = usersRepository.findById(userId);
	    if (userOpt.isEmpty()) {
	        throw new LCP_Exception("service.USER_NOT_EXIST");
	    }
	    notificationsRepository.markAllAsRead(userId);
	}



}