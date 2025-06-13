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

import com.infy.lcp.dto.EventRegistrationsDTO;
import com.infy.lcp.dto.EventsDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.AccountStatus;
import com.infy.lcp.entity.EventRegistrations;
import com.infy.lcp.entity.Events;
import com.infy.lcp.entity.EventsCategory;
import com.infy.lcp.entity.Location;
import com.infy.lcp.entity.Notifications;
import com.infy.lcp.entity.RegistrationStatus;
import com.infy.lcp.entity.UserActivity;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.EventRegistrationsRepo;
import com.infy.lcp.repository.EventsRepo;
import com.infy.lcp.repository.NotificationsRepo;
import com.infy.lcp.repository.UserActivityRepo;
import com.infy.lcp.repository.UsersRepo;
import com.infy.lcp.service.EventRegistrationsService;
import com.infy.lcp.service.EventRegistrationsServiceImpl;

@SpringBootTest
public class EventRegistrationsTests {
	@Mock
	private UsersRepo userRepo;
	@Mock
	private UserActivityRepo userActivityRepo;
	@Mock
	private NotificationsRepo notificationRepo;
	@Mock
	private EventsRepo eventsRepository;

	@Mock
	private UsersRepo usersRepository;

	@Mock
	private EventRegistrationsRepo eventRegRepo;

	@InjectMocks
	private EventRegistrationsService eventRegService=new EventRegistrationsServiceImpl();

