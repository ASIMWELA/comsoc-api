package com.comsoc.api.repository;

import com.comsoc.api.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends PagingAndSortingRepository<Member, Long> {
    Optional<Member> findByRegNumber(String regNumber);
    boolean existsByRegNumber(String regNumber);
    boolean existsByEmail(String email);
    @Transactional
    @Query(value = "SELECT * FROM members JOIN positions ON members.id=positions.member_id", nativeQuery = true)
    Slice<Member> getMembersWithPositions(Pageable page);
    @Transactional
    @Query(value = "SELECT * FROM members WHERE members.id NOT IN (SELECT members.id FROM members JOIN positions ON members.id=positions.member_id)", nativeQuery = true)
    Slice<Member> getMembers(Pageable page);
}
