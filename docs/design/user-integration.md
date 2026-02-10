# User Integration Notes

This document captures integration-stage requirements for the User domain.
It is intended for the integration pull request review checklist.

## Scope

- User domain implementation lives in `wt-be-user`.
- Integration work is expected to happen in the integration/base worktree (e.g. `wt-be-integrate` / `wt-be-base`) due to shared-path ownership rules.

## Spec Decisions (MVP)

- User modification is excluded from MVP.
  - Source of truth: `docs/spec/prd.md` (User 1.3 marked MVP excluded + Decision Log entry).
- User status change API is not provided in MVP.
  - BLOCKED is still part of the data model (ERD) and used for behavior restrictions.

## Integration Checklist

### 1) Session-based Dummy Login Wiring

Spec references:
- `docs/spec/prd.md` 1.5
- `docs/spec/trd.md` 2.7

Required in base/integration worktree:
- Provide a `CurrentUserProvider` Spring bean under `backend/src/main/java/com/example/walkservice/common/security/**`.
- Add Spring Security configuration for session-based auth.
- Ensure unauthenticated requests are rejected consistently using the existing error envelope.

Why:
- Existing controllers (dog/meetup/communication/user/participation/safety) depend on `CurrentUserProvider`.

### 2) DB Migration + Runtime Config (Flyway)

Spec references:
- `docs/spec/trd.md` 2.5 (Flyway)
- `docs/spec/erd.md`

Required in base/integration worktree:
- Add `backend/src/main/resources/db/migration/**` migrations for ERD tables.
- Add datasource/JPA/Flyway config in `backend/src/main/resources/application.yml`.

Why:
- Some services already read `users.status` via native query (BLOCKED policy).

### 3) User Domain API Smoke Checks

From `wt-be-user` expected endpoints:
- `POST /api/v1/login` (dummy login by email; creates user if missing)
- `GET /api/v1/users/me`

Smoke test ideas (integration stage):
- Login with a new email returns `success: true` and user payload.
- Subsequent request to `/api/v1/users/me` in the same session returns the same user.
- Validation failures return `COMMON_VALIDATION_FAILED` with `error.details` formatted as `"{field}: {message}"`.

### 4) Ownership/Worktree Merge Notes

Spec reference:
- `docs/spec/repo-structure.md` 9.1

Rules to follow during integration:
- Domain worktrees should not modify shared paths (`common/**`, `resources/**`, Gradle files).
- Shared wiring (security, resources, build config) must be applied in the base/integration worktree.

## Commands (integration stage)

- Run backend tests: `./backend/gradlew test`
