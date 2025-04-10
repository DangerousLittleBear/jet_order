package com.example.ordersystem.service;


import com.example.ordersystem.entity.Member;
import com.example.ordersystem.payload.request.SignupRequest;
import com.example.ordersystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    public Member createMember(SignupRequest signupRequest ,String encodedPassword ) {

        Member member = new Member();
        member.setEmail(signupRequest.getEmail());
        member.setPassword(encodedPassword);

        Member savedMember = memberRepository.save(member);

        return savedMember;

    }

    public Member findMemberById(UUID id) {
        return memberRepository.findById(id).orElse(null);
    }

//    public Member isMemberValid(UUID memberId) {
//
//        Member member = memberRepository.findById(memberId).orElse(null);
//
//        if (member == null) {
//            return null;
//        }
//        return member;
//    }


    public boolean isExistMember(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }


}
