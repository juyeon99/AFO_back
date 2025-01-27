package com.banghyang.object.product.controller;

import com.banghyang.object.product.dto.ProductCreateRequest;
import com.banghyang.object.product.dto.ProductModifyRequest;
import com.banghyang.object.product.dto.PerfumeResponse;
import com.banghyang.object.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/products")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 모든 향수 조회하기
     */
    @GetMapping
    public ResponseEntity<List<PerfumeResponse>> getAllPerfumes() {
        return ResponseEntity.ok(productService.getAllPerfumeResponses());
    }

    /**
     * 새로운 제품 등록하기
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest productCreateRequest) {
        productService.createProduct(productCreateRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 기존 제품 정보 수정하기
     */
    @PutMapping
    public ResponseEntity<?> modifyProduct(@RequestBody ProductModifyRequest productModifyRequest) {
        productService.modifyPerfume(productModifyRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 기존 제품 삭제하기
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deletePerfume(productId);
        return ResponseEntity.ok().build();
    }
}
