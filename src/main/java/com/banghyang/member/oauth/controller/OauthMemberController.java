package com.banghyang.member.oauth.controller;

import com.banghyang.member.oauth.model.dto.OauthMemberDTO;
import com.banghyang.member.oauth.service.OauthMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class OauthMemberController {

    private final OauthMemberService oauthMemberService;

    @GetMapping
    public ResponseEntity<List<OauthMemberDTO>> getAllMembers() {
        List<OauthMemberDTO> memberList = oauthMemberService.findAllOauthMember();
        return ResponseEntity.ok(memberList);
    }
}
