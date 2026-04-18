# 딸깍 콘테스트 (애니멀 리그)

개발자용 명세 v0.1  
기준 문서: `Docs/product-plan-v1.0.md`

## 1. 문서 목적

이 문서는 기획안 v1.0을 실제 구현 가능한 수준으로 재정의한 개발자용 명세다.

목표는 아래 네 가지다.

1. 프론트엔드와 백엔드가 공유할 공통 용어와 상태를 고정한다.
2. 세션, 호감도, 방치 판정, 이벤트 발생 규칙을 수식과 API 기준으로 고정한다.
3. MVP 범위와 후순위 범위를 분리한다.
4. 이후 UI 설계, DB 설계, API 구현, 테스트 케이스 작성의 기준 문서로 사용한다.

## 2. 범위

### 2.1 MVP 포함 범위

- 회원 가입 및 로그인
- 시험 종료일 저장
- 교수 등록 및 선택
- 교수 사진 기반 캐릭터 에셋 생성
- 교수 호감도 관리
- 공부 세션 시작/종료
- Page Visibility API 기반 방치 감지
- 상황별 대사 출력
- 공부 종료 후 30% 확률의 시나리오 이벤트
- 선택지 기반 호감도 변화
- 시험 종료일 이후 최종 점수 입력 및 결과 화면 출력

### 2.2 MVP 제외 범위

- AI 기반 교수 성격 자동 추론
- 텍스트만으로 신규 캐릭터를 임의 생성하는 기능
- 버튜버 변환
- 실시간 경쟁 요소
- 확장형 미니게임
- 복잡한 커스터마이징

## 3. 구현 원칙

### 3.1 모바일 웹 우선

- 모든 핵심 플로우는 모바일 브라우저 기준으로 설계한다.
- 백그라운드 전환, 탭 숨김, OS 개입으로 인한 타이머 정지 가능성을 전제로 한다.

### 3.2 서버 계산 우선

- 클라이언트는 입력 수집, 타이머 표시, 가시성 이벤트 감지만 담당한다.
- 세션 정산, 유효 공부 시간, 호감도 증감, 이벤트 발생 여부는 서버가 최종 계산한다.

### 3.3 교수별 상태와 공통 공부 보상

- 각 교수는 별도 호감도와 별도 대사 맥락을 가진다.
- 공부 세션 보상은 등록된 모든 교수의 호감도에 동일하게 적용한다.
- 이벤트 선택과 세션 대상 교수 기준 패널티는 해당 교수의 호감도에만 반영한다.

### 3.4 대사 중심 구조

- UI보다 대사와 이벤트 데이터 구조를 먼저 안정화한다.
- 모든 핵심 행동은 대사 또는 이벤트로 감정 보상을 반환해야 한다.

### 3.5 재진입 가능 구조

- 모바일 환경에서는 페이지 새로고침, 브라우저 강제 종료, 백그라운드 복귀가 흔하다.
- 활성 세션은 복구 가능해야 하며, 이벤트 선택 전 상태도 재조회 가능해야 한다.

## 4. 권장 시스템 구조

이 문서는 특정 프레임워크를 강제하지 않는다. 다만 MVP 구현 기준 권장 구조는 아래와 같다.

- 프론트엔드: 모바일 우선 단일 페이지 애플리케이션
- 백엔드: REST API 서버
- 데이터베이스: 관계형 DB
- 파일 저장소: 교수 원본 사진과 생성된 캐릭터 에셋 저장용 외부 스토리지 또는 서버 저장소
- 이미지 생성 워커: `nanobanana` 호출을 통해 교수 사진 기반 전신 2D 캐릭터 에셋 세트를 생성하는 비동기 작업 계층

권장 책임 분리는 아래와 같다.

프론트엔드 책임:

- 화면 렌더링
- 폼 입력 검증
- 타이머 시각화
- 가시성 이벤트 감지
- API 호출 및 오류 처리
- 캐릭터 에셋 생성 상태 표시 및 재조회

백엔드 책임:

- 인증/인가
- 교수 원본 사진 저장
- `nanobanana` 기반 캐릭터 에셋 생성 요청과 생성 상태 관리
- 세션 시작 및 종료 처리
- 유효 공부 시간 계산
- 호감도 정산
- 이벤트 추첨 및 선택지 결과 처리
- 결과 화면용 집계 데이터 생성

## 5. 핵심 용어

