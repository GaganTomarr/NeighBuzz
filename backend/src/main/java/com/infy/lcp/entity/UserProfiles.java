package com.infy.lcp.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="user_profiles")
@Data
public class UserProfiles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="profile_id")
 private int profileId;
	@OneToOne
	@JoinColumn(name="user_id", unique=true, nullable=false)
 private Users user;
	@Column(length=100)
 private String displayName;
 private String profilePicture;
 @Column(columnDefinition="Bio")
 private String bio;
 @Column(length=100)
 private String location;
 @Enumerated(EnumType.STRING)
 @Column(nullable=false)
 private ProfileVisibility profileVisibility;
 
 @Enumerated(EnumType.STRING)
 @Column(nullable=false)
 private ContactType contactType;
}
