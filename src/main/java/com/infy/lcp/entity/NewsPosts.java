package com.infy.lcp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
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
@Table(name="news_posts")
@Data
public class NewsPosts {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="post_id")
	private Integer postId;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="author_id", nullable=false)
	private Users author;
	@Column(nullable=false)
	private String title;
	@Column(columnDefinition="Text", nullable=false)
	private String content;	//text
	@Lob
	private String featuredImage;// this is for an image
	private String excerpt;
	@Column(nullable=false, updatable=false)
	private LocalDateTime createdAt;
	@Column(nullable=false)
	private LocalDateTime updatedAt;
	private LocalDateTime publishedAt;
	@Enumerated(EnumType.STRING)
	private Location location;
	private Boolean isApproved;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="approval_user_id")
	private Users approvalUser;
	@Enumerated(EnumType.STRING)
	private NewsCategory newsCategory;

}
