# 개발자 B 진행 상황 보고 (3)

이 문서는 현재까지의 작업 내역을 요약하고, 남아있는 오류를 분석하며, 앞으로의 구체적인 작업 계획과 설계를 제시합니다.

## Ⅰ. 현재까지 완료된 작업

- **`ReportScreen` 및 `ReportViewModel` 프로토타입 생성**:
  - `presentation/ui/screens/`에 `ReportScreen.kt` 파일 생성
  - `presentation/viewmodel/`에 `ReportViewModel.kt` 파일 생성
- **네비게이션 설정**:
  - `navigation/Screen.kt`에 `Report` 스크린 경로 추가
  - `navigation/NavGraph.kt`에 `ReportScreen`으로 이동하는 로직 추가
- **빌드 오류 수정 시도**:
  - **Naver SDK 관련**:
    - `app/build.gradle.kts`에서 Naver Login SDK 의존성 주석 처리
    - `NugulmapApplication.kt`와 `LoginScreen.kt`에서 관련 코드 및 리소스 제거
  - **DI(Hilt) 모듈 수정**:
    - `di/RepositoryModule.kt`의 중복된 `ZoneRepository` import 문제 해결
  - **UI 컴포넌트 수정**:
    - `KakaoMap.kt`: `CameraUpdateFactory`의 API 사용법을 `newCenterPosition`으로 수정
    - `SearchBar.kt`: Material3 API 변경에 따라 `TextFieldDefaults.outlinedTextFieldColors`를 `OutlinedTextFieldDefaults.colors`로 수정
    - `ProfileScreen.kt`, `SignupScreen.kt`: `background` Modifier의 누락된 import 추가
  - **테마 및 리소스 수정**:
    - `ui/theme/Type.kt`: 참조 오류를 해결하기 위해 `KakaoFontFamily`, `RighteousFontFamily`를 `FontFamily.Default`로 대체
    - `LoginScreen.kt`: 누락된 `kakao_logo`, `google_logo` 리소스를 사용하는 `SocialLoginButton` 제거

## Ⅱ. 현재 빌드 오류 분석

현재 `clean & build` 시도 시 다음과 같은 주요 오류들이 발생하고 있습니다.

1.  **`HomeViewModel.kt`의 GMS 관련 오류**:
    - **오류 내용**: `Unresolved reference 'maps'`, `Unresolved reference 'LatLng'` 등
    - **원인 분석**: `play-services-location` 의존성이 `build.gradle.kts`에 추가되었음에도 불구하고, Gradle이 해당 의존성을 제대로 인식하지 못하는 것으로 보입니다. 이는 Android Studio의 Gradle 캐시 문제이거나, 다른 라이브러리와의 충돌일 수 있습니다. 특히 `HomeViewModel`의 `com.google.android.gms.maps.model.LatLng`와 `KakaoMap.kt`의 `com.kakao.vectormap.LatLng`가 충돌할 가능성이 있습니다.

2.  **`HomeScreen.kt`의 타입 추론 오류**:
    - **오류 내용**: `Cannot infer type for this parameter`
    - **원인 분석**: `isLocationPermissionGranted` 상태 변수 선언부에서 발생하며, 이는 보통 다른 근본적인 오류(예: GMS 관련 오류)로 인해 컴파일러가 타입 추론에 실패할 때 나타나는 연쇄적인 문제입니다.

## Ⅲ. 향후 작업 계획 및 설계

### 1단계: 빌드 오류 해결

가장 먼저 모든 빌드 오류를 해결하여 앱을 실행 가능한 상태로 만드는 데 집중합니다.

- **`HomeViewModel.kt` GMS 오류 해결**:
  1.  `HomeViewModel`에서 사용하는 `LatLng`을 `com.google.android.gms.maps.model.LatLng`에서 **`com.kakao.vectormap.LatLng`** 로 변경합니다. `currentLocation` 상태를 `KakaoMap` 컴포저블에 전달하고 있으므로, 타입을 일치시키는 것이 논리적으로 맞습니다.
  2.  `FusedLocationProviderClient` 관련 오류는 `play-services-location` 의존성이 이미 있으므로, `LatLng` 타입 변경 후에도 문제가 지속되면 Gradle 캐시를 다시 한번 초기화(`File > Invalidate Caches / Restart`)하거나 `build.gradle.kts` 파일의 다른 의존성을 검토합니다.

- **`HomeScreen.kt` 타입 추론 오류 해결**:
  - 위의 GMS 오류가 해결되면 이 문제는 자동으로 해결될 가능성이 높습니다.

### 2단계: `ReportScreen` 기능 구현

빌드가 성공하면, `ReportScreen`의 구체적인 UI와 로직을 구현합니다. `AddLocationModal.kt`의 UI/UX를 참고하여 일관성을 유지합니다.

- **`ReportViewModel.kt` 설계**:
  - **상태 관리**: `ReportUiState`를 확장하여 주소, 상세 설명, 이미지 URI 등 제보에 필요한 데이터 필드를 포함시킵니다.
    ```kotlin
    data class ReportUiState(
        val address: String = "",
        val description: String = "",
        val imageUri: Uri? = null,
        val isLoading: Boolean = false,
        val canSubmit: Boolean = false,
        val error: String? = null,
        val reportSuccess: Boolean = false
    )
    ```
  - **UseCase 의존성 추가**: 서버에 제보 데이터를 전송할 `SubmitReportUseCase`를 생성하고 ViewModel에 주입합니다.
  - **함수 구현**:
    - `onAddressChanged(String)`
    - `onDescriptionChanged(String)`
    - `onImageSelected(Uri?)`
    - `submitReport()`: `SubmitReportUseCase`를 호출하여 서버에 데이터를 전송하고 `isLoading`, `reportSuccess` 등 상태를 업데이트합니다.

- **`ReportScreen.kt` UI 설계**:
  - `AddLocationModal`처럼 `OutlinedTextField`를 사용하여 주소와 상세 설명을 입력받습니다.
  - 이미지 추가를 위한 `IconButton`과 선택된 이미지를 보여주는 `Image` 컴포저블을 구현합니다.
  - `NugulPrimaryButton`을 사용하여 "제보하기" 버튼을 만듭니다.
  - `ReportUiState`의 `isLoading` 상태에 따라 로딩 인디케이터를 표시합니다.
  - 제보 성공/실패 시 `Snackbar`를 통해 사용자에게 피드백을 제공합니다.

### 3단계: 최종 정리 및 문서화

- 모든 기능이 정상적으로 동작하는지 확인합니다.
- `dev_B_1.md`와 `dev_B_3.md`를 최종 작업 내역에 맞게 업데이트하여 작업을 마무리합니다.
