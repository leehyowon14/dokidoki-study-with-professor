# 작업 목록: 백엔드 MVP 기반

**입력**: `/specs/001-backend-mvp/`의 설계 문서  
**선행 문서**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `quickstart.md`, `contracts/backend-api.openapi.yaml`  
**테스트 원칙**: 모든 사용자 스토리는 TDD에 따라 실패하는 테스트부터 시작해야 한다.  
**분류 원칙**: 이 문서는 `Sprint > Task(issue 단위) > atomic task` 구조를 사용한다. 각 Task는 GitHub 이슈 1개에 대응하고, atomic task는 이슈 내부 체크리스트로 바로 옮길 수 있는 실행 단위다.  
**협업 원칙**: 2인 백엔드 협업이 실제로 가능하도록 Task는 가능한 한 `서로 다른 파일 집합`을 소유하도록 분리한다. 즉, 같은 스프린트 안에서도 서로 다른 사람이 동시에 작업할 수 있어야 한다.

## 형식: `[ID] [P?] [Story] 설명`

- **[P]**: 병렬 실행 가능. 서로 다른 파일을 다루고 미완료 의존성이 없는 작업만 표기한다.
- **[Story]**: 사용자 스토리 작업에만 `US1`, `US2`, `US3` 라벨을 붙인다.
- 모든 atomic task 설명에는 정확한 파일 경로를 포함한다.

## 경로 규칙

- **백엔드 코드**: `Backend/src/main/java/com/animalleague/april/`
- **백엔드 테스트**: `Backend/src/test/java/com/animalleague/april/`
- **백엔드 리소스**: `Backend/src/main/resources/`
- **문서**: `Docs/`, `specs/001-backend-mvp/`
- **시나리오 원문**: `Backend/Reference/`
- **GitHub 워크플로**: `.github/workflows/`

## Sprint 1: 기반 고정과 US1 병렬 구현

**목표**: 프로젝트 골격, 공통 기반, 인증, 교수 등록/조회, 캐릭터 에셋 상태 조회까지 완료한다.  
**독립 검증 기준**: 신규 사용자가 회원 가입 후 로그인하고 교수 1명을 등록한 뒤, 교수 상세에서 `characterAssetStatus`, 기본 호감도, 대표 에셋 또는 fallback 상태를 확인할 수 있어야 한다.

### Task 1.1 (Issue): 공통 플랫폼과 CI 초기화

**Owner**: Shared  
**의존성**: 없음

- [ ] T001 `Backend/build.gradle.kts`와 `Backend/settings.gradle.kts`에 Spring Boot, Spring Security, Spring Data JPA, Flyway, Testcontainers 기반 Gradle 구성을 생성한다.
- [ ] T002 `Backend/src/main/java/com/animalleague/april/AprilApplication.java`와 `Backend/src/main/resources/application.yml`에 애플리케이션 진입점과 기본 런타임 설정을 생성한다.
- [ ] T003 [P] `Backend/src/test/resources/application-test.yml`에 PostgreSQL Testcontainers용 테스트 설정을 생성한다.
- [ ] T004 [P] `.github/workflows/backend-ci.yml`에 백엔드 빌드, 단위 테스트, 계약 테스트, 통합 테스트를 실행하는 CI 워크플로를 추가한다.
- [ ] T005 `specs/001-backend-mvp/quickstart.md`와 `Docs/BE-comment-v1.0.md`에 실행 명령과 필수 환경 변수 기준을 구현 착수 기준으로 동기화한다.

### Task 1.2 (Issue): 공통 런타임과 테스트 기반 구축

**Owner**: Shared  
**의존성**: Task 1.1

