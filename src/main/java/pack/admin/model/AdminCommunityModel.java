package pack.admin.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pack.dto.PostDto;
import pack.dto.ReportedPostDto;
import pack.entity.Alert;
import pack.entity.Comment;
import pack.entity.Post;
import pack.entity.ReportedPost;
import pack.entity.User;
import pack.repository.AlertsRepository;
import pack.repository.CommentLikeRepository;
import pack.repository.CommentsRepository;
import pack.repository.PostLikeRepository;
import pack.repository.PostsRepository;
import pack.repository.ReportedPostsRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AdminCommunityModel {

    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private ReportedPostsRepository reportedPostsRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private AlertsRepository alertRepo;

    public Page<PostDto> getAllPosts(Pageable pageable) {
        return postsRepository.findByDeletedIsFalse(pageable).map(Post::toDto);
    }

    public Page<PostDto> getAllReportedPosts(Pageable pageable) {
        return postsRepository.findByReportsCountGreaterThanOrderByNoDesc(0, pageable).map(Post::toDto);
    }

    public Page<PostDto> getMostReportedPosts(Pageable pageable) {
        return postsRepository.findByReportsCountGreaterThanOrderByReportsCountDesc(0, pageable).map(Post::toDto);
    }
    
    public List<ReportedPostDto> getReportedInfos(){
    	return reportedPostsRepository.findAll().stream().map(ReportedPost::toDto).toList();
    }
    public PostDto getPostDetail(int no) {
        Optional<Post> postOptional = postsRepository.findById(no);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            return convertToPostDto(post);
        }
        return null; // 게시물이 없을 경우
    }


    // Post 엔티티를 PostDto로 변환하는 메서드
    private PostDto convertToPostDto(Post post) {
        return PostDto.builder()
            .no(post.getNo())
            .content(post.getContent())
            .date(post.getDate())
            .pic(post.getPic())
            .userId(post.getUser().getId())
            .likesCount(post.getLikesCount())
            .commentsCount(post.getCommentsCount())
            .reportsCount(post.getReportsCount())
            .reportedPostsList(post.getReportedPosts().stream().map(ReportedPost::getNo).collect(Collectors.toList()))
            .productNo(post.getProduct() != null ? post.getProduct().getNo() : null)
            .userNickname(post.getUser().getNickname())
            .userNo(post.getUser().getNo())
            .userPic(post.getUser().getPic())
            .commentsList(post.getComments().stream().map(Comment::getNo).collect(Collectors.toList()))
            .deleted(post.isDeleted())
            .deletedAt(post.getDeletedAt())
            .build();
    }
    
    @Transactional
    public void deletePostData(int no) { // Post PK로 6개 테이블 처리
    	// Alert (작성자에게 경고알림 전송) - 삭제 전 처리해야 no 유효
    	Alert alert = new Alert();
    	alert.setUser(postsRepository.findUserByPostNo(no));
    	alert.setPath("/user/mypage/notice/1");
    	alert.setCategory("커뮤니티");
    	alert.setContent("작성하신 게시물이 커뮤니티 이용 수칙을 위반하여 삭제 조치되었습니다. 공지 재확인 부탁드려요!");
    	alertRepo.save(alert);
    	// Comment_like
    	commentsRepository.findByPostNo(no).stream()
        	.map(Comment::getNo) // 각 댓글의 no 값 추출
        	.forEach(commentNo -> commentLikeRepository.deleteByCommentNo(commentNo));
    	// Comment
    	commentsRepository.deleteByPostNo(no);
    	// Post_like
    	postLikeRepository.deleteByPostNo(no);
    	// Reported_post
    	reportedPostsRepository.deleteByPostNo(no);
    	// Post
    	postsRepository.deleteById(no);
    }
}