	@Test
	public void makeRegistrationsNoEventsFoundTest() {
	    ModelMapper mapper = new ModelMapper();

	    EventRegistrationsDTO eventRegDto = new EventRegistrationsDTO();
	    eventRegDto.setRegistrationStatus(RegistrationStatus.REGISTERED);
	    eventRegDto.setRegistrationDate(LocalDateTime.of(2025, 5, 12, 14, 30));
	    eventRegDto.setCancellationDate(LocalDateTime.of(2025, 5, 20, 20, 30));

	    UsersDTO usersDto = new UsersDTO();
	    usersDto.setUserId(1);
	    eventRegDto.setUsers(usersDto);

	    EventsDTO eventDto = new EventsDTO();
	    eventDto.setEventId(99); 
	    eventRegDto.setEvents(eventDto);

	    Users user = mapper.map(usersDto, Users.class);

	    Mockito.when(usersRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
	    Mockito.when(eventsRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

	    LCP_Exception exception = Assertions.assertThrows(
	        LCP_Exception.class,
	        () -> eventRegService.makeRegistrations(eventRegDto)
	    );

	    Assertions.assertEquals("service.EVENT_REGISTRATION_NO_EVENT", exception.getMessage());
	}


	@Test
	public void makeRegistrationsSuccessfulRegistrationsTest() throws LCP_Exception {
	    ModelMapper mapper = new ModelMapper();

	    EventRegistrationsDTO eventRegDto = new EventRegistrationsDTO();

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

	    EventRegistrations eventReg = new EventRegistrations();
	    eventReg.setRegistrationId(1);
	    eventReg.setUser(users);
	    eventReg.setEvent(events);
	    eventReg.setRegistrationStatus(RegistrationStatus.REGISTERED);
	    eventReg.setRegistrationDate(LocalDateTime.of(2025, 12, 14, 16, 5));

	    
	    eventRegDto = mapper.map(eventReg, EventRegistrationsDTO.class);
	    eventRegDto.setEvents(mapper.map(events, EventsDTO.class));
	    eventRegDto.setUsers(mapper.map(users, UsersDTO.class));

	    
	    Mockito.when(usersRepository.findById(1)).thenReturn(Optional.of(users));
	    Mockito.when(eventsRepository.findById(1)).thenReturn(Optional.of(events));
	    Mockito.when(eventRegRepo.existsByEventAndUser(events, users)).thenReturn(false); // critical!
	    Mockito.when(eventRegRepo.save(Mockito.any(EventRegistrations.class))).thenReturn(eventReg);

	    
	    Mockito.when(userActivityRepo.save(Mockito.any(UserActivity.class))).thenReturn(new UserActivity());
	    Mockito.when(notificationRepo.save(Mockito.any(Notifications.class))).thenReturn(new Notifications());

	    
	    Integer regId = eventRegService.makeRegistrations(eventRegDto);

	   
	    Assertions.assertEquals(1, regId);
	    
	    
	    Mockito.verify(usersRepository).findById(1);
	    Mockito.verify(eventsRepository).findById(1);
	    Mockito.verify(eventRegRepo).existsByEventAndUser(events, users);
	    Mockito.verify(eventRegRepo).save(Mockito.any(EventRegistrations.class));
	    Mockito.verify(userActivityRepo).save(Mockito.any(UserActivity.class));
	    Mockito.verify(notificationRepo).save(Mockito.any(Notifications.class));
	}

	
	@Test
	public void fetchAllRegistrationsNoRegistrationsFoundTest() {
		Mockito.when(eventRegRepo.findAll()).thenReturn(new ArrayList<EventRegistrations>());
		LCP_Exception exception=Assertions.assertThrows(LCP_Exception.class, ()->eventRegService.fetchAllRegistrations());
		Assertions.assertEquals("service.EVENT_SERVICE_NO_REGISTRATION", exception.getMessage());
	}
	
	@Test
	public void fetchAllRegistrationsValidTest() throws LCP_Exception {
		ModelMapper mapper=new ModelMapper();
		
		EventRegistrationsDTO eventRegDto=new EventRegistrationsDTO();

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

		EventRegistrations eventReg=new EventRegistrations();
		eventReg.setRegistrationId(1);
		eventReg.setUser(users);
		eventReg.setEvent(events);
		eventReg.setRegistrationStatus(RegistrationStatus.REGISTERED);
		eventReg.setRegistrationDate(LocalDateTime.of(2025, 12, 14, 16, 05));
		
		eventRegDto=mapper.map(eventReg, EventRegistrationsDTO.class);
		eventRegDto.setEvents(mapper.map(events, EventsDTO.class));
		eventRegDto.setUsers(mapper.map(users, UsersDTO.class));
		
		
		EventRegistrationsDTO eventRegDto2=new EventRegistrationsDTO();

		Users users2=new Users();
		users2.setUserId(2);
		users2.setEmail("cbc@gmail.com");
		users2.setUsername("abc");
		users2.setPasswordHash("1234dca@ASD");
		users2.setRegistrationDate(LocalDateTime.of(2025, 9, 12, 15, 12));
		users2.setAccountStatus(AccountStatus.ACTIVE);
		users2.setIsAdmin(false);

		Events events2=new Events();
		events2.setEventId(2);
		events2.setOrganizer(users2);
		events2.setTitle("hfyg");
		events2.setDescription("skcbhabchchcb");
		events2.setLocation(Location.BENGALURU);
		events2.setEventDate(LocalDate.of(2024, 06, 07));
		events2.setStartTime(LocalTime.of(17, 0));
		events2.setEndTime(LocalTime.of(21, 0));
		events2.setCapacity(700);
		events2.setRegistrationDeadline(LocalDateTime.of(2025, 12, 14, 16, 05));
		events2.setIsPublished(true);
		events2.setCreatedAt(LocalDateTime.of(2025, 04, 30, 16, 05));
		events2.setUpdatedAt(LocalDateTime.of(2025, 04, 30, 16, 05));
		events2.setEventsCategory(EventsCategory.BOOKMEET);

		EventRegistrations eventReg2=new EventRegistrations();
		eventReg2.setRegistrationId(2);
		eventReg2.setUser(users2);
		eventReg2.setEvent(events2);
		eventReg2.setRegistrationStatus(RegistrationStatus.REGISTERED);
		eventReg2.setRegistrationDate(LocalDateTime.of(2025, 12, 14, 16, 05));
		
		eventRegDto2=mapper.map(eventReg2, EventRegistrationsDTO.class);
		eventRegDto2.setEvents(mapper.map(events2, EventsDTO.class));
		eventRegDto2.setUsers(mapper.map(users2, UsersDTO.class));
		
		
		List<EventRegistrations> eventRegList=new ArrayList<EventRegistrations>(List.of(eventReg, eventReg2));
		List<EventRegistrationsDTO> eventRegOutList=new ArrayList<EventRegistrationsDTO>(List.of(eventRegDto, eventRegDto2));
		
		
		Mockito.when(eventRegRepo.findAll()).thenReturn(eventRegList);
		List<EventRegistrationsDTO> eventRegDtoOutList=eventRegService.fetchAllRegistrations();
		Assertions.assertEquals(eventRegOutList, eventRegDtoOutList);
	}
	

	@Test
	public void cancelRegistrationsNoRegistrationFoundTest() {
	    Integer regId = 1;

	    Mockito.when(eventRegRepo.findById(regId)).thenReturn(Optional.empty());

	    LCP_Exception exception = Assertions.assertThrows(
	        LCP_Exception.class,
	        () -> eventRegService.cancelRegistrations(regId)
	    );

	    Assertions.assertEquals("service.EVENT_REGISTRATION_CANCEL", exception.getMessage());
	}

	
	@Test
	public void cancelRegistrationsValidTest() throws LCP_Exception {
		Integer regId=1;
		
		EventRegistrations eventReg=new EventRegistrations();
		eventReg.setRegistrationId(1);
		
		Mockito.when(eventRegRepo.findById(regId)).thenReturn(Optional.of(eventReg));
		eventRegService.cancelRegistrations(regId);
		Mockito.verify(eventRegRepo, times(1)).deleteById(regId);
	}
}