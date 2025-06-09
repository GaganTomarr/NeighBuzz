package com.infy.lcp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.lcp.dto.EventRegistrationsDTO;
import com.infy.lcp.dto.EventsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ActivityType;
import com.infy.lcp.entity.EntityType;
import com.infy.lcp.entity.EventRegistrations;
import com.infy.lcp.entity.Events;
import com.infy.lcp.entity.Notifications;
import com.infy.lcp.entity.NotificationsType;
import com.infy.lcp.entity.RegistrationStatus;
import com.infy.lcp.entity.RelatedEntityType;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.EventRegistrationsRepo;
import com.infy.lcp.repository.EventsRepo;
import com.infy.lcp.repository.NotificationsRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;

import jakarta.transaction.Transactional;

@Service(value="eventRegistrationService")
@Transactional
public class EventRegistrationsServiceImpl implements EventRegistrationsService{
	
	ModelMapper mapper=new ModelMapper();

	@Autowired
	EventsRepo eventsRepository;

	@Autowired
	UsersRepo usersRepository;
	
	@Autowired
	EventRegistrationsRepo eventRegRepo;
	
	@Autowired
	UserActivityRepo userActivityRepo;
	
	@Autowired
	private NotificationsRepo notificationRepo;

	@Override
	public Integer makeRegistrations(EventRegistrationsDTO eventRegistrationDto) throws LCP_Exception {
		Optional<Users> usersOpt=usersRepository.findById(eventRegistrationDto.getUsers().getUserId());
		Users user=usersOpt.orElseThrow(()->new LCP_Exception("service.USER_NOT_EXIST"));
		
		Optional<Events> eventOpt=eventsRepository.findById(eventRegistrationDto.getEvents().getEventId());
		Events event=eventOpt.orElseThrow(()->new LCP_Exception("service.EVENT_REGISTRATION_NO_EVENT"));
		
		if (eventRegRepo.existsByEventAndUser(event, user)) {
		    throw new LCP_Exception("service.EVENT_REGISTRATION_ALREADY_PRESENT");
		}
		
		EventRegistrations eventReg=mapper.map(eventRegistrationDto, EventRegistrations.class);
		eventReg.setUser(mapper.map(eventRegistrationDto.getUsers(), Users.class));
		eventReg.setEvent(mapper.map(eventRegistrationDto.getEvents(), Events.class));
		
		eventRegRepo.save(eventReg);
		
		UserActivity userActivity = new UserActivity();
		userActivity.setActivityType(ActivityType.RSVP);
		userActivity.setEntityType(EntityType.EVENT);
		userActivity.setEntityId(eventReg.getRegistrationId());
		userActivity.setUser(usersOpt.get());
		userActivity.setOccurredAt(LocalDateTime.now());
		userActivityRepo.save(userActivity);
		
		Notifications notification = new Notifications();
		notification.setCreatedAt(LocalDateTime.now());
		notification.setNotificationsType(NotificationsType.EVENT_UPDATE);
		notification.setContent("service.EVENT_REGISTRATION_SUCCESS" + eventOpt.get().getTitle());
		notification.setUser(usersOpt.get());
		notification.setRelatedEntityType(RelatedEntityType.EVENT);
		notification.setRelatedEntityId(eventOpt.get().getEventId());
		notification.setPostId(eventOpt.get().getEventId());
		notificationRepo.save(notification);
		
		return eventReg.getRegistrationId();
	}

	@Override
	public List<EventRegistrationsDTO> fetchAllRegistrations() throws LCP_Exception {
		List<EventRegistrations> eventRegInList=eventRegRepo.findAll();
		if(eventRegInList.isEmpty()) {
			throw new LCP_Exception("service.EVENT_SERVICE_NO_REGISTRATION");
		}
		
		List<EventRegistrationsDTO> eventRegOutList=new ArrayList<>();
		eventRegInList.stream().forEach(obj->{
			EventRegistrationsDTO eventRegDto=mapper.map(obj, EventRegistrationsDTO.class);
			eventRegDto.setUsers(mapper.map(obj.getUser(), UsersDTO.class));
			eventRegDto.setEvents(mapper.map(obj.getEvent(), EventsDTO.class));
			eventRegOutList.add(eventRegDto);
		});
		
		return eventRegOutList;
	}

	@Override
	public void cancelRegistrations(Integer registrationId) throws LCP_Exception {
		Optional<EventRegistrations> eventRegOpt=eventRegRepo.findById(registrationId);
		EventRegistrations eventReg=eventRegOpt.orElseThrow(()->new LCP_Exception("service.EVENT_REGISTRATION_CANCEL"));
		
		if(eventReg.getEvent()!=null || eventReg.getUser()!=null) {
			eventReg.setEvent(null);
			eventReg.setUser(null);
			eventReg.setRegistrationStatus(RegistrationStatus.CANCELLED);
		}
		
		eventRegRepo.deleteById(registrationId);
	}
}