package com.infy.lcp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="users")
@Data
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Integer userId;
	@Column(nullable=false, unique=true)
	private String email;
	@Column(nullable=false, unique=true, length=50)
	private String username;
	@Column(nullable=false)
	private String passwordHash;
	@Column(nullable=false, updatable=false)
	private LocalDateTime registrationDate;
	private LocalDateTime lastLogin;
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	@Column(nullable=false)
	private Boolean isAdmin;
	
}
