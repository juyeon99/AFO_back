package com.banghyang.oauth.domain.authcode;

import com.banghyang.oauth.domain.OauthServerType;

// 플랫폼마다 형태가 같을것이므로 재사용을 위해 인터페이스로 지정
public interface AuthCodeRequestUrlProvider {
    /**
     * 이용할 플랫폼 명시하는 메소드
     * @return OauthServerType 반환
     */
    OauthServerType supportServer();

    /**
     * AuthCode 발급할 URL 생성하는 메소드
     * @return Uri String 반환
     */
    String provide();
}
