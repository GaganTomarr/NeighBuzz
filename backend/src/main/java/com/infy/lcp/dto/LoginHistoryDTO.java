package com.infy.lcp.dto;

import java.time.LocalDateTime;

import com.infy.lcp.entity.LoginStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class LoginHistoryDTO {

	@NotNull(message = "{userLogin.loginId.notNull}")
    private Integer loginId;

    @NotNull(message = "{userLogin.userDTO.notNull}")
    @Valid
    private UsersDTO userDTO;

    @NotNull(message = "{userLogin.loginTimestamp.notNull}")
    private LocalDateTime loginTimestamp;

    @NotNull(message = "{userLogin.loginStatus.notNull}")
    private LoginStatus loginStatus;

    @Size(max = 45, message = "{userLogin.ipAddress.size}")
    private String ipAddress;
}
