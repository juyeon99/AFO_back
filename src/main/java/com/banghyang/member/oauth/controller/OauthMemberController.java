package com.banghyang.member.oauth.controller;

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

    /**
     * 전체 회원 조회 컨트롤러
     * @return
     */
    @GetMapping
    public ResponseEntity<List<OauthMemberDTO>> getAllMembers() {
        List<OauthMemberDTO> memberList = oauthMemberService.findAllOauthMember();
        return ResponseEntity.ok(memberList);
    }
}
