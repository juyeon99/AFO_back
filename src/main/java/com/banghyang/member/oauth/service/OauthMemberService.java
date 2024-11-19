package com.banghyang.member.oauth.service;

import com.banghyang.member.oauth.model.dto.OauthMemberDTO;
import com.banghyang.oauth.domain.OauthMember;
import com.banghyang.oauth.domain.OauthMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        return null;
    }
}
