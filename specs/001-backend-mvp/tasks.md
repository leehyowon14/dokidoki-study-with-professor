# 작업 목록: 백엔드 MVP 기반

**입력**: `/specs/001-backend-mvp/`의 설계 문서
**선행 문서**: plan.md, spec.md, research.md, data-model.md, contracts/
**테스트**: 모든 사용자 스토리는 TDD에 따라 실패하는 테스트부터 시작해야 한다.
**구성 원칙**: 작업은 스프린트, 단계, 작업 묶음, 원자 작업 기준으로 정리해 백엔드 팀이 프론트엔드와 독립적으로 실행할 수 있게 한다.

## 형식: `[ID] [P?] [Story] 설명`

- **[P]**: 병렬 실행 가능 (서로 다른 파일, 미완료 의존성 없음)
- **[Story]**: 어떤 사용자 스토리에 속하는지 표시 (예: US1, US2, US3)
- 모든 항목 설명에는 정확한 파일 경로를 포함한다.

## 경로 규칙

- **백엔드**: `Backend/src/main/`, `Backend/src/test/`, `Backend/src/main/resources/`
- **문서**: `Docs/`, `specs/001-backend-mvp/`
- **GitHub 워크플로**: `.github/workflows/`

## 스프린트 1: 기반 작업과 계정/교수 MVP

### 1단계: 준비 (공통 인프라)

**목적**: Spring Boot 백엔드 작업 공간을 초기화하고 백엔드 우선 계약 기준을 고정한다.

#### 작업 1.1: 백엔드 작업 공간 초기화

- [ ] T001 `Backend/build.gradle.kts`와 `Backend/settings.gradle.kts`에 Gradle 프로젝트 파일을 생성한다.
- [ ] T002 `Backend/src/main/java/com/animalleague/april/AprilApplication.java`와 `Backend/src/main/resources/application.yml`에 Spring Boot 진입점과 런타임 설정을 생성한다.
- [ ] T003 [P] `Backend/src/test/resources/application-test.yml`에 백엔드 테스트 런타임 설정을 생성한다.

#### 작업 1.2: 계약과 CI 기준선 정리

- [ ] T004 [P] `Docs/api-spec-v0.1.md`와 `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`에 백엔드 계약 기준선을 고정한다.
- [ ] T005 [P] `.github/workflows/backend-ci.yml`에 백엔드 빌드/테스트 CI 워크플로를 추가한다.

---

### 2단계: 기반 작업 (차단 선행조건)

**목적**: 모든 사용자 스토리를 가로막는 공통 백엔드 기반을 마련한다.

**⚠️ 중요**: 이 단계가 끝나기 전에는 어떤 사용자 스토리 작업도 시작할 수 없다.

#### 작업 1.3: 공통 백엔드 기반

- [ ] T006 `Backend/src/main/java/com/animalleague/april/common/api/ErrorResponse.java`와 `Backend/src/main/java/com/animalleague/april/common/infrastructure/GlobalExceptionHandler.java`에 공통 오류 응답 구조와 예외 처리기를 만든다.
- [ ] T007 [P] `Backend/src/main/java/com/animalleague/april/common/domain/Gender.java`, `Backend/src/main/java/com/animalleague/april/common/domain/CharacterAssetStatus.java`, `Backend/src/main/java/com/animalleague/april/common/domain/PersonalityType.java`, `Backend/src/main/java/com/animalleague/april/common/domain/DialogueTriggerType.java`에 공통 enum과 값 객체를 만든다.
- [ ] T008 `Backend/src/main/java/com/animalleague/april/common/infrastructure/config/PersistenceConfig.java`와 `Backend/src/main/java/com/animalleague/april/common/infrastructure/config/SecurityConfig.java`에 영속성 및 세션 보안 설정을 만든다.

#### 작업 1.4: 테스트 기반과 협업 가드레일

- [ ] T009 [P] `Backend/src/test/java/com/animalleague/april/integration/support/PostgresIntegrationTest.java`와 `Backend/src/test/java/com/animalleague/april/contract/support/ApiContractTest.java`에 Testcontainers 및 MockMvc 공통 테스트 기반을 만든다.
- [ ] T010 `Docs/BE-comment-v1.0.md`와 `Docs/developer-spec-v0.1.md`에 백엔드 전용 계약 변경 및 차단 이슈 기록 규칙을 정리한다.

**체크포인트**: 백엔드 프로젝트가 기동 가능하고, 테스트 기반이 존재하며, 계약 소유권이 고정되었다.

---

