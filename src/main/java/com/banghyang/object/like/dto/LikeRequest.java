package com.banghyang.object.like.dto;

import lombok.Data;

@Data
public class LikeRequest {
    private Long memberId;
    private Long reviewId;
}
