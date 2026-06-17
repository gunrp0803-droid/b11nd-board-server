package com.example.b11ndboard.comment.controller;


import com.example.b11ndboard.auth.security.MemberDetails;
import com.example.b11ndboard.comment.dto.request.CommentRequest;
import com.example.b11ndboard.comment.dto.response.CommentResponse;
import com.example.b11ndboard.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;

    //댓글 api
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> createComment(@PathVariable Long postId,
                                                @RequestBody CommentRequest dto) {
        commentService.saveComment(postId, dto);
        return ResponseEntity.ok("댓글이 등록 되었습니다");
    }

    //댓글 목록 조회 api
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentList(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getComments(postId);
        return ResponseEntity.ok(comments);
    }

    //컨트롤러 수정 예시
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest requestDto,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        String currentWriter = memberDetails.getUsername();

        commentService.updateComment(commentId, currentWriter, requestDto.getContent());
        return ResponseEntity.ok("댓글 수정되었습니다");
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        String currentWriter = memberDetails.getUsername();

        commentService.deleteComment(commentId, currentWriter);
        return ResponseEntity.ok("댓글 삭제되었습니다");
    }
}
