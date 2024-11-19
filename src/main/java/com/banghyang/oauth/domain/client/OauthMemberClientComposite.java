package com.banghyang.oauth.domain.client;

import com.banghyang.oauth.domain.OauthMember;
import com.banghyang.oauth.domain.OauthServerType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class OauthMemberClientComposite {

    private final Map<OauthServerType, OauthMemberClient> mapping;

    /**
     * OauthServerType 에 따라 매핑 생성
     * @param clients
     */
    public OauthMemberClientComposite(Set<OauthMemberClient> clients) {
        mapping = clients.stream()
                .collect(toMap(
                        OauthMemberClient::supportServer,
                        identity()
                ));
        // 원형 참고
        // mapping = clients.stream().collect((toMap(client -> client.supportServer(), client -> client)));
    }

    /**
     * AuthCode 로 발급받은 AccessToken 으로 요청하여 받아온 사용자 정보로 OauthMember 객체 셍성하여 반환
     * @param oauthServerType
     * @param authCode
     * @return
     */
    public OauthMember fetch(OauthServerType oauthServerType, String authCode) {
        return getClient(oauthServerType).fetch(authCode);
    }

    /**
     * OauthServerType 에 해당하는 WebClient 반환
     * @param oauthServerType
     * @return
     */
    public OauthMemberClient getClient(OauthServerType oauthServerType) {
        return Optional.ofNullable(mapping.get(oauthServerType))
                .orElseThrow(() -> new RuntimeException("지원하지 않는 소셜 로그인 방식입니다."));
    }
}
