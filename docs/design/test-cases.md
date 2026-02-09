# 테스트 케이스(초안 -> 확정본)

본 문서는 MVP 구현을 위해 필요한 테스트 케이스를 "규칙 단위"로 고정한다.

기준 문서
- 요구사항: `docs/spec/prd.md`
- 기술/제약: `docs/spec/trd.md`
- ERD: `docs/spec/erd.md`
- 아키텍처/레이어 책임: `docs/spec/architecture.md`
- 템플릿(응답/예외): `templates/backend/**`

범위
- Backend API/DB/권한/상태전이/응답 포맷 테스트
- Frontend는 "내부 QA용" 최소 화면 기반 스모크/계약 테스트

비범위(명시)
- PRD에 없는 기능 테스트(추론/확장 금지)
- 자동 승인/자동 추천/자동 매칭 관련 테스트(비범위)
- 개인 간 DM 테스트(비범위)

결정사항(반드시 1.6에서 락)
- D1: Meetup이 ENDED가 되는 방식(수동 endpoint vs 자동)
- D2: BLOCKED 사용자의 제한 행위 목록(정확히 어떤 액션 차단?)
- D3: 모임 조회 "성향 기반 필터링" 최소 조건(어떤 필드로 필터링?)

---

## 공통 검증 규칙(모든 API 테스트에 적용)

API Envelope
- 성공 응답: `{ "success": true, "data": ... }`
- 실패 응답: `{ "success": false, "error": { "code": "...", "message": "...", "details": [] } }`

API Prefix / Pagination
- Prefix: `/api/v1`
- 목록 API는 `page`, `size` 파라미터를 사용한다

Enum 표현
- JSON <-> BE는 enum 문자열 토큰을 사용한다(예: `RECRUITING`)
- 잘못된 enum 문자열은 400(Bad Request)로 처리한다

시간 표현
- API 표준: ISO-8601
- Backend 도메인/DTO: `OffsetDateTime`

인증/인가(기본 원칙)
- user/dog: 본인만 접근 가능
- meetup: 생성자(host)만 수정/취소 가능
- participation: host만 승인/거절 가능
- review:
  - 생성: meetup이 ENDED이고, 참여 당사자(host 또는 participant)만 가능
  - 조회: host(참여 승인/거절 판단) + 후기 당사자(작성자/피작성자)만 가능

---

## 테스트 카탈로그(요약)

ID 규칙
- BE-SVC-*: Service 규칙/권한/상태전이(JUnit)
- BE-REPO-*: Repository/DB 제약(@DataJpaTest)
- BE-WEB-*: Controller/Envelope/Validation/Auth(MockMvc)
- FE-SMOKE-*: 프론트 내부 QA 스모크
- E2E-*: 통합 시나리오

---

## BE Service 테스트(JUnit5)

### BE-SVC-USER-01 사용자 생성(이메일 식별자)
사전조건
- 이메일 문자열이 유효하다

행동
- User 생성 유스케이스 호출

기대
- User가 생성되고 email이 저장된다
- 사용자 기본 status는 `ACTIVE`

### BE-SVC-USER-02 사용자 수정(이메일 변경 금지)
사전조건
- 사용자 A가 존재한다

행동
- A의 프로필 정보 수정(이메일 포함) 시도

기대
- 이메일 변경은 거부된다(에러 코드/메시지)
- 이메일 외 필드만 수정된다

### BE-SVC-USER-03 사용자 조회(본인만)
사전조건
- 사용자 A, B가 존재한다

행동
- A가 B의 정보를 조회

기대
- 금지(Forbidden) 처리

### BE-SVC-USER-04 BLOCKED 사용자 제한(D2)
사전조건
- 사용자 A status = BLOCKED

행동
- D2에서 정의된 "제한 액션" 각각 시도

기대
- 제한 액션은 모두 금지 처리

주의
- 이 케이스는 D2 확정 후 상세 액션 목록을 테스트로 확장한다

---

### BE-SVC-DOG-01 반려견 생성(성향 프로필 필수)
사전조건
- 사용자 A 존재

행동
- A가 dog 생성 시도(성향 프로필 누락/포함 각각)

기대
- 성향 프로필 누락: 거부
- 성향 프로필 포함: 생성 성공

### BE-SVC-DOG-02 반려견 수정(본인만)
사전조건
- 사용자 A 소유 dog1 존재
- 사용자 B 존재

행동
- B가 A의 dog1 수정 시도

기대
- 금지 처리

---

### BE-SVC-MEETUP-01 모임 생성
사전조건
- 사용자 A 존재

행동
- A가 meetup 생성(시간/장소/최대인원 포함)

기대
- meetup 생성 성공
- status = `RECRUITING`

### BE-SVC-MEETUP-02 모임 수정(ENDED 불가)
사전조건
- 사용자 A가 만든 meetup1 존재
- meetup1 status = ENDED

행동
- A가 meetup1 수정 시도

기대
- 금지 처리

### BE-SVC-MEETUP-03 모임 수정/취소(호스트만)
사전조건
- 사용자 A(host), 사용자 B 존재
- meetup1 host=A

행동
- B가 meetup1 수정/취소 시도

