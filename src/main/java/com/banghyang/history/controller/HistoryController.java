package com.banghyang.history.controller;

import com.banghyang.history.dto.HistoryResponse;
import com.banghyang.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    public ResponseEntity<?> createHistory(@RequestParam String chatId) {
        historyService.createHistoryByChat(chatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<HistoryResponse>> getMembersHistory(@RequestParam Long memberId) {
        return ResponseEntity.ok(historyService.getMembersHistory(memberId));
    }

    @DeleteMapping("/{historyId}")
    public ResponseEntity<?> deleteHistory(@RequestParam Long historyId) {
        historyService.deleteHistory(historyId);
        return ResponseEntity.ok().build();
    }
}
