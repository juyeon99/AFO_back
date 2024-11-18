package com.banghyang.auth.domain.client;

import com.banghyang.auth.domain.OauthMember;
import com.banghyang.auth.domain.OauthServerType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class OauthMemberClientComposite {

    private final Map<OauthServerType, OauthMemberClient> mapping;

    public OauthMemberClientComposite(Set<OauthMemberClient> clients) {
        mapping = clients.stream()
                .collect(toMap(
                        OauthMemberClient::supportServer,
                        identity()
                ));
    }

    public OauthMember fetch(OauthServerType oauthServerType) {
        return Optional.ofNullable(mapping.get(oauthServerType))
                .orElseThrow().fetch(() -> new RuntimeException("지원하지 않는 소셜 로그인 방식입니다."));
    }
}