- 유저: 서비스를 사용하는 계정 주체
- 교수: 공략 대상 캐릭터
- 교수 원본 사진: 사용자가 입력한 실제 교수 사진
- 캐릭터 에셋: 게임에서 사용하는 전신 2D 표정/포즈 이미지
- 호감도: 교수별로 유지되는 관계 수치. 단, 공부 보상은 모든 교수에 동일하게 적용
- 공부 세션: 시작부터 종료까지의 단일 공부 기록
- 유효 공부 시간: 전체 세션 시간에서 숨김 상태 시간을 제외한 시간
- 방치 구간: `hidden` 상태가 시작되어 다시 `visible`로 돌아오기 전까지의 구간
- 방치 확정: 방치 구간이 120초 이상 지속된 상태
- 시나리오 이벤트: 공부 종료 후 확률적으로 발생하는 선택지 이벤트
- 최종 결과: 시험 종료일 이후 점수 입력을 통해 보는 엔딩 화면

## 6. 도메인 모델

### 6.1 사용자

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 내부 유저 식별자 |
| loginId | string | 로그인용 ID, unique |
| name | string | 사용자 표시명 |
| passwordHash | string | 비밀번호 해시 |
| examEndDate | date | 시험 종료일 |
| createdAt | datetime | 생성 시각 |
| updatedAt | datetime | 수정 시각 |

참고:

- 요청 입력은 `password`를 받지만 저장은 `passwordHash`로 한다.

### 6.2 교수

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 교수 식별자 |
| userId | string | 소유 유저 |
| professorName | string | 교수 이름 |
| gender | enum | `male`, `female` |
| personalityType | enum | `gentle`, `tsundere`, `english_mix`, `shy` |
| sourcePhotoUrl | string nullable | 업로드한 교수 원본 사진 URL |
| characterAssetStatus | enum | `pending`, `ready` |
| representativeAssetUrl | string nullable | 대표 캐릭터 에셋 URL |
| isDefaultCharacterAssets | boolean | 기본 캐릭터 에셋 사용 여부 |
| createdAt | datetime | 생성 시각 |
| updatedAt | datetime | 수정 시각 |

### 6.2.1 교수 캐릭터 에셋

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 에셋 식별자 |
| professorId | string | 교수 식별자 |
| variantKey | string | 사전 정의된 표정/포즈 슬롯 키 |
| imageUrl | string | 생성된 전신 2D 캐릭터 이미지 URL |
| isDefaultAsset | boolean | 기본 에셋 여부 |
| createdAt | datetime | 생성 시각 |

규칙:

- 교수 사진이 있으면 `nanobanana`를 기준으로 여러 표정/포즈의 전신 2D 캐릭터 에셋 세트를 생성한다.
- 교수 사진이 없거나 생성에 실패하면 기본 캐릭터 에셋 세트를 사용한다.
- 게임 화면은 원본 교수 사진이 아니라 캐릭터 에셋을 사용한다.

### 6.3 호감도

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 레코드 식별자 |
| userId | string | 유저 식별자 |
| professorId | string | 교수 식별자 |
| affectionScore | integer | 현재 호감도, 0 이상 100 이하 |
| updatedAt | datetime | 마지막 변경 시각 |

제약:

- `(userId, professorId)`는 unique

### 6.4 공부 세션

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 세션 식별자 |
| userId | string | 유저 식별자 |
| professorId | string | 공부 대상 교수 |
| status | enum | `studying`, `completed`, `canceled` |
| startedAt | datetime | 세션 시작 시각 |
| endedAt | datetime nullable | 세션 종료 시각 |
| totalSeconds | integer nullable | 전체 경과 시간 |
| hiddenSeconds | integer nullable | 누적 숨김 시간 |
| effectiveStudySeconds | integer nullable | 유효 공부 시간 |
| idlePenaltyCount | integer | 방치 패널티 적용 횟수 |
| earlyTerminationPenalty | integer | 중도 포기 패널티 절대값 |
| globalAffectionGain | integer | 모든 교수에 동일하게 적용되는 공부 보상 증가량 |
| currentProfessorAffectionDelta | integer | 현재 교수 기준 총 호감도 변화량 |
| eventTriggered | boolean | 종료 후 이벤트 발생 여부 |
| createdAt | datetime | 생성 시각 |
| updatedAt | datetime | 수정 시각 |

### 6.5 공부 세션 숨김 구간

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 구간 식별자 |
| sessionId | string | 세션 식별자 |
| hiddenStartedAt | datetime | 숨김 시작 시각 |
| visibleReturnedAt | datetime nullable | 복귀 시각 |
| hiddenDurationSeconds | integer nullable | 숨김 지속 시간 |
| idlePenaltyApplied | boolean | 이 구간에 방치 패널티 적용 여부 |
| createdAt | datetime | 생성 시각 |

