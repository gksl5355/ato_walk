# templates.md

## 0. Purpose
본 문서는 본 프로젝트에서 사용하는 **Code Template의 의미와 사용 규칙**을 정의한다.  
본 문서는 코드 템플릿의 *구조*가 아닌, *역할과 사용 방식*만을 다룬다.

---

## 1. Code Template의 정의

본 프로젝트에서 Code template란 **기준 구현(reference implementation)**: AI가 그대로 복제·확장해야 하는 코드의 원형

---

## 2. Template의 위치

Code Template는 repository 내부 다음 위치에 존재한다.
```
templates/
└─ backend/
```

---

## 3. Template의 책임

Template는 다음을 강제한다.

- 패키지 구조
- 레이어 간 호출 흐름
- 클래스 책임 분리
- 공통 처리 방식(Response, Exception 등)

Template는 다음을 포함하지 않는다.

- 모든 도메인 구현
- 복잡한 비즈니스 로직
- 최적화 코드

---

## 4. Template 사용 규칙 

- 임의로 레이어를 생략하거나 합치지 않는다
- Template와 다른 구조의 코드는 허용되지 않는다

- 구현/코드 생성 시에는 `templates/**`를 먼저 복제·확장한다
- Template가 부족하거나 충돌하면 Template부터 수정한 뒤, 이후 생성 코드에 적용한다

---

## 5. Template 변경 규칙

Template 변경이 필요한 경우:

1. 변경 사유를 명확히 한다
2. architecture.md와의 충돌 여부를 검토한다
3. Template를 수정한다
4. 이후 생성되는 코드부터 새로운 Template를 따른다

기존 코드의 일괄 수정은 요구하지 않는다.

---

## 6. 우선순위

우선순위는 `docs/spec/spec.md`를 따른다.

추가 규칙:
- `templates/**`와 `docs/spec/conventions.md`가 충돌하면 **`templates/**`가 우선**한다.
- 템플릿이 부족하면 템플릿을 먼저 수정하고, 이후 생성/구현 코드에 반영한다.
