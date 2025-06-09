package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.Forum;
import com.infy.lcp.entity.ForumCategory;

public interface ForumRepo extends JpaRepository<Forum, Integer> {

	@Query("Select f from Forum f where f.forumCategory=?1")
	public List<Forum> findByCategory(ForumCategory forumCategory);

}