### 6.6 대사

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 대사 식별자 |
| professorId | string nullable | 특정 교수 전용 대사 |
| personalityType | enum nullable | 성격 공통 대사 |
| triggerType | enum | 발생 상황 |
| affectionMin | integer | 최소 호감도 |
| affectionMax | integer | 최대 호감도 |
| rangeBand | string | `0-19`, `20-49`, `50-84`, `85-100` |
| sourceMarkdown | text | `Reference` 기준 원문 마크다운 |
| referenceSourcePath | string | 원문 파일 경로 |
| weight | integer | 랜덤 선택 가중치 |
| isActive | boolean | 사용 여부 |

규칙:

- `professorId`와 `personalityType` 중 하나는 반드시 존재해야 한다.
- 원문 저장은 `Reference`의 마크다운 포맷을 기준으로 한다.
- `{교수}` 플레이스홀더는 API 응답 직전에 실제 교수 이름으로 치환한다.
- `{주인공}` 라벨은 작성 편의를 위한 메타데이터이며 API 응답에는 노출하지 않는다.

### 6.6.1 런타임 스크립트 라인

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| kind | enum | `narration`, `dialogue`, `stage_direction`, `inner_monologue` |
| speakerRole | enum optional | `dialogue` 라인일 때만 `professor`, `protagonist` |
| speakerName | string optional | 교수 대사 라인일 때만 실제 교수 이름 |
| text | text | 프론트엔드가 그대로 렌더하는 문장 |

규칙:

- 프론트엔드는 `lines[]` 배열을 순서대로 렌더한다.
- 모든 라인은 `kind`, `text`를 가진다.
- `dialogue` 라인만 `speakerRole`을 가진다.
- `speakerRole = professor`면 `speakerName`이 채워진다.
- `speakerRole = protagonist`면 `speakerName` 키를 보내지 않고, 프론트엔드는 화자 라벨을 렌더하지 않는다.
- `narration`, `stage_direction`, `inner_monologue` 라인은 `speakerRole`, `speakerName` 키를 보내지 않는다.
- 상황 설명, 내면 독백, 표정/포즈 지시는 모두 별도 `kind`로 분리되어 전달된다.

### 6.7 시나리오 이벤트

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 이벤트 식별자 |
| professorId | string nullable | 특정 교수 전용 이벤트 |
| personalityType | enum nullable | 성격 공통 이벤트 |
| title | string | 이벤트 제목 |
| rangeBand | string | `0-19`, `20-49`, `50-84`, `85-100` |
| branchKey | string | 예: `branch-special-consultation` |
| sourceMarkdown | text | 이벤트 원문 마크다운 |
| referenceSourcePath | string | 원문 파일 경로 |
| triggerType | enum | 기본값은 `study_end` |
| affectionMin | integer | 최소 호감도 |
| affectionMax | integer | 최대 호감도 |
| isActive | boolean | 사용 여부 |

### 6.8 시나리오 이벤트 선택지

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 선택지 식별자 |
| eventId | string | 이벤트 식별자 |
| choiceText | string | 사용자 선택지 문구 |
| resultScriptLines | json array | 선택 후 반응 스크립트 라인 |
| affectionDelta | integer | 호감도 변화량 |
| orderNo | integer | 표시 순서 |

### 6.9 최종 결과

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| id | string | 결과 식별자 |
| userId | string | 유저 식별자 |
| examScore | integer | 사용자가 입력한 최종 점수 |
| primaryProfessorId | string | 대표 엔딩 교수 |
| highestProfessorId | string | 최고 호감도 교수 |
| lowestProfessorId | string | 최저 호감도 교수 |
| scoreBand | string | 점수 구간 코드 |
| endingType | enum | `happy`, `normal`, `bad` |
| script | json array | 엔딩 스크립트 라인 |
| createdAt | datetime | 생성 시각 |

## 7. 핵심 비즈니스 규칙

### 7.1 단일 활성 세션

- 유저는 동시에 하나의 공부 세션만 가질 수 있다.
- 이미 `studying` 상태 세션이 있으면 새 세션 시작을 거절한다.

### 7.2 세션 시간 계산

기본 계산식:

```text
totalSeconds = endedAt - startedAt
effectiveStudySeconds = totalSeconds - sum(hiddenDurationSeconds)
effectiveStudySeconds = max(effectiveStudySeconds, 0)
```

설명:

- `hidden` 상태 시간은 공부 시간에 포함하지 않는다.
- 세션 종료 시 서버가 최종 계산한다.

