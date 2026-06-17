package com.example.b11ndboard.commentlike.controller;

import com.example.b11ndboard.auth.security.MemberDetails;
import com.example.b11ndboard.commentlike.User.CustomUser;
import com.example.b11ndboard.commentlike.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts/comments")
@RequiredArgsConstructor
public class CommentLikeApiController {

    private final CommentLikeService commentLikeService;

    //댓 좋아요 취소 api
    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long commentId,
                                             @AuthenticationPrincipal MemberDetails memberDetails) { //2. 타입을 MemberDetails로 변경!


        Long userId = memberDetails.getUserId();

        boolean isLiked = commentLikeService.toggleCommentLike(commentId, userId);
        return ResponseEntity.ok(isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.");
    }

}
