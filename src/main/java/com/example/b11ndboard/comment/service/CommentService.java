package com.example.b11ndboard.comment.service;

import com.example.b11ndboard.comment.dto.request.CommentRequest;
import com.example.b11ndboard.comment.dto.response.CommentResponse;
import com.example.b11ndboard.comment.entity.Comment;
import com.example.b11ndboard.comment.repository.CommentRepository;
import com.example.b11ndboard.commentlike.repository.CommentLikeRepository;
import com.example.b11ndboard.post.entity.Post;
import com.example.b11ndboard.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository PostRepository;
    //private final BoardRepository boardRepository; 게시글 확인용

    //댓 등록

    @Transactional
    public Long saveComment(Long postId , CommentRequest dto){
        Post post = PostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 개시글은 존재하지 않습니다"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .writer(dto.getWriter())
                .post(post)
                .build();
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void updateComment(Long commentId, String currentWriter, String newContent) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!java.util.Objects.equals(comment.getWriter(), currentWriter)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        // 댓글 수정 실행 (JPA 변경 감지 작동)
        comment.updateContent(newContent);
    }

    @Transactional
    public void deleteComment(Long commentId, String currentWriter) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!java.util.Objects.equals(comment.getWriter(), currentWriter)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        // DB에서 삭제
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> {
                    Long likecount = commentLikeRepository.countByCommentId(comment.getId());

                    return new CommentResponse(comment,likecount);
                }).toList();
    }

}
