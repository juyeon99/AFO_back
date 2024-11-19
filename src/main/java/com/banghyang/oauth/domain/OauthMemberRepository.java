package com.banghyang.oauth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthMemberRepository extends JpaRepository<OauthMember, Long> {
    /**
     * OauthId로 OauthMember 찾기
     * @param oauthId
     * @return
     */
    Optional<OauthMember> findByOauthId(OauthId oauthId);
}
