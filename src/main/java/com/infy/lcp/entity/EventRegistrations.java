package com.infy.lcp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="event_registrations")
public class EventRegistrations {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="registration_id")
	private Integer registrationId;
	@ManyToOne
	@JoinColumn(name="event_id", nullable=false)
	private Events event;
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private Users user;
	@Enumerated(EnumType.STRING)
	private RegistrationStatus registrationStatus;
	@Column(nullable=false, updatable=false)
	private LocalDateTime registrationDate;
	private LocalDateTime cancellationDate;
}
