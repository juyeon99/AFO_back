package com.banghyang.auth.kakao.controller;

import com.banghyang.auth.jwt.JwtProvider;
import com.banghyang.auth.jwt.JwtToken;
import com.banghyang.auth.kakao.model.dto.KakaoTokenResponseDto;
import com.banghyang.auth.kakao.service.KakaoLoginService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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

        // 회원 정보로 회원인지 판단
        Long memberId = kakaoLoginService.findMemberIdByEmail(userInfo.getKakaoAccount().getEmail());

        if(memberId != null) {
            // 토큰 발급
            JwtToken jwtToken = jwtService.login(memberId);

            Map<String, String> jwtMap = new HashMap<>();
            jwtMap.put(JwtProvider.ACCESS_HEADER_STRING, jwtToken.getAccessToken());
            jwtMap.put(JwtProvider.REFRESH_HEADER_STRING, jwtToken.getRefreshToken());

            SuccessResult result = new Builder(DetailedStatus.CREATED)
                    .message("로그인에 성공하여 JWT TOKEN 발행되었습니다.")
                    .data(jwtMap)
                    .build();

            return new ResponseEntity<>(result, HttpStatus.CREATED;
        } else { // 회원가입으로 리다이렉트
            Map<String, String> memberInfo = new HashMap<>();

            memberInfo.put("name", userInfo.getKakaoAccount().getName());
            memberInfo.put("email", userInfo.getKakaoAccount().getEmail());

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/auth/sing-up-page"));

            return new ResponseEntity<>(memberInfo, headers, HttpStatus.MOVED_PERMANENTLY);
        }
    }
}
