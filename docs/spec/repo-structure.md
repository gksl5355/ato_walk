## 0. Purpose

본 문서는 본 프로젝트의 **Repository 구조를 단일 기준으로 고정**한다.

모든 scaffold, 코드 템플릿, AI 생성 코드는 **본 문서를 정확히 따른다**.

본 문서에 명시되지 않은 구조 변경, 단순화, 생략, 추가는 **허용되지 않는다**.

---

## 1. Repository Strategy

- **Monorepo**
- **Backend / Frontend 완전 분리**
- **Docs는 repo 내부 포함**
- **Code Template는 repo 내부 포함**

---

## 2. Root Structure (고정)

```
repo/
├─ backend/
├─ frontend/
├─ templates/
├─ docs/
└─ README.md

```

### Rules

- Root에 추가 디렉토리 생성 금지
- `.gitignore`, 설정 파일은 각 영역(backend/frontend) 내부에서 관리

---

## 3. Backend Structure (고정)

```
backend/
├─ build.gradle.kts
├─ settings.gradle.kts
└─ src/
   ├─ main/
   │  ├─ java/com/example/walkservice/
   │  │  ├─ common/
   │  │  ├─ user/
   │  │  ├─ dog/
   │  │  ├─ meetup/
   │  │  ├─ participation/
   │  │  ├─ communication/
   │  │  └─ safety/
   │  └─ resources/
   └─ test/
      └─ java/com/example/walkservice/

```

### Backend Rules

- 패키지 루트는 `com.example.walkservice`로 고정
- 도메인은 **패키지 단위로만 분리**
- 도메인 추가/병합/삭제는 허용되나, 반드시 Spec을 먼저 수정한다

---

## 4. Domain Package Internal Structure (고정)

모든 도메인 패키지는 **동일한 내부 구조**를 가진다.

```
{domain}/
├─ controller/
├─ dto/
├─ service/
├─ entity/
└─ repository/

```

### Domain Rules

- `entity` 패키지는 **JPA Entity = Domain Model**
- 자동 Mapper 사용 금지
- DTO ↔ Entity 변환은 Service 레이어에서 수동 처리
- Repository 직접 호출은 Controller에서 금지

---

## 5. Common Package Structure

```
common/
├─ response/
├─ exception/
└─ security/

```

### Common Rules

- 공통 Response 래퍼는 `common/response`에 위치
- 전역 예외 처리는 `common/exception`에서만 정의
- 인증/인가 공통 코드는 `common/security`에 위치

---

## 6. Frontend Structure (고정)

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

### Frontend Rules

- API 호출은 `api/`에서만 수행
- 전역 상태는 `stores/`에서만 관리 (Pinia 최소 사용)
- `pages`는 라우트 단위 구성
- `components`는 UI 전용 (비즈니스 로직 금지)

---

## 7. Templates Structure (고정)

```
templates/
├─ backend/
└─ frontend/

```

### Template Rules

- Template는 **기준 구현(reference implementation)** 이다
- Template 코드는 실제 컴파일 가능해야 한다
- Template 구조 변경 시 반드시 본 문서를 먼저 수정한다
- Template는 “예시 코드”가 아니다

---

## 8. Docs Structure (고정)

```
docs/
├─ design/
│  ├─ problem.md
│  ├─ scope.md
│  └─ domains.md
│
└─ spec/
   ├─ spec.md
   ├─ scopes.md
   ├─ prd.md
   ├─ trd.md
   ├─ architecture.md
   ├─ repo-structure.md
   ├─ conventions.md
   ├─ templates.md
   ├─ erd.md
   └─ commerce/
      ├─ spec.md
      ├─ prd.md
      ├─ erd.md
      ├─ openapi.yaml
      ├─ integration.md
      └─ implementation-plan.md

```

### Docs Rules

- `design` 문서는 사람용 (AI 기본 미로딩)
- `spec` 문서는 AI 기준 문서 (항상 로딩)
- 폴더/경로 구조 변경 시 `repo-structure.md`가 최우선 기준
- 문서 간 충돌 우선순위는 `docs/spec/spec.md`를 따른다

---

## 9. Enforcement Rules (중요)

- 본 구조를 **단순화하지 않는다**
- 폴더 생략, 이름 변경, 통합 금지
- 구조 변경 필요 시:
    1. `repo-structure.md` 수정
    2. 변경 승인
    3. scaffold 재생성

---

## 9.1 Git Worktree Ownership Policy (병렬 개발 규칙)

본 프로젝트는 병렬 개발을 위해 Git worktree 사용을 허용한다.

### Ownership Rules

- 각 worktree(브랜치)는 **소유 경로(폴더)** 가 명확히 정의되어야 한다
- worktree는 본인의 소유 경로 외 파일/폴더를 수정하지 않는다
- 공통 파일(빌드/설정/common 등)이 필요한 경우, 공통 전담 브랜치(예: `be-base`)에서 선반영한 뒤 worktree들이 따라온다

### Forbidden Paths

- 도메인 worktree는 다음 경로를 수정하지 않는다:
    - `backend/build.gradle.kts`
    - `backend/settings.gradle.kts`
    - `backend/src/main/resources/**`
    - `backend/src/main/java/com/example/walkservice/common/**`
- Frontend worktree는 `backend/**`, `templates/**`, `docs/**`를 수정하지 않는다

### Integration Flow

- 병렬 작업 브랜치는 통합 브랜치(예: `integrate`)로 먼저 머지하여 충돌을 1차 해결한다
- `integrate`가 안정화되면 `main`으로 머지한다
- `integrate`는 `main` 반영 이후 삭제한다

### Workspace Note

- worktree 디렉토리는 repository root 외부에 생성하는 것을 권장한다 (root 구조 고정 규칙과 충돌 방지)

---

## 10. Declaration

본 문서는 본 프로젝트의 **Repository 구조에 대한 최종 기준**이다.

본 문서를 위반하는 모든 코드/구조는 **오류로 간주**한다.
