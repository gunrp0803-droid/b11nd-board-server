package com.example.b11ndboard.comment.repository;

import com.example.b11ndboard.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.b11ndboard.post.entity.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> { //jpa repository 를 상속받아 기본매서드 자동 생성
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId); //해당 id 의 게시글 댓글을 list 로 변환한다.
}
