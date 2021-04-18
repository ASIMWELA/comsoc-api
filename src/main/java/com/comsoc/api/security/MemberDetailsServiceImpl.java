package com.comsoc.api.security;

import com.comsoc.api.entity.Member;
import com.comsoc.api.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberDetailsServiceImpl implements UserDetailsService
{

    MemberRepository memberRepository;
    public MemberDetailsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String regNumber) throws UsernameNotFoundException {

        Member member = memberRepository.findByRegNumber(regNumber).orElseThrow(
                ()->new EntityNotFoundException("No user with Reg number "+ regNumber)
        );

        return MemberDetailsImpl.build(member);
    }
}
