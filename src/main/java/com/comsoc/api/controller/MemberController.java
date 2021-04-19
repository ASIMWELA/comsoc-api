package com.comsoc.api.controller;

import com.comsoc.api.payload.*;
import com.comsoc.api.service.MemberService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comsoc/members")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberController {

    final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    @Secured({"ROLE_ADMIN", "ROLE_MONITOR"})
    public ResponseEntity<ApiResponse>signup(@RequestBody SignupRequest signupRequest){
       return memberService.signupMember(signupRequest);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse>signIn(@RequestBody SigninRequest signinRequest) {
        return memberService.signIn(signinRequest);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/assign-role/{regNumber}/{role}")
    public ResponseEntity<ApiResponse> assignRole(@PathVariable String regNumber, @PathVariable String role){
        return memberService.assignRole(regNumber, role);
    }
    @Secured("ROLE_ADMIN")
    @PutMapping("/assign-position/{regNumber}/{position}")
    public ResponseEntity<ApiResponse> assignPosition(@PathVariable String regNumber, @PathVariable String position){
        return memberService.assignPosition(regNumber, position);
    }
    @Secured("ROLE_ADMIN")
    @PutMapping("/flush-positions")
    public ResponseEntity<ApiResponse> flushPositions(){
        return memberService.flushPositions();
    }

    //get executive members
    @GetMapping("/executives")
    public ResponseEntity<PagedResponse> getMembersWithPositions(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "6") Integer pageSize){
        return memberService.getMembersWithPositions(pageNo, pageSize);
    }

    //get members
    @GetMapping
    public ResponseEntity<PagedResponse> getMembers(@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "8") Integer pageSize){
        return memberService.getAllMembers(pageNo, pageSize);
    }
    @Secured("ROLE_ADMIN")
    @PutMapping("/remove-role/{regNumber}/{role}")
    public ResponseEntity<ApiResponse> removeRoleFromMember(@PathVariable String regNumber, @PathVariable String role){
        return memberService.removeRoleFromMember(regNumber, role);
    }
}
