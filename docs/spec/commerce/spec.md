## 0. Purpose

본 문서는 `commerce` scope의 진입 기준이다.
쇼핑/판매 기능 구현 시 본 문서와 하위 문서를 기준으로 한다.

---

## 1. Scope Boundary

- 본 scope는 상품 판매 페이지 및 주문 흐름의 최소 기능을 다룬다
- 기존 `walk` 도메인 규칙은 이 문서에서 명시한 연동 지점 외 직접 참조하지 않는다
- `docs/spec/scopes.md`의 로딩 규칙을 반드시 따른다

---

## 2. Authoritative Documents (commerce)

1. `docs/spec/commerce/prd.md`
2. `docs/spec/commerce/erd.md`
3. `docs/spec/commerce/openapi.yaml`
4. `docs/spec/commerce/integration.md`
5. `docs/spec/commerce/implementation-plan.md`

주의:
- 공통 규칙은 `global` 문서(`architecture.md`, `repo-structure.md`, `trd.md`, `conventions.md`)가 우선한다

---

## 3. Non-Goals (현재)

- 외부 PG 연동
- 환불/정산/세금 처리
- 다중 판매자 마켓플레이스
- 추천 알고리즘

---

## 4. Change Policy

- commerce 기능/구조 변경은 commerce 문서를 먼저 수정한다
- walk와의 교차 변경은 `integration.md`를 함께 수정한다
