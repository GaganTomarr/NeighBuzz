package com.infy.lcp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="news_post_comments")
public class NewsPostComments {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="comment_id")
	private Integer commentId;
//	@Enumerated(EnumType.STRING)
//	private ContentType contentType;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="content_id", nullable=false )
	private NewsPosts newsPosts;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_id", nullable=false )
	private Users user;
	@Column(columnDefinition="Text")
	private String commentText;//text
	@Column(nullable=false, updatable=false)
	private LocalDateTime createdAt;
	@Column(nullable=false)
	private LocalDateTime updatedAt;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="parent_Comment_id")
    private NewsPostComments parentComment;
	private Boolean isApproved;
}
