package com.banghyang.oauth.member.repository;

import com.banghyang.oauth.member.dto.OauthId;
import com.banghyang.oauth.member.entity.OauthMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthMemberRepository extends JpaRepository<OauthMember, Long> {
    Optional<OauthMember> findByOauthId(OauthId oauthId);
}
