package kuke.board.comment.api;

import kuke.board.comment.service.response.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiV2Test {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponse response1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        CommentResponse response2 = create(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
        CommentResponse response3 = create(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

        System.out.println("response1.getPath() = " + response1.getPath());
        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("\tresponse2.getPath() = " + response2.getPath());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getPath() = " + response3.getPath());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());

        /**
         response1.getPath() = 00000
         response1.getCommentId() = 262404302758727680
         response2.getPath() = 0000000000
         response2.getCommentId() = 262404303190740992
         response3.getPath() = 000000000000000
         response3.getCommentId() = 262404303245266944
         */
    }

    CommentResponse create(CommentCreateRequestV2 request) {
        return restClient.post()
                .uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v2/comments/{commentId}", 262404302758727680L)
                .retrieve()
                .body(CommentResponse.class);
        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v2/comments/{commentId}", 262404302758727680L)
                .retrieve();
    }
    
    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
            .uri("/v2/comments?articleId=1&pageSize=10&page=50000")
            .retrieve()
            .body(CommentPageResponse.class);
        
        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
        
        /**
         response.getCommentCount() = 500001
         comment.getCommentId() = 262406002748531132
         comment.getCommentId() = 262406002748531133
         comment.getCommentId() = 262406002748531134
         comment.getCommentId() = 262406002748531135
         comment.getCommentId() = 262406002748531136
         comment.getCommentId() = 262406002748531137
         comment.getCommentId() = 262406002748531138
         comment.getCommentId() = 262406002748531139
         comment.getCommentId() = 262406002748531140
         comment.getCommentId() = 262406002748531141
         */
    }
    
    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> responses1 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });
        
        System.out.println("firstPage");
        for (CommentResponse response : responses1) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }
        
        String lastPath = responses1.getLast().getPath();
        List<CommentResponse> responses2 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".formatted(lastPath))
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });
        
        System.out.println("secondPage");
        for (CommentResponse response : responses2) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }
    }
    
    @Test
    void countTest() {
        CommentResponse commentResponse = create(new CommentCreateRequestV2(2L, "my comment1", null, 1L));
        
        Long count1 = restClient.get()
            .uri("/v2/comments/articles/{articleId}/count", 2L)
            .retrieve()
            .body(Long.class);
        System.out.println("count1 = " + count1); // 1
        
        restClient.delete()
            .uri("/v2/comments/{commentId}", commentResponse.getCommentId())
            .retrieve();
        
        Long count2 = restClient.get()
            .uri("/v2/comments/articles/{articleId}/count", 2L)
            .retrieve()
            .body(Long.class);
        System.out.println("count2 = " + count2); // 0
    }
    
    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }
}
