package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.Location;
import com.infy.lcp.entity.NewsCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
//myslab42s08:3000/Jan25-SET-Interns1-CS-Java-React-A/group-16-backend.git
import lombok.Data;

@Data
public class NewsPostsDTO {
	@NotNull(message = "{newsPosts.postId.notNull}")
    private Integer postId;

    @NotNull(message = "{newsPosts.author.notNull}")
    @Valid
    private UsersDTO author;

    @NotBlank(message = "{newsPosts.title.notBlank}")
    @Size(max = 255, message = "{newsPosts.title.size}")
    private String title;

    @NotBlank(message = "{newsPosts.content.notBlank}")
    private String content;

    private String featuredImage;

    private String excerpt;

    @NotNull(message = "{newsPosts.createdAt.notNull}")
    private LocalDateTime createdAt;

    @NotNull(message = "{newsPosts.updatedAt.notNull}")
    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;
    
    private Location location;

    private Boolean isApproved;

    @Valid
    private UsersDTO approvalUser;

    private NewsCategory newsCategory;
}