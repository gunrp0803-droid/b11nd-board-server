package com.example.b11ndboard.domain.comment.service;

import com.example.b11ndboard.domain.comment.dto.request.CommentRequestDto;
import com.example.b11ndboard.domain.comment.dto.response.CommentResponseDto;
import com.example.b11ndboard.domain.comment.entity.Comment;
import com.example.b11ndboard.domain.comment.repository.CommentLikeRepository;
import com.example.b11ndboard.domain.comment.repository.CommentRepository;
import com.example.b11ndboard.domain.post.entity.Post;
import com.example.b11ndboard.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException; // 필수 import

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public void saveComment(Long postId, CommentRequestDto dto, Long userId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .post(post)
                .writer(username)
                .userId(userId)
                .build();

        commentRepository.save(comment);
    }

    public List<CommentResponseDto> getComments(Long postId, Long currentUserId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> {
                    long likeCount = commentLikeRepository.countByCommentId(comment.getId());
                    boolean isWriter = (currentUserId != null && comment.getUserId().equals(currentUserId));
                    boolean isLiked = (currentUserId != null && commentLikeRepository.existsByUserIdAndCommentId(currentUserId, comment.getId()));

                    return new CommentResponseDto(comment, likeCount, isWriter, isLiked);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Long commentId, Long currentUserId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (!comment.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        comment.updateContent(newContent);
    }

    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (!comment.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}