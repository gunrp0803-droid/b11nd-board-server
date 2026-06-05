package com.example.b11ndboard.controller;

import com.example.b11ndboard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 특정 게시글에 좋아요 누르기 API
    @PostMapping("/{postId}/likes")
    public ResponseEntity<String> likePost(@PathVariable Long postId) {
        //JWT 기능을 완성하기 전까지 임시로 사용할 로그인 유저 ID (1L)
        Long temporaryUserId = 1L;

        postService.likePost(postId, temporaryUserId);

        return ResponseEntity.ok("좋아요 성공");
    }
}