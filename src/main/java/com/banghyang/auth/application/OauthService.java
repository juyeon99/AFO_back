package com.banghyang.auth.application;

import com.banghyang.auth.domain.OauthMember;
import com.banghyang.auth.domain.OauthMemberRepository;
import com.banghyang.auth.domain.OauthServerType;
import com.banghyang.auth.domain.authcode.AuthCodeRequestUrlProviderComposite;
import com.banghyang.auth.domain.client.OauthMemberClientComposite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final OauthMemberRepository oauthMemberRepository;

    /**
     * @param oauthServerType
     * @return OauthServerType 받아서 해당 인증 서버에서 AuthCode 받아오기 위한 URL 주소 반환
     */
    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    public Long login(OauthServerType oauthServerType, String authCode) {
        OauthMember oauthMember = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        OauthMember saved = oauthMemberRepository.findByOauthId(oauthMember.oauthId())
                .orElseGet(() -> oauthMemberRepository.save(oauthMember));
        return saved.id();
    }
}