### 7.2.1 교수 캐릭터 에셋 생성 규칙

- 교수 등록 시 `gender`는 `male`, `female`만 허용한다.
- 교수 사진이 있으면 서버는 해당 사진을 저장한 뒤 `nanobanana` 기반 캐릭터 생성 작업을 시작한다.
- 생성 대상은 여러 표정과 포즈를 가진 전신 2D 캐릭터 에셋 세트다.
- 생성 작업이 완료되기 전까지 `characterAssetStatus`는 `pending`이다.
- 생성이 완료되거나 기본 에셋 세트가 준비되면 `characterAssetStatus`는 `ready`가 된다.
- 교수 사진이 없거나 생성 실패 시에도 게임 진행이 막히지 않도록 기본 캐릭터 에셋 세트를 사용한다.

### 7.3 호감도 상승 규칙

공부 보상 계산식:

```text
globalAffectionGain = min(floor(effectiveStudySeconds / 600) * 2, 10)
```

예시:

- 9분 59초: +0
- 10분: +2
- 30분: +6
- 50분 이상: +10
- 계산된 `globalAffectionGain`은 등록된 모든 교수의 호감도에 동일하게 가산한다.

### 7.4 중도 포기 패널티

기획안에는 "공부가 충분히 누적되기 전에 종료"라고만 되어 있으므로, 개발 기준에서는 아래처럼 고정한다.

중도 포기 조건:

```text
effectiveStudySeconds < 1200
```

패널티 계산식:

```text
earlyTerminationPenalty = 2
```

적용 규칙:

- 세션 종료 시 유효 공부 시간이 20분 미만이면 중도 포기 패널티를 적용한다.
- 중도 포기 패널티는 공통 공부 보상 계산 뒤, 현재 교수 기준 최종 변화량에 반영한다.

### 7.5 방치 패널티

방치 확정 조건:

```text
hiddenDurationSeconds >= 120
```

적용 규칙:

- 방치 구간 하나당 `max(currentAffectionScore // 10, 1)` 패널티를 1회 적용한다.
- 세션 중 여러 번 방치가 발생할 수 있으며, 구간별 누적 적용한다.
- 방치 시간은 유효 공부 시간에서 제외한다.

계산식:

```text
idlePenaltyPerOccurrence = max(currentAffectionScore // 10, 1)
idlePenalty = idlePenaltyCount * idlePenaltyPerOccurrence
```

### 7.6 최종 호감도 정산

세션 종료 시 계산식:

```text
currentProfessorAffectionDelta = globalAffectionGain - earlyTerminationPenalty - idlePenalty
currentProfessorNextAffectionScore = clamp(currentProfessorAffectionScore + currentProfessorAffectionDelta, 0, 100)
otherProfessorNextAffectionScore = clamp(otherProfessorAffectionScore + globalAffectionGain, 0, 100)
```

보정 규칙:

- 호감도는 0 미만으로 내려가지 않는다.
- 호감도는 100을 초과하지 않는다.
- 세션 대상이 아닌 다른 교수들은 패널티 없이 `globalAffectionGain`만 반영한다.

### 7.7 이벤트 발생 규칙

- 공부 종료 후 세션 정산이 완료된 뒤 이벤트 발생 여부를 판정한다.
- 발생 확률은 30%다.
- 이벤트는 `eventTriggered = true`로 저장한다.
- 이벤트가 발생하지 않으면 기본 공부 종료 대사만 반환한다.

구현 원칙:

- 이벤트 추첨은 서버에서 수행한다.
- 이벤트 후보는 `교수 우선 -> 성격 공통 fallback` 순으로 조회한다.

### 7.8 엔딩 선정 규칙

기획안의 "최종 점수 입력으로 엔딩 대상 교수 선택" 문장은 구현 규칙이 모호하므로, MVP에서는 아래처럼 고정한다.

- 대표 엔딩 교수는 현재 호감도가 가장 높은 교수를 사용한다.
- 최종 시험 점수는 엔딩 대사 분기와 성적 반응 분기에 사용한다.
- 최저 호감도 교수의 한마디를 별도 출력한다.

동점 규칙:

- 최고 호감도 동점이면 최근 공부 세션이 더 최근인 교수를 우선한다.
- 그래도 같으면 생성 시각이 더 빠른 교수를 우선한다.

## 8. 상태 머신

### 8.1 프론트엔드 주요 상태

