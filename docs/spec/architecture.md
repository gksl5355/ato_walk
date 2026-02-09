## 0. 목적

본 문서는 본 프로젝트의 **애플리케이션 내부 코드 구조와 책임 분리를 고정**한다.

모든 구현, 코드 템플릿, AI 생성 코드는 본 문서를 기준으로 한다.

---

## 1. 아키텍처 선택 요약

- 아키텍처 스타일: **Layered Architecture**
- 도메인 단위 패키지 분리 (단일 서비스 내부 격리)
- 목적:
    - AI 코딩 시 책임 혼선 방지
    - 코드 패턴 강제
    - 향후 도메인 단위 반출 가능성 확보


**AI 협업 친화적 단일 서비스 구조 ⭕**

---

## 2. 레이어 구성 (Backend)

```
Controller
   ↓
Service (Application)
   ↓
Domain (JPA Entity)
   ↓
Repository

```

형식상 4-tier이지만,

**Domain = Entity로 단순화된 실질적 3-tier 구조**다.

---

## 3. 레이어별 책임 정의

### 3.1 Presentation Layer (Controller)

**책임**

- HTTP 요청/응답 처리
- Request DTO Validation
- 인증 사용자 식별 정보 획득

**금지**

- 비즈니스 로직
- 트랜잭션 처리
- Repository 직접 호출

---

### 3.2 Application Layer (Service)

**책임**

- 유스케이스 실행
- 트랜잭션 경계
- 권한 검증
- 도메인 상태 전이 호출

**허용**

- 여러 Domain 조합
- Repository 호출

**금지**

- HTTP 객체 의존
- DB 쿼리 직접 작성

---

### 3.3 Domain Layer (Entity)

**결정**

- **Domain Model = JPA Entity (미분리)**

**책임**

- 상태
- 상태 전이 규칙
- 도메인 불변 조건

예:

- Meetup 상태 변경 가능 여부
- Participation 승인/거절 규칙

**금지**

- Controller / Service 의존
- 인프라 로직

---

### 3.4 Persistence Layer (Repository)

**책임**

- 데이터 접근
- 쿼리 정의

**원칙**

- Spring Data JPA 사용
- Repository는 Entity 기준으로만 동작

---

## 4. 공통 모듈 (common)

### 4.1 API Response

- 모든 응답은 공통 포맷으로 래핑
- `success / data / error` 구조 고정

---

### 4.2 Error Handling

- `@RestControllerAdvice`
- 에러 코드는 `DOMAIN_ACTION_REASON` 규칙

---

### 4.3 Security

- 세션 기반 인증
- 사용자 식별 정보는 Security Context에서 획득
- Controller에서 사용자 ID를 직접 받지 않는다

---

## 5. 패키지 구조 원칙

### 5.1 도메인 단위 패키지 분리

도메인 목록:

- user
- dog
- meetup
- participation
- communication
- safety

참고:
- 후기는 별도 도메인을 추가하지 않고, Participation 도메인 내부(참여 기반 피드백)로 다룬다
- 반려견 성향/설문 데이터는 Dog 도메인에서 관리하며, DB는 `dogs`에 저장한다 (별도 Survey 도메인/테이블 없음)

---

### 5.2 도메인 내부 구조 (고정)

```
{domain}/
 ├─ controller/
 ├─ dto/
 ├─ service/
 ├─ entity/
 └─ repository/

```

`entity` 패키지명 사용 (domain 대신)

---

## 6. 의존성 방향 규칙

허용:

- Controller → Service
- Service → Entity / Repository
- Repository → Entity

금지:

- Controller → Repository
- Domain(Entity) → Service
- 역방향 참조 전부 금지

---

## 7. 트랜잭션 정책

- Service 레이어에서만 트랜잭션 설정
- 조회: `@Transactional(readOnly = true)`
- 상태 변경: `@Transactional`

---

## 8. DTO / Entity 분리 규칙

- API 입출력은 DTO만 사용
- Entity 직접 노출 금지
- DTO ↔ Entity 변환은 **Service 내 수동 처리**

자동 Mapper 사용 ❌

---

## 9. Frontend 구조 원칙 (요약)

```
src/
 ├─ api/
 ├─ stores/   (Pinia, 최소)
 ├─ pages/
 └─ components/

```

- API 호출은 `api/`에서만 수행
- pages는 orchestration
- components는 UI 전용
