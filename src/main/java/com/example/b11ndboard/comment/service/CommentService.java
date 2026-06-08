package com.example.b11ndboard.comment.service;

import com.example.b11ndboard.comment.dto.CommentRequestDto;
import com.example.b11ndboard.comment.dto.CommentResponseDto;
import com.example.b11ndboard.comment.entity.Comment;
import com.example.b11ndboard.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    //private final BoardRepository boardRepository; 게시글 확인용

    //댓 등록

    @Transactional
    public Long saveComment(Long boardId ,CommentRequestDto dto){
        Board board = BoardRepository.findbyId(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 개시글은 존재하지 않습니다"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .writer(dto.getWriter())
                .board(board)
                .build();
        return commentRepository.save(comment).getId();
    }

    public List<CommentResponseDto> getComments(Long boardId){
        List<Comment> comments = commentRepository.findbyBoardIdOrderByCreatedAtAsc(boardId);

        return comments.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

}
