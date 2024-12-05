package com.banghyang.chat.controller;

import com.banghyang.chat.dto.UserRequest;
import com.banghyang.chat.dto.UserResponse;
import com.banghyang.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class chatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<UserResponse> processInputAndImage(@ModelAttribute UserRequest userRequest) {
        // ModelAttribute 를 이용하여 http 요청 데이터를 dto 로 바인딩(multipart 포함한 데이터 처리 가능)
        // service 에서 유저의 입력에 대한 답변을 dto 로 담아 전달
        return ResponseEntity.ok(chatService.answerToUserRequest(userRequest));
    }
}


