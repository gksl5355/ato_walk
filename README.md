# ato_walk

"No magic. Just walks."  (U^_^U)

반려견과 함께하는 산책/모임을 만들고, 참여하고, 안전하게 운영하기 위한 단일 웹 서비스(Monolith) 프로젝트입니다.

이 저장소는 **스펙 기반 개발**을 최우선으로 합니다. 구현/구조/규칙은 `docs/spec/**`가 기준이며, 명시되지 않은 확장은 하지 않습니다.

## What We Build (MVP)

- User: 이메일 기반 사용자 생성/조회 + 세션 기반 더미 로그인
- Dog: 사용자 종속 반려견 + 성향 프로필(고정 질문/항목 집합)
- Meetup: 모임 생성/조회/수정 + 상태(RECRUITING/ENDED/CANCELED)
- Participation: 참여 요청/승인/거절 + 상태 전이
- Review: 참여 기반의 제한된 피드백(평점 1~5, 내용 optional)
- Communication: 모임 단위 공지/메시지(개인 DM 없음)
- Safety: 신고/차단

## Non-Goals (MVP)

- 추천/자동 매칭/자동 승인
- 실시간 기능(WebSocket, Push)
- 외부 API 연동
- 휴대폰/실명 인증

## Docs (Source of Truth)

- Binding Spec entrypoint: `docs/spec/spec.md`
- Requirements: `docs/spec/prd.md`
- Tech decisions: `docs/spec/trd.md`
- Architecture: `docs/spec/architecture.md`
- Repo structure: `docs/spec/repo-structure.md`
- Conventions: `docs/spec/conventions.md`
- Templates guide: `docs/spec/templates.md`
- ERD: `docs/spec/erd.md`

`docs/design/**`는 배경 이해용이며, 구현 판단 기준이 아닙니다.

## Repository Layout (Fixed)

`docs/spec/repo-structure.md`에 의해 루트 구조는 고정입니다.

```
repo/
├─ backend/
├─ frontend/
├─ templates/
├─ docs/
└─ README.md
```

## Backend Direction

- Java 21 + Spring Boot 3.x
- Gradle (Kotlin DSL)
- Spring Data JPA(Hibernate) + Flyway
- Auth: Spring Security + HttpSession (세션 기반)
- API Prefix: `/api/v1`
- Response Envelope:

```json
{ "success": true, "data": {} }
```

```json
{ "success": false, "error": { "code": "...", "message": "...", "details": [] } }
```

## Frontend Direction

- Vue 3 + TypeScript + Vite
- Vue Router
- Axios
- Pinia (전역 상태는 최소)

## Architecture Rules (Backend)

레이어드 아키텍처를 강제합니다.

```
Controller -> Service -> Entity(JPA) -> Repository
```

- Controller: HTTP + DTO validation만 (비즈니스 로직/Repository 직접 호출 금지)
- Service: 유스케이스/트랜잭션/권한 검증/DTO<->Entity 수동 변환
- Entity: 상태/상태 전이 규칙(의도를 드러내는 메서드), setter 남용 금지

## Contributing (Spec-first)

- 새로운 구조/규칙/기술 도입이 필요하면, 코드를 먼저 늘리지 말고 `docs/spec/**`를 먼저 수정합니다.
- 구현은 `templates/**`를 기준(reference implementation)으로 복제/확장합니다.

## Getting Started

현재는 스펙과 템플릿 중심으로 저장소가 구성되어 있으며, 빌드/테스트 커맨드는 코드 스캐폴딩 이후 확정됩니다.

---

If you are reading this: welcome. Please keep it boring, explicit, and kind. (=^.^=)
