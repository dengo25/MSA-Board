package kuke.board.comment.api;

import kuke.board.comment.service.response.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiTest {
  
  RestClient restClient = RestClient.create("http://localhost:9001");
  
  @Test
  void create() {
    CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
    CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));
    CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response1.getCommentId(), 1L));
    
    System.out.println("commentId=%s".formatted(response1.getCommentId()));
    System.out.println("\tcommentId=%s".formatted(response2.getCommentId()));
    System.out.println("\tcommentId=%s".formatted(response3.getCommentId()));
    
//    commentId=262176364911771648
//    commentId=262176365238927360
//    commentId=262176365285064704
  }
  
  @Test
  void read() {
    CommentResponse response = restClient.get()
        .uri("/v1/comments/{commentId}", 262176365285064704L)
        .retrieve()
        .body(CommentResponse.class);
    
    System.out.println("response = " + response);
  }
  
  
  @Test
  void delete() {
    //    commentId=262176364911771648
    //       commentId=262176365238927360
    //      commentId=262176365285064704
    restClient.delete()
        .uri("/v1/comments/{commentId}", 262176365285064704L)
        .retrieve();
  }
  @Test
  void readAll() {
    CommentPageResponse response = restClient.get()
        .uri("/v1/comments?articleId=1&page=1&pageSize=10")
        .retrieve()
        .body(CommentPageResponse.class);
    
    System.out.println("response.getCommentCount() = " + response.getCommentCount());
    for (CommentResponse comment : response.getComments()) {
      if (!comment.getCommentId().equals(comment.getParentCommentId())) {
        System.out.print("\t");
      }
      System.out.println("comment.getCommentId() = " + comment.getCommentId());
    }
    
    /**
     * 1번 페이지 수행 결과
     comment.getCommentId() = 262177580632436736
     comment.getCommentId() = 262177580661796865
     comment.getCommentId() = 262177580632436737
     comment.getCommentId() = 262177580661796868
     comment.getCommentId() = 262177580632436738
     comment.getCommentId() = 262177580661796871
     comment.getCommentId() = 262177580632436739
     comment.getCommentId() = 262177580661796870
     comment.getCommentId() = 262177580636631040
     comment.getCommentId() = 262177580661796869
     */
  }
  
  @Test
  void readAllInfiniteScroll() {
    List<CommentResponse> responses1 = restClient.get()
        .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
        .retrieve()
        .body(new ParameterizedTypeReference<List<CommentResponse>>() {
        });
    
    System.out.println("firstPage");
    for (CommentResponse comment : responses1) {
      if (!comment.getCommentId().equals(comment.getParentCommentId())) {
        System.out.print("\t");
      }
      System.out.println("comment.getCommentId() = " + comment.getCommentId());
    }
    
    Long lastParentCommentId = responses1.getLast().getParentCommentId();
    Long lastCommentId = responses1.getLast().getCommentId();
    
    List<CommentResponse> responses2 = restClient.get()
        .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
            .formatted(lastParentCommentId, lastCommentId))
        .retrieve()
        .body(new ParameterizedTypeReference<List<CommentResponse>>() {
        });
    
    System.out.println("secondPage");
    for (CommentResponse comment : responses2) {
      if (!comment.getCommentId().equals(comment.getParentCommentId())) {
        System.out.print("\t");
      }
      System.out.println("comment.getCommentId() = " + comment.getCommentId());
    }
  }
  
  CommentResponse createComment(CommentCreateRequest request) {
    return restClient.post()
        .uri("/v1/comments")
        .body(request)
        .retrieve()
        .body(CommentResponse.class);
  }
  
  
  @AllArgsConstructor
  @Getter
  public static class CommentCreateRequest {
    private Long articleId;
    private String content;
    private Long parentCommentId;
    private Long writerId;
  }
  
}
