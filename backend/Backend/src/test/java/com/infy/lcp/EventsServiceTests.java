package com.infy.lcp;


import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.dto.EventsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.AccountStatus;
import com.infy.lcp.entity.Events;
import com.infy.lcp.entity.EventsCategory;
import com.infy.lcp.entity.Location;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.EventsRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.EventsService;
import com.infy.lcp.service.EventsServiceImpl;
import com.infy.lcp.service.FileStorageServiceImpl;

@SpringBootTest
public class EventsServiceTests {

	@Mock
	private UserActivityRepo userActivityRepo;
	
	@Mock
	EventsRepo eventRepository;

	@Mock
	UsersRepo usersRepository;
	
	@Mock
	FileStorageServiceImpl fileStorageServiceImpl;

	@InjectMocks
	private EventsService eventService= new EventsServiceImpl();
	
	@Test
	public void addEventUserDoesNotExistsTest() {
		UsersDTO userDto=new UsersDTO();
		userDto.setUserId(1);
		
		EventsDTO eventDto=new EventsDTO();
		eventDto.setOrganizer(userDto);
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
		
		Mockito.when(usersRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
		LCP_Exception exception=Assertions.assertThrows(LCP_Exception.class, ()->eventService.addEvent(eventDto, image));
		Assertions.assertEquals("service.USER_NOT_EXIST", exception.getMessage());
	}
	
	@Test
	public void addEventSuccessTest() throws LCP_Exception {
	    ModelMapper mapper = new ModelMapper();

	    Users users = new Users();
	    users.setUserId(1);
	    users.setEmail("cbc@gmail.com");
	    users.setUsername("abc");
	    users.setPasswordHash("1234dca@ASD");
	    users.setRegistrationDate(LocalDateTime.of(2025, 9, 12, 15, 12));
	    users.setAccountStatus(AccountStatus.ACTIVE);
	    users.setIsAdmin(false);

	    Events events = new Events();
	    events.setEventId(1);
	    events.setOrganizer(users);
	    events.setTitle("hfyg");
	    events.setDescription("skcbhabchchcb");
	    events.setLocation(Location.BENGALURU);
	    events.setEventDate(LocalDate.of(2024, 6, 7));
	    events.setStartTime(LocalTime.of(17, 0));
	    events.setEndTime(LocalTime.of(21, 0));
	    events.setCapacity(700);
	    events.setRegistrationDeadline(LocalDateTime.of(2025, 12, 14, 16, 5));
	    events.setIsPublished(true);
	    events.setCreatedAt(LocalDateTime.of(2025, 4, 30, 16, 5));
	    events.setUpdatedAt(LocalDateTime.of(2025, 4, 30, 16, 5));
	    events.setEventsCategory(EventsCategory.BOOKMEET);

	    MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

	    EventsDTO eventDto = mapper.map(events, EventsDTO.class);
	    

	    Mockito.when(usersRepository.findById(1)).thenReturn(Optional.of(users));
	    Mockito.when(eventRepository.save(Mockito.any(Events.class))).thenReturn(events);
	    Mockito.when(fileStorageServiceImpl.storeFile(image)).thenReturn("/images/test.jpg");

	    Integer eventId = eventService.addEvent(eventDto, image);
	    Assertions.assertEquals(1, eventId);

	    Mockito.verify(userActivityRepo).save(Mockito.any(UserActivity.class));
	}

	
	@Test
	public void updateEventInvalidTest() {
		ModelMapper mapper=new ModelMapper();
		
		Events events=new Events();
		events.setEventId(1);
		
		EventsDTO eventDto=mapper.map(events, EventsDTO.class);
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
		
		Mockito.when(eventRepository.findById(events.getEventId())).thenReturn(Optional.empty());
		LCP_Exception exception=Assertions.assertThrows(LCP_Exception.class, ()->eventService.updateEvent(events.getEventId(), eventDto, image));
		Assertions.assertEquals("service.EVENT_REGISTRATION_NO_EVENT", exception.getMessage());
	}
	
	@Test
	public void updateEventValidTest() throws LCP_Exception {
		ModelMapper mapper=new ModelMapper();
		
		Users users=new Users();
		users.setUserId(1);
		users.setEmail("cbc@gmail.com");
		users.setUsername("abc");
		users.setPasswordHash("1234dca@ASD");
		users.setRegistrationDate(LocalDateTime.of(2025, 9, 12, 15, 12));
		users.setAccountStatus(AccountStatus.ACTIVE);
		users.setIsAdmin(false);

		Events events=new Events();
		events.setEventId(1);
		events.setOrganizer(users);
		events.setTitle("hfyg");
		events.setDescription("skcbhabchchcb");
		events.setLocation(Location.BENGALURU);
		events.setEventDate(LocalDate.of(2024, 06, 07));
		events.setStartTime(LocalTime.of(17, 0));
		events.setEndTime(LocalTime.of(21, 0));
		events.setCapacity(700);
		events.setRegistrationDeadline(LocalDateTime.of(2025, 12, 14, 16, 05));
		events.setIsPublished(true);
		events.setCreatedAt(LocalDateTime.of(2025, 04, 30, 16, 05));
		events.setUpdatedAt(LocalDateTime.of(2025, 04, 30, 16, 05));
		events.setEventsCategory(EventsCategory.BOOKMEET);
		
		MultipartFile image=new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());
		
		EventsDTO eventDto=mapper.map(events, EventsDTO.class);
		eventDto.setOrganizer(mapper.map(eventDto.getOrganizer(), UsersDTO.class));
		eventDto.setCapacity(500);
		
		Mockito.when(eventRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(events));
		eventService.updateEvent(events.getEventId(), eventDto, image);
		Mockito.verify(eventRepository, times(1)).save(Mockito.any(Events.class));
	}
	
