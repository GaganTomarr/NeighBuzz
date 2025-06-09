package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.ForumCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ForumThreadsDTO {

	@NotNull(message = "{forumThreads.threadId.notNull}")
    private Integer threadId;

    @Valid
    private ForumDTO forum; // Optional unless explicitly required

    @NotBlank(message = "{forumThreads.title.notBlank}")
    private String title;

    @NotNull(message = "{forumThreads.users.notNull}")
    @Valid
    private UsersDTO users;

    private ForumCategory forumCategory; // Optional

    @NotNull(message = "{forumThreads.createdAt.notNull}")
    private LocalDateTime createdAt;

    @NotNull(message = "{forumThreads.updatedAt.notNull}")
    private LocalDateTime updatedAt;

    private Boolean isPinned;
    private Boolean isLocked;

    private Integer viewCount;

    private String featuredImage;

    @NotBlank(message = "{forumThreads.description.notBlank}")
    private String description;
}