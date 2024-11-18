package com.banghyang.auth.jwt.service;

import com.banghyang.auth.jwt.JwtProvider;
import com.banghyang.auth.jwt.JwtToken;
import com.banghyang.member.domain.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public JwtToken login(Long memberId) {
        // 멤버 찾기
        Optional<Member> member = memberRepository.findById(memberId);

        // 토큰 발행
        return jwtProvider.createJwtToken(memberId, member.getNickname(), member.getAdmin());
    }
}