	@Test
	public void deleteEventInvalidTest() {
		Events event=new Events();
		event.setEventId(1);
		
		Mockito.when(eventRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
		LCP_Exception exception=Assertions.assertThrows(LCP_Exception.class, ()->eventService.deleteEvent(event.getEventId()));
		Assertions.assertEquals("service.EVENT_REGISTRATION_NO_EVENT", exception.getMessage());
	}
	
	@Test
	public void deleteEventValidTest() throws LCP_Exception {
		Events event=new Events();
		event.setEventId(1);
		
		Mockito.when(eventRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(event));
		eventService.deleteEvent(event.getEventId());
		Mockito.verify(eventRepository, times(1)).deleteById(Mockito.anyInt());
	}

	@Test
	public void getEventByIdEventNotFound() throws LCP_Exception{
		Integer eventId=1;
		Mockito.when(eventRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
		LCP_Exception exception=Assertions.assertThrows(LCP_Exception.class,()->eventService.getEventById(eventId));
		Assertions.assertEquals("service.EVENT_REGISTRATION_NO_EVENT",exception.getMessage());
	}

	@Test
	public void getEventById_Success() throws LCP_Exception{

		ModelMapper mapper=new ModelMapper();

		Integer eventId=1;
		Users user= new Users();
		user.setUserId(1);
		user.setEmail("cbc@gmail.com");
		user.setUsername("abc");
		user.setPasswordHash("1234dca@ASD");
		user.setRegistrationDate(LocalDateTime.of(2025, 9, 12,15,12));
		user.setAccountStatus(AccountStatus.ACTIVE);
		user.setIsAdmin(false);

		Events event=new Events();
		event.setEventId(eventId);
		event.setOrganizer(user);
		event.setTitle("Game Event");
		event.setDescription("embjhdfwej end wehfvuhwebf");
		event.setEventDate(LocalDate.of(2025,01,04));
		event.setStartTime(LocalTime.of(8, 00, 00));

		EventsDTO eventDto=mapper.map(event,EventsDTO.class);
		eventDto.setOrganizer(mapper.map(user, UsersDTO.class));

		Mockito.when(usersRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
		Mockito.when(eventRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(event));

		EventsDTO eventDtoOut=eventService.getEventById(eventId);
		Assertions.assertEquals(eventDto,eventDtoOut);

	}


	@Test
	public void getEventByCategoryNoEventFound() throws LCP_Exception{
		EventsCategory eventsCategory=EventsCategory.BOOKMEET;
		Mockito.when(eventRepository.findByCategory(Mockito.any(EventsCategory.class))).thenReturn(new ArrayList<>());

		LCP_Exception exception=Assertions.assertThrows(LCP_Exception.class,()->eventService.getEventByCategory(eventsCategory));
		Assertions.assertEquals("service.EVENT_REGISTRATION_NO_EVENT",exception.getMessage());
	}

	@Test
	public void getEventByCategory_Success() throws LCP_Exception{

		ModelMapper mapper=new ModelMapper();

		EventsCategory eventsCategory=EventsCategory.BOOKMEET;
		Users user= new Users();
		user.setUserId(1);
		user.setEmail("cbc@gmail.com");
		user.setUsername("abc");
		user.setPasswordHash("1234dca@ASD");
		user.setRegistrationDate(LocalDateTime.of(2025, 9, 12,15,12));
		user.setAccountStatus(AccountStatus.ACTIVE);
		user.setIsAdmin(false);

		Events event=new Events();
		event.setEventId(1);
		event.setOrganizer(user);
		event.setTitle("Game Event");
		event.setDescription("embjhdfwej end wehfvuhwebf");
		event.setEventDate(LocalDate.of(2025,01,04));
		event.setStartTime(LocalTime.of(8, 00, 00));
		event.setEventsCategory(eventsCategory);

		EventsDTO eventDto=mapper.map(event,EventsDTO.class);
		eventDto.setOrganizer(mapper.map(user, UsersDTO.class));

		List<Events> eventList=new ArrayList<>();
		eventList.add(event);

		Mockito.when(usersRepository.findById(1)).thenReturn(Optional.of(user));
		Mockito.when(eventRepository.findByCategory(eventsCategory)).thenReturn(eventList);

		List<EventsDTO> eventDtolist=eventService.getEventByCategory(eventsCategory);
		Assertions.assertEquals(1,eventDtolist.size());

	}

	@Test
	public void getEventByDateNoEventFound() throws LCP_Exception{
		LocalDate eventDate=LocalDate.of(2025, 9, 13);
		Mockito.when(eventRepository.findByEventDate(Mockito.any(LocalDate.class))).thenReturn(new ArrayList<>());

		LCP_Exception exception=Assertions.assertThrows(LCP_Exception.class,()->eventService.getEventByDate(eventDate));
		Assertions.assertEquals("service.EVENT_REGISTRATION_NO_EVENT",exception.getMessage());
	}

	@Test
	public void getEventByDate_Success() throws LCP_Exception{

		ModelMapper mapper=new ModelMapper();

		LocalDate eventDate=LocalDate.of(2025, 9, 13);
		Users user= new Users();
		user.setUserId(1);
		user.setEmail("cbc@gmail.com");
		user.setUsername("abc");
		user.setPasswordHash("1234dca@ASD");
		user.setRegistrationDate(LocalDateTime.of(2025, 9, 12,15,12));
		user.setAccountStatus(AccountStatus.ACTIVE);
		user.setIsAdmin(false);

		Events event=new Events();
		event.setEventId(1);
		event.setOrganizer(user);
		event.setTitle("Game Event");
		event.setDescription("embjhdfwej end wehfvuhwebf");
		event.setEventDate(eventDate);
		event.setStartTime(LocalTime.of(8, 00, 00));
		event.setEventsCategory(EventsCategory.BOOKMEET);

		EventsDTO eventDto=mapper.map(event,EventsDTO.class);
		eventDto.setOrganizer(mapper.map(user, UsersDTO.class));

		List<Events> eventList=new ArrayList<>();
		eventList.add(event);

		Mockito.when(usersRepository.findById(1)).thenReturn(Optional.of(user));
		Mockito.when(eventRepository.findByEventDate(eventDate)).thenReturn(eventList);

		List<EventsDTO> eventDtolist=eventService.getEventByDate(eventDate);
		Assertions.assertEquals(1,eventDtolist.size());
	}
}