- [ ] T006 `Backend/src/main/java/com/animalleague/april/common/api/ErrorResponse.java`와 `Backend/src/main/java/com/animalleague/april/common/infrastructure/GlobalExceptionHandler.java`에 공통 오류 응답과 예외 매핑을 구현한다.
- [ ] T007 [P] `Backend/src/main/java/com/animalleague/april/common/domain/Gender.java`, `Backend/src/main/java/com/animalleague/april/common/domain/CharacterAssetStatus.java`, `Backend/src/main/java/com/animalleague/april/common/domain/PersonalityType.java`, `Backend/src/main/java/com/animalleague/april/common/domain/DialogueTriggerType.java`, `Backend/src/main/java/com/animalleague/april/common/domain/ScriptLineKind.java`, `Backend/src/main/java/com/animalleague/april/common/domain/SpeakerRole.java`에 공통 enum과 값 객체를 정의한다.
- [ ] T008 [P] `Backend/src/main/java/com/animalleague/april/common/infrastructure/config/PersistenceConfig.java`와 `Backend/src/main/java/com/animalleague/april/common/infrastructure/config/SecurityConfig.java`에 영속성 및 세션 인증 설정을 구현한다.
- [ ] T009 [P] `Backend/src/test/java/com/animalleague/april/contract/support/ApiContractTest.java`와 `Backend/src/test/java/com/animalleague/april/integration/support/PostgresIntegrationTest.java`에 MockMvc와 Testcontainers 공통 테스트 기반을 만든다.
- [ ] T010 `Docs/api-spec-v0.1.md`, `Docs/dialogue-content-spec-v0.1.md`, `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`에 구현 기준 계약 freeze 여부와 선행 미해결 항목을 기록한다.

### Task 1.3 (Issue): [US1] 인증 도메인과 로그인 API

**Owner**: 개발자 A  
**의존성**: Task 1.2

- [ ] T011 [P] [US1] `Backend/src/test/java/com/animalleague/april/contract/auth/AuthContractTest.java`에 회원 가입과 로그인 계약 실패 테스트를 추가한다.
- [ ] T012 [P] [US1] `Backend/src/test/java/com/animalleague/april/integration/auth/AuthFlowIntegrationTest.java`에 회원 가입 후 로그인과 활성 세션 조회 통합 실패 테스트를 추가한다.
- [ ] T013 [P] [US1] `Backend/src/test/java/com/animalleague/april/unit/auth/SignupPolicyUnitTest.java`와 `Backend/src/test/java/com/animalleague/april/unit/auth/LoginPolicyUnitTest.java`에 인증 정책 단위 실패 테스트를 추가한다.
- [ ] T014 [US1] `Backend/src/main/resources/db/migration/V1__create_users.sql`에 사용자 테이블 스키마를 정의한다.
- [ ] T015 [US1] `Backend/src/main/java/com/animalleague/april/auth/domain/User.java`와 `Backend/src/main/java/com/animalleague/april/auth/infrastructure/UserRepository.java`에 사용자 애그리거트와 저장소를 구현한다.
- [ ] T016 [US1] `Backend/src/main/java/com/animalleague/april/auth/application/AuthService.java`, `Backend/src/main/java/com/animalleague/april/auth/api/AuthController.java`, `Backend/src/main/java/com/animalleague/april/auth/api/SignupRequest.java`, `Backend/src/main/java/com/animalleague/april/auth/api/LoginRequest.java`에 회원 가입/로그인 서비스와 API를 구현한다.

### Task 1.4 (Issue): [US1] 교수, 호감도, 등록 API 코어

**Owner**: 개발자 A  
**의존성**: Task 1.2  
**병렬성**: Task 1.5와 병렬 진행 가능

- [ ] T017 [P] [US1] `Backend/src/test/java/com/animalleague/april/contract/professor/ProfessorContractTest.java`에 교수 등록/목록/상세 계약과 `male`, `female` 성별 검증 실패 테스트를 추가한다.
- [ ] T018 [P] [US1] `Backend/src/test/java/com/animalleague/april/integration/professor/ProfessorRegistrationIntegrationTest.java`에 교수 등록과 기본 호감도 생성 통합 실패 테스트를 추가한다.
- [ ] T019 [P] [US1] `Backend/src/test/java/com/animalleague/april/unit/professor/AffectionInitializationUnitTest.java`에 교수 등록 시 호감도 초기화 단위 실패 테스트를 추가한다.
- [ ] T020 [US1] `Backend/src/main/resources/db/migration/V2__create_professor_affection_tables.sql`에 `professors`, `affections` 테이블 스키마를 정의한다.
- [ ] T021 [US1] `Backend/src/main/java/com/animalleague/april/professor/domain/Professor.java`와 `Backend/src/main/java/com/animalleague/april/professor/domain/Affection.java`에 교수와 호감도 애그리거트를 구현한다.
- [ ] T022 [US1] `Backend/src/main/java/com/animalleague/april/professor/infrastructure/ProfessorRepository.java`, `Backend/src/main/java/com/animalleague/april/professor/infrastructure/AffectionRepository.java`, `Backend/src/main/java/com/animalleague/april/professor/application/ProfessorService.java`, `Backend/src/main/java/com/animalleague/april/professor/api/ProfessorController.java`에 교수 등록/조회 코어를 구현한다.

