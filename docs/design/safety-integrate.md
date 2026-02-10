## Safety integration notes

### 1) wt-be-safety에서 구현 완료(코드)

- 신고 생성
  - POST `/api/v1/safety/reports`
  - `backend/src/main/java/com/example/walkservice/safety/**`
- 차단 생성
  - POST `/api/v1/safety/blocks`
  - `backend/src/main/java/com/example/walkservice/safety/**`
- 테스트
  - WebMvc validation envelope: `backend/src/test/java/com/example/walkservice/safety/controller/*ValidationTest.java`
  - Service 단위 테스트: `backend/src/test/java/com/example/walkservice/safety/service/SafetyServiceTest.java`

### 2) 원래 의도/스펙 대비 미구현(통합에서 결정/구현 필요)

- DB 마이그레이션(Flyway)
  - `reports`, `blocks` 테이블 DDL 추가 필요(도메인 worktree에서 resources 수정 금지)
  - ERD 컬럼(보고자/피보고자/meetup_id/reason/created_at, blocker/blocked/created_at)과 일치하게 생성

- 차단에 따른 제한 적용(D2)
  - `docs/design/test-cases.md`의 BE-SVC-SAFETY-02는 "차단 생성 + 제한 적용(D2)"까지 기대
  - 현재 safety 도메인은 blocks 레코드 생성까지만 구현됨
  - "어떤 액션을 어떻게 제한"할지 도메인별로 규칙 정의 후 적용 필요

- 세션 기반 더미 로그인 + CurrentUserProvider 구현
  - 컨트롤러들이 `CurrentUserProvider.currentUser().userId()`를 사용 중이나, 구현체/보안 설정이 레포에서 확인되지 않음
  - 통합 단계에서:
    - 더미 로그인 엔드포인트 구현
    - 세션에 userId를 넣고 요청에서 CurrentUserProvider가 이를 읽게 wiring

- BLOCKED 사용자 쓰기/상태변경 금지 공통 적용
  - 현재 `DogService`에만 BLOCKED 체크가 존재(lookup repo도 dog 패키지에 있음)
  - PRD 기준으로 Meetup/Participation/Communication/Safety 등 write/state-change use-case에 일관 적용 필요
  - 공통화가 필요하면 `common/**` 변경이 수반될 수 있으니 통합 브랜치에서 처리

### 3) 통합 단계에서 권장 검증

- Flyway 적용 후 실제 API 호출로 insert 되는지 smoke test
- "blocked user" 제한이 모든 write API에서 403(_FORBIDDEN)로 떨어지는지 확인
- 차단(blocks)이 실제로 어떤 접근 제한을 의미하는지(정책) 문서화 후 테스트로 고정
