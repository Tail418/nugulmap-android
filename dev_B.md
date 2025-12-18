# 🧑‍💻 개발자 B: UI & Data 설계 및 계획

개발자 B의 역할 구체적인 설계와 개발 계획

## 1. 🎯 주요 목표
- 사용자 인증, API 연동, 전체적인 UI/UX 구현.
- 안정적이고 확장 가능한 데이터 흐름을 구축하고, 일관된 디자인 시스템을 적용하여 사용자 UI을 향상시킵니다.

## 2. 📋 상세 개발 계획

### Phase 1: 기반 구축 (Foundation)

#### 1.1. API 통신 모듈 및 데이터 계층 설계
- **Retrofit/OkHttp 설정:**
    - `build.gradle.kts`에 Retrofit, OkHttp, Gson/Moshi 라이브러리 의존성을 추가합니다.
    - Hilt를 사용하여 `OkHttpClient`와 `Retrofit` 인스턴스를 싱글톤으로 제공하는 `NetworkModule`을 구현합니다.
    - OkHttp Interceptor를 추가하여 API 요청/응답 로깅 및 JWT 토큰을 헤더에 삽입하는 로직을 구현합니다.
- **API 인터페이스 정의 (`NugulApi.kt`):**
    - `api_specification.md`를 기반으로 `/users`, `/zones` 등 주요 엔드포인트에 대한 suspend 함수를 정의합니다.
    - `ApiResponse<T>`와 같은 제네릭 래퍼를 사용하여 API 응답을 일관되게 처리합니다.
- **Repository 패턴 구현:**
    - `ZoneRepository` 인터페이스와 `ZoneRepositoryImpl` 구현체를 만듭니다.
    - `ZoneRepositoryImpl`은 `NugulApi`를 의존성으로 주입받아 실제 API를 호출하고, `ZoneDto`를 `Zone` 도메인 모델로 매핑합니다.
    - Hilt를 사용해 `RepositoryModule`에서 Repository 의존성을 주입합니다.

#### 1.2. 디자인 시스템 구축
- **Theme 정의 (`ui/theme/`):**
    - `Color.kt`: 앱의 Primary, Secondary, Surface 등 주요 색상을 Material 3 가이드에 맞춰 정의합니다.
    - `Type.kt`: `Typography` 객체를 사용하여 `headlineLarge`, `bodyMedium` 등 텍스트 스타일을 정의합니다.
    - `Theme.kt`: 정의된 색상과 타이포그래피를 앱 전체에 적용하는 `NugulmapTheme` Composable 함수를 만듭니다.
- **공용 컴포저블 (`presentation/ui/components/`):**
    - `NugulPrimaryButton`, `NugulTextField`, `TopAppBar` 등 앱 전반에서 재사용될 기본 UI 컴포넌트를 미리 제작합니다.

### Phase 2: 사용자 인증

#### 2.1. 소셜 로그인 구현 (카카오)
- **Kakao SDK 연동:**
    - 카카오 개발자 콘솔에서 앱을 등록하고 네이티브 앱 키를 발급받습니다.
    - `local.properties`에 키를 저장하고 `build.gradle.kts`에서 참조합니다.
    - Kakao SDK 초기화 및 로그인 API 호출 로직을 구현합니다.
- **로그인 화면 (`LoginScreen.kt`):**
    - "카카오로 로그인" 버튼을 포함한 UI를 구성합니다.
    - `LoginViewModel`을 만들어 로그인 비즈니스 로직을 처리합니다.
- **토큰 관리:**
    - 로그인 성공 시 백엔드로부터 받은 JWT(Access/Refresh Token)를 Jetpack DataStore에 안전하게 저장합니다.
    - 앱 실행 시 DataStore에 토큰이 있는지 확인하여 자동 로그인을 처리하는 로직을 `MainActivity` 또는 `SplashScreen`에 구현합니다.

### Phase 3: 핵심 UI 및 기능 구현

#### 3.1. 흡연 구역 제보 화면
- **UI/UX 설계:**
    - 지도에서 위치 선택 (개발자 A와 협업), 주소 텍스트 입력, 상세 설명, 이미지 첨부를 위한 UI를 Compose로 구현합니다.
    - Coil 라이브러리를 사용하여 사용자가 선택한 이미지를 미리 보여줍니다.
- **ViewModel 구현 (`ReportViewModel.kt`):**
    - 사용자 입력(위치, 설명, 이미지)을 `StateFlow`로 관리합니다.
    - `MultipartBody.Part`를 사용하여 이미지를 API 서버에 업로드하는 로직을 구현합니다.
    - 제보 성공/실패에 따른 UI 상태(스낵바, 로딩 인디케이터)를 관리합니다.

#### 3.2. 기타 화면 구현
- **마이페이지 (`MypageScreen.kt`):**
    - 내가 제보한 흡연 구역 목록, 로그아웃, 회원 탈퇴 기능을 제공합니다.
    - `MypageViewModel`을 통해 사용자 정보를 조회합니다.
- **검색 화면 (`SearchScreen.kt`):**
    - 장소나 주소 키워드로 흡연 구역을 검색하는 UI를 구현합니다.
- **공지사항 화면 (`NoticeScreen.kt`):**
    - 관리자가 등록한 공지사항 목록을 보여주는 간단한 리스트 UI를 구현합니다.

## 3. 🤝 개발자 A와의 협업 포인트
- **데이터 공유:** `Zone` 도메인 모델의 구조를 공유하여 지도 마커 표시 및 클러스터링에 필요한 데이터를 제공합니다.
- **UI 통합:** 개발자 A가 구현한 `KakaoMap` Composable을 `HomeScreen`에 통합하고, 지도와 상호작용하는 UI(예: 하단 BottomSheet)를 함께 설계합니다.
- **API 연동:** 사용자 위치 기반으로 주변 흡연 구역을 조회하는 API(`/zones?lat=...&lng=...`) 호출 시점을 동기화합니다.
- **화면 전환:** `HomeScreen`의 특정 UI(예: 제보하기 버튼) 클릭 시 `ReportScreen`으로 이동하는 네비게이션 로직을 협의합니다.

## 4. ⏳ 예상 타임라인 (2주 스프린트 기준)
- **1주차:**
    - API 통신 모듈 및 데이터 계층 완성
    - 디자인 시스템 기본 틀 구축
    - 카카오 로그인 기능 구현 및 토큰 저장 로직 완성
- **2주차:**
    - 흡연 구역 제보 화면 UI 및 기능 구현
    - 마이페이지, 검색, 공지사항 등 나머지 화면 UI 프로토타입 완성
    - 개발자 A의 지도 컴포넌트와 UI 통합 작업 시작
