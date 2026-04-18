# 빠른 시작 안내: 백엔드 MVP 기반

## 1. 목적

이 문서는 백엔드 MVP 구현 착수 시점에 필요한 최소 실행 흐름을 정리한다.
프론트엔드는 별도 트랙에서 진행하므로, 여기서는 `Backend/`와 계약 문서 검증만 다룬다.

## 2. 사전 준비

- Java 21
- Docker 또는 로컬 PostgreSQL 16
- `Backend/` 작업 디렉터리

환경 변수 예시:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/april
export SPRING_DATASOURCE_USERNAME=april
export SPRING_DATASOURCE_PASSWORD=april
export NANOBANANA_API_KEY=change-me
```

## 3. 구현 시작 순서

1. `Docs/api-spec-v0.1.md`, `Docs/dialogue-content-spec-v0.1.md`,
   `specs/001-backend-mvp/contracts/backend-api.openapi.yaml` 기준으로 계약을 다시 확인한다.
2. 구현 전에 실패하는 테스트를 먼저 만든다.
3. `Backend/` 아래에 모듈 골격을 만든다.
4. 계정/교수(캐릭터 에셋 생성 포함) -> 공부 세션 -> 이벤트/결과 순서로 도메인을 확장한다.

## 4. 권장 디렉터리 초기화

```text
Backend/
├── build.gradle.kts
├── src/main/java/com/animalleague/april/
│   ├── auth/
│   ├── professor/
│   ├── study/
│   ├── dialogue/
│   ├── result/
│   └── common/
└── src/test/java/com/animalleague/april/
    ├── contract/
    ├── integration/
    └── unit/
```

## 5. TDD 실행 흐름

### 1단계: 계약 테스트 먼저 작성

- 회원 가입 / 로그인
- 교수 등록 / 조회 / 캐릭터 에셋 상태 확인
- 공부 세션 시작 / 가시성 기록 / 종료
- 이벤트 선택
- 최종 결과 제출

예시 명령:

```bash
cd Backend
./gradlew test --tests "*Contract*"
```

### 2단계: 통합 테스트 작성

- 계정 생성 후 교수 등록
- 교수 원본 사진 URL 등록 후 캐릭터 에셋 생성 상태 확인
- 활성 세션 중복 차단
- 2분 이상 숨김 후 종료 시 패널티 적용
- 이벤트 선택 1회 처리
- 시험 종료일 이후 최종 결과 생성

예시 명령:

```bash
cd Backend
./gradlew test --tests "*Integration*"
```

### 3단계: 단위 테스트 작성

- 공부 보상 계산식
- 중도 포기 패널티 계산식
- 방치 패널티 계산식
- 교수 캐릭터 에셋 생성 오케스트레이션과 기본 에셋 대체 규칙
- 엔딩 대표 교수 동점 처리 규칙

예시 명령:

```bash
cd Backend
./gradlew test --tests "*Unit*"
```

## 6. 애플리케이션 실행

```bash
cd Backend
./gradlew bootRun
```

## 7. 최소 스모크 시나리오

1. 회원 가입
2. 로그인
3. 교수 등록
4. 교수 캐릭터 에셋 생성 상태 또는 기본 에셋 대체 상태 확인
5. 공부 세션 시작
6. 가시성 숨김 / 복귀 이벤트 기록
7. 공부 세션 종료
8. 이벤트 선택
9. 시험 종료일 이후 최종 결과 제출

이 시나리오가 계약 문서와 동일하게 동작하면 백엔드 MVP 기본 흐름이 준비된 것이다.

## 8. FE 연동 규칙

- 프론트엔드 요구가 계약과 다르면 구현 전에 `Docs/FE-comment-v1.0.md`에 기록한다.
- 백엔드에서 먼저 발견한 계약 이슈는 `Docs/BE-comment-v1.0.md`에 기록한다.
- 계약 변경은 `Docs/api-spec-v0.1.md`와 OpenAPI 계약을 함께 갱신한다.
- 시나리오 구조, 대사 포맷, 호감도 구간이 바뀌면 `Docs/dialogue-content-spec-v0.1.md`와 `Backend/Reference/`도 함께 갱신한다.
