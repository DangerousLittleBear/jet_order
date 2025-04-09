package com.example.ordersystem.controller;


import com.example.ordersystem.payload.request.LoginRequest;
import com.example.ordersystem.payload.request.SignupRequest;
import com.example.ordersystem.payload.response.JwtResponse;
import com.example.ordersystem.sercurity.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest loginRequest) {
        JwtResponse JwtToken = authService.login(loginRequest);
        return JwtToken;
    }


    @PostMapping("/signup")
    public JwtResponse signup(@RequestBody SignupRequest signupRequest) {
        JwtResponse JwtToken = authService.signup(signupRequest);
        return JwtToken;
    }

    // 로그아웃은 일단 브라우저단에서 토큰값을 삭제하는 것으로 구현한다.
}
