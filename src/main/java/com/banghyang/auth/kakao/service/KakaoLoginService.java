package com.banghyang.auth.kakao.service;

import com.banghyang.auth.kakao.model.dto.KakaoTokenResponseDto;
import com.banghyang.auth.kakao.model.dto.KakaoUserInfoDto;
import com.banghyang.auth.kakao.value.KakaoLoginProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginService {

    private final KakaoLoginProperties kakaoLoginProperties;

    /**
     * 토큰 얻어오는 메소드
     * @param code 인증코드
     * @return 카카오 토큰 정보
     */
    public KakaoTokenResponseDto getToken(String code) {
        // 토큰 요청 데이터 -> MultiValueMap
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoLoginProperties.getKakaoLoginApiKey());
        params.add("redirect_uri", kakaoLoginProperties.getRedirectUri());
        params.add("code", code);
//        params.add("client_secret", kakaoLoginProperties.getKakaoClientSecret());

        // WebClient 로 요청 보내기
        String response = WebClient.create(kakaoLoginProperties.getKakaoAuthBaseUri())
                .post()
                .uri(kakaoLoginProperties.getTokenRequestUri())
                .body(BodyInserters.fromFormData(params))
                .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // json 응답을 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoTokenResponseDto kakaoToken = null;

        try {
            kakaoToken = objectMapper.readValue(response, KakaoTokenResponseDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("[Line 72] kakao accessToken: {}", kakaoToken.getAccessToken());

        return kakaoToken;
    }

    public KakaoUserInfoDto getUserInfo(KakaoTokenResponseDto kakaoToken) {
        String response = WebClient.create(kakaoLoginProperties.getKakaoApiBaseUri())
                .post()
                .uri(kakaoLoginProperties.getKakaoApiUserInfoRequestUri())
                .header("Authorization", "Bearer " + kakaoToken.getAccessToken())
                .header("Content-type","application/x-www-form-urlencoded;charset=utf-8" )
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // json 응답을 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUserInfoDto userInfo = null;

        try {
            userInfo = objectMapper.readValue(response, KakaoTokenResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }

        return userInfo;
    }

}
