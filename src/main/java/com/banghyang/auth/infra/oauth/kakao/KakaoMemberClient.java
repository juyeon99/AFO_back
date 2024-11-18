package com.banghyang.auth.infra.oauth.kakao;

import com.banghyang.auth.domain.OauthMember;
import com.banghyang.auth.domain.OauthServerType;
import com.banghyang.auth.domain.client.OauthMemberClient;
import com.banghyang.auth.infra.oauth.kakao.client.KakaoApiClient;
import com.banghyang.auth.infra.oauth.kakao.dto.KakaoMemberResponse;
import com.banghyang.auth.infra.oauth.kakao.dto.KakaoToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class KakaoMemberClient implements OauthMemberClient {

    private final KakaoApiClient kakaoApiClient;
    private final KakaoOauthConfig kakaoOauthConfig;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.KAKAO;
    }

    @Override
    public OauthMember fetch(String authCode) {
        KakaoToken tokenInfo = kakaoApiClient.fetchToken(tokenRequestParams(authCode)); // AuthCode 로 AccessToken 조회
        KakaoMemberResponse kakaoMemberResponse = kakaoApiClient
                .fetchMember("Bearer " + tokenInfo.accessToken()); // AccessToken 으로 회원정보 조회
        return kakaoMemberResponse.toDomain(); // 회원정보를 OauthMember 객체로 변환
    }

    private MultiValueMap<String, String> tokenRequestParams(String authCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOauthConfig.clientId());
        params.add("redirect_uri", kakaoOauthConfig.redirectUri());
        params.add("code", authCode);
        params.add("client_secret", kakaoOauthConfig.clientSecret());
        return params;
    }
}