| 상태 | 설명 |
| --- | --- |
| `idle` | 세션이 없는 기본 상태 |
| `studying` | 공부 진행 중 |
| `hidden` | 공부 중 브라우저가 숨김 상태 |
| `returned` | 숨김 상태에서 복귀한 직후 |
| `event` | 시나리오 이벤트 선택 대기 |
| `result` | 최종 결과 확인 상태 |

### 8.2 상태 전이

| 현재 상태 | 조건 | 다음 상태 |
| --- | --- | --- |
| `idle` | 공부 시작 | `studying` |
| `studying` | `visibilityState = hidden` | `hidden` |
| `hidden` | 120초 미만 후 복귀 | `returned` |
| `hidden` | 120초 이상 후 복귀 | `returned` |
| `returned` | 사용자 계속 진행 | `studying` |
| `studying` | 공부 종료, 이벤트 없음 | `idle` |
| `studying` | 공부 종료, 이벤트 있음 | `event` |
| `event` | 선택 완료 | `idle` |
| `idle` | 시험 종료일 이후 결과 보기 | `result` |

### 8.3 서버 상태

| 대상 | 상태 |
| --- | --- |
| StudySession | `studying`, `completed`, `canceled` |
| 시나리오 이벤트 처리 | `pending`, `resolved` |

## 9. 화면 명세

### 9.1 회원 가입 / 로그인 화면

목적:

- 유저 생성 및 인증
- 시험 종료일 최초 설정

입력 필드:

- 이름
- 로그인 ID
- 비밀번호
- 시험 종료일

검증:

- 이름: 1자 이상
- 로그인 ID: 영문/숫자 기준 4자 이상 권장
- 비밀번호: 8자 이상 권장
- 시험 종료일: 오늘 이전 날짜 금지

### 9.2 교수 등록 화면

입력 필드:

- 교수 이름
- 성별
- 성격
- 사진 업로드
- 캐릭터 에셋 생성 상태

처리 규칙:

- 성별은 `male`, `female`만 선택 가능하다.
- 사진 미업로드 시 기본 캐릭터 에셋 자동 지정
- 한 유저는 여러 교수를 등록할 수 있다

### 9.3 메인 화면

표시 요소:

- 현재 선택 교수 카드
- 대표 캐릭터 에셋
- 교수 이름
- 현재 호감도 게이지
- 오늘의 상태 문구
- 공부 시작 버튼
- 교수 변경 버튼
- 인터랙션 진입 버튼

행동:

- 최초 접속 또는 당일 첫 접속 시 진입 대사 노출
- 가장 많이 공부한 교수 / 가장 적게 공부한 교수 코멘트 영역은 후순위 위젯으로 분리 가능

### 9.4 공부 중 화면

표시 요소:

- 현재 교수 정보
- 실시간 경과 타이머
- 상태 라벨
- 공부 종료 버튼

행동:

- `visibilitychange` 이벤트 리스닝
- 숨김 전환 시 숨김 구간 시작 기록
- 복귀 시 숨김 구간 종료 기록

### 9.5 복귀/방치 피드백 모달

표시 조건:

- `hidden`에서 `visible`로 돌아온 시점

표시 내용:

- 정상 복귀 대사 또는 경고 대사
- 방치 패널티 발생 여부

### 9.6 공부 종료 결과 모달

표시 내용:

- 유효 공부 시간
- 호감도 변화량
- 적용된 패널티 요약
- 기본 반응 대사
- 이벤트 발생 여부

### 9.7 시나리오 이벤트 모달

표시 조건:

- 공부 종료 후 서버에서 이벤트 발생 반환 시

표시 내용:

- 이벤트 제목
- 이벤트 스크립트 라인 목록
- 선택지 목록

동작:

- 선택지 1회 선택 후 잠금
- 결과 반응 대사 출력
- 호감도 변화량 반영
- `speakerRole = protagonist` 라인은 화자 라벨 없이 대사 본문만 렌더

### 9.8 최종 결과 화면

진입 조건:

- 오늘 날짜가 `examEndDate` 이상

표시 내용:

- 최종 시험 점수 입력
- 대표 엔딩 교수
- 최고 호감도 교수 코멘트
- 최저 호감도 교수 코멘트
- 시험 점수 구간별 반응

## 10. API 명세

API 경로는 예시이며, 실제 라우트 네이밍은 구현 시 조정 가능하다. 다만 리소스 책임은 유지한다.

### 10.1 인증

#### `POST /api/auth/signup`

요청:

```json
{
  "name": "홍길동",
  "loginId": "hong1234",
  "password": "password123",
  "examEndDate": "2026-06-20"
}
```

응답:

