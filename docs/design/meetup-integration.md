# Meetup Integration Notes

이 문서는 `meetup` 도메인 작업을 통합(integrate)할 때, 공통/정책/정리가 필요한 항목을 기록한다.

## Current State

- `meetup` 도메인에서 PRD의 "BLOCKED 사용자는 쓰기/상태변경 불가"를 서비스 레벨에서 집행함.
- 단, 현재 구현은 도메인 내부에서 status lookup 로직을 중복 보유(공통화 전 상태).

## Integration TODO

- BLOCKED 집행 공통화
  - 현재: 도메인별로 `users.status` 조회 + guard 로직이 중복될 수 있음
  - 목표: `backend/src/main/java/com/example/walkservice/common/security/**` 등 공통 영역으로 이동(단, worktree policy에 따라 base/integrate에서만 변경)
  - 결과: 도메인 서비스에서는 공통 guard만 호출

- BLOCKED 에러 코드 정책 정리
  - write/state-change 차단 시 어떤 code를 쓸지 도메인별 규칙 확정
  - 예: `MEETUP_CREATE_FORBIDDEN`처럼 액션 단위로 세분화 vs. 공통 `COMMON_WRITE_FORBIDDEN`

- Communication 등 타 도메인에도 동일 정책 적용
  - PRD 상 BLOCKED 사용자는 "모임 공지/메시지 생성"도 불가
  - meetup 외 도메인에서 누락된 guard가 없는지 통합 단계에서 점검

- users.status의 소스/갱신 유스케이스 확정
  - BLOCKED 상태를 언제/어떻게 변경하는지(예: safety 도메인에서 차단 생성 시 변경?)를 스펙/구현으로 확정
  - status가 없거나 null일 때의 기본 정책(현재는 null != BLOCKED로 통과)

## Risk / Notes

- 도메인 내부 native query가 `users`/`dogs` 테이블에 의존하는 구간이 있으므로, Flyway 마이그레이션/스키마가 먼저 정리돼야 런타임 검증이 쉬움.