### Task 1.5 (Issue): [US1] 캐릭터 에셋 생성 파이프라인

**Owner**: 개발자 B  
**의존성**: Task 1.2  
**병렬성**: Task 1.4와 병렬 진행 가능

- [ ] T023 [P] [US1] `Backend/src/test/java/com/animalleague/april/unit/professor/ProfessorCharacterAssetGenerationServiceUnitTest.java`에 `nanobanana` 요청, `pending` 전이, 생성 실패 fallback 단위 실패 테스트를 추가한다.
- [ ] T024 [P] [US1] `Backend/src/test/java/com/animalleague/april/integration/professor/ProfessorCharacterAssetGenerationIntegrationTest.java`에 원본 사진 등록 후 생성 상태 전이와 대표 에셋 선택 통합 실패 테스트를 추가한다.
- [ ] T025 [US1] `Backend/src/main/resources/db/migration/V3__create_professor_character_assets_table.sql`에 `professor_character_assets` 테이블 스키마를 정의한다.
- [ ] T026 [US1] `Backend/src/main/resources/seed/characters/default-character-assets.json`와 `Backend/src/main/java/com/animalleague/april/professor/application/DefaultCharacterAssetCatalog.java`에 기본 캐릭터 에셋 매니페스트와 조회 계층을 구현한다.
- [ ] T027 [US1] `Backend/src/main/java/com/animalleague/april/professor/domain/ProfessorCharacterAsset.java`, `Backend/src/main/java/com/animalleague/april/professor/infrastructure/ProfessorCharacterAssetRepository.java`, `Backend/src/main/java/com/animalleague/april/professor/infrastructure/NanobananaClient.java`, `Backend/src/main/java/com/animalleague/april/professor/infrastructure/ProfessorImageStorage.java`, `Backend/src/main/java/com/animalleague/april/professor/application/ProfessorCharacterAssetGenerationService.java`에 에셋 생성 포트와 오케스트레이션을 구현한다.

### Task 1.6 (Issue): [US1] 교수 상세 응답 통합과 계약 동기화

**Owner**: Shared  
**의존성**: Task 1.3, Task 1.4, Task 1.5

- [ ] T028 [P] [US1] `Backend/src/test/java/com/animalleague/april/integration/professor/ProfessorDetailResponseIntegrationTest.java`에 교수 상세 응답의 `characterAssetStatus`, `representativeAssetUrl`, `characterAssets[]` 통합 실패 테스트를 추가한다.
- [ ] T029 [US1] `Backend/src/main/java/com/animalleague/april/professor/api/ProfessorCreateRequest.java`와 `Backend/src/main/java/com/animalleague/april/professor/api/ProfessorDetailResponse.java`에 캐릭터 에셋 관련 DTO 필드를 통합한다.
- [ ] T030 [US1] `Docs/api-spec-v0.1.md`, `Docs/developer-spec-v0.1.md`, `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`, `specs/001-backend-mvp/quickstart.md`에 US1 구현 결과와 계약 차이를 동기화한다.

## Sprint 2: US2 공부 세션과 콘텐츠 엔진 병렬 구현

**목표**: 공부 세션 정산, 전 교수 공통 보상, 현재 교수 패널티, `Reference` 파싱, `lines[]` 기반 대사/이벤트 응답을 구현한다.  
**독립 검증 기준**: 교수 3명이 등록된 계정에서 세션 종료 후 `globalAffectionGain`이 전 교수에 반영되고, 현재 교수에게만 패널티가 적용되며, 응답이 `lines[]`, `rangeBand`, `branchKey` 구조를 반환해야 한다.

### Task 2.1 (Issue): [US2] 공부 세션 정산 코어

**Owner**: 개발자 A  
**의존성**: Sprint 1 완료  
**병렬성**: Task 2.2와 병렬 진행 가능

