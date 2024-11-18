package com.banghyang.auth.infra.oauth.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

// application.yml 의 oauth.kakao 로 설정된 정보들을 통하여 생성
@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoOauthConfig(
        String redirectUri,
        String clientId,
        String clientSecret,
        String[] scope
) {
}
