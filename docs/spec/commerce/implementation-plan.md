## 0. Goal

기존 walk 서비스를 유지한 채, shopping 탭과 commerce API를 단계적으로 추가한다.

---

## 1. Planning Principles

- spec-first: 구현 전 문서 확정
- scope isolation: `global + commerce`만 참조
- small batch: 단계별로 작은 단위 구현/검증

---

## 2. Milestones

### M1. Spec Baseline (문서)

- `scopes.md` 확정
- commerce PRD/ERD/integration 확정
- AGENTS scope 규칙 반영

완료 기준:
- 구현자가 commerce 작업 시 참조 문서가 단일하게 결정됨

### M2. Backend Skeleton

- package: `com.example.walkservice.commerce`
- 내부 구조: `controller/ dto/ service/ entity/ repository/`
- Flyway migration으로 commerce 테이블 생성

완료 기준:
- 컴파일 가능
- migration 적용 성공

### M3. Commerce APIs (MVP)

- 상품 목록/상세
- 장바구니 CRUD
- 주문 생성/조회/취소

완료 기준:
- 공통 응답 envelope 준수
- 인증/인가 규칙 준수
- 기본 테스트 통과

### M4. Frontend Shopping Tab

- 라우트: Shopping 탭 추가
- 페이지: 목록/상세/장바구니/주문목록
- API 모듈 분리: `src/api/commerce/**`

완료 기준:
- 기존 walk 기능 회귀 없음
- 쇼핑 핵심 플로우 동작

### M5. Integration Hardening

- 교차 도메인 호출 점검
- 에러 코드/로그/권한 처리 점검
- 문서와 구현 정합성 점검

완료 기준:
- integration.md 위반 없음
- 운영 시나리오 기준 smoke 테스트 통과

---

## 3. Package Strategy Recommendation

초기에는 `commerce` 단일 도메인 패키지로 시작한다.

- 이유:
    - 과도한 도메인 분할로 인한 초기 복잡도 상승 방지
    - 현재 프로젝트의 기존 패턴(도메인 패키지 + 고정 내부 구조) 유지

분리 조건(후속 단계):
- `catalog`, `cart`, `order` 간 변경 충돌이 반복되는 경우
- 팀이 도메인 단위 병렬 개발이 필요한 경우

그 전까지는 `commerce` 내부에서 entity/service를 명확히 분리해 운영한다.

---

## 4. Requirement Coverage Checklist (Implementation Gate)

아래 항목은 구현 완료 판단의 필수 체크리스트다.

### 4.1 Product

- [ ] `GET /api/v1/products` 구현 (page, size)
- [ ] 카테고리/상태 필터 구현 (`category`, `status`)
- [ ] `GET /api/v1/products/{productId}` 구현
- [ ] 응답 envelope `success/data` 준수

### 4.2 Cart

- [ ] `GET /api/v1/cart-items` 구현
- [ ] `POST /api/v1/cart-items` 구현
- [ ] `PUT /api/v1/cart-items/{cartItemId}` 구현
- [ ] `DELETE /api/v1/cart-items/{cartItemId}` 구현
- [ ] 수량 검증 1~99 준수
- [ ] 동일 사용자-동일 상품 unique 정책 준수

### 4.3 Order

- [ ] `POST /api/v1/orders` 구현 (장바구니 기준 주문 생성)
- [ ] `GET /api/v1/orders` 구현 (page, size)
- [ ] `GET /api/v1/orders/{orderId}` 구현
- [ ] `POST /api/v1/orders/{orderId}/cancel` 구현
- [ ] 상태 전이 `CREATED -> CANCELED`만 허용
- [ ] 주문 생성 시 재고 차감, 취소 시 재고 복원

### 4.4 Common / Security / Error

- [ ] 인증 필요 API에 세션 인증 적용
- [ ] 비인증 요청은 `COMMON_AUTH_REQUIRED` 응답
- [ ] Validation 실패는 `COMMON_VALIDATION_FAILED` 응답
- [ ] 도메인 에러 코드는 `DOMAIN_ACTION_REASON` 규칙 준수
- [ ] 모든 목록 API 페이지네이션 적용

### 4.5 Boundary

- [ ] `commerce` 구현에서 `walk` 엔티티/리포지토리 직접 참조 금지
- [ ] 교차 도메인 연동은 service + DTO/primitive만 사용

---

## 5. Verification Scenarios (E2E-focused)

### S1. Product List/Detail

1) 상품 3건 이상 존재
2) `GET /products?page=0&size=20` 호출
3) 첫 상품 ID로 `GET /products/{id}` 호출

기대 결과:
- 목록/상세 모두 `success=true`
- 상세의 `id/name/category/price/stockQuantity` 존재

### S2. Cart CRUD

1) 로그인 사용자로 `POST /cart-items` (quantity=2)
2) `GET /cart-items`로 항목 확인
3) `PUT /cart-items/{id}` (quantity=3)
4) `DELETE /cart-items/{id}`

기대 결과:
- 수량 변경 반영
- 삭제 후 목록에서 제거

### S3. Order Create/Cancel Stock Recovery

1) 재고 10인 상품을 장바구니 수량 2로 담기
2) `POST /orders` 호출
3) 주문 상세에서 상태 `CREATED` 확인
4) 상품 재고가 8로 차감됐는지 확인
5) `POST /orders/{orderId}/cancel` 호출
6) 상태 `CANCELED` 확인
7) 상품 재고가 10으로 복원됐는지 확인

기대 결과:
- 재고 차감/복원 규칙 정확히 동작
- 허용 전이 외 상태 변경 차단

### S4. Validation/Auth/Forbidden

1) 비로그인 상태로 `POST /cart-items` 호출
2) quantity=0 또는 100으로 `POST /cart-items` 호출
3) 존재하지 않는 `productId`로 요청

기대 결과:
- 401 + `COMMON_AUTH_REQUIRED`
- 400 + `COMMON_VALIDATION_FAILED`
- 404 + 도메인 not found 코드
