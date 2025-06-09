package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.infy.lcp.entity.NewsPostComments;

public interface NewsPostCommentsRepo extends JpaRepository<NewsPostComments, Integer> {

    @Query("SELECT c FROM NewsPostComments c WHERE c.newsPosts.postId = ?1")
    List<NewsPostComments> findByContentId(Integer contentId);

    List<NewsPostComments> findByParentComment(NewsPostComments parent);
}