### 3단계: 사용자 스토리 1 - 계정 및 교수 데이터 준비 (우선순위: P1) 🎯 MVP

**목표**: 사용자가 계정을 생성하고 로그인한 뒤 교수, 기본 호감도 상태, 교수 원본 사진 기반 캐릭터 에셋 상태를 저장하고 조회할 수 있게 한다.

**독립 검증 방법**: 신규 사용자가 회원 가입 후 로그인하고 교수 한 명을 등록한 뒤 교수 목록과 상세 정보에서 동일 데이터, 기본 호감도 상태, 캐릭터 에셋 생성 상태 또는 기본 에셋 대체 상태를 확인하면 된다.

#### 작업 1.5: 사용자 스토리 1 실패 테스트

- [ ] T011 [P] [US1] `Backend/src/test/java/com/animalleague/april/contract/auth/AuthContractTest.java`에 인증 계약 실패 테스트를 추가한다.
- [ ] T012 [P] [US1] `Backend/src/test/java/com/animalleague/april/contract/professor/ProfessorContractTest.java`에 교수 성별 검증과 캐릭터 에셋 필드를 포함한 교수 계약 실패 테스트를 추가한다.
- [ ] T013 [P] [US1] `Backend/src/test/java/com/animalleague/april/integration/account/AccountProfessorFlowIntegrationTest.java`에 계정-교수 통합 흐름과 캐릭터 에셋 상태 조회 실패 테스트를 추가한다.
- [ ] T014 [P] [US1] `Backend/src/test/java/com/animalleague/april/unit/auth/SignupPolicyUnitTest.java`, `Backend/src/test/java/com/animalleague/april/unit/professor/AffectionInitializationUnitTest.java`, `Backend/src/test/java/com/animalleague/april/unit/professor/ProfessorCharacterAssetPolicyUnitTest.java`에 회원 가입 정책, 초기 호감도, 캐릭터 에셋 생성 대체 규칙 단위 실패 테스트를 추가한다.

#### 작업 1.6: 계정과 인증 구현

- [ ] T015 [US1] `Backend/src/main/java/com/animalleague/april/auth/domain/User.java`와 `Backend/src/main/java/com/animalleague/april/auth/infrastructure/UserRepository.java`에 사용자 애그리거트와 저장소를 구현한다.
- [ ] T016 [US1] `Backend/src/main/java/com/animalleague/april/auth/application/AuthService.java`에 회원 가입 및 로그인 애플리케이션 서비스를 구현한다.
- [ ] T017 [US1] `Backend/src/main/java/com/animalleague/april/auth/api/AuthController.java`, `Backend/src/main/java/com/animalleague/april/auth/api/SignupRequest.java`, `Backend/src/main/java/com/animalleague/april/auth/api/LoginRequest.java`에 인증 요청/응답 DTO와 컨트롤러를 구현한다.

#### 작업 1.7: 교수, 호감도, 캐릭터 에셋 구현

- [ ] T018 [P] [US1] `Backend/src/main/java/com/animalleague/april/professor/domain/Professor.java`, `Backend/src/main/java/com/animalleague/april/professor/domain/Affection.java`, `Backend/src/main/java/com/animalleague/april/professor/domain/ProfessorCharacterAsset.java`에 교수, 호감도, 캐릭터 에셋 애그리거트를 구현한다.
- [ ] T019 [P] [US1] `Backend/src/main/java/com/animalleague/april/professor/infrastructure/ProfessorRepository.java`, `Backend/src/main/java/com/animalleague/april/professor/infrastructure/AffectionRepository.java`, `Backend/src/main/java/com/animalleague/april/professor/infrastructure/ProfessorCharacterAssetRepository.java`에 교수, 호감도, 캐릭터 에셋 저장소를 구현한다.
- [ ] T020 [US1] `Backend/src/main/java/com/animalleague/april/professor/application/ProfessorService.java`와 `Backend/src/main/java/com/animalleague/april/professor/application/ProfessorCharacterAssetGenerationService.java`에 교수 서비스, 상세 조회, 에셋 생성 오케스트레이션을 구현한다.
- [ ] T021 [US1] `Backend/src/main/java/com/animalleague/april/professor/infrastructure/NanobananaClient.java`와 `Backend/src/main/java/com/animalleague/april/professor/infrastructure/ProfessorImageStorage.java`에 `nanobanana` 연동 클라이언트와 원본 사진/에셋 저장 포트를 구현한다.
- [ ] T022 [US1] `Backend/src/main/java/com/animalleague/april/professor/api/ProfessorController.java`, `Backend/src/main/java/com/animalleague/april/professor/api/ProfessorCreateRequest.java`, `Backend/src/main/java/com/animalleague/april/professor/api/ProfessorDetailResponse.java`에 `sourcePhotoUrl`, `characterAssetStatus`, `representativeAssetUrl`, `characterAssets`를 포함한 교수 등록/목록/상세 API와 DTO를 구현한다.