```json
{
  "user": {
    "id": "usr_1",
    "name": "홍길동",
    "loginId": "hong1234",
    "examEndDate": "2026-06-20"
  }
}
```

#### `POST /api/auth/login`

요청:

```json
{
  "loginId": "hong1234",
  "password": "password123"
}
```

응답:

```json
{
  "user": {
    "id": "usr_1",
    "name": "홍길동",
    "loginId": "hong1234",
    "examEndDate": "2026-06-20"
  },
  "activeSession": null
}
```

### 10.2 교수

#### `POST /api/professors`

설명:

- 새 교수 등록
- 교수 사진이 있으면 캐릭터 에셋 생성 작업 시작

요청:

```json
{
  "professorName": "김교수",
  "gender": "male",
  "personalityType": "gentle",
  "sourcePhotoUrl": "https://cdn.example.com/source/prof_1.jpg"
}
```

응답:

```json
{
  "professor": {
    "id": "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee",
    "professorName": "김교수",
    "gender": "male",
    "personalityType": "gentle",
    "sourcePhotoUrl": "https://cdn.example.com/source/prof_1.jpg",
    "characterAssetStatus": "pending",
    "representativeAssetUrl": null,
    "isDefaultCharacterAssets": false
  }
}
```

#### `GET /api/professors`

설명:

- 유저가 등록한 교수 목록 조회
- 목록에서는 대표 캐릭터 에셋과 생성 상태를 함께 확인할 수 있어야 한다

#### `GET /api/professors/:professorId`

설명:

- 단일 교수 상세 조회
- 현재 호감도 포함
- 생성된 캐릭터 에셋 목록 포함

응답 예시:

```json
{
  "professor": {
    "id": "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee",
    "professorName": "김교수",
    "gender": "male",
    "personalityType": "gentle",
    "sourcePhotoUrl": "https://cdn.example.com/source/prof_1.jpg",
    "characterAssetStatus": "ready",
    "representativeAssetUrl": "https://cdn.example.com/assets/prof_1/idle_neutral.png",
    "isDefaultCharacterAssets": false
  },
  "affection": {
    "professorId": "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee",
    "affectionScore": 0
  },
  "characterAssets": [
    {
      "variantKey": "idle_neutral",
      "imageUrl": "https://cdn.example.com/assets/prof_1/idle_neutral.png",
      "isDefaultAsset": false
    },
    {
      "variantKey": "idle_smile",
      "imageUrl": "https://cdn.example.com/assets/prof_1/idle_smile.png",
      "isDefaultAsset": false
    }
  ]
}
```

### 10.3 공부 세션

#### `POST /api/study-sessions`

설명:

- 세션 시작

요청:

```json
{
  "professorId": "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee"
}
```

응답:

```json
{
  "session": {
    "id": "sess_1",
    "status": "studying",
    "startedAt": "2026-04-18T15:30:00+09:00"
  }
}
```

#### `GET /api/study-sessions/active`

설명:

- 현재 활성 세션 복구용 조회

#### `POST /api/study-sessions/:sessionId/visibility`

설명:

- 숨김 구간 시작/복귀 이벤트 기록

요청 예시 1:

```json
{
  "type": "hidden",
  "occurredAt": "2026-04-18T15:40:00+09:00"
}
```

요청 예시 2:

```json
{
  "type": "visible",
  "occurredAt": "2026-04-18T15:43:10+09:00"
}
```

#### `POST /api/study-sessions/:sessionId/end`

설명:

- 세션 종료 및 정산

요청:

```json
{
  "endedAt": "2026-04-18T16:10:00+09:00"
}
```

응답:

```json
{
  "sessionSummary": {
    "totalSeconds": 2400,
    "hiddenSeconds": 190,
    "effectiveStudySeconds": 2210,
    "globalAffectionGain": 6,
    "updatedProfessorCount": 3,
    "earlyTerminationPenalty": 0,
    "idlePenaltyCount": 1,
    "currentProfessorAffectionDelta": 4,
    "currentProfessorNextAffectionScore": 32
  },
  "dialogue": {
    "triggerType": "study_end",
    "lines": [
      {
        "kind": "dialogue",
        "speakerRole": "professor",
        "speakerName": "김교수",
        "text": "오늘은 제법 성실했군."
      }
    ]
  },
  "event": {
    "triggered": true,
    "eventId": "11111111-2222-4333-8444-555555555555",
    "title": "늦은 연구실",
    "rangeBand": "50-84",
    "branchKey": "branch-midnight-lab",
    "lines": [
      {
        "kind": "narration",
        "text": "밤 11시. 연구실 불빛만 남아 있는 시간."
      },
      {
        "kind": "dialogue",
        "speakerRole": "professor",
        "speakerName": "김교수",
        "text": "아직 안 갔네요."
      }
    ],
    "choices": [
      {
        "id": "66666666-7777-4888-8999-aaaaaaaaaaaa",
        "text": "오늘 분량은 꼭 끝내고 싶어서요."
      },
      {
        "id": "bbbbbbbb-cccc-4ddd-8eee-ffffffffffff",
        "text": "조금 졸리긴 한데, 다 끝나가요."
      }
    ]
  }
}
```

