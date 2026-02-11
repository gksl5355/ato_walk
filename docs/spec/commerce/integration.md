## 0. Purpose

본 문서는 `walk`와 `commerce` 사이의 경계 및 연동 규칙을 고정한다.

---

## 1. Boundary Rules

- `walk`와 `commerce`는 패키지 경계를 유지한다
- 도메인 엔티티 직접 공유 금지
- Repository 교차 호출 금지
- 연동은 Service 레이어를 통해서만 수행한다

---

## 2. Allowed Dependency Direction

- `Controller -> Service -> Repository/Entity` 규칙은 각 scope 내부에서 동일하게 적용
- 교차 scope 연동은 다음만 허용:
    - `walk service -> commerce service`
    - `commerce service -> walk service`
- 단, 교차 연동 데이터는 DTO/primitive만 사용한다

---

## 3. Shared Modules

- `common/response`, `common/exception`, `common/security`는 공통으로 사용 가능
- 비즈니스 정책은 `common/**`에 두지 않는다

---

## 4. API and Frontend Separation

- Backend는 domain package로 분리한다
    - 예: `com.example.walkservice.commerce`
- Frontend는 기존 앱 내 탭/페이지 단위로 분리한다
    - `src/pages/shop/**`
    - `src/api/commerce/**`
    - `src/components/shop/**`

---

## 5. Change Control

- 교차 scope 영향이 있는 변경은 사전에 이 문서를 먼저 수정한다
- 이 문서에 없는 교차 연동은 구현하지 않는다

---

## 6. Point Payment Integration Rules (MVP)

- 결제 방식은 `POINT_ONLY`로 고정한다
- 주문 생성/취소에서 포인트 차감/환불은 `commerce service` 트랜잭션 내부에서 처리한다
- 포인트 차감/환불, 주문 생성/상태변경, 재고 차감/복원은 단일 트랜잭션으로 원자적으로 처리한다
- 포인트 잔액은 `users.point_balance`를 사용한다

교차 scope 접근 규칙:

- `commerce`는 `walk`의 `user` 도메인 Entity/Repository를 직접 참조하지 않는다
- `commerce`는 필요한 사용자 포인트 데이터만 read/write 하는 전용 lookup/update repository를 자체 소유한다
- 교차 데이터 전달은 DTO/primitive만 사용한다

실패 규칙:

- 포인트 부족 또는 재고 부족 항목이 1개라도 있으면 주문 생성은 전체 실패한다
- 부분 성공(포인트만 차감, 재고만 차감, 주문만 생성)은 허용하지 않는다
