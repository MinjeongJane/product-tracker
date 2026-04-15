# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# 전체 빌드
./gradlew build

# 단일 모듈 빌드
./gradlew :api:build

# 앱 실행 (PostgreSQL 필요)
./gradlew :api:bootRun

# 전체 테스트
./gradlew test

# 특정 테스트 클래스 실행
./gradlew :product:test --tests "com.tracker.product.SomeTest"

# DB 실행
docker compose up -d

# DB 중지
docker compose down
```

## 멀티모듈 구조

```
product-tracker/
├── common/      # ApiResponse<T>, BusinessException, ErrorCode, GlobalExceptionHandler
├── product/     # Product 엔티티, ProductStatus 상태 머신, ProductService
├── spec/        # SpecHistory 엔티티 (버전 스냅샷), SpecService
└── api/         # REST Controllers, DTO, SpringBoot 메인 진입점
```

### 모듈 의존성
```
api → common, product, spec
spec → common, product
product → common
common → (없음)
```

## 기술 스택

| 항목 | 버전 |
|------|------|
| Java | 21 (JDK: temurin-21.0.7) |
| Spring Boot | 3.4.5 |
| Gradle | 8.14.2 (Kotlin DSL) |
| PostgreSQL | 16 (Docker) |
| SpringDoc OpenAPI | `springdoc-openapi-starter-webmvc-ui:2.6.0` |

> **주의**: 시스템 Java가 21보다 높으면 `gradle.properties`의 `org.gradle.java.home`이 Java 21을 가리켜야 합니다.

## 패키지 구조

```
com.tracker.common/    → exception/, response/
com.tracker.product/   → domain/, repository/, service/
com.tracker.spec/      → domain/, repository/, service/
com.tracker.api/       → product/(request,response), spec/(request,response)
```

## DB 설정

→ [`docs/DATABASE.md`](docs/DATABASE.md) 참고

로컬 연결 정보: `localhost:5432 / product_tracker / tracker / tracker1234`

## 핵심 비즈니스 규칙

1. **상태 전이**: 허용된 경로만 가능. 위반 시 `BusinessException` throw
   - `IDEA → SPEC_DONE → DEV → QA → RELEASED → DISCONTINUED` (일부 되돌리기 가능)
2. **스펙 불변**: 스펙 변경 시 기존 row 수정 금지 — 항상 새 버전으로 insert
3. **변경 사유 필수**: 스펙 등록 시 `changeReason` 필수
4. **상품 삭제 제한**: `IDEA` 상태일 때만 삭제 가능

## API 진입점

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Product API: `/api/products`
- Spec API: `/api/products/{productId}/specs`
