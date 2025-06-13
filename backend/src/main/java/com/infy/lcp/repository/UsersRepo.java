package com.infy.lcp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.lcp.entity.Users;

public interface UsersRepo extends JpaRepository<Users, Integer>{

	Optional<Users> findByUsername(String username);
	Optional<Users> findByEmail(String email);
	public boolean existsByEmail(String email);
	public boolean existsByUsername(String username);
	
}
