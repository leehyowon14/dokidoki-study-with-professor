# 구현 계획: [FEATURE]

**기능 ID**: `[FEATURE_ID]` | **작업 브랜치**: `[WORKING_BRANCH]` | **날짜**: [DATE] | **스펙**: [link]
**입력**: `/specs/[FEATURE_ID]/spec.md`의 기능 명세

**참고**: 이 템플릿은 `/speckit.plan` 명령이 채운다. 작업 브랜치는 기본 Spec Kit 숫자형 브랜치 규칙이 아니라 프로젝트 헌장을 따라야 한다. `issueNum`은 `#123` 같은 GitHub 이슈 번호에서 숫자 부분인 `123`을 뜻한다.

## 요약

[기능 스펙에서 핵심 요구사항, FE/BE 분리 방식, 기술 접근을 요약]

## 기술 맥락

**프론트엔드**: [React 버전 / 앱 라우터 / 상태 모델 또는 추가 확인 필요]  
**백엔드**: [Spring Boot 버전 / 모듈 구조 / API 스타일 또는 추가 확인 필요]  
**저장소**: [예: PostgreSQL, Redis, S3, 로컬 파일 또는 추가 확인 필요]  
**테스트**: [프론트엔드 테스트 스택] / [백엔드 테스트 스택]  
**대상 플랫폼**: [모바일 웹 / HTTP API / 관리자 콘솔 / 배치 작업]  
**프로젝트 유형**: 프론트엔드-백엔드가 엄격히 분리된 웹 애플리케이션  
**문서 영향 범위**: [`Docs/` 아래에서 생성하거나 갱신할 버전 문서]  
**배포 대상 브랜치**: `main` -> 운영, `dev/front-end` -> FE 개발 서버, `dev/back-end` -> BE 개발 서버  
**제약사항**: TDD 우선, 버전 문서 필수, FE/BE 계약 우선 소통, CI/CD 필수  
**규모/범위**: [예상 사용자 수, 화면 수, 엔드포인트 수, 작업 수, 외부 연동 수]

## 헌장 점검

*게이트: 0단계 조사 전에 통과해야 하며, 1단계 설계 후 다시 확인한다.*

- [ ] FE/BE 책임 경계와 파일 소유권이 명확하다.
- [ ] `Docs/`의 버전 계약 문서 갱신 대상이 식별되었다.
- [ ] TDD 순서가 명시되었다. 즉, 실패하는 테스트가 구현보다 먼저다.
- [ ] 백엔드 설계가 2인 병렬 협업을 암묵적 결합 없이 지원한다.
- [ ] 작업 브랜치와 배포 대상이 헌장과 일치한다.
- [ ] 워크플로 변경 시 GitHub 이슈/PR 템플릿과 라벨 영향이 식별되었다.

## 프로젝트 구조

### 문서 구조 (저장소 루트)

```text
Docs/
├── product-plan-vX.Y.md
├── developer-spec-vX.Y.md
├── api-spec-vX.Y.md
├── FE-comment-vX.Y.md
└── BE-comment-vX.Y.md

specs/[FEATURE_ID]/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
└── tasks.md
```

### 소스 코드 구조 (저장소 루트)

```text
frontend/
├── src/
│   ├── app/
│   ├── components/
│   ├── features/
│   ├── services/
│   └── types/
└── tests/
    ├── integration/
    └── unit/

backend/
├── src/main/
│   ├── java/ or kotlin/
│   └── resources/
└── src/test/

.github/
├── ISSUE_TEMPLATE/
└── pull_request_template.md
```

**구조 결정**: [이번 기능에 실제로 사용하는 디렉터리를 적고, 왜 이 분리가 FE/BE 병렬 실행을 지원하는지 설명]

## 복잡도 추적

> **헌장 점검에서 위반이 발생했고 그 위반을 정당화해야 할 때만 작성**

| 위반 항목 | 필요한 이유 | 더 단순한 대안을 기각한 이유 |
|-----------|-------------|------------------------------|
| [예: 임시 공유 DTO 패키지] | [현재 필요성] | [엄격한 분리만으로는 부족했던 이유] |