- [ ] T031 [P] [US2] `Backend/src/test/java/com/animalleague/april/contract/study/StudySessionContractTest.java`에 세션 시작, 활성 세션 조회, visibility 기록, 종료 응답 계약 실패 테스트를 추가한다.
- [ ] T032 [P] [US2] `Backend/src/test/java/com/animalleague/april/integration/study/StudySessionSettlementIntegrationTest.java`에 전 교수 공통 보상, 현재 교수 패널티, 활성 세션 중복 차단 통합 실패 테스트를 추가한다.
- [ ] T033 [P] [US2] `Backend/src/test/java/com/animalleague/april/unit/study/ActiveStudySessionPolicyUnitTest.java`와 `Backend/src/test/java/com/animalleague/april/unit/study/AffectionSettlementCalculatorUnitTest.java`에 20분 미만 중도 포기, hidden 패널티, 전 교수 일괄 보상 단위 실패 테스트를 추가한다.
- [ ] T034 [US2] `Backend/src/main/resources/db/migration/V4__create_study_session_tables.sql`에 `study_sessions`, `study_session_hidden_segments` 테이블 스키마를 정의한다.
- [ ] T035 [US2] `Backend/src/main/java/com/animalleague/april/study/domain/StudySession.java`, `Backend/src/main/java/com/animalleague/april/study/domain/StudySessionHiddenSegment.java`, `Backend/src/main/java/com/animalleague/april/study/domain/AffectionSettlementCalculator.java`, `Backend/src/main/java/com/animalleague/april/study/domain/ActiveStudySessionPolicy.java`에 정산 코어를 구현한다.
- [ ] T036 [US2] `Backend/src/main/java/com/animalleague/april/study/infrastructure/StudySessionRepository.java`, `Backend/src/main/java/com/animalleague/april/study/infrastructure/StudySessionHiddenSegmentRepository.java`, `Backend/src/main/java/com/animalleague/april/study/application/StudySessionService.java`에 세션 저장소와 서비스 코어를 구현한다.

### Task 2.2 (Issue): [US2] Reference 파싱과 런타임 스크립트 모델

**Owner**: 개발자 B  
**의존성**: Sprint 1 완료  
**병렬성**: Task 2.1과 병렬 진행 가능

- [ ] T037 [P] [US2] `Backend/src/test/java/com/animalleague/april/contract/dialogue/DialoguePayloadContractTest.java`와 `Backend/src/test/java/com/animalleague/april/contract/dialogue/ScenarioEventContractTest.java`에 `lines[]`, `rangeBand`, `branchKey`, `nextState` 계약 실패 테스트를 추가한다.
- [ ] T038 [P] [US2] `Backend/src/test/java/com/animalleague/april/integration/dialogue/ReferenceParsingIntegrationTest.java`에 `Backend/Reference/` 원문 파싱, 성격별 분기 선택, `{교수}` 치환 통합 실패 테스트를 추가한다.
- [ ] T039 [P] [US2] `Backend/src/test/java/com/animalleague/april/unit/dialogue/ReferenceMarkdownParserUnitTest.java`, `Backend/src/test/java/com/animalleague/april/unit/dialogue/ScriptLineMapperUnitTest.java`, `Backend/src/test/java/com/animalleague/april/unit/dialogue/PlaceholderSubstitutionPolicyUnitTest.java`에 콘텐츠 파서 단위 실패 테스트를 추가한다.
- [ ] T040 [US2] `Backend/src/main/resources/db/migration/V5__create_dialogue_event_tables.sql`에 `dialogues`, `scenario_events`, `scenario_event_choices` 테이블 스키마를 정의한다.
- [ ] T041 [US2] `Backend/src/main/java/com/animalleague/april/dialogue/domain/Dialogue.java`, `Backend/src/main/java/com/animalleague/april/dialogue/domain/ScenarioEvent.java`, `Backend/src/main/java/com/animalleague/april/dialogue/domain/ScenarioEventChoice.java`, `Backend/src/main/java/com/animalleague/april/dialogue/domain/ScriptLine.java`에 런타임 콘텐츠 모델을 구현한다.
- [ ] T042 [US2] `Backend/src/main/java/com/animalleague/april/dialogue/infrastructure/ReferenceMarkdownParser.java`, `Backend/src/main/java/com/animalleague/april/dialogue/infrastructure/DialogueRepository.java`, `Backend/src/main/java/com/animalleague/april/dialogue/infrastructure/ScenarioEventRepository.java`, `Backend/src/main/java/com/animalleague/april/dialogue/application/DialogueContentLoader.java`에 Reference 파서와 조회 계층을 구현한다.

