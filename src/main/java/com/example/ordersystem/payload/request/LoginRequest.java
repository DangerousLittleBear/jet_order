package com.example.ordersystem.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "유저 이메일은 필수 값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    private String password;
}
