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
@Table(name="user_activity")
public class UserActivity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="activity_id")
	private Integer activityId;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_id",nullable=false)
	private Users user;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private ActivityType activityType;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private EntityType entityType;
	@Column(nullable=false)
	private Integer entityId;
    @Column(nullable=false)
	private LocalDateTime occurredAt;
//    @Column(columnDefinition="json")
//	private Map<String, Object> metadata;
	//metadata JSON
}
