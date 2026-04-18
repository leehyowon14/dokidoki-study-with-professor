# 데이터 모델: 백엔드 MVP 기반

## 1. 모델링 원칙

- 모든 핵심 비즈니스 규칙은 서버 모델 기준으로 계산한다.
- 사용자와 교수 간 상태는 교수별로 완전히 독립적이다.
- 공부 세션의 원본 이벤트와 정산 결과를 모두 저장해 사후 검증이 가능해야 한다.
- 프론트엔드는 별도 진행이므로, 응답 형상보다 먼저 도메인 무결성과 상태 전이를 고정한다.

## 2. 애그리거트 개요

### 사용자

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| loginId | string | 영문/숫자 기준 4자 이상, 고유 |
| name | string | 1자 이상 |
| passwordHash | string | 평문 저장 금지 |
| examEndDate | date | 오늘 이전 금지 |
| createdAt | datetime | 생성 시각 |
| updatedAt | datetime | 수정 시각 |

관계:

- 사용자 1:N 교수
- 사용자 1:N 공부 세션
- 사용자 1:N 최종 결과

### 교수

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| userId | UUID | 사용자 FK |
| subjectName | string | 필수 |
| professorName | string | 필수 |
| gender | enum | `male`, `female` |
| personalityType | enum | `cold`, `kind`, `obsessive`, `tsundere` |
| sourcePhotoUrl | string nullable | 업로드한 교수 원본 사진 URL |
| characterAssetStatus | enum | `pending`, `ready` |
| representativeAssetUrl | string nullable | 대표 캐릭터 에셋 URL |
| isDefaultCharacterAssets | boolean | 기본 캐릭터 에셋 사용 여부 |
| createdAt | datetime | 생성 시각 |
| updatedAt | datetime | 수정 시각 |

관계:

- 교수 1:1 호감도 상태(사용자 기준)
- 교수 1:N 교수 캐릭터 에셋
- 교수 1:N 공부 세션
- 교수 1:N 대사
- 교수 1:N 시나리오 이벤트

핵심 규칙:

- `sourcePhotoUrl`이 있으면 교수 등록 직후 `nanobanana` 기반 캐릭터 에셋 생성 절차를 시작
- 원본 사진이 없거나 생성 실패 시 기본 캐릭터 에셋 세트 사용

### 교수 캐릭터 에셋

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| professorId | UUID | 교수 FK |
| variantKey | string | 사전 정의된 표정/포즈 슬롯 키 |
| imageUrl | string | 전신 2D 캐릭터 에셋 URL |
| isDefaultAsset | boolean | 기본 에셋 여부 |
| createdAt | datetime | 생성 시각 |

제약:

- `(professorId, variantKey)` 고유

핵심 규칙:

- 동일 교수에 대해 여러 표정/포즈 에셋을 저장할 수 있어야 한다.
- 게임 화면은 원본 교수 사진이 아니라 이 에셋을 사용한다.

### 호감도

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| userId | UUID | 사용자 FK |
| professorId | UUID | 교수 FK |
| affectionScore | integer | 0 이상 100 이하 |
| updatedAt | datetime | 마지막 변경 시각 |

제약:

- `(userId, professorId)` 고유

### 공부 세션

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| userId | UUID | 사용자 FK |
| professorId | UUID | 교수 FK |
| status | enum | `studying`, `completed`, `canceled` |
| startedAt | datetime | 필수 |
| endedAt | datetime nullable | 종료 시 필수 |
| totalSeconds | integer nullable | 종료 시 계산 |
| hiddenSeconds | integer nullable | 종료 시 계산 |
| effectiveStudySeconds | integer nullable | `totalSeconds - hiddenSeconds` |
| idlePenaltyCount | integer | 기본 0 |
| earlyTerminationPenalty | integer | 기본 0 |
| affectionGain | integer | 기본 0 |
| affectionDelta | integer | 최종 정산 값 |
| eventTriggered | boolean | 기본 false |
| createdAt | datetime | 생성 시각 |
| updatedAt | datetime | 수정 시각 |

핵심 규칙:

- 사용자당 동시에 하나의 `studying` 세션만 허용
- 종료 시 열린 숨김 구간이 남아 있으면 `endedAt`으로 강제 마감

