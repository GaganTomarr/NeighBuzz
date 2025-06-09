package com.infy.lcp.dto;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewsPostCommentsDTO {

	@NotNull(message = "{newsPostComments.commentId.notNull}")
    private Integer commentId;

    @NotNull(message = "{newsPostComments.newsPosts.notNull}")
    @Valid
    private NewsPostsDTO newsPosts;

    @NotNull(message = "{newsPostComments.userDTO.notNull}")
    @Valid
    private UsersDTO userDTO;

    @NotBlank(message = "{newsPostComments.commentText.notBlank}")
    @Size(max = 2000, message = "{newsPostComments.commentText.size}")
    private String commentText;

    @NotNull(message = "{newsPostComments.createdAt.notNull}")
    private LocalDateTime createdAt;

    @NotNull(message = "{newsPostComments.updatedAt.notNull}")
    private LocalDateTime updatedAt;

    private NewsPostCommentsDTO parentComment;

    @NotNull(message = "{newsPostComments.isApproved.notNull}")
    private Boolean isApproved;

    private List<NewsPostCommentsDTO> replies;

}