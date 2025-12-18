# 개발자 B 진행 상황 보고 (1)

이 문서는 `dev_B.md`의 초기 계획에 따라 현재까지 완료된 작업과 앞으로의 계획을 정리합니다.

## Ⅰ. 비교 및 요약 (vs `dev_B.md`)

`dev_B.md`의 **Phase 1, 2** 및 **Phase 3**의 일부를 완료했습니다.

### 완료된 작업:

1.  **API 및 데이터 계층 (`Phase 1.1`):**
    *   **DI (Hilt) 모듈 리팩토링:** `AppModule`을 `NetworkModule`, `ApiModule`, `RepositoryModule`로 분리하여 역할과 책임을 명확히 했습니다.
    *   **API 통신 계층 구현:** `AuthInterceptor`, `NugulApi` 인터페이스, Request DTOs를 구현하여 서버 통신 기반을 마련했습니다.
    *   **Repository 패턴 구현:** `Zone`/`ZoneDto` 모델과 매퍼, `ZoneRepository` 인터페이스 및 `ZoneRepositoryImpl` 구현을 통해 데이터 계층을 완성했습니다. `Result<T>`를 사용하여 안정성을 높였습니다.

2.  **UseCase 구현 (`Phase 1.2`):**
    *   `GetZonesUseCase`, `LoginUseCase`, `SaveTokenUseCase` 등 로그인 및 데이터 조회에 필요한 비즈니스 로직을 캡슐화하는 UseCase들을 구현했습니다.

3.  **디자인 시스템 기반 구축 (`Phase 1.3`):**
    *   **커스텀 테마 적용:** 앱의 정체성을 나타내는 신규 Color Palette(Light/Dark)를 `Color.kt`에 정의하고, 이를 `Theme.kt`에 적용하여 `NeogulmapTheme`을 완성했습니다.
    *   **공용 컴포넌트 제작:** 앱 전반에서 재사용될 `NugulPrimaryButton`을 `presentation/ui/components`에 추가했습니다.

4.  **사용자 인증 (`Phase 2`):**
    *   **Kakao SDK 연동:** 카카오 v2 SDK 의존성을 추가하고 `Application` 클래스에서 초기화했습니다.
    *   **로그인 로직 구현:** `LoginViewModel`과 `LoginScreen`을 구현하여 카카오 소셜 로그인부터 앱 자체 토큰 저장까지의 전체 인증 흐름을 완성했습니다.
    *   **네비게이션 연동:** `NavGraph`를 수정하여 앱 시작 시 `LoginScreen`을 보여주고, 로그인 성공 시 `HomeScreen`으로 이동하도록 설정했습니다.

5.  **UI & ViewModel 연동 (`Phase 3`):**
    *   `HomeViewModel`을 리팩토링하여 `Loading`, `Success`, `Error` 상태를 관리하는 `HomeUiState`를 노출하도록 변경했습니다.
    *   `HomeScreen`이 `HomeUiState`를 구독하여 상태별(로딩 중, 에러 발생, 데이터 로딩 성공) UI를 다르게 표시하도록 구현했습니다. 이로써 데이터 흐름이 UI까지 완전히 연결되었습니다.

## Ⅱ. 다음 단계 (Next Steps)

-   [x] **Phase 1.1: API 및 데이터 계층 설계**
-   [x] **Phase 1.2: UseCase 구현**
-   [x] **Phase 1.3: 디자인 시스템 기반 구축**
-   [x] **Phase 2: 사용자 인증**
-   [x] **Phase 3: HomeScreen과 ViewModel 연동**
-   [x] **Phase 3.1: 제보 화면(`ReportScreen`) 및 `ReportViewModel` 구현**
-   [ ] **Phase 3.2: 기타 화면(마이페이지, 검색, 공지사항) 프로토타입 구현**
-   [ ] **Phase 4: 테스트 및 안정화**
    -   [ ] 에뮬레이터 및 실기기 테스트
    -   [ ] 발견된 버그 수정
