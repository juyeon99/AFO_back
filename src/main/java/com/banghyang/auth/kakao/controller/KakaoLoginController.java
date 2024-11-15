package com.banghyang.auth.kakao.controller;

import com.banghyang.auth.kakao.model.dto.KakaoTokenResponseDto;
import com.banghyang.auth.kakao.service.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/oauth/kakao")
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;
    private final JwtService jwtService;

    @GetMapping("/code")
    public ResponseEntity<Object> kakaoLogin(@RequestParam(value = "code", required = false) String code,
                                             @RequestParam(value = "error", required = false) String error,
                                             @RequestParam(value = "error_description", required = false) String errorDescription,
                                             @RequestParam(value = "state", required = false) String state) {

        KakaoTokenResponseDto kakaoToken = kakaoLoginService.getToken(code);

        // 토큰으로 회원 정보 얻어오기
        KakaoUserInfoDto userInfo = kakaoLoginService.getUserInfo(kakaoToken);

        return null;
    }
}
