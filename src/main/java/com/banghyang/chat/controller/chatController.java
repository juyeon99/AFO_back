package com.banghyang.chat.controller;

import com.banghyang.chat.dto.UserChatRequest;
import com.banghyang.chat.dto.UserChatResponse;
import com.banghyang.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class chatController {

    private final ChatService chatService;

    /**
     * 유저의 채팅에 대한 답변 제공 메소드
     */
    @PostMapping
    public ResponseEntity<UserChatResponse> processInputAndImage(@ModelAttribute UserChatRequest userChatRequest) {
        // ModelAttribute 를 이용하여 http 요청 데이터를 dto 로 바인딩(multipart 포함한 데이터 처리 가능)
        // service 에서 유저의 입력에 대한 답변을 dto 로 담아 전달
        return ResponseEntity.ok(chatService.answerToUserRequest(userChatRequest));
    }

    /**
     * 채팅 기록 제공 메소드
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<List<UserChatResponse>> getAllChats(@PathVariable Long memberId) {
        return ResponseEntity.ok(chatService.getAllChats(memberId));
    }
}


