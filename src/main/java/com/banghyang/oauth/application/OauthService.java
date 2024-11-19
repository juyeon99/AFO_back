package com.banghyang.oauth.application;

import com.banghyang.oauth.domain.OauthMember;
import com.banghyang.oauth.domain.OauthMemberRepository;
import com.banghyang.oauth.domain.OauthServerType;
import com.banghyang.oauth.domain.authcode.AuthCodeRequestUrlProviderComposite;
import com.banghyang.oauth.domain.client.OauthMemberClientComposite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final OauthMemberRepository oauthMemberRepository;

    /**
     * AuthCode 발급받기 위한 URL 반환
     * @param oauthServerType
     * @return Uri String
     */
    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    /**
     *
     * @param oauthServerType
     * @param authCode
     * @return
     */
    public Long login(OauthServerType oauthServerType, String authCode) {
        OauthMember oauthMember = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        OauthMember saved = oauthMemberRepository.findByOauthId(oauthMember.oauthId())
                .orElseGet(() -> oauthMemberRepository.save(oauthMember));
        return saved.id();
    }
}
