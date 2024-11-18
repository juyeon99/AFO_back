package com.banghyang.auth.domain.authcode;

import com.banghyang.auth.domain.OauthServerType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class AuthCodeRequestUrlProviderComposite {

    private final Map<OauthServerType, AuthCodeRequestUrlProvider> mapping;

    public AuthCodeRequestUrlProviderComposite(Set<AuthCodeRequestUrlProvider> providers) {
        mapping = providers.stream()
                .collect(toMap(
                        AuthCodeRequestUrlProvider::supportServer,
                        identity()
                ));
    }

    /**
     * @param oauthServerType
     * @return 소셜 로그인 지원 타입 반환
     */
    public String provide(OauthServerType oauthServerType) {
        return getProvider(oauthServerType).provide();
    }

    private AuthCodeRequestUrlProvider getProvider(OauthServerType oauthServerType) {
        return Optional.ofNullable(mapping.get(oauthServerType))
                .orElseThrow(() -> new RuntimeException("지원하지 않는 소셜 로그인 방식입니다."));
    }
}
