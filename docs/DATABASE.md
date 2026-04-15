# Database 구성

product-tracker 프로젝트의 DB 스키마 및 로컬 실행 가이드.

---

## 로컬 DB 실행 (Docker)

```bash
docker compose up -d
```

또는 직접 실행:

```bash
docker run -d \
  --name product-tracker-db \
  -e POSTGRES_DB=product_tracker \
  -e POSTGRES_USER=tracker \
  -e POSTGRES_PASSWORD=tracker1234 \
  -p 5432:5432 \
  postgres:16
```

| 항목     | 값               |
|--------|-----------------|
| Host   | localhost        |
| Port   | 5432             |
| DB     | product_tracker  |
| User   | tracker          |
| PW     | tracker1234      |

---

## 스키마

### product 테이블

```sql
CREATE TABLE product (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    status        VARCHAR(50)  NOT NULL DEFAULT 'IDEA',
    created_by    VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT now()
);
```

### spec_history 테이블

```sql
CREATE TABLE spec_history (
    id             BIGSERIAL PRIMARY KEY,
    product_id     BIGINT       NOT NULL REFERENCES product(id),
    version        INT          NOT NULL,
    content        TEXT         NOT NULL,
    change_reason  VARCHAR(500) NOT NULL,
    changed_by     VARCHAR(100) NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now(),

    UNIQUE (product_id, version)
);
```

---

## 상태 전이 규칙 (ProductStatus)

```
IDEA → SPEC_DONE → DEV → QA → RELEASED → DISCONTINUED
         ↑______↓    ↑___↓   ↑___↓
         (되돌리기 가능)
```

| FROM      | TO (허용)        |
|-----------|----------------|
| IDEA      | SPEC_DONE      |
| SPEC_DONE | DEV, IDEA      |
| DEV       | QA, SPEC_DONE  |
| QA        | RELEASED, DEV  |
| RELEASED  | DISCONTINUED   |
