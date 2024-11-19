package com.banghyang.oauth.infra.oauth.kakao.client;

import com.banghyang.oauth.infra.oauth.kakao.dto.KakaoMemberResponse;
import com.banghyang.oauth.infra.oauth.kakao.dto.KakaoToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

// Http Interface Client 사용
public interface KakaoApiClient {
    /**
     * AccessToken 가져오기
     * @param params
     * @return AccessToken
     */
    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoToken fetchToken(@RequestParam MultiValueMap<String, String> params);

    /**
     * 사용자 정보 가져오기
     * @param bearerToken (AccessToken)
     * @return 사용자 정보
     */
    @GetExchange("https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);
}
