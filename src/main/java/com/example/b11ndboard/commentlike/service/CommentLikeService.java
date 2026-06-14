package com.example.b11ndboard.commentlike.service;


import com.example.b11ndboard.auth.entity.Users;
import com.example.b11ndboard.auth.repository.UsersRepository;
import com.example.b11ndboard.comment.entity.Comment;
import com.example.b11ndboard.comment.repository.CommentRepository;
import com.example.b11ndboard.commentlike.entity.CommentLike;
import com.example.b11ndboard.commentlike.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UsersRepository usersRepository;

    //댓글 좋아요 토글
    @Transactional
    public boolean toggleCommentLike(Long commentId,Long userId){

        //이미 존재하는 댓글&유저 검증
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글은 존재하지 않습니다"));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다"));

        Optional<CommentLike> alreadyLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);

        if (alreadyLike.isPresent()){
            commentLikeRepository.delete(alreadyLike.get());
            return false;
        } else{
            CommentLike commentLike = CommentLike.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(commentLike);
            return true;
        }

    }

}
