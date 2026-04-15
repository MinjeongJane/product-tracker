# product-tracker

상품 개발 과정에서 발생하는 스펙 변경 이력 추적과 상품 상태 관리를 위한 멀티모듈 Spring Boot 프로젝트.

---

## 기술 스택

| 항목        | 내용                          |
|-----------|-----------------------------|
| Language  | Java 21                     |
| Framework | Spring Boot 3.4.x           |
| Build     | Gradle 8.14 (Multi-module)  |
| Database  | PostgreSQL                  |
| ORM       | Spring Data JPA (Hibernate) |
| API Docs  | SpringDoc OpenAPI 3         |
| Test      | JUnit 5, Mockito            |

---

## 멀티모듈 구조

```
product-tracker/
├── common/                         # 공통 예외, 응답 포맷
├── product/                        # 상품 도메인 (상태 머신 포함)
├── spec/                           # 스펙 버전 관리 도메인
└── api/                            # REST Controller 진입점
```

### 모듈별 역할

#### common

- 공통 API 응답 포맷 `ApiResponse<T>`
- 공통 예외 클래스 및 `GlobalExceptionHandler`
- 공통 유틸 (날짜 포맷 등)

#### product

- `Product` 엔티티 및 `ProductStatus` 상태 머신
- 허용된 상태 전이 규칙 강제
- `ProductService`, `ProductRepository`

#### spec

- `SpecHistory` 엔티티 (버전별 스냅샷 저장)
- 스펙 변경 시 새 row 삽입 방식 (기존 row 수정 X)
- `SpecService`, `SpecHistoryRepository`
- product 모듈에 의존

#### api

- `ProductController`, `SpecController`
- 요청/응답 DTO
- common, product, spec 모두에 의존

---

## 모듈 의존성

```
api ──────────────────────────────┐
 ├── product ──── common          │
 ├── spec ─────── common          │
 │                └── product     │
 └── common ◄──────────────────── ┘
```

### build.gradle 의존성 선언

```groovy
// api/build.gradle
dependencies {
    implementation project(':common')
    implementation project(':product')
    implementation project(':spec')
}

// spec/build.gradle
dependencies {
    implementation project(':common')
    implementation project(':product')
}

// product/build.gradle
dependencies {
    implementation project(':common')
}
```

---

## DB 구성

→ [`docs/DATABASE.md`](docs/DATABASE.md) 참고

---

## API 명세

### Product API

| Method | URL                         | 설명       |
|--------|-----------------------------|----------|
| POST   | `/api/products`             | 상품 등록    |
| GET    | `/api/products`             | 상품 목록 조회 |
| GET    | `/api/products/{id}`        | 상품 단건 조회 |
| PATCH  | `/api/products/{id}/status` | 상태 변경    |
| DELETE | `/api/products/{id}`        | 상품 삭제    |

#### POST /api/products

```json
// Request
{
  "name": "신규 상품 A",
  "description": "상품 설명",
  "createdBy": "hong"
}

// Response
{
  "success": true,
  "data": {
    "id": 1,
    "name": "신규 상품 A",
    "status": "IDEA",
    "createdBy": "hong",
    "createdAt": "2026-04-15T10:00:00"
  }
}
```

#### PATCH /api/products/{id}/status

```json
// Request
{
  "status": "SPEC_DONE",
  "changedBy": "hong"
}

// Response - 허용되지 않은 전이 시 400 에러
{
  "success": false,
  "error": "INVALID_STATUS_TRANSITION",
  "message": "IDEA에서 DEV로 직접 전이할 수 없습니다."
}
```

---

### Spec API

| Method | URL                                         | 설명              |
|--------|---------------------------------------------|-----------------|
| POST   | `/api/products/{productId}/specs`           | 스펙 등록 (새 버전 생성) |
| GET    | `/api/products/{productId}/specs`           | 전체 버전 이력 조회     |
| GET    | `/api/products/{productId}/specs/latest`    | 최신 버전 조회        |
| GET    | `/api/products/{productId}/specs/{version}` | 특정 버전 조회        |

#### POST /api/products/{productId}/specs

