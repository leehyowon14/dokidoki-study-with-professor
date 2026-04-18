# 구현 계획: 백엔드 MVP 기반

**기능 ID**: `001-backend-mvp` | **작업 브랜치**: `main` | **날짜**: 2026-04-18 | **스펙**: [spec.md](./spec.md)
**입력**: `/specs/001-backend-mvp/spec.md`의 기능 명세

**참고**: 이 템플릿은 `/speckit.plan` 명령이 채운다. 작업 브랜치는 기본 Spec Kit 숫자형 브랜치 규칙이 아니라 프로젝트 헌장을 따라야 한다. `issueNum`은 `#123` 같은 GitHub 이슈 번호에서 숫자 부분인 `123`을 뜻한다.

## 요약

백엔드 MVP는 계정 생성과 로그인, 교수 등록 및 조회, 공부 세션 정산, 방치 패널티,
시나리오 이벤트, 최종 결과 산출, 교수 원본 사진 기반 캐릭터 에셋 생성까지의 핵심
도메인을 하나의 Spring Boot 백엔드 서비스로 구현한다. 프론트엔드는 별도 트랙에서
진행하므로, 이번 계획의 핵심은 버전이 명시된 계약 문서를 기준으로 백엔드가 독립적으로
검증 가능한 도메인 규칙, `nanobanana` 기반 에셋 생성 오케스트레이션, HTTP 계약을
완성하는 것이다.

## 기술 맥락

**프론트엔드**: 별도 React 워크스트림. 이 plan 범위 밖이며 `Docs/api-spec-v0.1.md`,
`Docs/dialogue-content-spec-v0.1.md`를 계약 기준으로 소비한다.  
**백엔드**: Java 21, Spring Boot 3.5 기반 REST JSON API, `api/application/domain/infrastructure`
계층과 `auth`, `professor`, `study`, `dialogue`, `result`, `common` 모듈로 나눈
모듈형 모놀리스. `professor` 모듈은 교수 프로필, 호감도, 캐릭터 에셋 생성 오케스트레이션을 함께 소유  
**저장소**: PostgreSQL 16을 트랜잭션 저장소로 사용하고, 교수 원본 사진과 생성된 캐릭터
에셋 메타데이터는 DB에 저장하며, 실제 이미지 파일 저장은 로컬 개발 구현과 배포 시 객체
저장소 구현을 교체 가능한 포트로 분리. `nanobanana` 호출은 비동기 생성 포트로 분리  
**테스트**: 프론트엔드 별도 트랙 / 백엔드: JUnit 5, Spring Boot Test, MockMvc,
Testcontainers(PostgreSQL), JSON 계약 스냅샷 테스트  
**대상 플랫폼**: 모바일 웹 클라이언트를 위한 HTTP API  
**프로젝트 유형**: 프론트엔드-백엔드가 엄격히 분리된 웹 애플리케이션  
**문서 영향 범위**: `Docs/api-spec-v0.1.md`, `Docs/developer-spec-v0.1.md`,
`Docs/dialogue-content-spec-v0.1.md`, `Docs/BE-comment-v1.0.md`,
`Backend/Reference/` 검토 및 후속 변경 시 버전 갱신  
**배포 대상 브랜치**: `main` -> 운영, `dev/front-end` -> FE 개발 서버, `dev/back-end` -> BE 개발 서버  
**제약사항**: TDD 우선, 버전 문서 필수, FE/BE 계약 우선 소통, CI/CD 필수,
서버 주도 공부 세션 계산, 2인 병렬 협업 가능한 백엔드 설계 필요  
**규모/범위**: 단일 배포형 백엔드 서비스, 핵심 엔티티 10개, HTTP 엔드포인트 그룹 8개,
중소 규모 MVP 사용자 트래픽과 결정적 비즈니스 규칙 정확성 보장

## 헌장 점검

*게이트: 0단계 조사 전에 통과해야 하며, 1단계 설계 후 다시 확인한다.*

- [x] FE/BE 책임 경계와 파일 소유권이 명확하다.
- [x] `Docs/`의 버전 계약 문서 갱신 대상이 식별되었다.
- [x] TDD 순서가 명시되었다. 즉, 실패하는 테스트가 구현보다 먼저다.
- [x] 백엔드 설계가 2인 병렬 협업을 암묵적 결합 없이 지원한다.
- [x] 작업 브랜치와 배포 대상이 헌장과 일치한다.
- [x] 워크플로 변경 시 GitHub 이슈/PR 템플릿과 라벨 영향이 식별되었다.

설계 후 재점검 결과: 통과. `research.md`, `data-model.md`, `contracts/`, `quickstart.md`
기준으로 다시 확인했고 헌장 위반이나 예외 승인이 필요한 항목은 없다.

## 프로젝트 구조

### 문서 구조 (저장소 루트)

```text
Docs/
├── product-plan-v1.0.md
├── developer-spec-v0.1.md
├── api-spec-v0.1.md
├── dialogue-content-spec-v0.1.md
├── FE-comment-v1.0.md
└── BE-comment-v1.0.md

Backend/
└── Reference/

specs/001-backend-mvp/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── backend-api.openapi.yaml
└── tasks.md
```

### 소스 코드 구조 (저장소 루트)

```text
Backend/
├── build.gradle.kts
├── src/main/java/com/animalleague/april/
│   ├── auth/
│   │   ├── api/
│   │   ├── application/
│   │   ├── domain/
│   │   └── infrastructure/
│   ├── professor/
│   ├── study/
│   ├── dialogue/
│   ├── result/
│   └── common/
├── src/main/resources/
└── src/test/java/com/animalleague/april/
    ├── contract/
    ├── integration/
    └── unit/

.github/
├── ISSUE_TEMPLATE/
├── pull_request_template.md
└── workflows/
```

**구조 결정**: 이번 기능은 기존 `Backend/` 디렉터리를 유일한 코드 루트로 사용한다.
프론트엔드 코드는 의도적으로 제외하고, FE 통합은 버전이 명시된 `Docs/` 계약 문서로만
맞춘다. 백엔드 내부는 `auth/professor(호감도/캐릭터 에셋 포함)` 축과
`study/dialogue/result` 축으로 자연스럽게 나뉘므로 두 명의 백엔드 개발자가 서로
다른 모듈을 병렬로 진행할 수 있다. 공통 코드는 `common`과 application interface에만
제한한다.

## 복잡도 추적

계획 단계에서 정당화가 필요한 헌장 위반 사항은 없다.
