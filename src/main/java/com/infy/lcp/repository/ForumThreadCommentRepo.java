package com.infy.lcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.lcp.entity.ForumThreadComments;

public interface ForumThreadCommentRepo extends JpaRepository<ForumThreadComments, Integer>{

	List<ForumThreadComments> findByParentComment(ForumThreadComments parent);
}
