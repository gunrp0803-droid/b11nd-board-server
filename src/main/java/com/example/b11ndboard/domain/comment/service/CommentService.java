package com.example.b11ndboard.domain.comment.service;

import com.example.b11ndboard.domain.comment.entity.Comment;
import com.example.b11ndboard.domain.post.entity.Post;
import com.example.b11ndboard.domain.comment.repository.CommentRepository;
import com.example.b11ndboard.domain.comment.dto.request.CommentRequestDto;
import com.example.b11ndboard.domain.comment.dto.response.CommentResponseDto;
import com.example.b11ndboard.domain.comment.repository.CommentLikeRepository;
import com.example.b11ndboard.global.exception.ErrorCode;
import com.example.b11ndboard.domain.post.exception.PostException;
import com.example.b11ndboard.domain.post.repository.PostRepository;
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
    public void updateComment(Long commentId, Long userId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostException(ErrorCode.COMMENT_NOT_FOUND));

        if (!java.util.Objects.equals(comment.getUserId(), userId)) {
            throw new PostException(ErrorCode.COMMENT_UPDATE_FORBIDDEN);
        }

        comment.updateContent(newContent);
    }

    // 3. 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostException(ErrorCode.COMMENT_NOT_FOUND));

        if (!java.util.Objects.equals(comment.getUserId(), userId)) {
            throw new PostException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }

        commentRepository.delete(comment);
    }

    // 4. 댓글 목록 조회
    public List<CommentResponseDto> getComments(Long postId) {
        return getComments(postId, null);
    }

    public List<CommentResponseDto> getComments(Long postId, Long userId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> {
                    long likeCount = commentLikeRepository.countByCommentId(comment.getId());
                    boolean isWriter = (userId != null) && comment.getUserId().equals(userId);
                    boolean liked = (userId != null) && commentLikeRepository.existsByUserIdAndCommentId(userId, comment.getId());
                    return new CommentResponseDto(comment, likeCount, isWriter, liked);
                })
                .collect(Collectors.toList());
    }
}
