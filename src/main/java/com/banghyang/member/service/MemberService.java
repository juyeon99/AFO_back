package com.banghyang.member.service;

import com.banghyang.member.model.dto.MemberResponse;
import com.banghyang.member.model.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> getAllMembers() {
        List<Member> memberList = memberRepository.findAll();
        List<MemberResponse> memberResponseList = memberList.stream().map(member ->
                new MemberResponse().from(member)).toList();
        return memberResponseList;
    }
}