### 10.4 이벤트 선택

#### `POST /api/scenario-events/:eventId/choices/:choiceId`

설명:

- 이벤트 선택지 결과 처리

응답:

```json
{
  "result": {
    "lines": [
      {
        "kind": "dialogue",
        "speakerRole": "professor",
        "speakerName": "김교수",
        "text": "그 고집, 나쁘지 않아요. 뭘 위한 건지 자네가 알면."
      }
    ],
    "affectionDelta": 3,
    "nextAffectionScore": 35,
    "nextState": "resolved"
  }
}
```

제약:

- 동일 이벤트는 1회만 해결 가능하다.

### 10.5 결과

#### `POST /api/final-results`

설명:

- 시험 점수 입력 및 엔딩 결과 생성

요청:

```json
{
  "examScore": 87
}
```

응답:

```json
{
  "result": {
    "primaryProfessorId": "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee",
    "highestProfessorId": "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee",
    "lowestProfessorId": "ffffffff-1111-4222-8333-444444444444",
    "scoreBand": "80_89",
    "endingType": "normal",
    "script": [
      {
        "kind": "dialogue",
        "speakerRole": "professor",
        "speakerName": "김교수",
        "text": "다음 학기에도 내 수업 들을 생각이야?"
      },
      {
        "kind": "dialogue",
        "speakerRole": "professor",
        "speakerName": "김교수",
        "text": "……아니, 그것보다. 그냥 자네가 잘 지내는지 궁금해서."
      }
    ]
  }
}
```

## 11. 대사 및 이벤트 콘텐츠 규칙

### 11.1 triggerType 표준값

- `first_visit`
- `daily_first_visit`
- `return_to_school`
- `study_start`
- `study_resume`
- `study_hidden_return`
- `cheer_up`
- `study_end`
- `event_result`
- `final_result`

### 11.2 대사 선택 우선순위

1. 특정 교수 대사
2. 동일 성격 공통 대사
3. 시스템 기본 대사

### 11.3 호감도 구간

콘텐츠 분기 기본 구간:

- `0-19`
- `20-49`
- `50-84`
- `85-100`

### 11.4 작성 원문 마크다운 규칙

- `Reference`의 시나리오 원문은 마크다운으로 관리한다.
- 헤더에는 이모지를 사용하지 않는다.
- 상황 설명, 내면 독백, 표정/포즈 지시는 이텔릭으로 작성한다.
- 교수 대사 원문은 `**{교수}:**` 라벨을 사용한다.
- 주인공 대사 원문은 `**{주인공}:**` 라벨을 사용한다.
- 선택지 헤더는 `**[선택지 n]**` 형식을 사용한다.

### 11.5 런타임 전달 규칙

- 서버는 `Reference` 원문을 그대로 프론트엔드에 전달하지 않는다.
- `{교수}`는 실제 교수 이름으로 치환한 뒤 `lines[]` 구조로 전달한다.
- `{주인공}`은 작성용 라벨이므로 프론트엔드 응답에서 문자열 라벨로 보내지 않는다.
- 프론트엔드는 스크립트 라인 배열을 렌더하고, 주인공 대사는 본문만 출력한다.

### 11.6 이벤트 작성 규칙

- 이벤트마다 선택지는 2개 이상
- 모든 선택지는 결과 대사를 가진다
- 적어도 하나의 선택지는 긍정적 호감도 변화가 있어야 한다
- 무조건 정답이 너무 명확하지 않게 성격 기반 추론 요소를 유지한다

## 12. 클라이언트 로직 세부 규칙

### 12.1 active session 로컬 보존

- 활성 세션 ID는 로컬 저장소에 임시 보관할 수 있다.
- 앱 재진입 시 `GET /api/study-sessions/active`로 서버 상태를 우선 복원한다.

### 12.2 네트워크 일시 장애

- 가시성 이벤트 전송이 실패하면 메모리 또는 로컬 큐에 임시 적재한다.
- 재연결 시 발생 시각 기준으로 재전송한다.

주의:

