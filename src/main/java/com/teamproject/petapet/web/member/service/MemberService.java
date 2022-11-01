package com.teamproject.petapet.web.member.service;

import com.teamproject.petapet.domain.member.Member;
import com.teamproject.petapet.web.member.dto.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * 박채원 22.10.09 작성
 * 장사론 22.10.19 로그인 추가
 */

public interface MemberService {
    List<Member> getMemberList();
    void deleteMember(String memberId);
    void addMemberReport(String memberId);
    void updateMemberStopDate(String memberId);
    int[] getGenderList();
    List<Integer> getAgeList();

    TokenDto login(LoginDto loginDto);
    void join(JoinDto joinDto);
    boolean duplicateCheckMemberId(String memberId);
    Map<String, String> validateHandling(BindingResult bindingResult);
    MemberDto memberInfo(String memberId);

    boolean checkMemberPw(String memberId, String memberPw);





}
