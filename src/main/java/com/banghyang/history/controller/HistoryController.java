package com.banghyang.history.controller;

import com.banghyang.history.dto.HistoryResponse;
import com.banghyang.history.entity.History;
import com.banghyang.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    /**
     * 새로운 채팅카드를 생성하는 엔드포인트
     *
     * @param chatId 생성할 채팅의 고유 ID
     * @return 생성된 히스토리 정보를 담은 HistoryResponse
     */
    @PostMapping("/history")
    public ResponseEntity<HistoryResponse> createHistory(@RequestParam String chatId) {
        HistoryResponse result = historyService.createHistory(chatId);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 회원의 카드 히스토리를 조회하는 엔드포인트
     *
     * @param memberId 조회할 회원의 ID
     * @return 해당 회원의 전체 카드 히스토리 목록
     */
    @GetMapping("/history/{memberId}")
    public ResponseEntity<List<History>> getChatHistory(@PathVariable Long memberId) {
        List<History> history = historyService.getCardHistory(memberId);
        return ResponseEntity.ok(history);
    }
}
