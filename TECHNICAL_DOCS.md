# 🛠 너굴맵 (Neogulmap) 통합 기술 문서

## 1. 시스템 아키텍처 (System Architecture)

너굴맵은 클라이언트, 서버, 데이터 파이프라인의 유기적인 연동을 통해 실시간 흡연구역 정보를 제공합니다.

### 1.1 기술 스택 및 계층별 역할
| 계층 (Layer) | 기술 스택 (Tech Stack) | 주요 역할 및 상세 내용 |
| :--- | :--- | :--- |
| **Client** | Android (Kotlin), Jetpack Compose, Kakao Map SDK v2 | - 선언형 UI 구성 및 UX 최적화<br>- 사용자 실시간 위치 추적 및 권한 관리<br>- 지도 상의 마커 렌더링 및 클러스터링 기반 제공 |
| **Server** | Spring Boot 3.x, Spring Security 6, Spring Data JPA | - 비즈니스 로직 처리 및 REST API 제공<br>- 카카오 OAuth2 토큰 검증 및 JWT 자체 발급<br>- 이미지 업로드 처리 및 파일 시스템 관리 |
| **Data** | Python 3.11, FastAPI, Pandas | - 공공데이터(CSV) 수집 및 데이터 정규화<br>- Kakao API를 통한 주소 기반 위경도 추출(지오코딩)<br>- 대량 데이터 백엔드 적재 자동화 |
| **Storage** | H2 (Dev) / MySQL (Prod), Jetpack DataStore | - 흡연구역 정보 및 사용자 메타데이터 저장<br>- 클라이언트 내 보안 인증 토큰(JWT) 비동기 저장 |

---

## 2. 데이터베이스 설계 (ERD)

데이터베이스는 확장성과 데이터 무결성을 고려하여 관계형 모델로 설계되었습니다.

### 2.1 [Table] USERS (사용자 정보)
| 컬럼명 (Column) | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | bigint | PK, AI | 시스템 내부 사용자 고유 번호 |
| `oauth_id` | varchar(255) | UK, NN | 카카오 소셜 로그인 고유 식별자 |
| `email` | varchar(100) | NN | 사용자 이메일 (카카오 계정 기반) |
| `nickname` | varchar(50) | - | 서비스 내 노출될 사용자 닉네임 |
| `provider` | varchar(20) | NN | OAuth 제공자 (예: kakao) |
| `created_at` | datetime | NN | 최초 가입 및 생성 일시 |

### 2.2 [Table] ZONE (흡연구역 정보)
| 컬럼명 (Column) | 데이터 타입 | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | int | PK, AI | 흡연구역 고유 고유 번호 |
| `address` | varchar(255) | UK, NN | 흡연구역 상세 주소 |
| `latitude` | decimal(10, 7) | NN | 구역 위치 위도 좌표 |
| `longitude` | decimal(10, 7) | NN | 구역 위치 경도 좌표 |
| `region` | varchar(100) | NN | 행정 구역 구분 (예: 서울특별시 서초구) |
| `type` | varchar(50) | - | 장소 유형 (개방형, 실내, 부스 등) |
| `description` | text (Lob) | - | 장소에 대한 추가 설명 및 특징 |
| `image` | varchar(255) | - | 저장된 현장 사진 파일명 |
| `creator` | varchar(100) | FK | 제보자 ID (USERS 테이블 연관) |
| `date` | date | NN | 데이터 등록 일자 |

---

## 3. API 명세서 (API Specification)

### 3.1 인증 API
| 기능 | 메서드 | 엔드포인트 | 설명 |
| :--- | :--- | :--- | :--- |
| **카카오 로그인** | `POST` | `/api/auth/login/kakao` | 카카오 토큰을 서비스 JWT로 교환 |
| **내 정보 조회** | `GET` | `/users/me` | 로그인된 유저의 프로필 정보 반환 |

### 3.2 흡연구역 API
| 기능 | 메서드 | 엔드포인트 | 설명 |
| :--- | :--- | :--- | :--- |
| **전체 목록 조회** | `GET` | `/zones` | 등록된 모든 흡연구역 목록 조회 |
| **반경 검색** | `GET` | `/zones?latitude=...` | 현재 위치 중심 특정 반경 내 검색 |
| **신규 등록(제보)** | `POST` | `/zones` | 사진을 포함한 신규 구역 데이터 등록 |
| **구역 정보 수정** | `PUT` | `/zones/{id}` | 기존 등록된 구역 정보 업데이트 |
| **구역 삭제** | `DELETE` | `/zones/{id}` | 특정 구역 정보 삭제 (제보자 권한) |

---
*문서 버전: v1.1*
*최종 수정일: 2025년 12월 20일*
