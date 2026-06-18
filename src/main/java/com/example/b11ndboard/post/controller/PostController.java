package com.example.b11ndboard.post.controller;

import com.example.b11ndboard.auth.security.MemberDetails;
import com.example.b11ndboard.post.dto.PostRequestDto;
import com.example.b11ndboard.post.dto.PostResponseDto;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import com.example.b11ndboard.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 1. 게시글 작성 API (C) - JWT 연동
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody PostRequestDto requestDto) {

        PostResponseDto response = postService.createPost(requestDto, memberDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("게시글이 성공적으로 작성되었습니다.", ResponseKind.POST_CREATE, response));
    }

    // 2. 전체 게시글 목록 조회 API (R) - 페이징 처리 반영 완료
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponseDto>>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        Page<PostResponseDto> response = postService.getAllPosts(page);
        return ResponseEntity.ok(
                ApiResponse.ok("전체 게시글 목록 조회 (페이지: " + page + ")", ResponseKind.POST_GET_ALL, response)
        );
    }

    // 3. 게시글 상세 조회 API (R) - 좋아요 총 개수 및 내 좋아요 여부 포함
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        Long userId = (memberDetails != null) ? memberDetails.getUserId() : null;
        PostResponseDto response = postService.getPost(postId, userId);
        return ResponseEntity.ok(
                ApiResponse.ok("게시글 상세 조회", ResponseKind.POST_GET_DETAIL, response)
        );
    }

    // 4. 게시글 수정 API (U)
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody PostRequestDto requestDto) {

        if (memberDetails == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("로그인이 필요합니다.", ResponseKind.VALIDATION_ERROR));
        }

        PostResponseDto response = postService.updatePost(postId, requestDto, memberDetails.getUserId());
        return ResponseEntity.ok(
                ApiResponse.ok("게시글이 성공적으로 수정되었습니다.", ResponseKind.POST_UPDATE, response)
        );
    }

    // 5. 게시글 삭제 API (D)
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        if (memberDetails == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("로그인이 필요합니다.", ResponseKind.VALIDATION_ERROR));
        }

        postService.deletePost(postId, memberDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("게시글이 성공적으로 삭제되었습니다.", ResponseKind.POST_DELETE, null));
    }

    // 6. 특정 게시글에 좋아요 누르기 API
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        postService.likePost(postId, memberDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("좋아요 반영", ResponseKind.POST_LIKE, null));
    }
    // 7. 게시글 좋아요 취소 api
    @DeleteMapping("/{postId}/likes") // 주소 경로(/{postId}/likes)를 추가했습니다.
    public ResponseEntity<ApiResponse<Void>> cancelLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        // 파라미터를 Long 타입(getUserId())으로 맞춰 에러를 해결합니다.
        postService.cancelPostLike(postId, memberDetails.getUserId());

        // 기존의 일관된 API 응답 규격(ApiResponse) 형태로 반환합니다.
        return ResponseEntity.ok(ApiResponse.ok("좋아요가 취소되었습니다.", ResponseKind.POST_LIKE_CANCEL, null));
    }
}