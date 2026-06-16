package com.example.b11ndboard.comment.controller;


import com.example.b11ndboard.auth.security.MemberDetails;
import com.example.b11ndboard.comment.service.CommentService;
import com.example.b11ndboard.commentlike.dto.CommentRequestDto;
import com.example.b11ndboard.commentlike.dto.CommentResponseDto;
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
    public ResponseEntity<String> createComment(@PathVariable Long boardId,
                                                @RequestBody CommentRequestDto dto) {
        commentService.saveComment(boardId, dto);
        return ResponseEntity.ok("댓글이 등록 되었습니다");
    }

    //댓글 목록 조회 api
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentList(@PathVariable Long boardId) {
        List<CommentResponseDto> comments = commentService.getComments(boardId);
        return ResponseEntity.ok(comments);
    }

    // 💡 컨트롤러 수정 예시
    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        String currentWriter = memberDetails.getUsername();

        commentService.updateComment(commentId, currentWriter, requestDto.getContent());
        return ResponseEntity.ok("댓글 수정되었습니다");
    }
}
