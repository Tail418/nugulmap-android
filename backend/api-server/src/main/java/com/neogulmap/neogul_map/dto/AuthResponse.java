package com.neogulmap.neogul_map.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String token;      // 우리 서버의 JWT
    private String email;
    private String nickname;
    private String profileImage;
    private Long userId;
}
