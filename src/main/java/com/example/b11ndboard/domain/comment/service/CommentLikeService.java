package com.example.b11ndboard.domain.comment.service;


import com.example.b11ndboard.domain.auth.entity.Users;
import com.example.b11ndboard.domain.auth.repository.UsersRepository;
import com.example.b11ndboard.domain.comment.entity.Comment;
import com.example.b11ndboard.domain.comment.repository.CommentRepository;
import com.example.b11ndboard.domain.comment.entity.CommentLike;
import com.example.b11ndboard.domain.comment.repository.CommentLikeRepository;
import com.example.b11ndboard.global.exception.ErrorCode;
import com.example.b11ndboard.domain.post.exception.PostException;
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
                .orElseThrow(() -> new PostException(ErrorCode.COMMENT_NOT_FOUND));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new PostException(ErrorCode.LOGIN_FAILED));

        Optional<CommentLike> alreadyLike = commentLikeRepository.findByUserIdAndCommentId(userId,commentId);

        if (alreadyLike.isPresent()){
            commentLikeRepository.delete(alreadyLike.get());
            return false;
        } else{

            CommentLike commentLike = new CommentLike(user,comment);
            commentLikeRepository.save(commentLike);
            return true;
        }

    }

}
