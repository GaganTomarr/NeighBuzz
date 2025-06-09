package com.infy.lcp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.entity.ForumThreads;

public interface ForumThreadsRepo extends JpaRepository<ForumThreads, Integer>{
	@Query("Select f from ForumThreads f where f.forumCategory=?1")
	public List<ForumThreads> findByCategory(ForumCategory forumCategory);
	
	@Query("Select f from ForumThreads f where f.createdAt=?1")
	public List<ForumThreads> findByDate(LocalDateTime createdAt);
	
	@Query("select ft from ForumThreads ft where ft.title like (%?1%)")
	public List<ForumThreads> findByTitleContainingIgnoreCase( String keyword);
}
