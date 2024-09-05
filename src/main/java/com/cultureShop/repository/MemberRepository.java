package com.cultureShop.repository;

import com.cultureShop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    Member findByName(String name);

    @Query("select m from Member m where m.email = :email")
    Optional<Member> findSocialMem(String email);

}
