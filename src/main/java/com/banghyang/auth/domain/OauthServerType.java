package com.banghyang.auth.domain;

import static java.util.Locale.ENGLISH;

// OAuth2.0 인증 제공하는 서버의 종류 명시
public enum OauthServerType {

    // 추후에 다른 플랫폼 추가시 여기에 명시하세요
    KAKAO,
    ;

    public static OauthServerType fromName(String type) {
        return OauthServerType.valueOf(type.toUpperCase(ENGLISH));
    }
}