### Task 2.3 (Issue): [US2] 공부 세션 API와 전 교수 보상 반영

**Owner**: 개발자 A  
**의존성**: Task 2.1

- [ ] T043 [P] [US2] `Backend/src/test/java/com/animalleague/april/integration/study/StudySessionAffectionFanoutIntegrationTest.java`에 세션 대상이 아닌 다른 교수들에게 `globalAffectionGain`만 반영되는 통합 실패 테스트를 추가한다.
- [ ] T044 [P] [US2] `Backend/src/test/java/com/animalleague/april/contract/study/StudySessionEndResponseContractTest.java`에 `globalAffectionGain`, `updatedProfessorCount`, `currentProfessorAffectionDelta` 응답 계약 실패 테스트를 추가한다.
- [ ] T045 [US2] `Backend/src/main/java/com/animalleague/april/study/api/StudySessionController.java`, `Backend/src/main/java/com/animalleague/april/study/api/VisibilityEventRequest.java`, `Backend/src/main/java/com/animalleague/april/study/api/StudySessionEndResponse.java`에 공부 세션 API와 DTO를 구현한다.
- [ ] T046 [US2] `Backend/src/main/java/com/animalleague/april/study/application/StudySessionService.java`에 전 교수 공통 보상 fan-out과 현재 교수 패널티 반영을 통합한다.

### Task 2.4 (Issue): [US2] 이벤트 해결 API와 선택 결과 확정

**Owner**: 개발자 B  
**의존성**: Task 2.2

- [ ] T047 [P] [US2] `Backend/src/test/java/com/animalleague/april/integration/dialogue/StudySessionEventFlowIntegrationTest.java`에 세션 종료 후 이벤트 판정과 선택 결과 확정 통합 실패 테스트를 추가한다.
- [ ] T048 [P] [US2] `Backend/src/test/java/com/animalleague/april/contract/dialogue/ScenarioChoiceResolutionContractTest.java`에 선택 결과 `lines[]`와 `resolved` 상태 계약 실패 테스트를 추가한다.
- [ ] T049 [P] [US2] `Backend/src/test/java/com/animalleague/april/unit/dialogue/ScenarioEventResolverUnitTest.java`와 `Backend/src/test/java/com/animalleague/april/unit/dialogue/ScenarioChoiceResolutionPolicyUnitTest.java`에 이벤트 선택 우선순위와 1회 처리 단위 실패 테스트를 추가한다.
- [ ] T050 [US2] `Backend/src/main/java/com/animalleague/april/dialogue/application/ScenarioEventService.java`, `Backend/src/main/java/com/animalleague/april/dialogue/api/ScenarioEventController.java`, `Backend/src/main/java/com/animalleague/april/dialogue/api/ScenarioChoiceResolutionResponse.java`에 이벤트 해결 API를 구현한다.

### Task 2.5 (Issue): [US2] 세션/이벤트 통합과 콘텐츠 계약 동기화

**Owner**: Shared  
**의존성**: Task 2.3, Task 2.4

- [ ] T051 [P] [US2] `Backend/src/test/java/com/animalleague/april/integration/study/StudySessionAndDialogueE2EIntegrationTest.java`에 세션 종료 응답과 이벤트 응답을 함께 검증하는 종단 실패 테스트를 추가한다.
- [ ] T052 [US2] `Docs/api-spec-v0.1.md`, `Docs/dialogue-content-spec-v0.1.md`, `Docs/developer-spec-v0.1.md`, `Docs/BE-comment-v1.0.md`, `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`에 US2 구현 결과와 콘텐츠 동기화 규칙을 반영한다.

## Sprint 3: US3 최종 결과와 릴리즈 하드닝

**목표**: 시험 종료일 가드, 대표 교수 선정, `script[]` 기반 최종 결과 응답, 콘텐츠 검증 자동화를 구현한다.  
**독립 검증 기준**: 시험 종료일이 지난 사용자가 점수를 제출하면 동점 규칙이 반영된 대표 교수, `endingType`, `script[]`를 포함한 최종 결과를 받아야 하고, CI가 콘텐츠 구조 검증까지 통과해야 한다.

