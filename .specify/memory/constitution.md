<!--
Sync Impact Report
Version change: template -> 1.0.0
Modified principles:
- [PRINCIPLE_1_NAME] -> I. Frontend-Backend Separation & Docs-as-Contract
- [PRINCIPLE_2_NAME] -> II. Test-Driven Delivery (NON-NEGOTIABLE)
- [PRINCIPLE_3_NAME] -> III. Expandable Modular Architecture
- [PRINCIPLE_4_NAME] -> IV. Branching, CI/CD, and Environment Discipline
- [PRINCIPLE_5_NAME] -> V. GitHub Traceability & Review Standardization
Added sections:
- Engineering Standards
- Workflow & Collaboration
Removed sections:
- None
Templates requiring updates:
- ✅ .specify/templates/plan-template.md
- ✅ .specify/templates/spec-template.md
- ✅ .specify/templates/tasks-template.md
- ✅ .specify/extensions.yml
- ✅ .specify/scripts/bash/common.sh
- ✅ Docs/README.md
- ✅ .github/ISSUE_TEMPLATE/config.yml
- ✅ .github/ISSUE_TEMPLATE/feature.md
- ✅ .github/ISSUE_TEMPLATE/fix.md
- ✅ .github/ISSUE_TEMPLATE/task.md
- ✅ .github/pull_request_template.md
Follow-up TODOs:
- None
-->
# 딸깍 콘테스트 (애니멀 리그) Constitution

## Core Principles

### I. Frontend-Backend Separation & Docs-as-Contract
React Frontend와 Spring Boot Backend는 작업 경로, 책임, 배포 흐름을 분리해야 한다.
프론트엔드와 백엔드가 동시에 변경되는 기능은 구현보다 먼저 버전이 명시된 계약
문서를 `Docs/`에 기록해야 하며, API 변경은 해당 Docs 업데이트 없이 머지될 수 없다.
프론트엔드 작업 중 백엔드에 대한 오류나 질문은 버전이 명시된 `FE-comment` 문서에,
백엔드 작업 중 프론트엔드에 대한 오류나 질문은 버전이 명시된 `BE-comment` 문서에
남겨 추적 가능하게 유지한다. 이 원칙은 병렬 협업 시 암묵적 구두 합의를 금지하고,
문서를 단일 계약 소스로 강제하기 위한 것이다.

### II. Test-Driven Delivery (NON-NEGOTIABLE)
모든 구현 작업은 Red-Green-Refactor 순서를 반드시 따라야 한다. 프론트엔드는
컴포넌트, 훅, 화면 흐름, API 연동에 대한 테스트를 먼저 추가하고 실패를 확인한 뒤
구현해야 하며, 백엔드는 도메인 서비스, 애플리케이션 서비스, 컨트롤러, 통합 경로에
대한 테스트를 먼저 추가하고 실패를 확인한 뒤 구현해야 한다. 스펙, 플랜, 태스크는
각 사용자 스토리마다 어떤 테스트가 먼저 작성되는지 명시해야 하며, CI는 테스트가
실패하는 브랜치의 병합과 배포를 차단해야 한다. 이 원칙은 기능 속도보다 회귀 억제를
우선하는 팀 운영을 보장한다.

### III. Expandable Modular Architecture
아키텍처는 기능 확장과 팀 병렬 작업을 우선하는 모듈 경계 위에 설계해야 한다.
프론트엔드는 화면, 상태, API 클라이언트, 공통 UI를 명확히 분리하고, 백엔드는
도메인, 애플리케이션, 인프라, API 계층을 분리해야 한다. 특히 백엔드 설계는 최소
2인의 동시 협업을 가정해야 하며, 모듈 책임과 인터페이스만 명확하면 선형 개발을
강제하지 않아야 한다. 모듈 경계가 흐려지는 구현은 헌장 위반으로 간주한다. 이 원칙의
목적은 신규 기능과 유지보수 작업이 서로를 막지 않게 하는 것이다.

### IV. Branching, CI/CD, and Environment Discipline
이 저장소는 Spec Kit의 기본 숫자형 feature branch 규칙을 따르지 않는다. 허용되는
브랜치는 `main`, `dev/front-end`, `dev/back-end`,
`feat/front-end/{issueNum}-{title}`, `hotfix/front-end/{issueNum}-{title}`,
`feat/back-end/{issueNum}-{title}`, `hotfix/back-end/{issueNum}-{title}`뿐이다.
`main`은 운영 서버 배포 대상이고, `dev/front-end`와 `dev/back-end`는 각 개발 서버
배포 대상이다. Feature와 hotfix 브랜치는 반드시 GitHub 이슈 `#123`의 숫자 부분인
`123`을 포함해야 하며, 예시는 `feat/front-end/123-login`이다. 해당 브랜치는 영역별
dev 브랜치 또는 `main`으로 향하는 PR과 연결되어야 한다. CI/CD는 각 브랜치의 역할에
맞는 검증과 배포를 수행해야 한다. 이 원칙은 배포 목적이 다른 브랜치를
혼용하지 않게 하고, 릴리즈 경로를 일관되게 유지하기 위한 것이다.

