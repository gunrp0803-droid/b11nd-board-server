package com.example.b11ndboard.controller;

import com.example.b11ndboard.auth.security.MemberDetails;
import com.example.b11ndboard.dto.PostRequestDto;
import com.example.b11ndboard.dto.PostResponseDto;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import com.example.b11ndboard.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 1. 게시글 작성 API (C) - JWT 연동
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody PostRequestDto requestDto) {

        PostResponseDto response = postService.createPost(requestDto, memberDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("게시글이 성공적으로 작성되었습니다.", ResponseKind.VALIDATION_ERROR, response));
    }

    // 2. 전체 게시글 목록 조회 API (R) - 페이징 처리 반영 완료
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponseDto>>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        Page<PostResponseDto> response = postService.getAllPosts(page);
        return ResponseEntity.ok(
                ApiResponse.ok("전체 게시글 목록 조회 (페이지: " + page + ")", ResponseKind.VALIDATION_ERROR, response)
        );
    }

    // 3. 게시글 상세 조회 API (R) - 좋아요 총 개수 및 내 좋아요 여부 포함
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        PostResponseDto response = postService.getPost(postId, memberDetails.getUserId());
        return ResponseEntity.ok(
                ApiResponse.ok("게시글 상세 조회", ResponseKind.VALIDATION_ERROR, response)
        );
    }

    // 4. 게시글 수정 API (U)
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody PostRequestDto requestDto) {

        PostResponseDto response = postService.updatePost(postId, requestDto, memberDetails.getUserId());
        return ResponseEntity.ok(
                ApiResponse.ok("게시글이 성공적으로 수정되었습니다.", ResponseKind.VALIDATION_ERROR, response)
        );
    }

    // 5. 게시글 삭제 API (D)
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        postService.deletePost(postId, memberDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("게시글이 성공적으로 삭제되었습니다.", ResponseKind.VALIDATION_ERROR, null));
    }

    // 6. 특정 게시글에 좋아요 누르기 API
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        postService.likePost(postId, memberDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("좋아요 반영", ResponseKind.VALIDATION_ERROR, null));
    }
}