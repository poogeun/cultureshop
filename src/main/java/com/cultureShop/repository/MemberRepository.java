package com.cultureShop.repository;

import com.cultureShop.dto.MemberFormDto;
import com.cultureShop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    Member findByName(String name);

    // 소셜로그인 회원 조회
    @Query("select m from Member m where m.email = :email")
    Optional<Member> findSocialMem(String email);

    // 회원정보 조회 - 정보수정시
    @Query("select new com.cultureShop.dto.MemberFormDto(m.name, m.email, m.password, m.address, m.dtlAddress, m.tel) " +
            "from Member m where m.email = :email")
    MemberFormDto findMemDto(String email);

}
