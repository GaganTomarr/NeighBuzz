package com.infy.lcp.dto;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForumThreadCommentsDTO {

	@NotNull(message = "{forumComment.ftCommentId.notNull}")
    private Integer ftCommentId;

    @NotNull(message = "{forumComment.thread.notNull}")
    @Valid
    private ForumThreadsDTO thread;

    @NotNull(message = "{forumComment.users.notNull}")
    @Valid
    private UsersDTO users;

    @NotBlank(message = "{forumComment.commentText.notBlank}")
    private String commentText;

    @NotNull(message = "{forumComment.createdAt.notNull}")
    private LocalDateTime createdAt;

    @NotNull(message = "{forumComment.updatedAt.notNull}")
    private LocalDateTime updatedAt;

    // Optional: No validation message needed unless made required
    private ForumThreadCommentsDTO parentComment;
}
