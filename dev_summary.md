# nugulmap-android 개발 현황 보고

이 문서는 `dev_B_1.md`와 `dev_B_3.md`의 내용을 종합하여 현재까지의 개발 진행 상황, 향후 계획, 발생한 문제점 및 프로젝트 분석을 정리합니다.

## 1. 진행 상황

### 완료된 작업

- **API 및 데이터 계층 구축 (`Phase 1.1`)**
    - **DI 리팩토링**: `AppModule`을 `NetworkModule`, `ApiModule`, `RepositoryModule`로 분리하여 역할과 책임을 명확히 함.
    - **API 통신 계층 구현**: `AuthInterceptor`, `NugulApi` 인터페이스를 구현하여 서버 통신 기반을 마련함.
    - **Repository 패턴 적용**: `Zone`/`ZoneDto` 모델과 `ZoneRepository`를 구현하고 `Result<T>`를 사용하여 데이터 처리의 안정성을 높임.

- **비즈니스 로직 캡슐화 (`Phase 1.2`)**
    - `GetZonesUseCase`, `LoginUseCase`, `SaveTokenUseCase` 등 핵심 비즈니스 로직을 UseCase로 구현함.

- **디자인 시스템 및 UI 기반 구축 (`Phase 1.3`)**
    - **커스텀 테마**: `NeogulmapTheme`을 정의하고 Light/Dark 컬러 팔레트를 적용함.
    - **공용 컴포넌트**: 앱 전반에서 재사용될 `NugulPrimaryButton`을 제작함.

- **사용자 인증 구현 (`Phase 2`)**
    - **Kakao SDK 연동**: 카카오 v2 SDK를 초기화하고, `LoginViewModel`과 `LoginScreen`을 통해 소셜 로그인부터 토큰 저장까지의 인증 흐름을 완성함.
    - **네비게이션 설정**: 로그인 상태에 따라 `LoginScreen` 또는 `HomeScreen`으로 이동하도록 `NavGraph`를 구성함.

- **화면 및 ViewModel 연동 (`Phase 3`)**
    - **`HomeScreen`**: `HomeViewModel`의 `HomeUiState`(`Loading`, `Success`, `Error`)를 구독하여 상태에 따른 UI를 동적으로 표시하도록 구현함.
    - **`ReportScreen` 프로토타입**: `ReportScreen`과 `ReportViewModel`의 기본 구조를 생성하고 네비게이션에 연결함.

- **빌드 오류 수정 시도**
    - 사용되지 않는 Naver Login SDK 의존성과 관련 코드를 제거함.
    - DI 모듈의 중복 import, UI 컴포넌트의 변경된 API 사용법, 누락된 import 등을 수정함.
    - 테마에서 발생하던 폰트 참조 오류를 `FontFamily.Default`로 대체하여 해결함.

## 2. 계획 및 todolist

- [ ] **1단계: 빌드 오류 해결**
    - [ ] `HomeViewModel`의 GMS 관련 `LatLng` 참조 오류 해결 (`com.kakao.vectormap.LatLng`으로 통일)
    - [ ] Gradle 캐시 초기화 및 의존성 재검토
- [ ] **2단계: `ReportScreen` 기능 구현**
    - [ ] `ReportViewModel`에 제보 데이터(`address`, `description`, `imageUri` 등)를 관리할 `ReportUiState` 설계
    - [ ] 서버 통신을 위한 `SubmitReportUseCase` 구현 및 ViewModel에 주입
    - [ ] `ReportScreen`에 주소/상세설명 입력 필드, 이미지 추가 버튼, "제보하기" 버튼 등 UI 요소 구현
    - [ ] 제보 성공/실패 시 `Snackbar`를 통한 사용자 피드백 제공 로직 구현
- [ ] **3단계: 기타 화면 프로토타입 구현**
    - [ ] 마이페이지 (`ProfileScreen`) 기능 구체화
    - [ ] 검색 화면 프로토타입 구현
    - [ ] 공지사항 화면 프로토타입 구현
- [ ] **4단계: 테스트 및 안정화**
    - [ ] 에뮬레이터 및 실기기 테스트를 통한 기능 검증
    - [ ] 발견된 버그 수정 및 최종 안정화

## 3. 문제점

현재 `clean & build` 시 다음과 같은 주요 빌드 오류가 발생하여 앱 실행이 불가능한 상태입니다.

1.  **`HomeViewModel.kt`의 GMS(Google Mobile Services) 관련 오류**
    - **오류**: `Unresolved reference 'maps'`, `Unresolved reference 'LatLng'`
    - **원인 분석**: `play-services-location` 의존성이 추가되었음에도 Gradle이 이를 인식하지 못하는 문제로 추정됩니다. 특히 `HomeViewModel`의 `com.google.android.gms.maps.model.LatLng`와 `KakaoMap.kt`의 `com.kakao.vectormap.LatLng` 간의 타입 충돌이 핵심 원인일 가능성이 높습니다.

2.  **`HomeScreen.kt`의 타입 추론 오류**
    - **오류**: `Cannot infer type for this parameter` (`isLocationPermissionGranted` 변수 선언부)
    - **원인 분석**: 1번의 GMS 관련 오류로 인해 컴파일러가 다른 코드의 타입을 정상적으로 추론하지 못하는 연쇄적인 문제입니다. 핵심 오류가 해결되면 자동으로 해결될 것으로 예상됩니다.

## 4. 프로젝트 분석

- **아키텍처**: 프로젝트는 **MVVM, UseCase, Repository 패턴, Hilt(DI)**를 사용하는 현대적인 안드로이드 아키텍처를 잘 따르고 있습니다. 이는 역할 분리가 명확하고 테스트 용이성이 높은 구조입니다.
- **의존성 문제**: 현재 발생한 빌드 오류의 근본 원인은 **라이브러리 의존성 관리**에 있습니다. Google 지도 서비스와 Kakao 지도 SDK를 동시에 사용하면서 발생한 `LatLng` 클래스의 네임스페이스 충돌이 대표적입니다. `HomeViewModel`은 위치 정보(Google)를, `KakaoMap` 컴포저블은 지도 표시(Kakao)를 담당하므로 두 `LatLng` 타입을 명확히 구분하거나 하나로 통일하는 작업이 시급합니다.
- **개발 단계**: 기능 구현(로그인, 메인 화면)과 프로토타입(제보 화면)이 혼재되어 있으며, 일부 기능(네이버 로그인, 특정 폰트)은 통합 과정에서 제거되었습니다. 이는 프로젝트가 기능 확장에서 안정화 단계로 넘어가기 전 **의존성을 정리하고 빌드를 안정시키는 과도기**에 있음을 시사합니다. 따라서 현재 가장 중요한 목표는 모든 빌드 오류를 해결하여 실행 가능한 상태를 만드는 것입니다.