#### 작업 1.8: 사용자 스토리 1 계약 동기화

- [ ] T023 [US1] `Docs/api-spec-v0.1.md`, `Docs/developer-spec-v0.1.md`, `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`에 계정, 교수, 캐릭터 에셋 계약 내용을 반영한다.

**체크포인트**: 사용자 스토리 1이 완전히 동작하고, 문서화되며, 독립적으로 테스트 가능하다.

---

## 스프린트 2: 공부 세션 정산과 시나리오 이벤트 MVP

### 4단계: 사용자 스토리 2 - 공부 세션 정산 및 이벤트 처리 (우선순위: P1)

**목표**: 사용자가 공부 세션을 시작하고 종료했을 때 유효 공부 시간, 패널티, 대사, 이벤트를 서버 기준으로 일관되게 정산한다.

**독립 검증 방법**: 인증된 사용자가 교수를 선택해 세션을 시작하고 가시성 이벤트를 기록한 뒤 세션 종료와 이벤트 선택까지 수행했을 때 정산 결과가 문서 규칙과 일치하면 된다.

#### 작업 2.1: 사용자 스토리 2 실패 테스트

- [ ] T024 [P] [US2] `Backend/src/test/java/com/animalleague/april/contract/study/StudySessionContractTest.java`에 공부 세션 계약 실패 테스트를 추가한다.
- [ ] T025 [P] [US2] `Backend/src/test/java/com/animalleague/april/contract/dialogue/ScenarioEventContractTest.java`에 시나리오 이벤트 계약 실패 테스트를 추가한다.
- [ ] T026 [P] [US2] `Backend/src/test/java/com/animalleague/april/integration/study/StudySessionSettlementIntegrationTest.java`에 공부 정산 통합 실패 테스트를 추가한다.
- [ ] T027 [P] [US2] `Backend/src/test/java/com/animalleague/april/unit/study/AffectionSettlementCalculatorUnitTest.java`와 `Backend/src/test/java/com/animalleague/april/unit/dialogue/ScenarioEventResolverUnitTest.java`에 정산 계산기와 이벤트 해석기 단위 실패 테스트를 추가한다.

#### 작업 2.2: 공부 세션과 숨김 구간 구현

- [ ] T028 [P] [US2] `Backend/src/main/java/com/animalleague/april/study/domain/StudySession.java`와 `Backend/src/main/java/com/animalleague/april/study/domain/StudySessionHiddenSegment.java`에 공부 세션과 숨김 구간 애그리거트를 구현한다.
- [ ] T029 [P] [US2] `Backend/src/main/java/com/animalleague/april/study/infrastructure/StudySessionRepository.java`와 `Backend/src/main/java/com/animalleague/april/study/infrastructure/StudySessionHiddenSegmentRepository.java`에 공부 세션 저장소를 구현한다.
- [ ] T030 [US2] `Backend/src/main/java/com/animalleague/april/study/domain/ActiveStudySessionPolicy.java`와 `Backend/src/main/java/com/animalleague/april/study/domain/AffectionSettlementCalculator.java`에 활성 세션 정책과 정산 계산기를 구현한다.
- [ ] T031 [US2] `Backend/src/main/java/com/animalleague/april/study/application/StudySessionService.java`에 세션 시작/가시성 기록/종료 서비스를 구현한다.
- [ ] T032 [US2] `Backend/src/main/java/com/animalleague/april/study/api/StudySessionController.java`, `Backend/src/main/java/com/animalleague/april/study/api/VisibilityEventRequest.java`, `Backend/src/main/java/com/animalleague/april/study/api/StudySessionEndResponse.java`에 공부 세션 API와 DTO를 구현한다.

#### 작업 2.3: 대사와 시나리오 이벤트 구현

