package com.neogulmap.neogul_map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neogulmap.neogul_map.config.security.jwt.TokenProvider;
import com.neogulmap.neogul_map.domain.User;
import com.neogulmap.neogul_map.dto.AuthResponse;
import com.neogulmap.neogul_map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthResponse login(String accessToken) {
        // 1. 카카오 API로 유저 정보 요청
        JsonNode userInfo = getKakaoUserInfo(accessToken);
        
        String oauthId = userInfo.get("id").asText();
        String nickname = userInfo.path("properties").path("nickname").asText();
        String rawEmail = userInfo.path("kakao_account").path("email").asText();
        String profileImage = userInfo.path("properties").path("profile_image").asText();

        final String email = (rawEmail == null || rawEmail.isEmpty()) ? oauthId + "@kakao.com" : rawEmail;

        // 2. DB 확인 및 저장 (가입/로그인)
        User user = userRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .oauthId(oauthId)
                            .oauthProvider("kakao")
                            .email(email)
                            .nickname(nickname)
                            .profileImage(profileImage)
                            .createdAt(LocalDateTime.now().toString())
                            .build();
                    return userRepository.save(newUser);
                });

        // 3. 우리 서버 전용 JWT 발급 (1일 유효)
        String jwtToken = tokenProvider.generateToken(user, Duration.ofDays(1));

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }

    private JsonNode getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.getBody());
        } catch (Exception e) {
            log.error("Kakao API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("카카오 유저 정보 조회 실패");
        }
    }
}
