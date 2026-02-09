# AGENTS.md
- This repo contains binding specs under `docs/spec/` and reference implementations under `templates/`.
- Agents MUST follow `docs/spec/**` and copy patterns from `templates/**`.

## Source Of Truth
- Authoritative spec entrypoint: `docs/spec/spec.md`
- Document priority order (highest wins on conflicts):
  1. `docs/spec/architecture.md`
  2. `docs/spec/repo-structure.md`
  3. `docs/spec/prd.md`
  4. `docs/spec/trd.md`
  5. `docs/spec/erd.md`
  6. `docs/spec/templates.md`
  7. `docs/spec/conventions.md`

Rules from `docs/spec/spec.md`:
- Agents MUST rely on `docs/spec/**` for implementation decisions.
- `docs/design/**` is background context only; not a binding implementation spec.
- Do not implement features/structures/rules that are not explicitly specified.
- If a spec change is needed: update the relevant spec doc first.

## Repository Structure
Intended fixed structure (per `docs/spec/repo-structure.md`):

```
repo/
├─ backend/
├─ frontend/
├─ templates/
├─ docs/
└─ README.md
```

Hard constraints from `docs/spec/repo-structure.md`:
- Do not add extra top-level directories beyond the fixed list.
- Backend package root is fixed: `com.example.walkservice`.
- Keep domain boundaries at the package level.
- Domain internal structure is fixed: `controller/ dto/ service/ entity/ repository/`.
- Common modules live under `common/` (response/exception/security).

Backend structure (planned; see `docs/spec/repo-structure.md`):
```
backend/
├─ build.gradle.kts
├─ settings.gradle.kts
└─ src/
   ├─ main/java/com/example/walkservice/
   │  ├─ common/
   │  ├─ user/
   │  ├─ dog/
   │  ├─ meetup/
   │  ├─ participation/
   │  ├─ communication/
   │  └─ safety/
   └─ test/java/com/example/walkservice/
```

Frontend structure (planned; see `docs/spec/repo-structure.md`):
```
frontend/
├─ package.json
├─ vite.config.ts
└─ src/
   ├─ api/
   ├─ stores/
   ├─ pages/
   └─ components/
```

## Build / Lint / Test Commands
- Current repo: no build/lint/test commands exist yet (only `docs/`).
- Once `backend/` exists (Java 21 + Spring Boot 3.x + Gradle Kotlin DSL; see `docs/spec/trd.md`):
  - build: `./gradlew build`
  - test: `./gradlew test`
  - single test: `./gradlew test --tests 'com.example.walkservice.SomeTest'`
  - single method: `./gradlew test --tests 'com.example.walkservice.SomeTest.someMethod'`
- Once `frontend/` exists (Vue 3 + TS + Vite; see `docs/spec/trd.md`):
  - use `frontend/package.json` scripts as source of truth; run via `npm run <script> -- <args>` (or `pnpm`/`yarn`).

Command discovery rule (once code exists):
- Backend: `backend/gradlew`, `backend/build.gradle.kts`, and CI config (if present) are the source of truth.
- Frontend: `frontend/package.json` and lockfile are the source of truth.

## Coding Conventions (Backend)
- Architecture (`docs/spec/architecture.md`): Controller -> Service -> Entity -> Repository; no Controller -> Repository; no Entity -> Service/Controller.
- Controller (`docs/spec/conventions.md`): HTTP + DTO validation only; no business logic; get auth identity from security context.
- Service (`docs/spec/conventions.md`): one method = one use-case; owns transactions; does authz checks; converts DTO <-> Entity manually (no auto-mappers).
- Transactions (`docs/spec/architecture.md`): reads use `@Transactional(readOnly = true)`, writes use `@Transactional`.
- Entity (`docs/spec/conventions.md`): avoid setters/public fields; use intent-revealing state transition methods.
- Naming (`docs/spec/conventions.md`): Class PascalCase; method camelCase (verb + object); package lowercase domain nouns.
- Formatting (`docs/spec/conventions.md`): Google Java Style baseline; formatter/checkstyle optional.
- Comments (`docs/spec/conventions.md`): comment only when intent/business rule is non-obvious; do not restate obvious code.
- Errors (`docs/spec/trd.md`): RuntimeException-based domain exceptions; global `@RestControllerAdvice` in `common/exception`.
- Error codes: `DOMAIN_ACTION_REASON`.
- API envelope (`docs/spec/architecture.md`/`docs/spec/trd.md`): `{ "success": true, "data": ... }` and `{ "success": false, "error": { "code", "message", "details": [] } }`.
- Logging (`docs/spec/conventions.md`): log key business points; never log PII/request bodies.
- Testing (`docs/spec/trd.md`): JUnit 5; MockMvc; `@DataJpaTest`; focus on state transitions + authorization violations.

