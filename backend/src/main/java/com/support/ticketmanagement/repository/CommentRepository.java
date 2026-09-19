package com.support.ticketmanagement.repository;

import com.support.ticketmanagement.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
