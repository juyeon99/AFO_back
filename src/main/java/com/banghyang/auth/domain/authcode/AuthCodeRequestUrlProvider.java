package com.banghyang.auth.domain.authcode;

import com.banghyang.auth.domain.OauthServerType;

// OauthServerType 별로 해당 서비스의 AuthCode 요청 URL 생성하는 클래스
// 플랫폼마다 형태가 같을것이므로 재사용을 위해 인터페이스로 지정
public interface AuthCodeRequestUrlProvider {
    // OauthServerType 명시하는 메소드
    OauthServerType supportServer();

    // 지원 타입으로 URL 생성하는 메소드
    String provide();
}