### API conventions (Backend)
- API prefix is fixed: `/api/v1` (see `docs/spec/trd.md`).
- Response envelope: always wrap responses with `success/data/error` (see `docs/spec/architecture.md`).
- Pagination: list APIs should be paginated with `page` and `size` (see `docs/spec/trd.md`).

### Backend defaults (only if unspecified elsewhere)
- Imports: prefer explicit imports; no wildcard imports; one import per line; static imports grouped separately.
- DTO validation: validate at request DTO boundary (Jakarta Bean Validation per `docs/spec/trd.md`); do not rely on controller-side manual checks.
- Mapping: keep DTO <-> Entity mapping manual in service layer (spec requires this); keep mapping code boring and explicit.
- Types: avoid `null` as a control signal; prefer explicit state/enum/Optional patterns when they make state transitions clearer.
- Time: use `OffsetDateTime` for backend time values (per `docs/spec/trd.md`).
- Security: session-based auth; do not accept user IDs from clients for authorization decisions; read identity from security context.

## Coding Conventions (Frontend)
From `docs/spec/repo-structure.md` and `docs/spec/architecture.md`:
- Vue 3 + TypeScript + Vite (see `docs/spec/trd.md`).
- `src/api/`: all HTTP calls happen here.
- `src/stores/`: global state; Pinia minimal; keep only truly global state.
- `src/pages/`: route-level orchestration.
- `src/components/`: UI-only; no business logic.

### Frontend defaults (only if unspecified elsewhere)
- TypeScript: avoid `any`; type API boundaries (request/response DTOs) and store state.
- Imports: keep relative imports short; prefer `@/` alias if configured by Vite/TS config (verify before using).
- Error handling: surface API errors via a single translation layer in `src/api/` (map backend envelope to UI-friendly errors).
- State: keep Pinia minimal; do not push per-page state into global stores.

## Documentation Rules
- `docs/spec/**` is the binding spec for agents; keep it precise and up to date.
- If you need to change behavior/structure, update the relevant spec doc first (then implement).
- Keep headings and file names stable; other docs and future templates will link to them.
- Prefer small, reviewable edits; avoid mixing spec changes with unrelated rewrites.

## Git and workspace hygiene (agents)
- Do not commit unless explicitly asked.
- Do not revert or rewrite unrelated local changes.
- Avoid destructive git commands (hard reset/force push) unless explicitly requested.

## Database Conventions

From `docs/spec/trd.md`:
- PostgreSQL 15+.
- Table names: snake_case, plural.
- Column names: snake_case.
- PK column: `id` (BIGINT, auto-increment).
- Soft delete: not used.

## Dependency / Tech Introduction
- Do not introduce frameworks/libraries/tools that are not listed in `docs/spec/trd.md`.
- If a new tool is needed (linter, formatter, test runner, etc.), propose it by updating `docs/spec/trd.md` first.

## Templates (reference implementations)
From `docs/spec/templates.md`:
- Templates are reference implementations to copy/extend (not sample code).
- Do not drop or merge layers; do not invent alternative structures.
- If templates must change: check for conflicts with `docs/spec/architecture.md`.
- Priority for conflicts: `architecture.md` > `templates.md` > `conventions.md`.

## Cursor / Copilot Rules

- No Cursor rules found: `.cursorrules` or `.cursor/rules/**` not present.
- No Copilot instructions found: `.github/copilot-instructions.md` not present.

## Practical agent workflow
- Before coding: read `docs/spec/spec.md` and the relevant high-priority spec.
- When adding code later: follow the fixed repo structure and package rules.
- When uncertain: prefer explicit spec text over inference.
