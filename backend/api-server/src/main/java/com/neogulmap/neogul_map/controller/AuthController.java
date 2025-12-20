package com.neogulmap.neogul_map.controller;

import com.neogulmap.neogul_map.dto.AuthRequest;
import com.neogulmap.neogul_map.dto.AuthResponse;
import com.neogulmap.neogul_map.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/login/kakao")
    public ResponseEntity<?> loginWithKakao(@RequestBody AuthRequest authRequest) {
        log.info("카카오 로그인 요청 시작");
        try {
            AuthResponse response = kakaoAuthService.login(authRequest.getAccessToken());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            log.error("카카오 로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
