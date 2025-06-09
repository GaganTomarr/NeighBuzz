package com.infy.lcp.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.infy.lcp.entity.Users;

public class CutomUserDetails implements UserDetails{
	private final Users user;
	public CutomUserDetails(Users user) {
		this.user = user;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(Boolean.TRUE.equals(user.getIsAdmin())) {
			return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		else {
			return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
		}
	}
	@Override
	public String getPassword() {
		return user.getPasswordHash();
	}
	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public Integer getUserId() {
		return user.getUserId();
	}
	
	public Users getUser() {
		return user;
	}
}
