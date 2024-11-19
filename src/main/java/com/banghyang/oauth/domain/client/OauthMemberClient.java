package com.banghyang.oauth.domain.client;

import com.banghyang.oauth.domain.OauthMember;
import com.banghyang.oauth.domain.OauthServerType;

public interface OauthMemberClient {
    /**
     * 이용할 플랫폼 명시하는 메소드
     * @return OauthServerType
     */
    OauthServerType supportServer();

    /**
     * AuthCode 로 발급받은 AccessToken 으로 요청한 사용자 정보로 OauthMember 객체 생성하여 반환
     * @param code (AuthCode)
     * @return
     */
    OauthMember fetch(String code);
}
