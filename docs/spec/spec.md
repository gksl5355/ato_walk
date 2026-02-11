# spec.md

## 0. Purpose
본 문서는 본 프로젝트의 **진입 기준** 이다.  
AI 및 모든 개발 행위는 본 문서에 명시된 문서들만을 기준으로 수행한다.

---

## 1. Scope of Spec

본 Spec은 다음을 포함한다.

- 기능 요구사항 (prd.md)
- 기술 선택 및 제약 (trd.md)
- 애플리케이션 (architecture.md)
- Repository (repo-structure.md)
- Scope 로딩 규칙 (scopes.md)
- Code Templates (templates.md)
- Coding Conventions (conventions.md)
- ERD (erd.md)

본 Spec은 다음을 포함하지 않는다.

- UI/UX 설계
- 인프라/배포 구조
- 운영 정책

---

## 2. Authoritative Documents (우선순위)

문서 간 충돌이 발생할 경우, 우선순위는 다음과 같다.

1. architecture.md  
2. repo-structure.md  
3. scopes.md  
4. prd.md  
5. trd.md  
6. erd.md  
7. templates.md  
8. conventions.md  

위 순서보다 낮은 문서는 상위 문서를 위반할 수 없다.

---

## 3. Document Index

### Design (사람용, AI 기본 미로딩)
- `design/problem.md`
- `design/scope.md`
- `design/domains.md`

### Spec (AI 기준 문서)
- `spec/prd.md`
- `spec/trd.md`
- `spec/architecture.md`
- `spec/repo-structure.md`
- `spec/scopes.md`
- `spec/erd.md`
- `spec/templates.md`
- `spec/conventions.md`

### Scope Modules (AI 기준 문서)
- `spec/commerce/spec.md`
- `spec/commerce/prd.md`
- `spec/commerce/erd.md`
- `spec/commerce/openapi.yaml`
- `spec/commerce/integration.md`
- `spec/commerce/implementation-plan.md`

---

## 4. AI Usage Rules (중요)

- AI는 **spec 디렉토리 내 문서만** 참조한다
- design 문서는 배경 이해용이며, 구현 판단 기준이 아니다
- 명시되지 않은 기능/구조/규칙은 구현하지 않는다
- 임의의 추론, 보완, 확장은 금지한다

---

## 5. Change Policy

Spec 변경이 필요한 경우:

1. 해당 문서를 먼저 수정한다
2. 변경 내용을 명확히 기록한다
3. 이후 생성되는 코드부터 변경 사항을 적용한다

기존 코드의 자동 수정은 요구하지 않는다.
