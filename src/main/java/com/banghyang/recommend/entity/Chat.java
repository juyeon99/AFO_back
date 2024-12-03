    package com.banghyang.recommend.entity;

    import com.banghyang.member.entity.Member;
    import jakarta.persistence.Id;
    import lombok.Builder;
    import lombok.Getter;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.springframework.data.mongodb.core.mapping.DocumentReference;
    import org.springframework.data.mongodb.core.mapping.Field;

    import java.time.LocalDateTime;

    @Builder
    @Getter
    @Document(collection = "chat")
    public class Chat {

        @Id
        private String id;

        @Field("member_id")
        @DocumentReference(lazy = true)  // Member 컬렉션을 참조
        private Member member;

        @Field("content")
        private String content;

        @Field("type")
        private MessageType type;  // enum으로 변경

        @Field("timestamp")
        private LocalDateTime timestamp;

        @Field("chat_image")
        private String chatImage;  // @DocumentReference 제거하고 String으로 변경

        // MessageType enum 정의
        public enum MessageType {
            USER,
            AI
        }
    }
