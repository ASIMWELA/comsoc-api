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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
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
        if(memberRepository.existsByRegNumber(signupRequest.getRegNumber().toLowerCase())) {
            return new ResponseEntity<>(new ApiResponse(false, "Registration number already exist in the system"), HttpStatus.CONFLICT);
        }
        if(memberRepository.existsByEmail(signupRequest.getEmail().toLowerCase())){
            return new ResponseEntity<>(new ApiResponse(false, "Email already exist in the system"), HttpStatus.CONFLICT);
        }


        if(!signupRequest.getRegNumber().trim().toLowerCase().contains("bsc") && (!signupRequest.getRegNumber().trim().toLowerCase().contains("bed-com"))){
            return new ResponseEntity<>(new ApiResponse(false, "Registration number not allowed"), HttpStatus.BAD_REQUEST);
        }
        Member member = new Member(signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getEmail().toLowerCase(),
                signupRequest.getPassword(),
                signupRequest.getRegNumber().toLowerCase(),
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
                new UsernamePasswordAuthenticationToken(signinRequest.getRegNumber().toLowerCase(), signinRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateJwtToken(authentication);
        Member member =memberRepository.findByRegNumber(tokenProvider.getRegNumberFromToken(jwt).toLowerCase()).orElseThrow(
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
        Member member = memberRepository.findByRegNumber(regNumber.toLowerCase()).orElseThrow(
                ()->new EntityNotFoundException("No member found with reg number " + regNumber)
        );

       if(!ERole.contains(roleName.toUpperCase().trim())){
           return new ResponseEntity<>(new ApiResponse(false, "Invalid role name"), HttpStatus.BAD_REQUEST);
       }

        Role role = roleRepository.findByName(ERole.valueOf(roleName.toUpperCase())).orElseThrow(()->new EntityNotFoundException("Role not set"));
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
        Member member = memberRepository.findByRegNumber(regNumber.toLowerCase()).orElseThrow(
                ()->new EntityNotFoundException("No member found with reg number " + regNumber)
        );
        if(!EPosition.contains(position.toUpperCase().trim())){
            return new ResponseEntity<>(new ApiResponse(false, "Invalid position name"), HttpStatus.BAD_REQUEST);
        }
        Position mPosition = positionRepository.findByName(EPosition.valueOf(position.toUpperCase())).orElseThrow(()->new EntityNotFoundException("No position found with name "+position));

        if(member.getPosition() != null){
           return new ResponseEntity<>(new ApiResponse(false, "Member is already assigned a position"), HttpStatus.BAD_REQUEST);
        }
        mPosition.setMember(member);
        member.setPosition(mPosition);
        positionRepository.save(mPosition);
        memberRepository.save(member);
        return new ResponseEntity<>(new ApiResponse(true, "Position assigned"), HttpStatus.OK);
    }

    public ResponseEntity<PagedResponse> getMembersWithPositions(int pageNo, int pageSize){
         Pageable pageRequest = PageRequest.of(pageNo, pageSize);
         Slice<Member> membersWithPositions = memberRepository.getMembersWithPositions(pageRequest);

         PageMetadata pageMetadata = new PageMetadata();
         pageMetadata.setFirstPage(membersWithPositions.isFirst());
         pageMetadata.setLastPage(membersWithPositions.isLast());
         pageMetadata.setHasNext(membersWithPositions.hasNext());
         pageMetadata.setHasPrevious(membersWithPositions.hasPrevious());
         pageMetadata.setPageNumber(membersWithPositions.getNumber());
         pageMetadata.setPageSize((membersWithPositions.getSize()));
         pageMetadata.setNumberOfElementsOnPage(membersWithPositions.getNumberOfElements());

         PagedResponse response = new PagedResponse();
         response.setMembersList(membersWithPositions.getContent());
         response.setPageMetadata(pageMetadata);
         return new ResponseEntity<>(response,HttpStatus.OK );
    }

    public ResponseEntity<PagedResponse> getAllMembers(int pageNo, int pageSize){
        Pageable pageRequest = PageRequest.of(pageNo, pageSize);
        Slice<Member> members = memberRepository.getMembers(pageRequest);
        //get page metadata
        PageMetadata pageMeta = PageMetadata.builder()
                .firstPage(members.isFirst())
                .hasNext(members.hasNext())
                .hasPrevious(members.hasPrevious())
                .lastPage(members.isLast())
                .pageSize(members.getSize())
                .pageNumber(members.getNumber())
                .numberOfElementsOnPage(members.getNumberOfElements())
                .build();
        return new ResponseEntity<>(PagedResponse.builder().membersList(members.getContent()).pageMetadata(pageMeta).build(),HttpStatus.OK );
    }
    public ResponseEntity<ApiResponse> flushPositions(){

        List<Position> positions = positionRepository.findAll();

        //All positions will be picked since the society only has a maximum on 13 positions
        Pageable page = PageRequest.of(0, 15);
        Slice<Member> members = memberRepository.getMembersWithPositions(page);
        if(members.hasContent()){
            members.getContent().forEach(member -> {
                member.setPosition(null);
                memberRepository.save(member);
            });
        }
        positions.forEach(position -> {
            position.setMember(null);
            positionRepository.save(position);
        });
        return new ResponseEntity<>(new ApiResponse(true, "Operation successful"), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> removeRoleFromMember(String regNumber, String roleName){

        Member member = memberRepository.findByRegNumber(regNumber.toLowerCase()).orElseThrow(
                ()->new EntityNotFoundException("No member found with Registration number "+regNumber)
        );
        if(!ERole.contains(roleName.toUpperCase())){
            return new ResponseEntity<>(new ApiResponse(false, "Invalid role name"), HttpStatus.BAD_REQUEST);
        }
        Role role = roleRepository.findByName(ERole.valueOf(roleName.toUpperCase())).orElseThrow(()->new EntityNotFoundException("No reol found with name " + roleName));
        List<Role> memberRoles = member.getRoles();
        if(memberRoles.size() == 1 ){
            return new ResponseEntity<>(new ApiResponse(false, "Member has only 1 role"), HttpStatus.BAD_REQUEST);
        }
        int index = -1;
        for (int i = 0 ; i < memberRoles.size(); i++) {
            if (memberRoles.get(i).getName().name().equals(role.getName().name())) {
                index = i;
            }
        }
        if(index == -1){
            return new ResponseEntity<>(new ApiResponse(false, "Member does not have this role"), HttpStatus.BAD_REQUEST);
        }else {
            memberRoles.remove(index);
            member.setRoles(memberRoles);
            memberRepository.save(member);
            return new ResponseEntity<>(new ApiResponse(true, "Role removed successfully"), HttpStatus.OK);

        }
    }
}
