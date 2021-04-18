package com.comsoc.api.repository;

import com.comsoc.api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByRegNumber(String regNumber);

    boolean existsByRegNumber(String regNumber);

    boolean existsByEmail(String email);
    @Transactional
    @Query(value = "SELECT * FROM members JOIN positions ON members.id=positions.member_id", nativeQuery = true)
    List<Member> getMembersWithPositions();
}
