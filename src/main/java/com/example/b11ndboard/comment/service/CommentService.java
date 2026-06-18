package com.example.b11ndboard.comment.service;

import com.example.b11ndboard.comment.entity.Comment;
import com.example.b11ndboard.post.entity.Post;
import com.example.b11ndboard.comment.repository.CommentRepository;
import com.example.b11ndboard.commentlike.dto.CommentRequestDto;
import com.example.b11ndboard.commentlike.dto.CommentResponseDto;
import com.example.b11ndboard.commentlike.repository.CommentLikeRepository;
import com.example.b11ndboard.commentlike.service.CommentLikeService;
import com.example.b11ndboard.global.exception.ErrorCode;
import com.example.b11ndboard.post.exception.PostException;
import com.example.b11ndboard.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final CommentLikeService commentLikeService;

    // 1. 댓글 등록
    @Transactional
    public Long saveComment(Long postId, CommentRequestDto dto, Long userId, String writer) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .writer(writer)
                .post(post)
                .userId(userId)
                .build();
        return commentRepository.save(comment).getId();
    }

    // 2. 댓글 수정
    @Transactional
    public void updateComment(Long commentId, String currentWriter, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostException(ErrorCode.COMMENT_NOT_FOUND));

        if (!java.util.Objects.equals(comment.getWriter(), currentWriter)) {
            throw new PostException(ErrorCode.COMMENT_UPDATE_FORBIDDEN);
        }

        comment.updateContent(newContent);
    }

    // 3. 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, String currentWriter) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostException(ErrorCode.COMMENT_NOT_FOUND));

        if (!java.util.Objects.equals(comment.getWriter(), currentWriter)) {
            throw new PostException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }

        commentRepository.delete(comment);
    }

    // 4. 댓글 목록 조회
    public List<CommentResponseDto> getComments(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> {
                    long likeCount = commentLikeRepository.countByCommentId(comment.getId());
                    return new CommentResponseDto(comment, likeCount);
                })
                .collect(Collectors.toList());
    }

    // 5. 댓글 좋아요 toggle
    @Transactional
    public boolean toggleCommentLike(Long commentId, Long userId) {
        return commentLikeService.toggleCommentLike(commentId, userId);
    }
}
