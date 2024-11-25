package com.banghyang.member.model.dto;

import com.banghyang.member.model.entity.Member;
import com.banghyang.member.model.type.MemberRoleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberResponse {
    private String email;
    private String name;
    private String gender;
    private String birthyear;
    private MemberRoleType role;
    private LocalDateTime createdAt;

    public MemberResponse from(Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
        this.gender = member.getGender();
        this.birthyear = member.getBirthyear();
        this.role = member.getRole();
        this.createdAt = member.getCreatedAt();
        return this;
    }
}
