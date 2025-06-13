package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.NewsCategory;
import com.infy.lcp.entity.NewsPosts;

public interface NewsPostRepo extends JpaRepository<NewsPosts, Integer>{
	@Query("select c from NewsPosts c where c.newsCategory=?1 order by c.publishedAt desc")
	List<NewsPosts> findByCategory(NewsCategory cat);
}
