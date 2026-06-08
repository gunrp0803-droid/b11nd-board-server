package com.example.b11ndboard.controller;

import com.example.b11ndboard.dto.PostRequestDto;
import com.example.b11ndboard.dto.PostResponseDto;
import com.example.b11ndboard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 1. 게시글 작성 API
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto requestDto) {
        Long temporaryUserId = 1L; // JWT 연동 전 임시 유저 ID
        PostResponseDto response = postService.createPost(requestDto, temporaryUserId);
        return ResponseEntity.ok(response);
    }

    // 2. 전체 게시글 목록 조회 API
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        List<PostResponseDto> response = postService.getAllPosts();
        return ResponseEntity.ok(response);
    }

    // 특정 게시글에 좋아요 누르기 API
    @PostMapping("/{postId}/likes")
    public ResponseEntity<String> likePost(@PathVariable Long postId) {
        //JWT 기능을 완성하기 전까지 임시로 사용할 로그인 유저 ID (1L)
        Long temporaryUserId = 1L;

        postService.likePost(postId, temporaryUserId);

        return ResponseEntity.ok("좋아요 성공");
    }
}