package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.ActionType;
import com.infy.lcp.entity.ContentType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ModerationLogDTO {

	@NotNull(message = "{adminLogs.logId.notNull}")
    private Integer logId;

    @NotNull(message = "{adminLogs.admin.notNull}")
    @Valid
    private UsersDTO admin;

    @NotNull(message = "{adminLogs.contentType.notNull}")
    private ContentType contentType;

    @NotNull(message = "{adminLogs.contentId.notNull}")
    private Integer contentId;

    @NotNull(message = "{adminLogs.actionType.notNull}")
    private ActionType actionType;

    // Optional in entity, but generally useful to validate
    @NotBlank(message = "Reason must not be blank")
    private String reason;

    @NotNull(message = "Action date must not be null")
    private LocalDateTime actionDate;
}
