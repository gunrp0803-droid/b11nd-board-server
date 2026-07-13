package com.example.b11ndboard.domain.post.service;

import com.example.b11ndboard.domain.user.entity.Users;
import com.example.b11ndboard.domain.user.repository.UsersRepository;
import com.example.b11ndboard.domain.post.dto.request.PostRequestDto;
import com.example.b11ndboard.domain.post.dto.response.PostResponseDto;
import com.example.b11ndboard.domain.post.entity.Post;
import com.example.b11ndboard.domain.post.entity.PostLike;
import com.example.b11ndboard.global.exception.ErrorCode;
import com.example.b11ndboard.domain.post.exception.PostException;
import com.example.b11ndboard.domain.post.repository.PostRepository;
import com.example.b11ndboard.domain.post.repository.PostLikeRepository;
import com.example.b11ndboard.domain.comment.repository.CommentRepository;
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

    // 2. 게시글 상세 조회
    public PostResponseDto getPost(Long postId, Long userId) {
        // 1. 게시글 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 2. 총 좋아요 개수
        long likeCount = postLikeRepository.countByPost(post);

        // 3. 좋아요 클릭 여부
        boolean liked = (userId != null) && postLikeRepository.existsByUserIdAndPost(userId, post);

        // 4. 총 댓글 개수
        long commentCount = commentRepository.countByPostId(postId);

        // 5. 작성자 이름 조회 (삭제된 유저인 경우 "알 수 없음" 반환)
        String username = usersRepository.findById(post.getUserId())
                .map(Users::getUsername)
                .orElse("알 수 없음");

        // 6. 작성자 판별
        boolean isWriter = (userId != null) && post.getUserId().equals(userId);

        return new PostResponseDto(post, username, likeCount, liked, commentCount, isWriter);
    }

    // 3. 게시글 수정
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

    // 4. 게시글 삭제
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

    // 5. 좋아요 로직
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

    // 6. 전체 게시글 목록 조회 (N+1 / 3N+2 배치 쿼리 최적화 완료)
    public Page<PostResponseDto> getAllPosts(int page, Long userId){
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage = postRepository.findAll(pageable);
        List<Post> posts = postPage.getContent();

        List<Long> userIds = posts.stream()
                .map(Post::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 1. 유저 ID -> Username 일괄 매핑 (삭제된 유저는 "알 수 없음"으로 표시)
        Map<Long, String> userIdToUsernameMap = usersRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(Users::getId, Users::getUsername));

        // 2. 게시글 ID -> 좋아요 개수 일괄 매핑
        Map<Long, Long> postLikeCountMap = postLikeRepository.countLikesByPosts(posts).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        // 3. 게시글 ID -> 댓글 개수 일괄 매핑
        Map<Long, Long> postCommentCountMap = commentRepository.countCommentsByPosts(posts).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        // 4. 로그인한 유저가 좋아요한 게시글 ID 일괄 조회 세트
        java.util.Set<Long> likedPostIds = new java.util.HashSet<>();
        if (userId != null && !posts.isEmpty()) {
            likedPostIds.addAll(postLikeRepository.findLikedPostIdsByUserIdAndPosts(userId, posts));
        }

        return postPage.map(post -> {
            long likeCount = postLikeCountMap.getOrDefault(post.getId(), 0L);
            long commentCount = postCommentCountMap.getOrDefault(post.getId(), 0L);
            boolean liked = likedPostIds.contains(post.getId());
            boolean isWriter = (userId != null) && post.getUserId().equals(userId);
            String username = userIdToUsernameMap.getOrDefault(post.getUserId(), "알 수 없음");
            return new PostResponseDto(post, username, likeCount, liked, commentCount, isWriter);
        });
    }

    // 7. 게시글 좋아요 취소 로직
    @Transactional
    public void cancelPostLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        PostLike postLike = postLikeRepository.findByUserIdAndPost(userId, post)
                .orElseThrow(() -> new PostException(ErrorCode.LIKE_NOT_FOUND));

        postLikeRepository.delete(postLike);
    }
}