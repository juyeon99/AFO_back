package com.banghyang.history.controller;

import com.banghyang.history.dto.HistoryResponse;
import com.banghyang.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping("/history")
    public ResponseEntity<HistoryResponse> createHistory(@RequestParam String chatId) {
        HistoryResponse result = historyService.createHistory(chatId);
        return ResponseEntity.ok(result);
    }
}
