# Communication Worktree Integration Notes

이 문서는 `wt-be-communication` worktree 변경을 통합 브랜치로 합칠 때, 결정/확인이 필요한 사항을 정리한다.

## 변경 범위

- `backend/src/main/java/com/example/walkservice/communication/service/CommunicationService.java`
  - 공지/메시지 생성(POST) 유스케이스에 `BLOCKED` 사용자 차단 로직 추가(체크 순서: BLOCKED 우선)
- `backend/src/main/java/com/example/walkservice/communication/repository/UserStatusLookupRepository.java`
  - `users.status` 조회를 위한 네이티브 쿼리 lookup repository 추가(communication 도메인 내부)
- `backend/src/test/java/com/example/walkservice/communication/service/CommunicationServiceTest.java`
  - `BLOCKED` 사용자 차단 케이스 테스트 추가 및 서비스 생성자 변경 반영

## 스펙 근거

- `docs/spec/prd.md` 1.5: `BLOCKED` 사용자는 쓰기/상태변경 액션 수행 불가(예: 모임 공지/메시지 생성)

## 통합 시 결정/확인 필요 사항

### 1) UserStatusLookupRepository 위치(공유 vs 도메인별)

현재 상태:
- communication 도메인에 `communication.repository.UserStatusLookupRepository`를 새로 추가함
- dog 도메인에도 별도의 `dog.repository.UserStatusLookupRepository`가 이미 존재함

결정 포인트:
- 도메인별로 동일 패턴을 복제 유지할지
- 아니면 통합 브랜치에서 `user/**` 또는 `common/**`로 승격해 공유할지

주의:
- 도메인 worktree는 `common/**` 수정이 금지되어 있어서, 공유 승격은 통합/베이스 브랜치에서 처리해야 함

### 2) FORBIDDEN 에러코드 세분화 여부

현재 상태:
- BLOCKED 차단도, "호스트가 아님"도 둘 다 `COMMUNICATION_CREATE_FORBIDDEN`으로 응답(메시지만 다름)

결정 포인트:
- 차단 사유를 코드로 분리할지(예: `COMMUNICATION_CREATE_BLOCKED` 같은 형태) 또는 현 상태 유지할지
- 현 전역 예외 매핑은 `*_FORBIDDEN` 접미로 403 처리하므로, 코드 분리 시 접미 규칙 유지 필요

### 3) DI/생성자 변경으로 인한 충돌 가능성

현재 변경:
- `CommunicationService` 생성자에 `UserStatusLookupRepository` 파라미터가 추가됨

확인 포인트:
- 다른 worktree에서 `CommunicationService` 생성자/빈 구성이 변경되었으면 머지 충돌 가능
- 통합 시 스프링 컨텍스트 주입(생성자 주입) 오류가 없는지 빌드/테스트로 확인

### 4) BLOCKED 체크 순서(보안/정보노출 관점)

현재 구현:
- `BLOCKED` 체크를 가장 먼저 수행함(meetup 존재 여부/호스트 여부보다 우선)

의도:
- 차단 사용자에게 리소스 존재 여부 등 추가 정보를 불필요하게 노출하지 않음

### 5) JDK 경로 경고 재확인

로컬 테스트에서는 `./gradlew test`가 성공했지만, 로그에 JDK 경로 관련 경고가 출력될 수 있음:
- 예: `/usr/lib/jvm/openjdk-21` 경로에 java 실행 파일이 없다는 메시지

확인 포인트:
- 통합 브랜치/CI 환경에서 동일 경고가 실패로 이어지지 않는지 1회 확인
