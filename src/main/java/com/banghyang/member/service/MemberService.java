package com.banghyang.member.service;

import com.banghyang.member.dto.MemberResponse;
import com.banghyang.member.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 멤버 전체 조회 서비스 메소드
     */
//    public List<MemberResponse> getAllMembers() {
//        // 멤버 전체 엔티티 리스트에 담기
//        List<Member> memberList = memberRepository.findAll();
//
//        return memberList.stream() // stream 으로 엔티티 리스트 모든 항목에 접근
//                .map(Mapper::mapMemberEntityToResponse) // 매퍼 클래스의 메소드로 response 로 변환
//                .toList();
//    }

    /**
     * 멤버 타입 탈퇴로 변경하는 서비스 메소드
     */
    public void setMemberLeave(Long memberId) {
        // 회원 아이디로 탈퇴처리할 회원 찾아오기
        Member laveMemberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 회원 정보를 찾지 못했습니다."));

        laveMemberEntity.setMemberLeave(); // RoleType Leave 로 변경
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new EntityNotFoundException(
                        "[MemberService:getMemberById] 아이디에 해당하는 회원 정보를 찾을 수 없습니다.")
        );
    }
}
