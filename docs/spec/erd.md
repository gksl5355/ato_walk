# ERD (Entity Relationship Definition)

## 0. Purpose
본 문서는 본 서비스의 데이터 모델과 핵심 도메인 관계를 정의한다.
본 ERD는 MVP 기준이며 구조 안정성을 최우선으로 한다.

---

## 1. Entity List

- users
- dogs
- meetups
- participations
- reviews
- communications
- reports
- blocks

---

## 2. Relationship Summary

- User 1 : N Dog
- User 1 : N Meetup (host)
- User N : M Meetup (participant)
  - 참여 관계는 participations 엔티티로 관리한다
- Participation 1 : N Review
- Meetup 1 : N Communication
- User 1 : N Report (신고자 / 피신고자)
- User 1 : N Block (차단자 / 피차단자)

---

## 3. Entity Definitions

### users
- id (PK, bigint)
- email
- status (ENUM)
- created_at

### dogs
- id (PK, bigint)
- user_id (FK)
- name
- breed
- size (ENUM)
- neutered (BOOLEAN)
- sociability_level (ENUM)
- reactivity_level (ENUM)
- notes
- created_at

### meetups
- id (PK, bigint)
- host_user_id (FK)
- title
- description
- location
- max_participants
- scheduled_at
- status (ENUM)
- created_at

### participations
- id (PK, bigint)
- meetup_id (FK)
- user_id (FK)
- status (ENUM)
- created_at

### reviews
- id (PK, bigint)
- participation_id (FK)
- reviewer_user_id (FK)
- reviewee_user_id (FK)
- rating
- content (nullable)
- created_at

Constraints:
- UNIQUE(participation_id, reviewer_user_id)

### communications
- id (PK, bigint)
- meetup_id (FK)
- content
- created_at

### reports
- id (PK, bigint)
- reporter_user_id (FK)
- reported_user_id (FK)
- meetup_id (FK, nullable)
- reason
- created_at

### blocks
- id (PK, bigint)
- blocker_user_id (FK)
- blocked_user_id (FK)
- created_at

---

## 4. Design Constraints

- 모든 PK는 bigint
- soft delete 미사용
- 상태 값은 ENUM
- 컬럼 추가는 허용, 관계 변경은 스펙 변경으로 간주

---

## 5. Enum Definitions (MVP)

### users.status

- ACTIVE
- BLOCKED

### dogs.size

- SMALL
- MEDIUM
- LARGE

### dogs.sociability_level

- LOW
- MEDIUM
- HIGH

### dogs.reactivity_level

- LOW
- MEDIUM
- HIGH

### meetups.status

- RECRUITING
- ENDED
- CANCELED

### participations.status

- REQUESTED
- APPROVED
- REJECTED