### V. GitHub Traceability & Review Standardization
모든 진행 상황은 GitHub 이슈로 관리해야 하며, 이슈는 반드시 `Front-end` 또는
`Back-end` 중 하나의 라벨과 `Feature`, `Fix`, `Task` 중 하나의 라벨을 사용해야 한다.
모든 이슈와 PR은 저장소 표준 템플릿을 사용해야 하며, PR은 연결된 이슈, 테스트 증거,
문서 변경 여부, FE/BE 계약 영향, 배포 대상 브랜치를 명시해야 한다. 템플릿을 우회한
이슈 생성이나 문서 없는 PR 병합은 허용되지 않는다. 이 원칙은 업무 단위를 추적 가능
하게 유지하고, 리뷰 품질을 균일하게 맞추기 위한 것이다.

## Engineering Standards

- 프론트엔드 구현 스택은 React를 기본으로 한다.
- 백엔드 구현 스택은 Spring Boot를 기본으로 한다.
- 모든 프로젝트 문서는 버전이 명시되어야 하며, `Docs/` 아래 문서명 또는 문서 본문
  첫머리에서 현재 버전을 식별할 수 있어야 한다.
- API 명세, 프론트엔드-백엔드 계약 문서, 협업 질문 문서는 `Docs/`에 저장한다.
- 백엔드 설계 문서는 2인 병렬 협업을 고려한 책임 경계, 모듈 소유권, 선행 계약을
  반드시 포함해야 한다.
- CI는 테스트, 정적 검증, 문서/계약 반영 여부를 확인해야 하며, CD는 브랜치 역할에
  따라 개발 서버 또는 운영 서버로 분기되어야 한다.

## Workflow & Collaboration

- Spec, plan, tasks 생성 시 기본 Spec Kit branch naming은 사용하지 않으며, 현재 작업
  브랜치 또는 `.specify/feature.json`에 저장된 컨텍스트를 기준으로 문서를 생성한다.
  여기서 `issueNum`은 GitHub 이슈 `#123`의 숫자 부분을 의미한다.
- 프론트엔드와 백엔드가 동시에 작업하는 기능은 구현 착수 전에 버전이 명시된
  `Docs/api-spec-vX.Y.md` 또는 이에 준하는 계약 문서를 먼저 갱신해야 한다.
- 프론트엔드 작업 중 백엔드 관련 오류, 계약 불일치, 질문은 `Docs/FE-comment-vX.Y.md`
  에 기록하고, 백엔드 작업 중 프론트엔드 관련 오류, 계약 불일치, 질문은
  `Docs/BE-comment-vX.Y.md`에 기록한다.
- 기능 이슈는 사용자 가치와 작업 영역이 분명한 단위로 쪼개야 하며, 문서가 명확하면
  FE와 BE는 같은 기능 안에서도 병렬로 작업할 수 있어야 한다.
- 헌장과 템플릿이 변경되면 영향을 받는 문서와 자동화 설정을 같은 변경 세트에서 함께
  갱신해야 한다.

## Governance

이 헌장은 저장소 내 다른 관행보다 우선한다. 헌장 수정은 변경 이유, 영향 범위,
관련 템플릿 동기화 결과를 포함한 PR로만 가능하다. 버전 정책은 Semantic Versioning을
따르며, 원칙 삭제나 의미 변경은 MAJOR, 원칙 또는 운영 섹션 추가는 MINOR, 의미 변화가
없는 표현 정리와 명확화는 PATCH를 증가시킨다. 모든 PR 리뷰는 헌장 준수 여부를
명시적으로 확인해야 하며, 특히 FE/BE 분리, TDD, 버전이 명시된 Docs 업데이트,
브랜치/배포 규칙, 이슈/PR 템플릿 준수를 검토해야 한다. 위반 사항은 머지 전에 수정해야
하며, 예외는 헌장 수정 PR 없이 인정되지 않는다.

**Version**: 1.0.0 | **Ratified**: 2026-04-18 | **Last Amended**: 2026-04-18
