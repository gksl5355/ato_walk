# Participation Integration Notes

This worktree (wt-be-participation) implemented Participation domain APIs and service logic.

To run the application end-to-end, the integration/base worktree needs to provide shared wiring
that this domain worktree is not allowed to modify (per worktree ownership policy).

## Why base changes are needed

- Controllers in this repo depend on `com.example.walkservice.common.security.CurrentUserProvider`.
- In this worktree, only the interface exists; a Spring bean implementation is not present.
- Without a bean, the application will likely fail to start due to missing dependency injection.

## Required base worktree changes (no Flyway assumed)

1) Implement and register a `CurrentUserProvider` bean under:
   - `backend/src/main/java/com/example/walkservice/common/security/**`

2) Decide how the current user id is stored/read (aligned with spec):
   - Session-based auth (Spring Security + HttpSession)
   - Dummy login for MVP

   A simple approach is:
   - Dummy login endpoint sets `userId` into HttpSession (or SecurityContext)
   - `CurrentUserProvider` reads it and returns `new CurrentUser(userId)`

3) Ensure unauthenticated requests are handled consistently:
   - Either reject with an `ApiException` (e.g. `COMMON_AUTH_REQUIRED`)
   - Or provide a deterministic dummy user (only if spec explicitly allows)

## Participation API surfaces added in this worktree

- POST `/api/v1/meetups/{meetupId}/participations`
- GET  `/api/v1/meetups/{meetupId}/participations?page&size` (host-only, REQUESTED only)
- POST `/api/v1/meetups/{meetupId}/participations/{participationId}/approve` (host-only)
- POST `/api/v1/meetups/{meetupId}/participations/{participationId}/reject` (host-only)

## Files added (owned paths)

- `backend/src/main/java/com/example/walkservice/participation/**`
- `backend/src/test/java/com/example/walkservice/participation/**`
