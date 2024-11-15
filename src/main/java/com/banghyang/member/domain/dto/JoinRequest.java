package com.banghyang.member.domain.dto;

import com.banghyang.member.domain.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class JoinRequest {

    private String loginId;
    private String password;
    private String passwordCheck;

    public Member toEntity() {
        return Member.builder()
                .loginId(this.loginId)
                .password(this.password)
                .build();
    }

}