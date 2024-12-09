package com.banghyang.member.controller;

import com.banghyang.member.dto.MemberResponse;
import com.banghyang.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<?> setMemberLeave(@RequestParam Long memberId) {
        memberService.setMemberLeave(memberId);
        return ResponseEntity.ok().build();
    }
}
