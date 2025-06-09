package com.infy.lcp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="forum_threads")
public class ForumThreads {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="thread_id")
	private Integer threadId;
	@ManyToOne
	@JoinColumn(name="forum_id" )
	private Forum forum;
	@Column(nullable=false)
	private String title;
	@ManyToOne
	@JoinColumn(name="creator_id", nullable=false)
	private Users creator;
	@Enumerated(EnumType.STRING)
	private ForumCategory forumCategory;
	@Column(nullable=false, updatable=false)
	private LocalDateTime createdAt;
	@Column(nullable=false)
	private LocalDateTime updatedAt;
	private Boolean isPinned;
	private Boolean isLocked;
	private Integer viewCount;
	@Lob
	private String featuredImage;
	@Column(columnDefinition = "Text", nullable = false)
	private String description;
}
