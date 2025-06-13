package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.NotificationsType;
import com.infy.lcp.entity.RelatedEntityType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class NotificationsDTO {

	@NotNull(message = "{notifications.notificationId.notNull}")
    private Integer notificationId;

    @NotNull(message = "{notifications.userDTO.notNull}")
    @Valid
    private UsersDTO userDTO;

    @NotNull(message = "{notifications.notificationsType.notNull}")
    private NotificationsType notificationsType;

    @NotBlank(message = "{notifications.content.notBlank}")
    @Size(max = 1000, message = "{notifications.content.size}")
    private String content;

    private RelatedEntityType relatedEntityType;

    private Integer relatedEntityId;

    @NotNull(message = "{notifications.isRead.notNull}")
    private Boolean isRead;

    @NotNull(message = "{notifications.createdAt.notNull}")
    private LocalDateTime createdAt;

    private Integer postId;

}
