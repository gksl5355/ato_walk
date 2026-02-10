# Work Items (Backlog)

본 문서는 현재 구현 상태를 `docs/spec/prd.md` 기준으로 체크하고, 남은 작업을 정리하기 위한 작업 메모다.

규칙:
- 구현 판단/기준은 항상 `docs/spec/**`
- 도메인 worktree는 owned path만 수정
- 공통(common) / 빌드 / 리소스 / 문서 변경은 통합 브랜치에서 관리

## Current Backend Surface (Controllers)

주의: 아래 목록은 "현재 integrate 브랜치에 반영된 기준"이다. (각 도메인 worktree에서 구현이 끝났더라도, 아직 통합되지 않았다면 여기에는 나타나지 않을 수 있다.)

- Dog: `backend/src/main/java/com/example/walkservice/dog/controller/DogController.java`
  - `POST /api/v1/dogs`
  - `PUT /api/v1/dogs/{dogId}`
- Meetup: `backend/src/main/java/com/example/walkservice/meetup/controller/MeetupController.java`
  - `POST /api/v1/meetups`
  - `PUT /api/v1/meetups/{meetupId}`
  - `POST /api/v1/meetups/{meetupId}/cancel`
  - `POST /api/v1/meetups/{meetupId}/end`
  - `GET /api/v1/meetups/{meetupId}`
  - `GET /api/v1/meetups` (page/size + filters)
- Communication: `backend/src/main/java/com/example/walkservice/communication/controller/CommunicationController.java`
  - `POST /api/v1/meetups/{meetupId}/communications`
  - `GET /api/v1/meetups/{meetupId}/communications`

공통 응답/에러:
- Envelope: `backend/src/main/java/com/example/walkservice/common/response/ApiResponse.java`
- Exception handler: `backend/src/main/java/com/example/walkservice/common/exception/GlobalExceptionHandler.java`

## PRD Coverage Checklist (MVP)

Status legend: Done / Partial / Missing

### User

- 사용자 생성/조회/수정: Partial (도메인 worktree에 구현되어 있으나 integrate 미반영)
- 더미 로그인(세션 기반): Partial (도메인 worktree에 구현되어 있으나 integrate 미반영)
- 사용자 상태(BLOCKED) 모델/관리: Partial (도메인 worktree에 구현되어 있으나 integrate 미반영)

### Dog

- 반려견 생성: Done (service/controller 존재)
- 반려견 수정: Done
- 성향 프로필(필수 항목/enum): Done (DTO + entity)
- 조회(요구사항: 사용자 정보 조회 범위/기능과 연계): Partial (dog 조회 API 없음)
- BLOCKED 사용자 write 금지: Done (`DogService`에서 status lookup)

### Meetup

- 모임 생성: Done
- 모임 조회(단건): Done
- 모임 목록 조회 + 필터(AND, optional): Done (native query + paging)
- 모임 상태(ENDED/CANCELED): Done
- 종료(ENDED) 수동 전환 + host-only: Done
- 모임 수정(ENDED면 불가): Done (RECRUITING only)
- BLOCKED 사용자 write/state-change 금지: Done (`MeetupService`에 guard)
- 테스트(상태전이/권한 위반): Partial (service unit은 있음, controller MockMvc는 없음)

### Participation

- 참여 요청/승인/거절 + 상태: Partial (도메인 worktree에 구현되어 있으나 integrate 미반영)
- 후기(Participation 기반): Partial (도메인 worktree에 구현되어 있으나 integrate 미반영)

### Communication

- 모임 공지/메시지 생성: Done (host-only)
- 모임 메시지 목록: Done
- BLOCKED 사용자 write 금지: Missing (createCommunication에 guard 필요)

### Safety

- 신고/차단 생성: Missing (`backend/src/main/java/com/example/walkservice/safety/**` 구현 없음)
- BLOCKED 상태를 언제/어떻게 세팅할지: Missing (정책/유스케이스)

## Cross-cutting Work Items (Integrate/Base)

- [ ] BLOCKED enforcement 공통화
  - 현재: dog/meetup 도메인에 status lookup/guard가 중복
  - 목표: `common/security`로 guard/lookup 이동 후 도메인별 중복 제거
- [ ] 세션 기반 더미 로그인 + CurrentUserProvider 실제 구현
- [ ] Flyway 마이그레이션으로 ERD 테이블/제약 반영 (특히 reviews UNIQUE)
- [ ] MockMvc 기반 API 테스트 템플릿/가이드 추가 (envelope + validation + forbidden/not found)
