## 0. Purpose

본 문서는 본 저장소의 스펙을 **적용 범위(scope) 단위로 분리**하고,
에이전트/개발자가 어떤 문서를 우선 참조해야 하는지 고정한다.

---

## 1. Scope Definitions

- `global`: 전 도메인 공통 규칙
- `walk`: 기존 산책/모임 도메인 규칙
- `commerce`: 쇼핑/판매 도메인 규칙

---

## 2. Scope-to-Doc Mapping

### 2.1 global (항상 적용)

- `docs/spec/spec.md`
- `docs/spec/repo-structure.md`
- `docs/spec/architecture.md`
- `docs/spec/conventions.md`
- `docs/spec/trd.md`
- `docs/spec/templates.md`
- `docs/spec/scopes.md`

### 2.2 walk

- `docs/spec/prd.md`
- `docs/spec/erd.md`

### 2.3 commerce

- `docs/spec/commerce/spec.md`
- `docs/spec/commerce/prd.md`
- `docs/spec/commerce/erd.md`
- `docs/spec/commerce/openapi.yaml`
- `docs/spec/commerce/integration.md`
- `docs/spec/commerce/implementation-plan.md`

---

## 3. Loading Rules (Agent / Human)

- 모든 작업은 시작 시 `SCOPE`를 명시한다: `global|walk|commerce`
- `walk` 작업은 `global + walk` 문서만 구현 기준으로 사용한다
- `commerce` 작업은 `global + commerce` 문서만 구현 기준으로 사용한다
- `docs/design/**`는 배경 참고이며 구현 기준이 아니다

---

## 4. Conflict Rules

- 전체 우선순위 기본값은 `docs/spec/spec.md`를 따른다
- 동일 주제 충돌 시:
    1. `global` 문서
    2. 해당 scope 문서(`walk` 또는 `commerce`)
- scope 문서는 global 문서의 공통 규칙(레이어, 응답 포맷, 보안 기본 원칙)을 위반할 수 없다

---

## 5. Cross-Scope Change Rule

- `walk` 코드/스펙을 변경하는 `commerce` 작업은 금지한다
- 교차 변경이 필요한 경우, 같은 변경 묶음에 다음 문서를 함께 수정한다:
    - `docs/spec/scopes.md`
    - `docs/spec/commerce/integration.md`
- 교차 변경은 "왜 교차가 필요한지"를 명시한 뒤 진행한다

---

## 6. Declaration

본 문서는 scope 분리 운영의 기준 문서이며,
문서 오참조로 인한 도메인 규칙 혼합을 방지하기 위한 강제 규칙이다.
