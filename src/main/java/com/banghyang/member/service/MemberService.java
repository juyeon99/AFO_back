package com.banghyang.member.service;

import com.banghyang.member.domain.dto.JoinRequest;
import com.banghyang.member.domain.dto.LoginRequest;
import com.banghyang.member.domain.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepo;

    // 아이디 중복검사 메소드(이미 존재하면 true 반환)
    public boolean checkMemberDuplicateByLoginId(String loginId) {
        return memberRepo.existsByLoginId(loginId);
    }

    // 회원가입 메소드
    public void join(JoinRequest joinRequest) {
        memberRepo.save(joinRequest.toEntity());
    }

    // 로그인 메소드
    public Member login(LoginRequest loginRequest) {
        Member findMember = memberRepo.findByLoginId(loginRequest.getLoginId());
        if (findMember == null) {
            return null;
        } else if (!findMember.getPassword().equals(loginRequest.getPassword())) {
            return null;
        } else {
            return findMember;
        }
    }

    // 고유식별자로 Member 객체 찾는 메소드
    public Member getMemberById(Long id) {
        return memberRepo.findById(id).orElse(null);
    }
}
