# 딸깍 콘테스트 (애니멀 리그)

API 명세 v0.1

기준 문서:

- `Docs/product-plan-v1.0.md`
- `Docs/developer-spec-v0.1.md`

## 1. 문서 목적

이 문서는 프론트엔드와 백엔드가 공유하는 HTTP API 계약의 현재 기준 초안이다.
하위 호환을 깨는 변경이 발생하면 구현보다 먼저 이 문서를 갱신해야 한다.

## 2. 공통 규칙

- 모든 시간 값은 ISO 8601 문자열을 사용한다.
- 모든 응답은 성공 시 JSON 객체를 반환한다.
- 인증이 필요한 엔드포인트는 미인증 요청에 대해 401을 반환한다.
- 계약 불일치 또는 미해결 질문은 `Docs/FE-comment-v1.0.md` 또는
  `Docs/BE-comment-v1.0.md`에 기록한다.

## 3. 인증

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| POST | `/api/auth/signup` | 회원 가입 및 시험 종료일 저장 |
| POST | `/api/auth/login` | 로그인 및 활성 세션 정보 조회 |

### `POST /api/auth/signup`

요청 필드:

- `name`
- `loginId`
- `password`
- `examEndDate`

응답 필드:

- `user.id`
- `user.name`
- `user.loginId`
- `user.examEndDate`

### `POST /api/auth/login`

요청 필드:

- `loginId`
- `password`

응답 필드:

- `user`
- `activeSession`

## 4. 교수

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| POST | `/api/professors` | 교수 등록 |
| GET | `/api/professors` | 교수 목록 조회 |
| GET | `/api/professors/:professorId` | 교수 상세 및 현재 호감도 조회 |

### `POST /api/professors`

요청 필드:

- `professorName`
- `gender`: `male` 또는 `female`
- `personalityType`: `gentle`, `tsundere`, `english_mix`, `shy`
- `sourcePhotoUrl` nullable: 업로드 완료된 교수 원본 사진 URL

처리 규칙:

- `sourcePhotoUrl`이 있으면 백엔드는 해당 원본 사진을 기준으로 `nanobanana` 기반
  캐릭터 에셋 생성 작업을 시작한다.
- 생성 대상은 여러 표정과 포즈를 가진 전신 2D 캐릭터 에셋 세트다.
- 생성 작업이 진행 중이면 `characterAssetStatus`는 `pending`이고,
  `representativeAssetUrl`은 아직 없을 수 있다.
- 교수 사진이 없거나 생성에 실패하면 기본 캐릭터 에셋 세트를 사용하며,
  이 경우 `characterAssetStatus`는 `ready`이고
  `isDefaultCharacterAssets`는 `true`다.

응답 필드:

- `professor.id`
- `professor.professorName`
- `professor.gender`
- `professor.personalityType`
- `professor.sourcePhotoUrl`
- `professor.characterAssetStatus`
- `professor.representativeAssetUrl`
- `professor.isDefaultCharacterAssets`

### `GET /api/professors`

응답 필드:

- `professors[].id`
- `professors[].professorName`
- `professors[].gender`
- `professors[].personalityType`
- `professors[].sourcePhotoUrl`
- `professors[].characterAssetStatus`
- `professors[].representativeAssetUrl`
- `professors[].isDefaultCharacterAssets`

### `GET /api/professors/:professorId`

응답 필드:

- `professor.id`
- `professor.professorName`
- `professor.gender`
- `professor.personalityType`
- `professor.sourcePhotoUrl`
- `professor.characterAssetStatus`
- `professor.representativeAssetUrl`
- `professor.isDefaultCharacterAssets`
- `affection.professorId`
- `affection.affectionScore`
- `characterAssets[].variantKey`
- `characterAssets[].imageUrl`
- `characterAssets[].isDefaultAsset`

## 5. 공부 세션

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| POST | `/api/study-sessions` | 공부 세션 시작 |
| GET | `/api/study-sessions/active` | 활성 세션 복구 |
| POST | `/api/study-sessions/:sessionId/visibility` | 숨김/복귀 이벤트 기록 |
| POST | `/api/study-sessions/:sessionId/end` | 공부 세션 종료 및 정산 |

### `POST /api/study-sessions/:sessionId/visibility`

요청 필드:

- `type`: `hidden` 또는 `visible`
- `occurredAt`

### `POST /api/study-sessions/:sessionId/end`

응답 핵심 필드:

- `sessionSummary.totalSeconds`
- `sessionSummary.hiddenSeconds`
- `sessionSummary.effectiveStudySeconds`
- `sessionSummary.globalAffectionGain`
- `sessionSummary.updatedProfessorCount`
- `sessionSummary.earlyTerminationPenalty`
- `sessionSummary.idlePenaltyCount`
- `sessionSummary.currentProfessorAffectionDelta`
- `sessionSummary.currentProfessorNextAffectionScore`
- `dialogue.triggerType`
- `dialogue.lines[]`
- `event`

`dialogue.lines[]` 규칙:

- 모든 항목은 `kind`, `text`를 가진다.
- `kind = dialogue`인 항목만 `speakerRole`을 가진다.
- `speakerRole = professor`인 대사 라인만 `speakerName`을 가진다.
- `{교수}`는 서버에서 실제 교수 이름으로 치환한 뒤 `speakerName`으로 전달한다.
- `{주인공}`은 작성용 라벨이며 응답 본문에는 포함하지 않는다.
- 적용되지 않는 키는 `null`로 보내지 않고 응답에서 생략한다.

## 6. 시나리오 이벤트

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| POST | `/api/scenario-events/:eventId/choices/:choiceId` | 선택지 결과 처리 |

응답 핵심 필드:

- `result.lines[]`
- `result.affectionDelta`
- `result.nextAffectionScore`
- `result.nextState`: `resolved`

`event` 응답 필드:

- `event.triggered`
- `event.eventId`
- `event.title`
- `event.rangeBand`: `0-19`, `20-49`, `50-84`, `85-100`
- `event.branchKey`
- `event.lines[]`
- `event.choices[]`

## 7. 최종 결과

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| POST | `/api/final-results` | 시험 점수 입력 및 엔딩 결과 생성 |

요청 필드:

- `examScore`

응답 핵심 필드:

- `result.primaryProfessorId`
- `result.highestProfessorId`
- `result.lowestProfessorId`
- `result.scoreBand`
- `result.endingType`
- `result.script[]`

## 8. 버전 관리 규칙

- 하위 호환을 깨는 API 변경은 문서 버전을 올리고 관련 FE/BE 작업을 동시에 조정한다.
- 구현은 이 문서보다 앞설 수 없으며, 문서가 먼저 갱신되어야 한다.
- 미확정 계약은 PR 대화로 흩어두지 않고 코멘트 문서에 남긴다.
