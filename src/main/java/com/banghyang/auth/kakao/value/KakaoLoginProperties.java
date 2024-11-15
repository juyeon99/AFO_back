package com.banghyang.auth.kakao.value;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 카카오 연동 로그인에 필요한 상수값들
 * 참고블로그 : https://velog.io/@clean01/Project-JWT%EC%99%80-oauth2%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%B4%EC%84%9C-%EC%B9%B4%EC%B9%B4%EC%98%A4-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84https://velog.io/@clean01/Project-JWT%EC%99%80-oauth2%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%B4%EC%84%9C-%EC%B9%B4%EC%B9%B4%EC%98%A4-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84
 */
@Getter
@Component
@PropertySource("classpath:property/application-auth.properties")
public class KakaoLoginProperties {

    @Value("${kakao.login.api_key}")
    private String kakaoLoginApiKey;

    @Value("${kakao.login.redirect_uri}")
    private String redirectUri;

    @Value("${kakao.login.uri.code}")
    private String codeReqeustUri;

    @Value("${kakao.login.uri.base}")
    private String kakaoAuthBaseUri;

    @Value("${kakao.login.uri.token}")
    private String tokenRequestUri;

    @Value("${kakao.api.uri.base}")
    private String kakaoApiBaseUri;

    @Value("${kakao.api.uri.user}")
    private String kakaoApiUserInfoRequestUri;

    @Value("${kakao.login.client_secret}")
    private String kakaoClientSecret;

}
