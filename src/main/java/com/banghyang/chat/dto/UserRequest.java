package com.banghyang.chat.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {
    private Long memberId;
    private String content;
    private MultipartFile image;
}
