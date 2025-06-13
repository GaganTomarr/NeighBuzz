package com.infy.lcp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.EventsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ActionType;
import com.infy.lcp.entity.ActivityType;
import com.infy.lcp.entity.ContentType;
import com.infy.lcp.entity.EntityType;
import com.infy.lcp.entity.Events;
import com.infy.lcp.entity.EventsCategory;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.EventsRepo;
import com.infy.lcp.repository.ModerationLogRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;

import jakarta.transaction.Transactional;

@Service(value="eventsService")
@Transactional
public class EventsServiceImpl implements EventsService {

	ModelMapper mapper=new ModelMapper();

	@Autowired
	public EventsRepo eventRepository;

	@Autowired
	UsersRepo usersRepository;

	@Autowired
	private FileStorageServiceImpl fileStorageServiceImpl;

	@Autowired
	UserActivityRepo userActivityRepo;

	@Autowired
	ModerationLogRepo moderationLogRepo;

	@Override
	public Integer addEvent(EventsDTO eventDto, MultipartFile image) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(eventDto.getOrganizer().getUserId());
		Users users=userOpt.orElseThrow(()-> new LCP_Exception("service.USER_NOT_EXIST"));

		Events event=mapper.map(eventDto, Events.class);
		event.setOrganizer(users);
		event.setCreatedAt(LocalDateTime.now());
		event.setUpdatedAt(LocalDateTime.now());
		event.setIsPublished(false);

		String imageUrl=null;
		if(image!=null && !image.isEmpty()) {
			imageUrl=fileStorageServiceImpl.storeFile(image);
			event.setFeaturedImage(imageUrl);
		}

		event=eventRepository.save(event);

		UserActivity userActivity = new UserActivity();
		userActivity.setActivityType(ActivityType.EVENT_CREATED);
		userActivity.setEntityType(EntityType.EVENT);
		userActivity.setEntityId(event.getEventId());
		userActivity.setUser(userOpt.get());
		userActivity.setOccurredAt(LocalDateTime.now());
		userActivityRepo.save(userActivity);

