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
@Table(name="forum_thread_comments")
public class ForumThreadComments {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ft_comment_id")
	private Integer ftCommentId;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="thread_id", nullable=false)
	private ForumThreads thread;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_id", nullable=false)
    private Users user;
	@Column(columnDefinition="Text")
	private String commentText;
	@Column(nullable=false, updatable=false)
	private LocalDateTime createdAt;
	@Column(nullable=false)
	private LocalDateTime updatedAt;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="parent_comment_id" )
	private ForumThreadComments parentComment;

}
