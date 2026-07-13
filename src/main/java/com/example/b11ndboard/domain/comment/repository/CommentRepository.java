package com.example.b11ndboard.domain.comment.repository;

import com.example.b11ndboard.domain.comment.entity.Comment;
import com.example.b11ndboard.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    long countByPostId(Long postId);

    @Query("select c.post.id, count(c) from Comment c where c.post in :posts group by c.post.id")
    List<Object[]> countCommentsByPosts(@Param("posts") List<Post> posts);
}
