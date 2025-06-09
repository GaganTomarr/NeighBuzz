package com.infy.lcp.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.infy.lcp.entity.Users;
import com.infy.lcp.repository.UsersRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter{
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UsersRepo usersRepo;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException { 
		final String authHeader = request.getHeader("Authorization");
		Integer userId = null;
		String jwt = null;
		if(authHeader != null && authHeader.startsWith("Bearer")) {
			jwt = authHeader.substring(7);
			userId = jwtUtil.extractUserId(jwt);
		}
		if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			Optional<Users> user = usersRepo.findById(userId);
			if(user.isPresent()) {
				CutomUserDetails userDetails = new CutomUserDetails(user.get());
				if(jwtUtil.validateToken(jwt)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken
							(userDetails,null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
					}
			}
		}
		filterChain.doFilter(request, response);
	}
}

