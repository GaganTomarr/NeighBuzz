package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.ForumCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForumDTO {
	 @NotNull(message = "{forum.forumId.notNull}")
	    private Integer forumId;

	    @NotNull(message = "{forum.users.notNull}")
	    @Valid
	    private UsersDTO users;

	    @NotBlank(message = "{forum.forumDescription.notBlank}")
	    private String forumDescription;

	    @NotNull(message = "{forum.createdAt.notNull}")
	    private LocalDateTime createdAt;

	    @NotNull(message = "{forum.updatedAt.notNull}")
	    private LocalDateTime updatedAt;

	    // Optional: Add message only if you plan to validate it
	    private ForumCategory forumCategory;
}