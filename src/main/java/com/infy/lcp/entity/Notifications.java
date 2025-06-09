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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="notifications")
public class Notifications {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="notification_id")
	private Integer notificationId;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private Users user;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false, name="notification_type")
	private NotificationsType notificationsType;
	
	@Column(columnDefinition="Text", nullable=false)
	private String content;//TEXT
	
	@Enumerated(EnumType.STRING)
	private RelatedEntityType relatedEntityType;
	
	private Integer relatedEntityId;
	
	@Column(nullable=false)
	private LocalDateTime createdAt;
	
	private Integer postId;
	
	@Column(name = "is_read", nullable = false)
	private Boolean isRead = false;

}