- MVP에서는 완전한 오프라인 보장을 하지 않는다.
- 다만 세션 종료 직전 누락된 숨김 구간는 최대한 동기화해야 한다.

### 12.3 타이머 표시 규칙

- 화면 타이머는 사용자 피드백용이다.
- 최종 정산 값과 1~2초 수준 차이가 날 수 있다.
- 서버 응답 값을 최종 진실 소스로 사용한다.

## 13. 예외 처리

### 13.1 인증 관련

- 중복 `loginId` 가입 차단
- 잘못된 비밀번호 로그인 차단
- 인증 없는 API 접근 차단

### 13.2 세션 관련

- 활성 세션이 있는데 새 세션 시작 시도 시 409 반환
- 종료된 세션 재종료 차단
- 존재하지 않는 세션 접근 차단
- 다른 유저 세션 접근 차단

### 13.3 visibility 관련

- `visible` 이벤트만 단독 도착하면 무시
- 열린 숨김 구간가 이미 있는데 또 `hidden`이 오면 중복 무시
- 종료 시 열린 숨김 구간가 남아 있으면 `endedAt` 기준으로 자동 닫기

### 13.4 이벤트 관련

- 이미 해결한 이벤트 재선택 차단
- 해당 세션과 무관한 이벤트 선택 차단

### 13.5 결과 관련

- `examEndDate` 이전에는 최종 결과 생성 차단
- 교수 미등록 상태에서는 결과 화면 진입 차단

## 14. 보안 및 데이터 처리 원칙

- 비밀번호는 평문 저장 금지
- 인증 세션 또는 토큰은 서버 검증 필수
- 이미지 업로드 파일 형식과 크기 제한 필요
- 유저 간 데이터 접근은 모든 조회/수정 API에서 소유권 검증 필수

## 15. 테스트 기준

### 15.1 단위 테스트 우선 대상

- 호감도 증가 계산
- 중도 포기 패널티 계산
- hidden 구간 누적 계산
- 방치 패널티 계산
- 이벤트 30% 추첨 분기
- 엔딩 대표 교수 선정 동점 처리

### 15.2 통합 테스트 우선 시나리오

1. 회원 가입 후 교수 등록
2. 세션 시작 후 30분 공부 종료
3. 세션 중 3분 이탈 후 복귀
4. 20분 미만 공부 후 종료
5. 이벤트 발생 후 선택지 반영
6. 시험 종료일 이후 결과 생성

### 15.3 수동 QA 핵심 체크

1. 모바일 브라우저에서 백그라운드 전환 시 타이머 UI 복귀 여부
2. 세션 중 새로고침 후 active session 복원 여부
3. 방치 경고와 공부 종료 결과 모달의 문맥 충돌 여부

## 16. 구현 우선순위

### 16.1 Phase 1

- 인증
- 교수 등록
- 교수 목록 / 선택
- 단일 활성 세션 시작 / 종료
- 유효 공부 시간 계산
- 호감도 정산

### 16.2 Phase 2

- visibility 기반 방치 판정
- 복귀 대사
- 공부 종료 대사

### 16.3 Phase 3

- 시나리오 이벤트
- 선택지 결과 처리
- 이벤트 콘텐츠 관리

### 16.4 Phase 4

- 최종 결과 화면
- 엔딩 분기
- 결과 대사 확장

## 17. 미결정 항목

아래 항목은 추후 기획 확정이 필요하지만, 현재 spec은 MVP 구현이 막히지 않도록 임시 기준을 제안한 상태다.

1. 최종 시험 점수의 입력 범위와 성적 구간 기준
2. 대표 엔딩 교수 선정이 최고 호감도 고정인지, 점수/활동 기반 추가 규칙이 필요한지
3. 인터랙션 대사의 정확한 발생 트리거 목록
4. 교수 성별이 실제 콘텐츠 분기에 영향을 주는지 여부
5. 이미지 저장 방식이 서버 로컬인지 외부 스토리지인지

## 18. 완료 기준

아래를 만족하면 MVP 백엔드/프론트엔드 개발 착수 기준을 충족한 것으로 본다.

1. 개발자가 세션 정산 수식만 보고 동일 결과를 구현할 수 있다.
2. 프론트엔드가 API 명세만으로 화면 흐름을 연결할 수 있다.
3. 콘텐츠 작업자가 `Dialogue`, `ScenarioEvent`, `ScenarioEventChoice` 구조에 맞춰 데이터를 투입할 수 있다.
4. QA가 상태 전이와 예외 처리 기준으로 테스트 케이스를 작성할 수 있다.