### Task 3.1 (Issue): [US3] 최종 결과 도메인과 API

**Owner**: 개발자 B  
**의존성**: Sprint 2 완료

- [ ] T053 [P] [US3] `Backend/src/test/java/com/animalleague/april/contract/result/FinalResultContractTest.java`에 `scoreBand`, `endingType`, `script[]` 계약 실패 테스트를 추가한다.
- [ ] T054 [P] [US3] `Backend/src/test/java/com/animalleague/april/integration/result/FinalResultIntegrationTest.java`에 시험 종료일 가드, 최고 호감도 동점 규칙, 엔딩 스크립트 반환 통합 실패 테스트를 추가한다.
- [ ] T055 [P] [US3] `Backend/src/test/java/com/animalleague/april/unit/result/FinalResultPolicyUnitTest.java`와 `Backend/src/test/java/com/animalleague/april/unit/result/ExamEndDateGuardUnitTest.java`에 점수 구간과 엔딩 선정 단위 실패 테스트를 추가한다.
- [ ] T056 [US3] `Backend/src/main/resources/db/migration/V6__create_final_results_table.sql`에 `final_results` 테이블 스키마를 정의한다.
- [ ] T057 [US3] `Backend/src/main/java/com/animalleague/april/result/domain/FinalResult.java`, `Backend/src/main/java/com/animalleague/april/result/domain/FinalResultPolicy.java`, `Backend/src/main/java/com/animalleague/april/result/domain/ExamEndDateGuard.java`, `Backend/src/main/java/com/animalleague/april/result/infrastructure/FinalResultRepository.java`, `Backend/src/main/java/com/animalleague/april/result/application/FinalResultService.java`, `Backend/src/main/java/com/animalleague/april/result/api/FinalResultController.java`, `Backend/src/main/java/com/animalleague/april/result/api/FinalResultRequest.java`, `Backend/src/main/java/com/animalleague/april/result/api/FinalResultResponse.java`에 최종 결과 도메인과 API를 구현한다.

### Task 3.2 (Issue): 콘텐츠 검증 자동화와 운영 안전장치

**Owner**: 개발자 A  
**의존성**: Sprint 2 완료  
**병렬성**: Task 3.1과 병렬 진행 가능

- [ ] T058 [P] `Backend/src/test/java/com/animalleague/april/integration/dialogue/ReferenceContentValidationIntegrationTest.java`에 `Backend/Reference/` 구조, 분기 파일명, 플레이스홀더 규칙 검증 테스트를 추가한다.
- [ ] T059 [P] `Backend/src/main/resources/seed/dialogues/default-dialogues.json`와 `Backend/src/main/resources/seed/events/default-events.json`에 시스템 fallback 대사와 이벤트 시드 데이터를 정리한다.
- [ ] T060 [P] `Backend/src/main/java/com/animalleague/april/dialogue/application/DialogueContentHealthCheckService.java`와 `Backend/src/main/java/com/animalleague/april/common/infrastructure/startup/ReferenceContentStartupValidator.java`에 콘텐츠 기동 검증을 구현한다.

### Task 3.3 (Issue): 릴리즈 하드닝과 최종 문서 동기화

**Owner**: Shared  
**의존성**: Task 3.1, Task 3.2

- [ ] T061 `Backend/build.gradle.kts`와 `.github/workflows/backend-ci.yml`에 콘텐츠 검증 테스트와 전체 테스트 파이프라인을 통합한다.
- [ ] T062 `Docs/api-spec-v0.1.md`, `Docs/dialogue-content-spec-v0.1.md`, `Docs/developer-spec-v0.1.md`, `specs/001-backend-mvp/quickstart.md`, `Docs/BE-comment-v1.0.md`에 릴리즈 검증 절차와 운영 전 체크 항목을 반영한다.

## 의존성과 실행 순서

### Sprint 의존성

- **Sprint 1**: 상위 의존성이 없으며, 공통 기반과 US1 MVP를 만든다.
- **Sprint 2**: 인증된 사용자와 등록된 교수가 필요하므로 Sprint 1 완료에 의존한다.
- **Sprint 3**: 실제 호감도 상태와 콘텐츠 조회 기반이 필요하므로 Sprint 2 완료에 의존한다.

