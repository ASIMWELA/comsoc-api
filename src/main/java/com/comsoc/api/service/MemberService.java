package com.comsoc.api.service;

import com.comsoc.api.Enum.EPosition;
import com.comsoc.api.Enum.ERole;
import com.comsoc.api.entity.Member;
import com.comsoc.api.entity.Position;
import com.comsoc.api.entity.Role;
import com.comsoc.api.exception.EntityNotFoundException;
import com.comsoc.api.payload.*;
import com.comsoc.api.repository.MemberRepository;
import com.comsoc.api.repository.PositionRepository;
import com.comsoc.api.repository.RoleRepository;
import com.comsoc.api.security.JwtTokenProvider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberService {

    final MemberRepository memberRepository;
    final RoleRepository roleRepository;
    final PasswordEncoder passwordEncoder;
    final AuthenticationManager authenticationManager;
    final JwtTokenProvider tokenProvider;
    final PositionRepository positionRepository;

    public MemberService(MemberRepository memberRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager,
                         JwtTokenProvider tokenProvider,
                         PositionRepository positionRepository) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.positionRepository = positionRepository;
    }

    public ResponseEntity<ApiResponse> signupMember(@Valid @RequestBody SignupRequest signupRequest){
        if(memberRepository.existsByRegNumber(signupRequest.getRegNumber())) {
            return new ResponseEntity<>(new ApiResponse(false, "Registration number already exist in the system"), HttpStatus.CONFLICT);
        }
        if(memberRepository.existsByEmail(signupRequest.getEmail())){
            return new ResponseEntity<>(new ApiResponse(false, "Email already exist in the system"), HttpStatus.CONFLICT);
        }


        if(!signupRequest.getRegNumber().trim().toLowerCase().contains("bsc") && (!signupRequest.getRegNumber().trim().toLowerCase().contains("bed-com"))){
            return new ResponseEntity<>(new ApiResponse(false, "Registration number not allowed"), HttpStatus.BAD_REQUEST);
        }
        Member member = new Member(signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getRegNumber(),
                signupRequest.getYear());
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Role roleUser = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(
                ()->new EntityNotFoundException("Role not set")
        );
        member.setRoles(Collections.singletonList(roleUser));
        member.setDisabled(false);
        memberRepository.save(member);
        return new ResponseEntity<>(new ApiResponse(true, "Member saved successfully"), HttpStatus.CREATED);
    }

    public ResponseEntity<SigninResponse> signIn(@Valid @RequestBody SigninRequest signinRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getRegNumber(), signinRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateJwtToken(authentication);
        Member member =memberRepository.findByRegNumber(tokenProvider.getRegNumberFromToken(jwt)).orElseThrow(
                ()->new EntityNotFoundException("User not found with the name")
        );

        SigninResponse response = new SigninResponse();
        TokenPayload tokenPayload = new TokenPayload();

        tokenPayload.setAccessToken(jwt);
        tokenPayload.setType("Bearer");
        tokenPayload.setExpiresIn(tokenProvider.getExpirationMinutes(jwt));
        response.setUserData(member);
        response.setTokenPayload(tokenPayload);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> assignRole(String regNumber, String roleName){
        Member member = memberRepository.findByRegNumber(regNumber).orElseThrow(
                ()->new EntityNotFoundException("No member found with reg number " + regNumber)
        );

       if(!ERole.contains(roleName.toUpperCase().trim())){
           return new ResponseEntity<>(new ApiResponse(false, "Invalid role name"), HttpStatus.BAD_REQUEST);
       }

        Role role = roleRepository.findByName(ERole.valueOf(roleName)).orElseThrow(()->new EntityNotFoundException("Role not set"));
        List<Role> memberRoles = member.getRoles();
        //check if the user has already been given the role
        for (Role value : memberRoles) {
            if (value.getName().name().equals(role.getName().name())) {
                return new ResponseEntity<>(new ApiResponse(false, "Role Already assigned"), HttpStatus.BAD_REQUEST);
            }
        }
        memberRoles.add(role);
        member.setRoles(memberRoles);
        memberRepository.save(member);
        return new ResponseEntity<>(new ApiResponse(true, "Role assigned successfully"), HttpStatus.OK);
    }
    public ResponseEntity<ApiResponse> assignPosition(String regNumber, String position){
        Member member = memberRepository.findByRegNumber(regNumber).orElseThrow(
                ()->new EntityNotFoundException("No member found with reg number " + regNumber)
        );
        if(!EPosition.contains(position.toUpperCase().trim())){
            return new ResponseEntity<>(new ApiResponse(false, "Invalid position name"), HttpStatus.BAD_REQUEST);
        }
        Position mPosition = positionRepository.findByName(EPosition.valueOf(position)).orElseThrow(()->new EntityNotFoundException("No position found with name "+position));

        if(member.getPosition() != null){
           return new ResponseEntity<>(new ApiResponse(false, "Member is already assigned a position"), HttpStatus.BAD_REQUEST);
        }
        mPosition.setMember(member);
        member.setPosition(mPosition);
        positionRepository.save(mPosition);
        memberRepository.save(member);
        return new ResponseEntity<>(new ApiResponse(true, "Position assigned"), HttpStatus.OK);
    }

    public ResponseEntity<Page<ExecutiveMembers>> getMembersWithPositions(){
//        List<Member> membersWithPositions = memberRepository.getMembersWithPositions();
//        return new ResponseEntity<>(ExecutiveMembers.builder().executives(membersWithPositions).build(), HttpStatus.OK);
         //TODO:Complete The method by implementing pagination
        return null;
    }

    public ResponseEntity<Members> getAllMembers(){
        //TODO: Complete the method by paginating the response

        return null;
    }
    public ResponseEntity<ApiResponse> flushPositions(){
        List<Position> positions = positionRepository.findAll();
        List<Member> members = memberRepository.getMembersWithPositions();
        positions.forEach(position -> {
            position.setMember(null);
            positionRepository.save(position);
        });

        members.forEach(member -> {
            member.setPosition(null);
            memberRepository.save(member);
        });
        return new ResponseEntity<>(new ApiResponse(true, "Operation successful"), HttpStatus.OK);
    }
}
