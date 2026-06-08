package com.example.b11ndboard.service;

import com.example.b11ndboard.dto.PostRequestDto;
import com.example.b11ndboard.dto.PostResponseDto;
import com.example.b11ndboard.entity.Post;
import com.example.b11ndboard.entity.PostLike;
import com.example.b11ndboard.repository.PostLikeRepository;
import com.example.b11ndboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, Long userId) {
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .userId(userId)
                .build();

        Post savedPost = postRepository.save(post);
        return new PostResponseDto(savedPost);
    }

    // 2. 전체 게시글 목록 조회
    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

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
