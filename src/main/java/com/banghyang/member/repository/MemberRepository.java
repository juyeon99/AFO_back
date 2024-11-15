package com.banghyang.member.repository;

import com.banghyang.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 아이디 중복검사에 사용할 boolean값
    boolean existsByLoginId(String loginId);

    // 로그인시 입력하는 아이디로 Member 객체 반환
    Member findByLoginId(String loginId);

}
