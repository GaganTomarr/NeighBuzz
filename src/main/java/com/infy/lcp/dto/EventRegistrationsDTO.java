package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.RegistrationStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventRegistrationsDTO {

	@NotNull(message = "{eventRegistrations.registrationId.notNull}")
	private Integer registrationId;

	@NotNull(message = "{eventRegistrations.events.notNull}")
	@Valid
	private EventsDTO events;

	@NotNull(message = "{eventRegistrations.users.notNull}")
	@Valid
	private UsersDTO users;

	private RegistrationStatus registrationStatus;
	
	@NotNull(message = "{eventRegistrations.registrationDate.notNull}")
	private LocalDateTime registrationDate;
	
	private LocalDateTime cancellationDate;

}
