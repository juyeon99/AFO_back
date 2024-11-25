package com.banghyang.member.oauth.service;

import com.banghyang.member.oauth.model.dto.OauthMemberDTO;
import com.banghyang.oauth.domain.OauthMember;
import com.banghyang.oauth.domain.OauthMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OauthMemberService {

    private final OauthMemberRepository oauthMemberRepository;

    /**
     * 전체 회원 조회
     */
    public List<OauthMemberDTO> findAllOauthMember() {
        // 레파지토리에서 전체 OauthMember 가져와서 List 만들기
        List<OauthMember> oauthMemberList = oauthMemberRepository.findAll();

        // DTO 로 변환하여 List 로 저장하기
        List<OauthMemberDTO> oauthMemberDTOList = oauthMemberList
                .stream().map(memberEntity -> new OauthMemberDTO(
                        memberEntity.getName(),
                        memberEntity.getEmail(),
                        memberEntity.getBirthyear(),
                        memberEntity.getGender()
                )).toList();

        return oauthMemberDTOList;
    }
}
