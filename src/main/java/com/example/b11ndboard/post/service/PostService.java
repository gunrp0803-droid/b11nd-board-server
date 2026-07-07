package com.example.b11ndboard.post.service;

import com.example.b11ndboard.auth.entity.Users;
import com.example.b11ndboard.auth.repository.UsersRepository;
import com.example.b11ndboard.post.dto.PostRequestDto;
import com.example.b11ndboard.post.dto.PostResponseDto;
import com.example.b11ndboard.post.entity.Post;
import com.example.b11ndboard.post.entity.PostLike;
import com.example.b11ndboard.global.exception.ErrorCode;
import com.example.b11ndboard.post.exception.PostException;
import com.example.b11ndboard.post.repository.PostLikeRepository;
import com.example.b11ndboard.post.repository.PostRepository;
import com.example.b11ndboard.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final UsersRepository usersRepository;

    // 1. 게시글 생성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, Long userId) {
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .userId(userId)
                .build();

        Post savedPost = postRepository.save(post);
        String username = usersRepository.findById(userId)
                .map(Users::getUsername)
                .orElse("알 수 없음");
        return new PostResponseDto(savedPost, username);
    }

    // 2. 전체 게시글 목록 조회
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<Long> userIds = posts.stream()
                .map(Post::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> userIdToUsernameMap = usersRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(Users::getId, Users::getUsername));

        return posts.stream()
                .map(post -> {
                    long likeCount = postLikeRepository.countByPost(post);
                    long commentCount = commentRepository.countByPostId(post.getId());
                    String username = userIdToUsernameMap.getOrDefault(post.getUserId(), "알 수 없음");
                    return new PostResponseDto(post, username, likeCount, false, commentCount, false);
                })
                .collect(Collectors.toList());
    }

    // 3. 게시글 상세 조회
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        long likeCount = postLikeRepository.countByPost(post);
        long commentCount = commentRepository.countByPostId(postId);
        String username = usersRepository.findById(post.getUserId())
                .map(Users::getUsername)
                .orElse("알 수 없음");
        return new PostResponseDto(post, username, likeCount, false, commentCount, false);
    }

    // 4. 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 작성자 검증
        if (!post.getUserId().equals(userId)) {
            throw new PostException(ErrorCode.POST_UPDATE_FORBIDDEN);
        }

        post.update(requestDto.getTitle(), requestDto.getContent());
        long likeCount = postLikeRepository.countByPost(post);
        boolean liked = postLikeRepository.existsByUserIdAndPost(userId, post);
        long commentCount = commentRepository.countByPostId(postId);
        String username = usersRepository.findById(post.getUserId())
                .map(Users::getUsername)
                .orElse("알 수 없음");
        return new PostResponseDto(post, username, likeCount, liked, commentCount, true);
    }

    // 5. 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 작성자 검증
        if (!post.getUserId().equals(userId)) {
            throw new PostException(ErrorCode.POST_DELETE_FORBIDDEN);
        }

        postRepository.delete(post);
    }

    // 6. 좋아요 로직
    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        if (postLikeRepository.existsByUserIdAndPost(userId, post)) {
            throw new PostException(ErrorCode.ALREADY_LIKED);
        }

        PostLike postLike = PostLike.builder()
                .userId(userId)
                .post(post)
                .build();

        postLikeRepository.save(postLike);
    }
    public Page<PostResponseDto> getAllPosts(int page){
        return getAllPosts(page, null);
    }

    public Page<PostResponseDto> getAllPosts(int page, Long userId){
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage = postRepository.findAll(pageable);

        List<Long> userIds = postPage.getContent().stream()
                .map(Post::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> userIdToUsernameMap = usersRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(Users::getId, Users::getUsername));

        return postPage.map(post -> {
            long likeCount = postLikeRepository.countByPost(post);
            long commentCount = commentRepository.countByPostId(post.getId());
            boolean liked = (userId != null) && postLikeRepository.existsByUserIdAndPost(userId, post);
            boolean isWriter = (userId != null) && post.getUserId().equals(userId);
            String username = userIdToUsernameMap.getOrDefault(post.getUserId(), "알 수 없음");
            return new PostResponseDto(post, username, likeCount, liked, commentCount, isWriter);
        });
    }
    public PostResponseDto getPost(Long postId, Long userId) {
        // 1. 게시글이 존재하는지 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 2. 이 게시글의 총 좋아요 개수 가져오기
        long likeCount = postLikeRepository.countByPost(post);

        // 3. 현재 로그인한 유저가 이 글에 좋아요를 눌렀는지 여부 가져오기
        boolean liked = (userId != null) && postLikeRepository.existsByUserIdAndPost(userId, post);

        // 4. 댓글 개수 가져오기
        long commentCount = commentRepository.countByPostId(postId);

        // 5. 작성자 이름 가져오기
        String username = usersRepository.findById(post.getUserId())
                .map(Users::getUsername)
                .orElse("알 수 없음");

        // 6. 작성자 판별
        boolean isWriter = (userId != null) && post.getUserId().equals(userId);

        // 7. 확장된 DTO 생성자를 통해 최종 결과 반환
        return new PostResponseDto(post, username, likeCount, liked, commentCount, isWriter);
    }
    // 7. 게시글 좋아요 취소 로직
    @Transactional
    public void cancelPostLike(Long postId, Long userId) {
        // 1. 게시글 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 2. 해당 유저가 이 게시글에 누른 좋아요가 있는지 확인 (기존 Users -> Long userId 기반 조회로 변경)
        PostLike postLike = postLikeRepository.findByUserIdAndPost(userId, post)
                .orElseThrow(() -> new PostException(ErrorCode.LIKE_NOT_FOUND));

        // 3. 좋아요 삭제
        postLikeRepository.delete(postLike);
    }
}