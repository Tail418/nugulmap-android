# 백엔드 API 요구사항: 사용자 인증 (소셜 로그인)

이 문서는 nugulmap-android 앱의 소셜 로그인 기능 구현을 위해 백엔드에 필요한 API 사양을 정의합니다.

## 1. 사용자 생성 및 로그인

- **Endpoint**: `/users`
- **HTTP Method**: `POST`
- **Content-Type**: `multipart/form-data`

### 설명

클라이언트에서 카카오 등 소셜 로그인을 완료한 후, 해당 사용자 정보를 받아 처리하는 API입니다.

-   **신규 사용자**: 전달받은 `oauthId`와 `oauthProvider`로 기존 사용자를 조회했을 때 정보가 없다면, 새로운 사용자로 데이터베이스에 등록합니다.
-   **기존 사용자**: 이미 등록된 사용자라면, 로그인 요청으로 간주하고 성공을 응답합니다. (필요시 닉네임, 이메일 등 정보 업데이트)

### 요청 (Request)

`multipart/form-data` 형식으로 아래 두 `Part`를 전송합니다.

1.  **`userData` (필수)**
    -   **타입**: `application/json` 형식의 문자열
    -   **설명**: 사용자의 기본 정보가 담긴 JSON 객체입니다.
    -   **JSON 구조**:
        ```json
        {
          "email": "user@example.com",
          "oauthId": "1234567890",
          "oauthProvider": "kakao",
          "nickname": "사용자닉네임"
        }
        ```

2.  **`profileImage` (선택)**
    -   **타입**: 이미지 파일 (e.g., `image/jpeg`, `image/png`)
    -   **설명**: 사용자의 프로필 이미지 파일입니다.
    -   **참고**: 현재 안드로이드 클라이언트는 로그인 시 이 필드를 `null`로 보내고 있으나, 추후 프로필 수정 기능 등을 위해 해당 파트를 처리할 수 있도록 구현해야 합니다.

### 응답 (Response)

#### 요청 성공 시

-   **HTTP Status Code**: `200 OK` 또는 `201 Created`
-   **Content-Type**: `application/json`
-   **Body**:
    ```json
    {
      "success": true,
      "message": "요청에 성공했습니다.",
      "data": null
    }
    ```
    -   `success`: 반드시 `true`여야 합니다.
    -   `data`: 현재 클라이언트에서 사용하지 않으므로 `null` 또는 비워둘 수 있습니다.

#### 요청 실패 시

-   **HTTP Status Code**: `4xx` 또는 `5xx` (상황에 맞는 코드 사용)
-   **Content-Type**: `application/json`
-   **Body**:
    ```json
    {
      "success": false,
      "message": "실패 원인에 대한 설명 메시지",
      "data": null
    }
    ```
    -   `success`: 반드시 `false`여야 합니다.
    -   `message`: 클라이언트에게 노출될 수 있는 에러 메시지입니다.