```json
// Request
{
  "content": "로그인 기능 포함, 소셜 로그인 지원",
  "changeReason": "영업팀 요청으로 소셜 로그인 추가",
  "changedBy": "hong"
}

// Response - 자동으로 version+1
{
  "success": true,
  "data": {
    "id": 3,
    "productId": 1,
    "version": 3,
    "content": "로그인 기능 포함, 소셜 로그인 지원",
    "changeReason": "영업팀 요청으로 소셜 로그인 추가",
    "changedBy": "hong",
    "createdAt": "2026-04-15T11:00:00"
  }
}
```

#### GET /api/products/{productId}/specs

```json
{
  "success": true,
  "data": [
    {
      "version": 3,
      "content": "로그인 기능 포함, 소셜 로그인 지원",
      "changeReason": "영업팀 요청으로 소셜 로그인 추가",
      "changedBy": "hong",
      "createdAt": "2026-04-15T11:00:00"
    },
    {
      "version": 2,
      "content": "로그인 기능 포함",
      "changeReason": "초기 스펙 확정",
      "changedBy": "kim",
      "createdAt": "2026-04-10T09:00:00"
    }
  ]
}
```

---

## 패키지 구조 (전체)

```
com.tracker.common/
├── exception/
│   ├── BusinessException.java
│   ├── ErrorCode.java
│   └── GlobalExceptionHandler.java
└── response/
    └── ApiResponse.java

com.tracker.product/
├── domain/
│   ├── Product.java
│   ├── ProductStatus.java
│   └── ProductStatusTransitionValidator.java
├── repository/
│   └── ProductRepository.java
└── service/
    └── ProductService.java

com.tracker.spec/
├── domain/
│   └── SpecHistory.java
├── repository/
│   └── SpecHistoryRepository.java
└── service/
    └── SpecService.java

com.tracker.api/
├── ProductTrackerApplication.java
├── product/
│   ├── ProductController.java
│   ├── request/
│   │   ├── ProductCreateRequest.java
│   │   └── ProductStatusUpdateRequest.java
│   └── response/
│       └── ProductResponse.java
└── spec/
    ├── SpecController.java
    ├── request/
    │   └── SpecCreateRequest.java
    └── response/
        └── SpecHistoryResponse.java
```

---

## Claude Code 작업 가이드 (Vibe Coding)

프로젝트 초기 세팅 이후 아래 순서로 Claude Code에게 요청하면 효율적.

### 1단계: 프로젝트 뼈대 생성

```
- Gradle 멀티모듈 settings.gradle, 각 모듈 build.gradle 생성
- common 모듈: ApiResponse, BusinessException, ErrorCode, GlobalExceptionHandler 구현
```

### 2단계: product 모듈

```
- Product 엔티티 생성 (id, name, description, status, createdBy, createdAt, updatedAt)
- ProductStatus enum + 상태 전이 규칙 구현
  - 허용되지 않은 전이 시 BusinessException throw
- ProductRepository, ProductService 구현
```

### 3단계: spec 모듈

```
- SpecHistory 엔티티 생성 (id, productId, version, content, changeReason, changedBy, createdAt)
- 스펙 등록 시 현재 최신 version + 1 자동 부여 로직
- SpecHistoryRepository, SpecService 구현
```

### 4단계: api 모듈

```
- ProductController, SpecController 구현
- 요청/응답 DTO 분리
- SpringDoc OpenAPI 연동 (Swagger UI)
```

### 5단계: 테스트

```
- ProductStatusTransitionValidator 단위 테스트
- SpecService 버전 자동 증가 단위 테스트
- ProductController 통합 테스트 (MockMvc)
```

---

## 실행 방법

```bash
# PostgreSQL 실행 (Docker) → docs/DATABASE.md 참고
docker compose up -d

# 빌드 및 실행
./gradlew :api:bootRun
```

Swagger UI: http://localhost:8080/swagger-ui.html

---

## 주요 비즈니스 규칙

1. 상품 상태는 정해진 전이 순서만 허용. 임의 변경 불가.
2. 스펙은 수정하지 않고 항상 새 버전으로 추가. 이전 버전 삭제 불가.
3. 스펙 등록 시 변경 사유(`changeReason`) 필수 입력.
4. 상품 삭제는 `IDEA` 상태일 때만 허용.