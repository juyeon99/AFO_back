package com.banghyang.oauth.domain.authcode;

import com.banghyang.oauth.domain.OauthServerType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class AuthCodeRequestUrlProviderComposite {

    private final Map<OauthServerType, AuthCodeRequestUrlProvider> mapping;

    /**
     * AuthCodeRequestUrlProvider 가 지원하는 OauthServerType 에 따라 mapping 생성
     * @param providers
     */
    public AuthCodeRequestUrlProviderComposite(Set<AuthCodeRequestUrlProvider> providers) {
        mapping = providers.stream()
                .collect(toMap(
                        // 참조연산자(::) -> 매개변수 작성하지 않는 람다식
                        AuthCodeRequestUrlProvider::supportServer,
                        identity() // 자기 자신을 인자로 전달
                ));
        // 원형 참고
        // mapping = providers.stream().collect(toMap(provider -> provider.supportServer(), provider -> provider));
    }

    /**
     * AuthCode 발급받는 Url 반환
     * @param oauthServerType
     * @return Uri String
     */
    public String provide(OauthServerType oauthServerType) {
        return getProvider(oauthServerType).provide();
    }

    /**
     * OauthServerType 에 해당하는 AuthCodeRequestUrlProvider 반환
     * @param oauthServerType
     * @return AuthCodeRequestUrlProvider
     */
    private AuthCodeRequestUrlProvider getProvider(OauthServerType oauthServerType) {
        return Optional.ofNullable(mapping.get(oauthServerType))
                .orElseThrow(() -> new RuntimeException("지원하지 않는 소셜 로그인 방식입니다."));
    }
}