### 공부 세션 숨김 구간

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| sessionId | UUID | 공부 세션 FK |
| hiddenStartedAt | datetime | 필수 |
| visibleReturnedAt | datetime nullable | 복귀 시 기록 |
| hiddenDurationSeconds | integer nullable | 복귀 또는 종료 시 계산 |
| idlePenaltyApplied | boolean | 120초 이상이면 true |
| createdAt | datetime | 생성 시각 |

핵심 규칙:

- `hiddenDurationSeconds >= 120` 이면 방치 패널티 1회
- 세션 중 여러 구간이 발생할 수 있으므로 누적 가능

### 대사

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| professorId | UUID nullable | 특정 교수 전용 |
| personalityType | enum nullable | 성격 공통 대체 규칙 |
| triggerType | enum | `first_visit`, `daily_first_visit`, `study_start`, `study_hidden_return`, `study_end`, `final_result` 등 |
| affectionMin | integer | 최소값 |
| affectionMax | integer | 최대값 |
| text | text | 필수 |
| weight | integer | 기본 1 이상 |
| isActive | boolean | 기본 true |

제약:

- `professorId`와 `personalityType` 중 하나는 반드시 존재

### 시나리오 이벤트

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| professorId | UUID nullable | 특정 교수 전용 |
| personalityType | enum nullable | 성격 공통 대체 규칙 |
| title | string | 필수 |
| body | text | 필수 |
| triggerType | enum | 기본 `study_end` |
| affectionMin | integer | 최소 호감도 |
| affectionMax | integer | 최대 호감도 |
| isActive | boolean | 기본 true |

### 시나리오 이벤트 선택지

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| eventId | UUID | 시나리오 이벤트 FK |
| choiceText | string | 필수 |
| resultText | text | 필수 |
| affectionDelta | integer | 음수, 0, 양수 가능 |
| orderNo | integer | 1부터 시작 |

### 최종 결과

| 필드 | 타입 | 규칙 |
| --- | --- | --- |
| id | UUID | 시스템 생성 |
| userId | UUID | 사용자 FK |
| examScore | integer | 정책 범위 내 값 |
| primaryProfessorId | UUID | 대표 엔딩 교수 |
| highestProfessorId | UUID | 최고 호감도 교수 |
| lowestProfessorId | UUID | 최저 호감도 교수 |
| createdAt | datetime | 생성 시각 |

## 3. 파생 규칙

### 공부 보상

```text
affectionGain = min(floor(effectiveStudySeconds / 600) * 2, 10)
```

### 중도 포기 패널티

조건:

```text
effectiveStudySeconds < 600
```

계산식:

```text
earlyTerminationPenalty = max(currentAffectionScore // 10, 1)
```

### 방치 패널티

조건:

```text
hiddenDurationSeconds >= 120
```

계산식:

```text
idlePenalty = idlePenaltyCount * 2
```

### 최종 호감도 정산

```text
affectionDelta = affectionGain - earlyTerminationPenalty - idlePenalty
nextAffectionScore = clamp(currentAffectionScore + affectionDelta, 0, 100)
```

## 4. 상태 전이

### 공부 세션

| 현재 상태 | 이벤트 | 다음 상태 |
| --- | --- | --- |
| `studying` | 사용자가 공부 종료 | `completed` |
| `studying` | 운영상 비정상 취소 | `canceled` |

참고:

- `hidden`과 `returned`는 프론트엔드 화면 상태이며 서버에서는 숨김 구간 기록으로만 표현한다.

### 시나리오 이벤트 처리 상태

| 상태 | 의미 |
| --- | --- |
| `pending` | 이벤트는 생성됐지만 선택이 끝나지 않음 |
| `resolved` | 선택지 1회 처리까지 완료 |

## 5. 2인 백엔드 협업 기준 소유권 분리

### 개발자 A

- 사용자 / 인증
- 교수 / 교수 캐릭터 에셋 / 호감도
- 공통 검증과 인증/인가

### 개발자 B

- 공부 세션 / 공부 세션 숨김 구간
- 대사 / 시나리오 이벤트 / 시나리오 이벤트 선택지
- 최종 결과

공유 주의점:

- 공용 DTO와 응답 형상은 `contracts/backend-api.openapi.yaml`과 `Docs/api-spec-v0.1.md`
  기준으로만 조율한다.
- 데이터베이스 마이그레이션은 애그리거트 단위로 나누되, FK 충돌이 나지 않게 순서를 정의한다.
