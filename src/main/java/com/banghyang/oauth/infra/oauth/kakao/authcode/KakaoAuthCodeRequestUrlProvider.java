package com.banghyang.oauth.infra.oauth.kakao.authcode;

import com.banghyang.oauth.domain.OauthServerType;
import com.banghyang.oauth.domain.authcode.AuthCodeRequestUrlProvider;
import com.banghyang.oauth.infra.oauth.kakao.KakaoOauthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KakaoAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider { // 상속 인터페이스 설명 확인

    private final KakaoOauthConfig kakaoOauthConfig;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.KAKAO; // 카카오 이용할 것을 명시
    }

    @Override
    public String provide() { // AuthCode 발급할 URL 생성 메소드
        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code
        // 위 링크에 AuthCode 받아오기 위한 설명 상세히 적혀있음(아래 파라미터값과 비교해보세요)
        return UriComponentsBuilder
                // 요청 URL
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                // 쿼리 파라미터
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoOauthConfig.clientId())
                .queryParam("redirect_uri", kakaoOauthConfig.redirectUri())
                .queryParam("scope", String.join(",", kakaoOauthConfig.scope()))
                .toUriString();
    }
}