- [ ] T033 [P] [US2] `Backend/src/main/java/com/animalleague/april/dialogue/domain/Dialogue.java`, `Backend/src/main/java/com/animalleague/april/dialogue/domain/ScenarioEvent.java`, `Backend/src/main/java/com/animalleague/april/dialogue/domain/ScenarioEventChoice.java`에 대사와 시나리오 이벤트 애그리거트를 구현한다.
- [ ] T034 [P] [US2] `Backend/src/main/java/com/animalleague/april/dialogue/infrastructure/DialogueRepository.java`와 `Backend/src/main/java/com/animalleague/april/dialogue/infrastructure/ScenarioEventRepository.java`에 대사 및 이벤트 조회 저장소를 구현한다.
- [ ] T035 [US2] `Backend/src/main/java/com/animalleague/april/dialogue/application/ScenarioEventService.java`에 대사 선택과 시나리오 이벤트 결과 처리 서비스를 구현한다.
- [ ] T036 [US2] `Backend/src/main/java/com/animalleague/april/dialogue/api/ScenarioEventController.java`에 시나리오 이벤트 선택 컨트롤러를 구현한다.

#### 작업 2.4: 사용자 스토리 2 계약 동기화

- [ ] T037 [US2] `Docs/api-spec-v0.1.md`, `Docs/BE-comment-v1.0.md`, `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`에 공부 세션 및 시나리오 이벤트 계약을 반영한다.

**체크포인트**: 사용자 스토리 2가 완전히 동작하고, 문서화되며, 준비된 계정/교수 데이터 기준으로 독립 테스트 가능하다.

---

## 스프린트 3: 최종 결과와 릴리즈 안정화

### 5단계: 사용자 스토리 3 - 최종 결과 산출 (우선순위: P2)

**목표**: 시험 종료일이 지난 사용자에게 대표 교수, 최고/최저 호감도 교수, 점수 반응이 포함된 최종 결과를 제공한다.

**독립 검증 방법**: 시험 종료일이 지난 사용자 계정에서 점수를 제출했을 때 대표 교수와 최고/최저 교수 반응이 함께 반환되면 된다.

#### 작업 3.1: 사용자 스토리 3 실패 테스트

- [ ] T038 [P] [US3] `Backend/src/test/java/com/animalleague/april/contract/result/FinalResultContractTest.java`에 최종 결과 계약 실패 테스트를 추가한다.
- [ ] T039 [P] [US3] `Backend/src/test/java/com/animalleague/april/integration/result/FinalResultIntegrationTest.java`에 최종 결과 통합 실패 테스트를 추가한다.
- [ ] T040 [P] [US3] `Backend/src/test/java/com/animalleague/april/unit/result/FinalResultPolicyUnitTest.java`에 최종 결과 정책 단위 실패 테스트를 추가한다.

#### 작업 3.2: 최종 결과 구현

- [ ] T041 [P] [US3] `Backend/src/main/java/com/animalleague/april/result/domain/FinalResult.java`와 `Backend/src/main/java/com/animalleague/april/result/infrastructure/FinalResultRepository.java`에 최종 결과 애그리거트와 저장소를 구현한다.
- [ ] T042 [P] [US3] `Backend/src/main/java/com/animalleague/april/result/domain/FinalResultPolicy.java`와 `Backend/src/main/java/com/animalleague/april/result/domain/ExamEndDateGuard.java`에 최종 결과 정책과 시험 종료일 가드를 구현한다.
- [ ] T043 [US3] `Backend/src/main/java/com/animalleague/april/result/application/FinalResultService.java`에 최종 결과 애플리케이션 서비스를 구현한다.
- [ ] T044 [US3] `Backend/src/main/java/com/animalleague/april/result/api/FinalResultController.java`, `Backend/src/main/java/com/animalleague/april/result/api/FinalResultRequest.java`, `Backend/src/main/java/com/animalleague/april/result/api/FinalResultResponse.java`에 최종 결과 API와 응답 DTO를 구현한다.

#### 작업 3.3: 사용자 스토리 3 계약 동기화

- [ ] T045 [US3] `Docs/api-spec-v0.1.md`, `Docs/developer-spec-v0.1.md`, `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`에 최종 결과 계약을 반영한다.

**체크포인트**: 사용자 스토리 3이 동작하고, 문서화되며, 준비된 호감도 데이터 기준으로 독립 테스트 가능하다.

---

### 6단계: 다듬기와 공통 개선

**목적**: 여러 사용자 스토리에 걸친 백엔드 릴리즈 품질을 강화한다.

#### 작업 3.4: 콘텐츠, CI, 릴리즈 안정화

