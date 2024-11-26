package com.banghyang.object.spice.controller;

import com.banghyang.object.spice.dto.SpiceResponse;
import com.banghyang.object.spice.service.SpiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
