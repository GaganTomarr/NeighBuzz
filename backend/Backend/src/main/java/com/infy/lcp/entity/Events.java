package com.infy.lcp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="events")
public class Events {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="event_id")
	private Integer eventId;
	@ManyToOne
	@JoinColumn(name="organizer_id", nullable=false)
	private Users organizer;
	@Column(nullable=false)
	private String title;
	@Column(columnDefinition="Text", nullable=false)
	private String description;//text
	@Enumerated(EnumType.STRING)
	private Location location;
	@Column(nullable=false)
	private LocalDate eventDate;
	@Column(nullable=false)
	private LocalTime startTime;
	private LocalTime endTime;
	private Integer capacity;
	private LocalDateTime registrationDeadline;
	private Boolean isPublished;
	@Column(nullable=false, updatable=false)
	private LocalDateTime createdAt;
	@Column(nullable=false)
	private LocalDateTime updatedAt;
	@Enumerated(EnumType.STRING)
	private EventsCategory eventsCategory;
	@Lob
	private String featuredImage;
}