기대
- 금지 처리

### BE-SVC-MEETUP-04 모임 ENDED 전환(D1)
사전조건
- meetup1 status = RECRUITING

행동
- D1에서 정의된 방식으로 ENDED 전환 시도

기대
- 전환 성공 및 이후 수정 불가 규칙이 활성화됨

주의
- D1 확정 후 구체화(수동 endpoint라면 host-only, 자동이라면 시간 조건)

---

### BE-SVC-PART-01 참여 요청(자동 승인 없음)
사전조건
- meetup1 status = RECRUITING
- 사용자 A(host), 사용자 B 존재

행동
- B가 meetup1 참여 요청

기대
- participation status = `REQUESTED`

### BE-SVC-PART-02 참여 승인/거절(호스트만)
사전조건
- participation1 status = REQUESTED

행동
- host(A)가 승인 -> APPROVED
- host(A)가 거절 -> REJECTED
- 비호스트(B)가 승인/거절 시도

기대
- host만 상태 전이 가능
- 비호스트는 금지 처리

---

### BE-SVC-REVIEW-01 후기 작성 조건(ENDED + 당사자)
사전조건
- meetup1 status = ENDED
- participation1 (meetup1, participant=B) 존재

행동
- host(A)가 후기 작성 시도
- participant(B)가 후기 작성 시도
- 제3자(C)가 후기 작성 시도
- meetup status != ENDED인 상태에서 작성 시도

기대
- host/participant는 성공
- 제3자는 금지
- ENDED가 아니면 금지

### BE-SVC-REVIEW-02 후기 조회 범위 제한
사전조건
- review1 존재(reviewer=A, reviewee=B)

행동
- host(A) 조회
- reviewer(A) 조회
- reviewee(B) 조회
- 제3자(C) 조회

기대
- host/reviewer/reviewee는 허용
- 제3자는 금지

---

### BE-SVC-COMM-01 모임 공지 생성(모임 단위)
사전조건
- meetup1 존재

행동
- meetup1 공지 생성

기대
- communication 레코드가 meetup_id로 저장됨
- 개인 간 DM 기능/엔드포인트는 없음(테스트 대상 아님)

---

### BE-SVC-SAFETY-01 신고 생성
사전조건
- reporter A, reported B 존재

행동
- A가 B를 신고(선택적으로 meetup_id 포함)

기대
- report 레코드 생성

### BE-SVC-SAFETY-02 차단 생성 + 제한 적용(D2)
사전조건
- blocker A, blocked B 존재

행동
- A가 B를 차단
- 이후 B의 제한 액션 수행(D2)

기대
- blocks 레코드 생성
- 제한 액션은 금지

---

## BE Repository 테스트(@DataJpaTest)

### BE-REPO-REVIEW-01 UNIQUE(participation_id, reviewer_user_id)
사전조건
- participation1 존재
- reviewer A 존재

행동
- 동일 (participation1, reviewer A)로 review 2개 저장 시도

기대
- DB 제약 위반으로 실패

### BE-REPO-REVIEW-02 후기 요약 집계(count, avg)
사전조건
- review 데이터 N개 준비(여러 rating)

행동
- 요약 쿼리 실행

기대
- count/avg가 기대값과 일치

---

## BE Web 테스트(MockMvc)

### BE-WEB-ENVELOPE-01 성공 응답 envelope
행동
- 임의의 성공 API 호출

기대
- HTTP 200
- JSON에 `success=true` 및 `data` 존재

### BE-WEB-ENVELOPE-02 실패 응답 envelope
행동
- 금지 케이스/validation 실패 호출

기대
- `success=false` 및 `error.code/message/details` 존재

### BE-WEB-VALID-01 DTO validation(400)
행동
- 필수 필드 누락/범위 위반 요청

기대
- HTTP 400
- error envelope 반환

### BE-WEB-AUTH-01 인증 필요(401/403)
행동
- 인증 없이 보호된 API 호출

기대
- 401 또는 403(정책에 따라)
- error envelope 반환

### BE-WEB-ENUM-01 잘못된 enum 문자열(400)
행동
- enum 필드에 정의되지 않은 토큰으로 요청

기대
- HTTP 400
- error envelope 반환

---

## FE 스모크(내부 QA)

### FE-SMOKE-API-01 API 호출 성공/실패 표시
행동
- FE에서 생성/조회/수정/금지 케이스 호출

기대
- 성공 시 data 표시
- 실패 시 error.message(또는 매핑된 메시지) 표시

### FE-SMOKE-SESSION-01 세션 쿠키(withCredentials) 확인
행동
- 로그인/세션이 필요한 API 흐름을 실행

기대
- 쿠키가 포함되어 인증이 유지됨

---

## 통합 시나리오(E2E 성격)

### E2E-01 핵심 플로우 1개
시나리오
1) user 생성/확인
2) dog 등록(성향 포함)
3) meetup 생성
4) participation 요청
5) host가 승인
6) meetup ENDED 전환(D1)
7) review 작성
8) review summary 조회

기대
- 모든 단계가 성공
- 중간중간 권한/상태 규칙을 어기면 즉시 금지 처리됨
