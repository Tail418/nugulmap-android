# 🦝 너굴맵(Nugulmap) Android Application

이 프로젝트는 2025학년도 모바일 프로그래밍 기말 프로젝트를 위해 개발된 너굴맵 안드로이드 앱입니다.
흡연구역 정보를 제공하고, 지도 기반으로 주변 흡연구역을 탐색하며 상세 정보를 확인할 수 있습니다.

## 🚀 개발 환경 및 기술 스택

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material Design 3)
*   **Architecture:** MVVM + Clean Architecture
*   **Dependency Injection:** Hilt
*   **Network:** Retrofit2 + OkHttp3
*   **Asynchronous Programming:** Coroutines + Flow
*   **Map SDK:** KakaoMap Android SDK v2 (Vector Map)
*   **Image Loading:** Coil (미사용 - Image 컴포넌트 추가 시 활용 예정)
*   **Location:** Google Play Services Location

## ✨ 주요 기능

*   **지도 기반 흡연구역 탐색:** 현재 위치 또는 특정 위치 주변의 흡연구역을 지도에서 확인할 수 있습니다.
*   **흡연구역 상세 정보:** 마커 클릭 시 흡연구역의 주소, 타입, 설명 등의 상세 정보를 BottomSheet로 제공합니다.
*   **외부 지도 연동:** 상세 정보에서 '카카오맵에서 열기' 버튼을 통해 외부 카카오맵 앱으로 이동하여 길찾기 등을 할 수 있습니다.
*   **위치 권한 관리:** 앱 실행 시 사용자에게 위치 권한을 요청하고 관리합니다.
*   **백엔드 API 연동:** 외부 백엔드 서버와 통신하여 흡연구역 데이터를 실시간으로 가져옵니다.

## 🛠 빌드 및 실행 방법

### 1. 전제 조건

*   **Android Studio**: 최신 버전 설치
*   **Android SDK**: API Level 34 이상
*   **JDK 17+ (Backend)**: 백엔드 Spring Boot 서버 실행을 위해 필요
*   **Git**: 소스 코드 관리를 위해 필요

### 2. 프로젝트 설정

1.  **저장소 클론**:
    ```bash
    git clone https://github.com/meffy2020/nugulmap-android.git
    cd nugulmap-android
    ```

2.  **`local.properties` 파일 설정**:
    프로젝트 루트에 `local.properties` 파일을 생성하고 다음 내용을 추가합니다. (Git에 커밋되지 않도록 주의)

    ```properties
    # 카카오 개발자 콘솔에서 발급받은 네이티브 앱 키
    KAKAO_NATIVE_APP_KEY=YOUR_KAKAO_NATIVE_APP_KEY_HERE

    # 백엔드 서버의 Base URL (에뮬레이터: 10.0.2.2, 실물기기: PC의 내부 IP)
    BASE_URL="http://10.0.2.2:8080" # 예시
    
    # (선택 사항) 소셜 로그인 관련 설정 (개발자 B가 추가한 내용)
    GOOGLE_WEB_CLIENT_ID=YOUR_GOOGLE_WEB_CLIENT_ID
    NAVER_CLIENT_ID=YOUR_NAVER_CLIENT_ID
    NAVER_CLIENT_SECRET=YOUR_NAVER_CLIENT_SECRET
    NAVER_CLIENT_NAME=YOUR_NAVER_CLIENT_NAME
    ```

    *   `KAKAO_NATIVE_APP_KEY`: 카카오 개발자 콘솔([내 애플리케이션] -> [요약 정보] -> [네이티브 앱 키])에서 발급받은 키를 입력합니다.
    *   **카카오 개발자 콘솔에 패키지명 (`com.example.neogulmap`)과 키 해시를 반드시 등록해야 지도가 정상 동작합니다.** (키 해시는 앱 실행 시 Logcat에서 확인할 수 있습니다.)
    *   `BASE_URL`: 백엔드 서버의 주소를 입력합니다. 에뮬레이터에서 로컬 서버를 돌린다면 `http://10.0.2.2:8080`, 실물 기기에서 테스트한다면 `http://[PC_내부_IP]:8080` 형식입니다.

### 3. 백엔드 서버 실행 방법

백엔드 서버는 **Spring Boot** 기반 프로젝트입니다.

1.  **백엔드 폴더로 이동**:
    ```bash
    cd backend/api-server
    ```

2.  **서버 실행**:
    ```bash
    # Gradle Wrapper를 사용하여 서버 실행
    ./gradlew bootRun
    ```
    *   서버는 기본적으로 `8080` 포트에서 실행됩니다.
    *   JDK 17 이상 버전이 환경변수에 설정되어 있어야 합니다.

### 4. 안드로이드 앱 실행 방법

1.  **Android Studio 열기**:
    `nugulmap-android` 프로젝트 폴더를 Android Studio에서 엽니다.

2.  **Gradle Sync**:
    Android Studio에서 프로젝트가 완전히 로드되고 Gradle Sync가 완료될 때까지 기다립니다. (오른쪽 상단의 코끼리 아이콘 🐘 클릭)

3.  **앱 실행**:
    Android Studio 상단 툴바에서 에뮬레이터 또는 실제 기기를 선택한 후, 초록색 'Run' 버튼(재생 아이콘)을 클릭하여 앱을 실행합니다.

#### ⚠️ 에뮬레이터 실행 시 주의사항 (위치 설정)

에뮬레이터는 기본적으로 위치 정보가 Google 본사(미국) 등으로 설정되어 있을 수 있습니다. 한국 지도를 정상적으로 보기 위해서는 **위치 정보를 수동으로 설정**해야 합니다.

1.  에뮬레이터 실행 후, 우측 메뉴의 **더보기(점 3개)** 버튼 클릭
2.  **Location** 메뉴 선택
3.  **Single points** 탭에서 검색창에 'Seoul' 입력 후 검색하거나, 원하는 위도/경도 입력 (예: 위도 `37.5665`, 경도 `126.9780`)
4.  **Set Location** 버튼 클릭
5.  앱 내에서 '내 위치' 버튼 클릭 시 해당 좌표로 이동하는지 확인

## ✅ 평가를 위한 주요 기능 확인

*   **지도 표시 및 마커**: 앱 실행 후, 지도 위에 흡연구역 마커(너구리 로고)가 표시되는지 확인합니다.
*   **위치 권한**: 앱 실행 시 위치 권한 요청 팝업이 뜨는지 확인합니다. 권한 허용 후, 지도 우측 하단의 내 위치 버튼을 클릭하면 현재 위치로 이동하는지 확인합니다.
*   **마커 클릭 시 상세 정보**: 지도 위의 마커를 클릭하면 화면 하단에 해당 흡연구역의 상세 정보가 담긴 BottomSheet가 올라오는지 확인합니다.
*   **외부 지도 연동**: BottomSheet에서 '카카오맵에서 열기' 버튼 클릭 시 외부 카카오맵 앱이 실행되는지 확인합니다.
*   **백엔드 통신**: 앱 실행 시 `Zones: XX` (XX는 0이 아닌 숫자)가 화면 좌측 상단에 표시되며, 데이터가 정상적으로 로드되었는지 확인합니다. (디버그용, 배포 시 제거 가능)

---

## 👨‍💻 공동 작업자 정보

*   **개발자 A (본인)**: 지도 연동, 마커 표시, 위치 기능, UI/UX (지도 관련)
*   **개발자 B**: 소셜 로그인 기능 (카카오, 네이버, 구글), 회원 가입, 사용자 정보 관리 (구현 예정)

---