- [ ] T046 [P] `Backend/src/main/resources/seed/dialogues/default-dialogues.json`, `Backend/src/main/resources/seed/events/default-events.json`, `Backend/src/main/resources/seed/characters/default-character-assets.json`에 백엔드 시드 콘텐츠와 기본 캐릭터 에셋 매니페스트 파일을 추가한다.
- [ ] T047 [P] `specs/001-backend-mvp/quickstart.md`와 `Docs/BE-comment-v1.0.md`에 백엔드 스모크 테스트, `nanobanana` 검증, 대체 에셋 확인 명령을 정리한다.
- [ ] T048 `.github/workflows/backend-ci.yml`와 `Backend/build.gradle.kts`에서 백엔드 테스트/빌드 파이프라인을 강화한다.
- [ ] T049 `Docs/developer-spec-v0.1.md`와 `.github/pull_request_template.md`에 브랜치별 백엔드 전달 규칙 준수 여부를 점검한다.

---

## 의존성과 실행 순서

### 스프린트 의존성

- **스프린트 1**: 상위 의존성이 없으며, 실행 가능한 백엔드 골격과 MVP 사용자 준비 흐름을 만든다.
- **스프린트 2**: 공부 세션은 인증된 사용자와 저장된 교수가 필요하므로 스프린트 1 완료에 의존한다.
- **스프린트 3**: 현실적인 호감도 상태와 최종 결과 입력이 필요하므로 스프린트 2 완료에 의존한다.

### 사용자 스토리 의존성

- **US1**: 준비 단계와 기반 단계 완료에만 의존한다.
- **US2**: 인증된 사용자와 교수 소유권 맥락이 필요하므로 US1에 의존한다.
- **US3**: 사용자/교수 상태를 위해 US1에, 호감도 진행 상태를 위해 US2에 의존한다.

### 사용자 스토리 내부 규칙

- 테스트는 반드시 구현보다 먼저 작성되고 실패를 확인해야 한다.
- 같은 스토리 안에서도 계약 테스트, 통합 테스트, 단위 테스트는 병렬로 진행할 수 있다.
- `[P]`가 붙은 도메인 및 저장소 작업은 서로 다른 모듈을 다루면 병렬로 진행할 수 있다.
- 문서 갱신은 구현과 함께 포함되어야 하며 나중으로 미루지 않는다.

### 병렬 작업 기회

- `T003`, `T004`, `T005`는 저장소 구조가 확인되면 병렬로 진행할 수 있다.
- `T007`, `T009`는 기반 작업 단계에서 병렬로 진행할 수 있다.
- `T011`부터 `T014`까지는 사용자 스토리 1의 실패 테스트로 병렬 진행할 수 있다.
- `T018`, `T019`는 인증 세션 처리가 준비된 뒤 병렬로 진행할 수 있다.
- `T024`부터 `T027`까지는 사용자 스토리 2의 실패 테스트로 병렬 진행할 수 있다.
- `T028`과 `T029`, `T033`와 `T034`는 서로 다른 백엔드 모듈을 다루므로 병렬 진행할 수 있다.
- `T038`부터 `T040`까지는 사용자 스토리 3의 실패 테스트로 병렬 진행할 수 있다.
- `T041`, `T042`는 최종 결과 서비스 통합 전 병렬로 진행할 수 있다.
- `T046`, `T047`은 최종 안정화 단계에서 병렬 진행할 수 있다.

---

## 구현 전략

### MVP 우선 전략 (스프린트 1 / 사용자 스토리 1)

1. 준비 단계를 완료한다.
2. 기반 단계를 완료한다.
3. 사용자 스토리 1의 실패 테스트를 먼저 작성한다.
4. 인증과 교수 흐름을 구현한다.
5. 계약 문서를 동기화하고 독립 검증을 확인한다.

### 점진적 전달 전략

1. 스프린트 1로 계정/교수 계약을 먼저 고정한다.
2. 스프린트 2로 핵심 공부 보상 루프를 완성한다.
3. 스프린트 3로 최종 결과 흐름과 릴리즈 안정화를 추가한다.

### 병렬 팀 전략

1. 준비 단계와 기반 단계를 함께 마무리한다.
2. 스프린트 1 구현은 `auth`와 `professor` 축으로 분리한다.
3. 스프린트 2 구현은 `study`와 `dialogue` 축으로 분리한다.
4. 각 스프린트는 계약 검증, 통합 테스트, 문서 갱신을 통해 다시 통합한다.

---

## 메모

- [P] 작업은 서로 다른 파일을 다루며 미충족 의존성이 없다.
- 각 사용자 스토리는 준비된 선행조건 위에서 독립적으로 검증 가능해야 한다.
- 백엔드 전용 범위는 의도된 것이며, 프론트엔드 통합은 버전 계약 문서로 처리한다.
- 계약 변경이 생기면 `Docs/api-spec-v0.1.md`와 `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`을 함께 갱신해야 한다.
