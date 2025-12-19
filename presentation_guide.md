# 🦝 너굴맵(Neogulmap) 발표 가이드 핵심 요약

## 1. 프로젝트 개요
- **서비스명**: 너굴맵 (Neogulmap)
- **핵심 가치**: 공공데이터 기반 전국 흡연구역 정보 제공 및 사용자 참여형 커뮤니티 지도 서비스.
- **주요 기능**: 실시간 내 주변 흡연구역 확인, 새로운 구역 제보/등록, 검색 기반 위치 확인.

---

## 2. 기술 스택 선택 이유 (Database)

### **[Backend] H2 Database (In-memory)**
- **이유**: **개발 생산성과 테스트 독립성.**
- **설명**: 프로젝트 초기 단계에서 잦은 스키마 변경에 유연하게 대응하고, 별도의 DB 서버 구축 없이도 팀원 모두가 동일한 환경에서 API를 테스트할 수 있도록 선택했습니다. 실제 운영 시에는 MySQL 등으로 전환이 용이하도록 JPA를 사용했습니다.

### **[Android] Room DB (Local)**
- **이유**: **Offline-first 경험 제공.**
- **설명**: 네트워크가 불안정한 환경에서도 사용자가 이전에 확인한 정보를 즉시 볼 수 있게 합니다. 서버 호출 횟수를 줄여 배터리와 데이터를 절약하며, Jetpack Compose와 연동하여 실시간 UI 업데이트를 구현하기에 최적입니다.

---

## 3. 프로젝트 아키텍처 (Clean Architecture)
저희는 코드를 크게 세 가지 계층으로 분리했습니다.

1. **Presentation Layer (UI)**: Jetpack Compose, ViewModel. (사용자의 눈에 보이는 부분)
2. **Domain Layer (Business Logic)**: UseCase, Domain Model. (가장 순수한 비즈니스 규칙)
3. **Data Layer (Data Source)**: Repository, API, RoomDB, DTO, Entity. (데이터를 가져오는 상세 방법)

> **왜 이렇게 나누었나요?**  
> "지도를 카카오맵에서 구글맵으로 바꾸거나, 서버 DB를 변경하더라도 앱의 핵심 로직(Domain)은 전혀 수정할 필요가 없도록 만들기 위해서입니다. 이를 **'관심사의 분리'**라고 합니다."

---

## 4. 데이터 모델의 종류 (DTO, Entity, Domain)
질문자가 "왜 모델이 이렇게 많냐"고 물으면 이렇게 답하세요.

- **DTO (Data Transfer Object)**: `ZoneDto` - **서버와 통신용**. API 응답 구조에 맞춘 바구니.
- **Entity**: `ZoneEntity` - **로컬 DB 저장용**. RoomDB 테이블 구조에 맞춘 설계도.
- **Domain Model**: `Zone` - **앱 내부 비즈니스용**. UI나 로직에서 실제로 사용하는 순수한 데이터 객체.
- **Mappers**: `ZoneMappers` - 이 세 가지 모델 사이를 변환해주는 통로. (계층 간 결합도를 낮춤)

---

## 5. 핵심 로직: 데이터가 화면에 나오기까지
1. **Fetch**: `NugulApi`가 서버에서 `ZoneDto` 리스트를 가져옵니다.
2. **Sync**: 받아온 데이터를 `ZoneEntity`로 변환하여 `RoomDB`에 저장(Upsert)합니다.
3. **Load**: `GetZonesUseCase`가 DB에서 데이터를 읽어와 `Zone` 모델로 변환합니다.
4. **Observe**: `HomeViewModel`의 `StateFlow`에 데이터가 담깁니다.
5. **Render**: `HomeScreen`의 지도가 변화를 감지하여 마커를 그립니다.

---

## 6. PR 및 Merge 컨플릭트 해결 사례
- **상황**: `feat/android-map-core`(지도 UI)와 `devB`(로컬 DB 로직) 병합 중 발생.
- **파일**: `HomeScreen.kt`, `HomeViewModel.kt`, `build.gradle.kts`.
- **해결**: 
  1. `Git Rebase`를 통해 베이스 코드를 최신화.
  2. 팀원 간 공동 코드 리뷰를 통해 UI 마커 표시 로직과 DB 로딩 로직의 접점을 `Domain Model`로 통일.
  3. 충돌을 계기로 `Mappers`를 도입하여 데이터 구조 의존성을 분리.

---

## 7. 주요 Q&A 대비

**Q: DTO를 안 쓰고 Entity를 바로 UI에 쓰면 안 되나요?**
**A:** "안 됩니다. 만약 DB 컬럼명이 바뀌면 UI 코드까지 다 수정해야 합니다. DTO와 Entity를 분리함으로써 내부 구조가 바뀌어도 외부(UI)에는 영향을 주지 않는 유연한 구조를 가질 수 있습니다."

**Q: H2 DB는 서버 끄면 날아가는데 실제 서비스는 어떻게 하나요?**
**A:** "H2는 개발/테스트용입니다. 실제 배포 시에는 `application.yml`의 설정값만 운영용 DB(MySQL 등) 주소로 바꿔주면 코드 수정 없이 바로 전환이 가능합니다."

**Q: 카카오맵 SDK 연동 시 가장 어려웠던 점은?**
**A:** "Compose와 기존 XML 방식인 SDK 사이의 라이프사이클 관리였습니다. `AndroidView`를 통해 Compose 내에서 지도를 안전하게 렌더링하고, ViewModel의 상태와 동기화하는 부분에 신경을 많이 썼습니다."
