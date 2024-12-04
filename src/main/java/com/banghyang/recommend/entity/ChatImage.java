package com.banghyang.recommend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table
public class ChatImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field("image_url")
    private String imageUrl;
}
