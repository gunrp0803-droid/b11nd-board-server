package com.example.b11ndboard.comment.controller;

import com.example.b11ndboard.auth.security.MemberDetails;
import com.example.b11ndboard.comment.service.CommentService;
import com.example.b11ndboard.commentlike.dto.CommentRequestDto;
import com.example.b11ndboard.commentlike.dto.CommentResponseDto;
import com.example.b11ndboard.global.common.ApiResponse;
import com.example.b11ndboard.global.common.ResponseKind;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;

    //댓글 api
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<ApiResponse<Long>> createComment(@PathVariable Long boardId,
                                                @RequestBody CommentRequestDto dto,
                                                @AuthenticationPrincipal MemberDetails memberDetails){
        // writer를 요청 DTO가 아닌 인증된 사용자 정보에서 가져오도록 리팩토링할 수 있으나, 
        // 현재 CommentRequestDto에 writer가 포함되어 있으므로 일단 유지하되 
        // 보안상 AuthenticationPrincipal을 사용하는 것이 좋습니다.
        Long commentId = commentService.saveComment(boardId, dto);
        return ResponseEntity.ok(ApiResponse.ok("댓글이 등록되었습니다", ResponseKind.VALIDATION_ERROR, commentId));
    }

    //댓글 수정 api
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(@PathVariable Long commentId,
                                                           @RequestBody CommentRequestDto dto,
                                                           @AuthenticationPrincipal MemberDetails memberDetails) {
        commentService.updateComment(commentId, dto, memberDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("댓글이 수정되었습니다", ResponseKind.VALIDATION_ERROR, null));
    }

    //댓글 삭제 api
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId,
                                                           @AuthenticationPrincipal MemberDetails memberDetails) {
        commentService.deleteComment(commentId, memberDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("댓글이 삭제되었습니다", ResponseKind.VALIDATION_ERROR, null));
    }

    //댓글 목록 조회 api
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getCommentList(@PathVariable Long boardId){
        List<CommentResponseDto> comments = commentService.getComments(boardId);
        return ResponseEntity.ok(ApiResponse.ok("댓글 목록 조회 성공", ResponseKind.VALIDATION_ERROR, comments));
    }
}
