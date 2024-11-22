package com.banghyang.oauth.member.dto;

import com.banghyang.oauth.member.entity.OauthMember;

public record OauthMemberResponse(
        String name,
        String email,
        String birthyear,
        String gender,
        String role
) {
    public static OauthMemberResponse from(OauthMember oauthMember) {
        return new OauthMemberResponse(
                oauthMember.getName(),
                oauthMember.getEmail(),
                oauthMember.getBirthyear(),
                oauthMember.getGender(),
                oauthMember.getRole().name()
        );
    }
}