		return event.getEventId();
	}

	@Override
	public void updateEvent(Integer eventId, EventsDTO eventDto, MultipartFile image) throws LCP_Exception {
		Optional<Events> eventOpt=eventRepository.findById(eventId);
		Events event=eventOpt.orElseThrow(()-> new LCP_Exception("service.EVENT_REGISTRATION_NO_EVENT"));

		if(event.getOrganizer().getUserId()!=eventDto.getOrganizer().getUserId()) {
			throw new LCP_Exception("service.EVENT_SERVICE_UPDATION");
		}

		event=mapper.map(eventDto, Events.class);
		event.setEventId(eventId);
		event.setUpdatedAt(LocalDateTime.now());

		String imageUrl=event.getFeaturedImage();
		if(image!=null && !image.isEmpty()) {
			imageUrl=fileStorageServiceImpl.storeFile(image);
			event.setFeaturedImage(imageUrl);
		}

		eventRepository.save(event);
	}

	@Override
	public void deleteEvent(Integer eventId) throws LCP_Exception {
		Optional<Events> eventOpt=eventRepository.findById(eventId);
		Events event=eventOpt.orElseThrow(()-> new LCP_Exception("service.EVENT_REGISTRATION_NO_EVENT"));
		if(event.getOrganizer()!=null) {
			event.setOrganizer(null);
		}
		eventRepository.deleteById(eventId);
	}

	@Override
	public List<EventsDTO> getAllEvents() throws LCP_Exception {
		List<Events> eventList=eventRepository.findAll();
		if(eventList.isEmpty()) {
			throw new LCP_Exception("service.EVENT_REGISTRATION_NO_EVENT");
		}

		List<EventsDTO> eventsDTOList= new ArrayList<>();
		eventList.stream().forEach(obj->{
			EventsDTO eventDto=mapper.map(obj, EventsDTO.class);
			eventDto.setOrganizer(mapper.map(obj.getOrganizer(), UsersDTO.class));
			eventsDTOList.add(eventDto);
		});

		return eventsDTOList;
	}

	@Override
	public EventsDTO getEventById(Integer eventId) throws LCP_Exception {

		Optional<Events> eventOpt=eventRepository.findById(eventId);
		Events event=eventOpt.orElseThrow(()->new LCP_Exception("service.EVENT_REGISTRATION_NO_EVENT"));

		EventsDTO eventDto=mapper.map(event, EventsDTO.class);
		eventDto.setOrganizer(mapper.map(event.getOrganizer(), UsersDTO.class));

		return eventDto;
	}


	@Override
	public List<EventsDTO> getEventByCategory(EventsCategory eventCategory) throws LCP_Exception{
		List<Events> eventList=eventRepository.findByCategory(eventCategory);
		if(eventList.isEmpty()) {
			throw new LCP_Exception("service.EVENT_REGISTRATION_NO_EVENT");
		}
		List<EventsDTO> eventsDTOList= new ArrayList<>();
		for(Events event: eventList) {
			EventsDTO event1=mapper.map(event,EventsDTO.class);
			eventsDTOList.add(event1);
		}
		return eventsDTOList.stream().sorted((p1,p2)->p2.getEventDate().compareTo(p1.getEventDate())).toList();
	}

	@Override
	public List<EventsDTO> getEventByDate(LocalDate eventDate) throws LCP_Exception{
		List<Events> eventList=eventRepository.findByEventDate(eventDate);
		if(eventList.isEmpty()) {
			throw new LCP_Exception("service.EVENT_REGISTRATION_NO_EVENT");
		}
		List<EventsDTO> eventsDTOList= new ArrayList<>();
		for(Events event: eventList) {
			EventsDTO event1=mapper.map(event,EventsDTO.class);
			eventsDTOList.add(event1);
		}
		return eventsDTOList.stream().sorted((p1,p2)->p2.getEventDate().compareTo(p1.getEventDate())).toList();
	}


	@Override
	public List<EventsDTO> getVisibleEventsForUser(Integer userId, boolean isAdmin) {
		List<Events> eventList = eventRepository.findAll();
		List<EventsDTO> visibleEvents = new ArrayList<>();

		for (Events event : eventList) {
			if (Boolean.TRUE.equals(event.getIsPublished()) || // Visible to everyone if published
					(event.getOrganizer().getUserId().equals(userId)) || // Visible to creator
					isAdmin) { // Visible to admin
				EventsDTO dto = mapper.map(event, EventsDTO.class);
				dto.setOrganizer(mapper.map(event.getOrganizer(), UsersDTO.class));
				visibleEvents.add(dto);
			}
		}
		return visibleEvents;
	}

	@Override
	public void approveEvent(Integer eventId, Integer userId) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(userId);
		Users user=userOpt.orElseThrow(()->new LCP_Exception("service.USER_NOT_EXIST"));

		ModerationLog modLog=new ModerationLog();
		modLog.setActionDate(LocalDateTime.now());
		modLog.setActionType(ActionType.APPROVE);
		modLog.setAdmin(user);
		modLog.setContentId(eventId);
		modLog.setContentType(ContentType.EVENT);
		modLog.setReason("This event is approved.");

		moderationLogRepo.save(modLog);
	}

	@Override
	public void rejectEvent(Integer eventId, Integer userId) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(userId);
		Users user=userOpt.orElseThrow(()->new LCP_Exception("service.USER_NOT_EXIST"));

		ModerationLog modLog=new ModerationLog();
		modLog.setActionDate(LocalDateTime.now());
		modLog.setActionType(ActionType.REJECT);
		modLog.setAdmin(user);
		modLog.setContentId(eventId);
		modLog.setContentType(ContentType.EVENT);
		modLog.setReason("This event is rejected.");

		moderationLogRepo.save(modLog);
	}
}