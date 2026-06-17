package com.example.b11ndboard.post.repository;

import com.example.b11ndboard.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // 스프링이 이 레포지토리를 인식하도록 붙여줍니다.
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> id(Long id);
}