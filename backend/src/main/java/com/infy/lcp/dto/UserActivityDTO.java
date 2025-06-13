package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.ActivityType;
import com.infy.lcp.entity.EntityType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class UserActivityDTO {

	 @NotNull(message = "{userActivity.activityId.notNull}")
	    private Integer activityId;

	    @NotNull(message = "{userActivity.users.notNull}")
	    @Valid
	    private UsersDTO users;

	    @NotNull(message = "{userActivity.activityType.notNull}")
	    private ActivityType activityType;

	    @NotNull(message = "{userActivity.entityType.notNull}")
	    private EntityType entityType;

	    @NotNull(message = "{userActivity.entityId.notNull}")
	    @Positive(message = "{userActivity.entityId.positive}")
	    private Integer entityId;

	    @NotNull(message = "{userActivity.occurredAt.notNull}")
	    private LocalDateTime occurredAt;
    // No validation for metadata as it's commented out in both the DTO and Entity classes.
}
