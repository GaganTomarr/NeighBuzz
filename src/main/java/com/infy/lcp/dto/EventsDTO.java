package com.infy.lcp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.infy.lcp.entity.EventsCategory;
import com.infy.lcp.entity.Location;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class EventsDTO {
	private Integer eventId;

    @NotNull(message = "{events.organizer.notNull}")
    @Valid
    private UsersDTO organizer;

    @NotBlank(message = "{events.title.notBlank}")
    @Size(max = 255, message = "{events.title.size}")
    private String title;

    @NotBlank(message = "{events.description.notBlank}")
    private String description;

    private Location location;

    @NotNull(message = "{events.eventDate.notNull}")
    @FutureOrPresent(message = "{events.eventDate.futureOrPresent}")
    private LocalDate eventDate;

    @NotNull(message = "{events.startTime.notNull}")
    private LocalTime startTime;

    private LocalTime endTime;

    @Positive(message = "{events.capacity.positive}")
    private Integer capacity;

    private LocalDateTime registrationDeadline;

    private Boolean isPublished;

    @NotNull(message = "{events.createdAt.notNull}")
    private LocalDateTime createdAt;

    @NotNull(message = "{events.updatedAt.notNull}")
    private LocalDateTime updatedAt;

    private EventsCategory eventsCategory;

    private String featuredImage;

}
