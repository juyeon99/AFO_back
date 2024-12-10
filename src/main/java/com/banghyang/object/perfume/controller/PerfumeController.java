package com.banghyang.object.perfume.controller;

import com.banghyang.object.perfume.dto.PerfumeCreateRequest;
import com.banghyang.object.perfume.dto.PerfumeModifyRequest;
import com.banghyang.object.perfume.dto.PerfumeResponse;
import com.banghyang.object.perfume.service.PerfumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/perfumes")
@RestController
@RequiredArgsConstructor
public class PerfumeController {

    private final PerfumeService perfumeService;

    /**
     * 모든 향수 조회하기
     */
    @GetMapping
    public ResponseEntity<Page<PerfumeResponse>> getAllPerfumes(Pageable pageable) {
        return ResponseEntity.ok(perfumeService.getAllPerfumeResponses(pageable));
    }

    /**
     * 새로운 향수 등록허가
     */
    @PostMapping
    public ResponseEntity<?> createPerfume(@RequestBody PerfumeCreateRequest perfumeCreateRequest) {
        perfumeService.createPerfume(perfumeCreateRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 기존 향수 정보 수정하기
     */
    @PutMapping
    public ResponseEntity<?> modifyPerfume(@RequestBody PerfumeModifyRequest perfumeModifyRequest) {
        perfumeService.modifyPerfume(perfumeModifyRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 기존 향수 삭제하기
     */
    @DeleteMapping("/{perfumeId}")
    public ResponseEntity<?> deletePerfume(@PathVariable Long perfumeId) {
        perfumeService.deletePerfume(perfumeId);
        return ResponseEntity.ok().build();
    }
}
