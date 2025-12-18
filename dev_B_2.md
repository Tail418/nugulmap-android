# 🧑‍💻 개발자 B -> 🧑‍💻 개발자 A 협업 가이드

안녕하세요, 개발자 A님. `dev_B.md` 계획에 따라 UI 및 데이터 파트의 기반 작업이 완료되어, 지도(Map)와의 연동을 위해 이 문서를 작성합니다.

백엔드 API 연동, 로그인, 데이터 관리 로직이 구현되었으니, 이제 지도 표시에 필요한 데이터를 `ViewModel`로부터 받아 사용하실 수 있습니다.

## 1. Home 화면 데이터 연동 방법

`HomeScreen`과 `HomeViewModel`이 새로운 데이터 흐름에 맞게 업데이트되었습니다.

### `HomeViewModel`과 `HomeUiState`

-   `HomeViewModel`은 이제 UI 상태를 `HomeUiState`라는 `sealed interface`로 관리합니다.
-   `HomeUiState`는 3가지 상태를 가집니다.
    -   `Loading`: 데이터를 불러오는 중
    -   `Success(zones: List<Zone>)`: 데이터 로딩 성공. 흡연구역 목록(`zones`)을 포함합니다.
    -   `Error(message: String)`: 데이터 로딩 실패. 에러 메시지를 포함합니다.

### `HomeScreen`에서의 상태 처리

`HomeScreen`에서는 `ViewModel`의 `uiState`를 `collect`하여 `when`으로 상태를 분기 처리합니다.

```kotlin
// presentation/ui/screens/HomeScreen.kt

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    // ...

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                // 로딩 인디케이터 표시
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is HomeUiState.Error -> {
                // 에러 메시지 표시
                Text(text = "Error: ${state.message}", ...)
            }
            is HomeUiState.Success -> {
                // 성공 시 KakaoMap에 Zone 목록 전달
                KakaoMap(
                    modifier = Modifier.fillMaxSize(),
                    zones = state.zones, // <-- 이 데이터를 사용하시면 됩니다.
                    onZoneClick = { zone ->
                        selectedZone = zone
                    }
                )
            }
        }
        // ...
    }
}
```

**✅ A님 Action Item:** `KakaoMap` 컴포저블은 이제 `HomeUiState.Success` 상태일 때 `state.zones`로부터 `List<Zone>`을 받습니다. 이 데이터를 사용하여 마커 렌더링 및 클러스터링 로직을 구현해 주세요.

## 2. 주요 데이터 모델: `Zone`

지도에 표시될 흡연구역의 데이터 모델은 `domain/model/Zone.kt`에 정의되어 있습니다.

```kotlin
// domain/model/Zone.kt
data class Zone(
    val id: Long,
    val name: String,         // 표시될 이름 (주소 또는 설명)
    val description: String?, // 상세 설명
    val latitude: Double,     // 위도
    val longitude: Double,    // 경도
    val imageUrl: String?,    // Coil 등으로 바로 로드 가능한 전체 이미지 URL
    val address: String?,     // 주소
    val type: String,         // 타입 (e.g., Outdoor, Booth)
    val size: String?
)
```

-   **핵심 필드:** `latitude`, `longitude`를 사용하여 마커 위치를 잡고, `name`, `address` 등으로 정보를 표시할 수 있습니다.
-   `imageUrl`은 백엔드 Base URL이 포함된 **전체 이미지 주소**이므로, Coil과 같은 이미지 로딩 라이브러리에서 바로 사용 가능합니다.

## 3. 지도 데이터 새로고침

사용자가 지도를 움직여 현재 위치의 데이터를 다시 불러오고 싶을 때를 위해 `HomeViewModel`에 공개 함수를 마련했습니다.

```kotlin
// presentation/viewmodel/HomeViewModel.kt

fun loadZones(latitude: Double, longitude: Double, radius: Int = 1000) {
    // ... 내부 로직 ...
}
```

**✅ A님 Action Item:** `KakaoMap` 컴포저블 내에서 지도 이동이 멈추거나, "현재 위치에서 검색"과 같은 버튼 클릭 시 `viewModel.loadZones(lat, lng)`를 호출하여 해당 위치의 데이터를 새로 불러오도록 구현해 주세요.

## 4. 앱 실행 및 테스트 가이드

1.  **`local.properties` 설정:** 카카오 로그인을 위해 본인의 **Kakao Native App Key**를 `local.properties` 파일에 아래와 같이 추가해야 합니다.
    ```properties
    KAKAO_NATIVE_APP_KEY=your_kakao_native_key_here
    ```
2.  **앱 시작 화면:** 이제 앱은 `LoginScreen`에서 시작됩니다.
3.  **로그인:** "카카오로 3초만에 시작하기" 버튼을 눌러 로그인을 완료하면 `HomeScreen`으로 자동 이동합니다. 이후 지도 기능 테스트를 진행하시면 됩니다.

---
궁금한 점이 있다면 언제든지 편하게 문의해 주세요!
