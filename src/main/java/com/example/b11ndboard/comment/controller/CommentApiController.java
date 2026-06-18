package com.example.b11ndboard.comment.controller;


import com.example.b11ndboard.auth.security.MemberDetails;
import com.example.b11ndboard.comment.service.CommentService;
import com.example.b11ndboard.commentlike.dto.CommentRequestDto;
import com.example.b11ndboard.commentlike.dto.CommentResponseDto;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;

    // 1. 댓글 작성 API
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody CommentRequestDto dto) {

        if (memberDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("로그인이 필요합니다.", ResponseKind.VALIDATION_ERROR));
        }

        commentService.saveComment(postId, dto, memberDetails.getUserId(), memberDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("댓글이 등록되었습니다.", ResponseKind.COMMENT_CREATE, null));
    }

    // 2. 댓글 목록 조회 API
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getCommentList(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getComments(postId);
        return ResponseEntity.ok(ApiResponse.ok("댓글 목록 조회 성공", ResponseKind.COMMENT_GET_ALL, comments));
    }

    // 3. 댓글 수정 API
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        if (memberDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("로그인이 필요합니다.", ResponseKind.VALIDATION_ERROR));
        }

        commentService.updateComment(commentId, memberDetails.getUsername(), requestDto.getContent());
        return ResponseEntity.ok(ApiResponse.ok("댓글이 수정되었습니다.", ResponseKind.COMMENT_UPDATE, null));
    }

    // 4. 댓글 삭제 API
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        if (memberDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("로그인이 필요합니다.", ResponseKind.VALIDATION_ERROR));
        }

        commentService.deleteComment(commentId, memberDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("댓글이 삭제되었습니다.", ResponseKind.COMMENT_DELETE, null));
    }

    // 5. 댓글 좋아요 toggle API
    @PostMapping("/comments/{commentId}/likes")
    public ResponseEntity<ApiResponse<Void>> toggleLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal MemberDetails memberDetails) {

        if (memberDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("로그인이 필요합니다.", ResponseKind.VALIDATION_ERROR));
        }

        boolean isLiked = commentService.toggleCommentLike(commentId, memberDetails.getUserId());
        String message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
        return ResponseEntity.ok(ApiResponse.ok(message, ResponseKind.COMMENT_CREATE, null));
    }
}
