package com.example.b11ndboard.comment.repository;

import com.example.b11ndboard.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findbyBoardIdOrderByCreatedAtAsc(Long boardId);
}
