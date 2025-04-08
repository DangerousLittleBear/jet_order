package com.example.ordersystem.sercurity;

import com.example.ordersystem.entity.Member;
import com.example.ordersystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("User not found with studentID: " + email);
                });

        if (!member.getActive()) {
            throw new UsernameNotFoundException("User account is inactive: " + email);
        }

        return new UserDetailsImpl(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getActive()
        );
    }
}