### Task(issue) 의존성

- **Task 1.1** 완료 후 **Task 1.2**를 진행한다.
- **Task 1.2** 완료 후 **Task 1.3**, **Task 1.4**, **Task 1.5**를 병렬로 진행할 수 있다.
- **Task 1.6**은 **Task 1.3**, **Task 1.4**, **Task 1.5** 완료 후 진행한다.
- **Task 2.1**과 **Task 2.2**는 Sprint 1 완료 후 병렬로 진행할 수 있다.
- **Task 2.3**은 **Task 2.1** 완료 후 진행한다.
- **Task 2.4**는 **Task 2.2** 완료 후 진행한다.
- **Task 2.5**는 **Task 2.3**과 **Task 2.4** 완료 후 진행한다.
- **Task 3.1**과 **Task 3.2**는 Sprint 2 완료 후 병렬로 진행할 수 있다.
- **Task 3.3**은 **Task 3.1**과 **Task 3.2** 완료 후 진행한다.

### 병렬 실행 기회

- `T003`, `T004`는 `T001`, `T002` 이후 병렬 실행할 수 있다.
- `T007`, `T008`, `T009`는 공통 기반 작업에서 병렬 실행할 수 있다.
- `T011`, `T012`, `T013`은 인증 이슈의 선행 실패 테스트로 병렬 실행할 수 있다.
- `T017`, `T018`, `T019`는 교수 코어 이슈의 선행 실패 테스트로 병렬 실행할 수 있다.
- `T023`, `T024`는 에셋 파이프라인 이슈의 선행 테스트로 병렬 실행할 수 있다.
- `T031`, `T032`, `T033`은 세션 정산 이슈의 선행 테스트로 병렬 실행할 수 있다.
- `T037`, `T038`, `T039`는 콘텐츠 엔진 이슈의 선행 테스트로 병렬 실행할 수 있다.
- `T047`, `T048`, `T049`는 이벤트 해결 이슈의 선행 테스트로 병렬 실행할 수 있다.
- `T053`, `T054`, `T055`는 최종 결과 이슈의 선행 테스트로 병렬 실행할 수 있다.
- `T058`, `T059`, `T060`는 콘텐츠 검증 이슈에서 병렬 실행할 수 있다.

## 구현 전략

### 실제 협업 전략

1. Sprint 1에서 공통 기반이 끝나면 개발자 A는 `auth/professor core`, 개발자 B는 `asset pipeline`을 바로 병렬 진행한다.
2. Sprint 2에서는 개발자 A가 `study settlement`, 개발자 B가 `dialogue/reference parser`를 병렬 진행한다.
3. Sprint 2 후반부터는 A가 세션 API, B가 이벤트 해결 API를 각각 마무리하고, Shared 이슈에서 종단 검증과 문서 동기화를 처리한다.
4. Sprint 3에서는 개발자 B가 `final result`, 개발자 A가 `content validation`을 병렬 진행한 뒤 Shared 이슈에서 릴리즈를 정리한다.

### 왜 이렇게 쪼갰는가

- 사용자 스토리 단위로만 자르면 한 사람이 선행 구현을 끝내야 다음 사람이 움직일 수 있다.
- 실제 협업은 `파일 충돌이 적은 write set` 단위로 자르는 편이 훨씬 안정적이다.
- 그래서 같은 사용자 스토리 안에서도 `auth/professor core`, `asset pipeline`, `study`, `dialogue`, `result`, `docs sync`를 별도 이슈로 분리했다.

## 메모

- 모든 atomic task는 GitHub 이슈 내부 체크리스트로 바로 전환할 수 있게 작성했다.
- `Backend/Reference/`는 콘텐츠 소스 기준선이므로, 파싱/구조 변경은 문서 계약과 함께 움직여야 한다.
- 구현 중 계약이 달라지면 `Docs/api-spec-v0.1.md`와 `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`을 먼저 갱신해야 한다.
- 대사/이벤트 렌더 구조가 달라지면 `Docs/dialogue-content-spec-v0.1.md`도 같은 변경에서 갱신해야 한다.
