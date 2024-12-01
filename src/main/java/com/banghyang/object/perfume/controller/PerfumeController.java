package com.banghyang.object.perfume.controller;

import com.banghyang.object.perfume.dto.PerfumeCreateRequest;
import com.banghyang.object.perfume.dto.PerfumeModifyRequest;
import com.banghyang.object.perfume.dto.PerfumeResponse;
import com.banghyang.object.perfume.service.PerfumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/perfumes")
@RestController
@RequiredArgsConstructor
public class PerfumeController {

    private final PerfumeService perfumeService;

    @GetMapping
    public ResponseEntity<List<PerfumeResponse>> getAllPerfumes() {
        return ResponseEntity.ok(perfumeService.getAllPerfumeResponses());
    }

    @PostMapping
    public ResponseEntity<?> createPerfume(@RequestBody PerfumeCreateRequest perfumeCreateRequest) {
        perfumeService.createPerfume(perfumeCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<?> modifyPerfume(@RequestBody PerfumeModifyRequest perfumeModifyRequest) {
        perfumeService.modifyPerfume(perfumeModifyRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deletePerfume(@RequestBody Long perfumeId) {
        perfumeService.deletePerfume(perfumeId);
        return ResponseEntity.ok().build();
    }
}
