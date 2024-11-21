package com.banghyang.oauth.application;

import com.banghyang.oauth.domain.OauthMember;
import com.banghyang.oauth.domain.OauthMemberRepository;
import com.banghyang.oauth.domain.OauthServerType;
import com.banghyang.oauth.domain.authcode.AuthCodeRequestUrlProviderComposite;
import com.banghyang.oauth.domain.client.OauthMemberClientComposite;
import com.banghyang.oauth.infra.oauth.kakao.client.KakaoApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final OauthMemberRepository oauthMemberRepository;
    private final KakaoApiClient kakaoApiClient;

    /**
     * AuthCode 발급받기 위한 URL 반환
     * @param oauthServerType
     * @return Uri String
     */
    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    /**
     * 회원가입 처리 메소드
     * @param oauthServerType
     * @param authCode
     * @return
     */
    public Long login(OauthServerType oauthServerType, String authCode) {
        // 플랫폼명과 AuthCode 보내서 사용자 정보 받아와서 객체로 만들기
        OauthMember oauthMember = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        // 객체 저장
        OauthMember saved = oauthMemberRepository.findByOauthId(oauthMember.getOauthId())
                .orElseGet(() -> oauthMemberRepository.save(oauthMember));
        // 저장한 객체 아이디 반환
        return saved.getId();
    }
}
