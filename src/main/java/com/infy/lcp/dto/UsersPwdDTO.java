package com.infy.lcp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsersPwdDTO {
	    @Email(message = "{login.email.valid}")
	    private String email;

	    
	    private String username;

	    @NotBlank(message = "{login.password.notBlank}")
	    private String password;

	    @Size(min = 8, message = "{login.newPassword.size}")
	    private String newPassword; 

	    
	    private Boolean isAdmin;
}
