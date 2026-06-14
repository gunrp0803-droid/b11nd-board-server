package com.example.b11ndboard.comment.service;

import com.example.b11ndboard.comment.entity.Comment;
import com.example.b11ndboard.post.entity.Post;
import com.example.b11ndboard.comment.repository.CommentRepository;
import com.example.b11ndboard.post.repository.PostRepository;
import com.example.b11ndboard.commentlike.dto.CommentRequestDto;
import com.example.b11ndboard.commentlike.dto.CommentResponseDto;
import com.example.b11ndboard.commentlike.repository.CommentLikeRepository;
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

    @Transactional
    public Long saveComment(Long boardId, CommentRequestDto dto) {
        Post post = postRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .writer(dto.getWriter())
                .post(post)
                .build();
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequestDto dto, String writer) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다"));

        if (!comment.getWriter().equals(writer)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다");
        }

        comment.update(dto.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId, String writer) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다"));

        if (!comment.getWriter().equals(writer)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponseDto> getComments(Long boardId){
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(boardId);

        return comments.stream()
                .map(comment -> {
                    // 각 댓글마다 DB에서 좋아요 개수를 세어 함께 DTO로 변환
                    long likeCount = commentLikeRepository.countByCommentId(comment.getId());
                    return new CommentResponseDto(comment, likeCount);
                })
                .collect(Collectors.toList());
    }

}
