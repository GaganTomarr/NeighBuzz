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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="moderation_log")
public class ModerationLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="log_id")
	private Integer logId;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="admin_id", nullable=false)
	private Users admin;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private ContentType contentType;
	@Column(nullable=false)
	private Integer contentId;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private ActionType actionType;
	@Column(columnDefinition="Text")
	private String reason;
	@Column(nullable=false)
	private LocalDateTime actionDate;
}

