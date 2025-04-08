package com.example.ordersystem.sercurity;

import com.example.ordersystem.entity.Member;
import com.example.ordersystem.payload.request.LoginRequest;
import com.example.ordersystem.payload.request.SignupRequest;
import com.example.ordersystem.payload.response.JwtResponse;
import com.example.ordersystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public JwtResponse login(LoginRequest loginRequest) {
        // 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // 인증된 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // JWT 토큰에 포함할 추가 정보
        Map<String, String> claims = new HashMap<>();
        claims.put("user_id", String.valueOf(member.getId()));
        claims.put("email", member.getEmail());

        // Access Token과 Refresh Token 생성
        String accessToken = jwtUtil.generateAccessToken(userDetails, claims);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 응답 객체 생성
        return new JwtResponse(
                accessToken,
                refreshToken,
                member.getId(),
                member.getEmail(),
                "Bearer"
        );
    }

    public JwtResponse signup(SignupRequest signupRequest) {
        if(isExistStudent(signupRequest)){

        }


        return new JwtResponse();
    }

    public JwtResponse refreshToken(String refreshToken) {
        try {
            // Refresh Token에서 사용자 정보 추출
            String email = jwtUtil.extractEmail(refreshToken);
            UserDetails userDetails = (UserDetails) authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, "")
            ).getPrincipal();

            // Refresh Token 유효성 검사
            if (!jwtUtil.validateToken(refreshToken, userDetails)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            }

            // Member 정보 조회
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 새로운 Access Token 생성
            Map<String, String> claims = new HashMap<>();
            claims.put("id", String.valueOf(member.getId()));
            claims.put("email", member.getEmail());

            String newAccessToken = jwtUtil.generateAccessToken(userDetails, claims);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            // 응답 객체 생성
            return new JwtResponse(
                    newAccessToken,
                    newRefreshToken,
                    member.getId(),
                    member.getEmail(),
                    "Bearer"
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    public boolean isExistStudent(SignupRequest signupRequest) {


        return true;
    }
}
