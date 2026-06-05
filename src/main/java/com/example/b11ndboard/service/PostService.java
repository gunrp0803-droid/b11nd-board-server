package com.example.b11ndboard.service;

import com.example.b11ndboard.entity.Post;
import com.example.b11ndboard.entity.PostLike;
import com.example.b11ndboard.repository.PostLikeRepository;
import com.example.b11ndboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public void likePost(Long postId, Long userId) {
        // 1. 해당 게시글이 존재하는지 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 이미 이 유저가 해당 게시글에 좋아요를 눌렀는지 확인
        if (postLikeRepository.existsByUserIdAndPost(userId, post)) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }

        // 3. 처음 누르는 거라면 좋아요 데이터 저장
        PostLike postLike = PostLike.builder()
                .userId(userId)
                .post(post)
                .build();

        postLikeRepository.save(postLike);
    }
}
