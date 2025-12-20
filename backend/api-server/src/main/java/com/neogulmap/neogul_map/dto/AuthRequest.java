package com.neogulmap.neogul_map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String accessToken; // 소셜 서비스(카카오 등)에서 받은 액세스 토큰
    private String provider;    // "kakao", "google", "naver" 등
}
