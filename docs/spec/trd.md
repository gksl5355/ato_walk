## 0. 목적

본 문서는 `prd.md`에 정의된 요구사항을 구현하기 위한 **기술 스택, 프레임워크, 라이브러리, 공통 규칙을 고정**한다.

본 문서에 명시되지 않은 기술, 패턴, 라이브러리는 도입하지 않는다.

---

## 1. 전체 구조

- 단일 웹 서비스 (Monolith)
- Frontend / Backend 분리
- Database 단일 인스턴스

---

## 2. Backend (BE)

### 2.1 Language / Runtime

- Java 21

### 2.2 Framework

- Spring Boot 3.x

### 2.3 Build Tool

- Gradle (Kotlin DSL)

---

### 2.4 API Style

- REST JSON
- RESTful 설계 원칙 준수
- API Prefix: `/api/v1` (고정)

---

### 2.5 Persistence

- Spring Data JPA (Hibernate)
- DB Migration: Flyway

---

### 2.6 Validation

- Jakarta Bean Validation
- 요청 DTO 단에서 Validation 수행
- 도메인 로직은 Validation 결과를 전제로 한다

---

### 2.7 Authentication (MVP)

- **세션 기반 인증**
- Spring Security + HttpSession
- 인증 목적:
    - “본인 리소스 접근 보장”
- 휴대폰 본인 인증 / 실명 인증은 MVP 범위에서 제외

---

### 2.8 Authorization (권한 모델)

- Role 기반 권한 분리 없음 (MVP)
- Authorization 규칙:
    - User / Dog: 본인만 접근 가능
    - Meetup: 생성자만 수정/취소 가능
    - Participation: 모임 생성자만 승인/거절 가능
    - Review(후기):
        - 생성: 모임 상태가 종료됨이고, 참여 당사자(모임 생성자 또는 참여 사용자)만 생성 가능
        - 조회: 모임 생성자(참여 승인/거절 판단 목적) 및 후기 당사자(작성자/피작성자)만 조회 가능

---

### 2.9 Error Handling (공통 에러 정책)

- 전역 예외 처리: `@RestControllerAdvice`
- 공통 응답 Envelope 사용 (아래 형식)

### 공통 응답 형식

```json
{
"success":false,
"error":{
"code":"MEETUP_UPDATE_FORBIDDEN",
"message":"권한이 없습니다",
"details":[]
}
}

```

- 에러 코드 규칙:
    - `DOMAIN_ACTION_REASON`
    - 예: `PARTICIPATION_APPROVE_FORBIDDEN`

---

### 2.10 Logging

- SLF4J + Logback
- 기본 애플리케이션 로그만 사용
- Correlation ID 미적용
- 민감 정보(Request Body 등) 로깅 금지

---

### 2.11 Testing

- Unit Test: JUnit 5
- Web Test: Spring Boot Test + MockMvc
- Repository Test: `@DataJpaTest`
- 테스트 우선 대상:
    - Participation 상태 전이
    - Meetup 상태 변경
    - 권한 위반 케이스

---

## 3. Frontend (FE)

### 3.1 Framework / Language

- Vue 3
- TypeScript 사용

---

### 3.2 Build Tool

- Vite

---

### 3.3 State Management

- Pinia 사용
- **최소 상태만 관리**
    - 인증 사용자 정보
    - 전역적으로 필요한 상태만 허용

---

### 3.4 HTTP Client

- Axios

---

### 3.5 Routing

- Vue Router

---

### 3.6 Validation

- FE Validation은 UX 보조 역할
- 최종 검증 기준은 BE Validation

---

## 4. Database

### 4.1 DBMS

- PostgreSQL **15 이상**

---

### 4.2 Naming Convention

- 테이블명: snake_case, **복수형**
- 컬럼명: snake_case
- PK: `id`

---

### 4.3 Primary Key

- 타입: **BIGINT (Auto Increment)**

---

### 4.4 Soft Delete

- 사용하지 않음
- 삭제는 물리 삭제를 기본으로 한다

---

## 5. API 공통 규칙

### 5.1 Response Envelope

- **모든 API 응답을 공통 포맷으로 래핑**

```json
{
"success":true,
"data":{}
}

```

---

### 5.2 Pagination

- 목록 API는 **모두 페이지네이션 적용**
- 기본 파라미터:
    - `page`
    - `size`

---

### 5.3 Date / Time

- Backend: **OffsetDateTime**
- API 표준: ISO-8601

---

## 6. Non-Goals 

- 실시간 기능 (WebSocket, Push)
- 추천 / 매칭
- 외부 API 연동
- 휴대폰 본인 인증 (MVP)
