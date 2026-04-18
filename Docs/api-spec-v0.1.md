# 딸깍 콘테스트 (애니멀 리그)

API 명세 v0.1

기준 문서:

- `Docs/product-plan-v1.0.md`
- `Docs/developer-spec-v0.1.md`

## 1. 문서 목적

이 문서는 프론트엔드와 백엔드가 공유하는 HTTP API 계약의 현재 기준 초안이다.
breaking change가 발생하면 구현보다 먼저 이 문서를 갱신해야 한다.

## 2. 공통 규칙

- 모든 시간 값은 ISO 8601 문자열을 사용한다.
- 모든 응답은 성공 시 JSON 객체를 반환한다.
- 인증이 필요한 엔드포인트는 미인증 요청에 대해 401을 반환한다.
- contract mismatch 또는 미해결 질문은 `Docs/FE-comment-v1.0.md` 또는
  `Docs/BE-comment-v1.0.md`에 기록한다.

## 3. 인증

| Method | Path | 설명 |
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

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/api/professors` | 교수 등록 |
| GET | `/api/professors` | 교수 목록 조회 |
| GET | `/api/professors/:professorId` | 교수 상세 및 현재 호감도 조회 |

## 5. 공부 세션

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/api/study-sessions` | 공부 세션 시작 |
| GET | `/api/study-sessions/active` | 활성 세션 복구 |
| POST | `/api/study-sessions/:sessionId/visibility` | hidden/visible 이벤트 기록 |
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
- `sessionSummary.affectionGain`
- `sessionSummary.earlyTerminationPenalty`
- `sessionSummary.idlePenaltyCount`
- `sessionSummary.affectionDelta`
- `sessionSummary.nextAffectionScore`
- `dialogue`
- `event`

## 6. 시나리오 이벤트

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/api/scenario-events/:eventId/choices/:choiceId` | 선택지 결과 처리 |

응답 핵심 필드:

- `result.resultText`
- `result.affectionDelta`
- `result.nextAffectionScore`

## 7. 최종 결과

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/api/final-results` | 시험 점수 입력 및 엔딩 결과 생성 |

요청 필드:

- `examScore`

응답 핵심 필드:

- `result.primaryProfessorId`
- `result.highestProfessorId`
- `result.lowestProfessorId`
- `result.scoreBand`
- `result.dialogues`

## 8. 버전 관리 규칙

- 하위 호환을 깨는 API 변경은 문서 버전을 증가시키고 관련 FE/BE 작업을 동시에 조정한다.
- 구현은 이 문서보다 앞설 수 없으며, 문서가 먼저 갱신되어야 한다.
- 미확정 계약은 PR 대화로 흩어두지 않고 comment 문서에 남긴다.

