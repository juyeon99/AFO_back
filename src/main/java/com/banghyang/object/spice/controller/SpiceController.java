package com.banghyang.object.spice.controller;

import com.banghyang.object.spice.dto.SpiceCreateRequest;
import com.banghyang.object.spice.dto.SpiceModifyRequest;
import com.banghyang.object.spice.dto.SpiceResponse;
import com.banghyang.object.spice.service.SpiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/spices")
@RestController
@RequiredArgsConstructor
public class SpiceController {

    private final SpiceService spiceService;

    @GetMapping
    public ResponseEntity<List<SpiceResponse>> getAllSpices() {
        return ResponseEntity.ok(spiceService.getAllSpiceResponses());
    }

    @PutMapping
    public ResponseEntity<?> modifySpice(@RequestBody SpiceModifyRequest spiceModifyRequest) {
        spiceService.modifySpice(spiceModifyRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteSpice(@RequestBody Long spiceId) {
        spiceService.deleteSpice(spiceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createSpice(@RequestBody SpiceCreateRequest spiceCreateRequest) {
        spiceService.createSpice(spiceCreateRequest);
        return ResponseEntity.ok().build();
    }
}
