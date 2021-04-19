package com.comsoc.api.security;

import com.comsoc.api.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MemberDetailsImpl implements UserDetails
{

    private String regNumber;

    private String email;

    @JsonIgnore
    private String password;

    public static MemberDetailsImpl build(Member member) {
        List<GrantedAuthority> authorities = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new MemberDetailsImpl(
                member.getRegNumber().toLowerCase(),
                member.getEmail().toLowerCase(),
                member.getPassword(),
                authorities);
    }

    private Collection<? extends GrantedAuthority> authorities;

    public String getEmail()
    {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    @Override
    public String getPassword()
    {
        return password ;
    }

    @Override
    public String getUsername()
    {
        return regNumber;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
