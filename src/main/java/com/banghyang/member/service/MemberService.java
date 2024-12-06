package com.banghyang.member.service;

import com.banghyang.member.dto.MemberResponse;
import com.banghyang.member.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import com.banghyang.common.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> getAllMembers() {
        // 멤버 전체 엔티티 리스트에 담기
        List<Member> memberList = memberRepository.findAll();

        return memberList.stream() // stream 으로 엔티티 리스트 모든 항목에 접근
                .map(Mapper::mapMemberEntityToResponse) // 매퍼 클래스의 메소드로 response 로 변환
                .toList();
    }
}
