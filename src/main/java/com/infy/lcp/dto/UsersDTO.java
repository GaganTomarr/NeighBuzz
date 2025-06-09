package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.AccountStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UsersDTO {

	@NotNull(message = "{users.userId.notNull}")
    private Integer userId;

    @NotBlank(message = "{users.email.notBlank}")
    @Email(message = "{users.email.valid}")
    private String email;

    @NotBlank(message = "{users.username.notBlank}")
    @Size(max = 50, message = "{users.username.size}")
    private String username;

    @NotNull(message = "{users.registrationDate.notNull}")
    private LocalDateTime registrationDate;

    private LocalDateTime lastLogin;

    @NotNull(message = "{users.accountStatus.notNull}")
    private AccountStatus accountStatus;

    @NotNull(message = "{users.isAdmin.notNull}")
    private Boolean isAdmin;
}