package com.banghyang.object.perfume.controller;

import com.banghyang.object.perfume.service.PerfumeService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/perfumes")
@RestController
@RequiredArgsConstructor
public class PerfumeController {

    private final PerfumeService perfumeService;

    @GetMapping
    public ResponseEntity<List<Object>> getAllPerfumes() {
        return ResponseEntity.ok(perfumeService.getAllPerfumes());
    }
}
