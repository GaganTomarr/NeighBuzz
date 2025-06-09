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
@Table(name="login_history")
@Data
public class LoginHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="login_id")
	private Integer loginId;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_id", nullable=false)
	private Users user;
	@Column(nullable=false, updatable=false)
	private LocalDateTime loginTimestamp;
	@Enumerated(EnumType.STRING)
	 @Column(nullable=false)
	private LoginStatus loginStatus;
	@Column(length=45)
	private String ipAddress;
}
