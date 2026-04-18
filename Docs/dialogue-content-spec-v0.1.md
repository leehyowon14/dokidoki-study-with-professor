# 딸깍 콘테스트 (애니멀 리그)

대사 콘텐츠 스펙 v0.1

기준 문서:

- `Backend/Reference/professor-character-spec.md`
- `Backend/Reference/professor-scenarios.md`
- `Docs/product-plan-v1.0.md`
- `Docs/developer-spec-v0.1.md`

## 1. 문서 목적

이 문서는 `Reference`에 저장된 시나리오 원문 구조와, 프론트엔드에 전달되는 런타임
대사 데이터 구조를 분리해서 정의한다.

목표는 아래 세 가지다.

1. 콘텐츠 작성 포맷을 고정한다.
2. 서버가 어떤 규칙으로 원문을 파싱하고 치환하는지 고정한다.
3. 프론트엔드가 어떤 구조를 렌더해야 하는지 고정한다.

## 1.1 구현 기준 freeze 현황

- 이 문서는 이슈 `#2` 기준으로 런타임 대사 공통 enum 값을 구현 기준으로 고정한다.
- freeze 대상:
  - 성격 타입 `gentle`, `tsundere`, `english_mix`, `shy`
  - `triggerType`
  - `kind`
  - `speakerRole`
- 현재 범위의 미해결 항목:
  - 대사별 세부 오류 복구 정책은 후속 이슈에서 정의한다.
  - `Reference` 파서 구현 세부 규칙은 이후 콘텐츠 엔진 이슈에서 확정한다.

## 2. 레퍼런스 기준 구조

### 2.1 호감도 구간

- `0-19`
- `20-49`
- `50-84`
- `85-100`

### 2.2 폴더 구조

```text
Backend/Reference/
├── professor-character-spec.md
├── professor-warning-dialogues.md
├── professor-encouragement-dialogues.md
├── professor-scenarios.md
├── 0-19/
├── 20-49/
├── 50-84/
└── 85-100/
```

구간 폴더 내부 규칙:

- `scenario.md`: 구간 기준 원문
- `common.md`: 구간 공통 장면, 조건, 엔딩 정리
- `professor-{type}/branch-{name}.md`: 성격별 분기 시나리오

루트 문서 규칙:

- `professor-character-spec.md`: 교수 성격 및 화법 기준 문서
- `professor-warning-dialogues.md`: 경고 대사 원문 문서
- `professor-encouragement-dialogues.md`: 독려 대사 원문 문서
- `professor-scenarios.md`: 전체 시나리오 인덱스 문서

### 2.3 성격 타입 표준값

| enum 값 | 의미 | Reference 폴더 |
| --- | --- | --- |
| `gentle` | 다정한 교수님 | `professor-gentle` |
| `tsundere` | 나한테만 츤데레 냉미남 교수님 | `professor-tsundere` |
| `english_mix` | 영어 섞어쓰는 유쾌한 교수님 | `professor-english-mix` |
| `shy` | 수줍은 교수님 | `professor-shy` |

## 3. 원문 작성 규칙

### 3.1 마크다운 규칙

- 헤더에는 이모지를 사용하지 않는다.
- 상황 설명, 내면 독백, 표정/포즈 지시는 이텔릭으로 작성한다.
- 표정 변화, 포즈, 시선, 손짓 같은 무대 지시는 대괄호 묘사를 유지한다.
- 표는 조건 정리와 루트 진입 규칙처럼 구조화된 정보에만 사용한다.

### 3.2 화자 라벨 규칙

- 교수 대사는 `**{교수}:**` 형식을 사용한다.
- 주인공 대사는 `**{주인공}:**` 형식을 사용한다.
- 선택지 헤더는 `**[선택지 n]**` 형식을 사용한다.
- 선택지 본문은 실제 레퍼런스 기준으로 `**1. {주인공}: "..."**`처럼 번호와
  주인공 라벨이 함께 붙을 수 있다.
- 선택지 문장은 일반 대사와 동일한 플레이스홀더 규칙을 따르지만, 파싱 시에는
  `choiceText`로 추출해야 한다.

### 3.3 플레이스홀더 규칙

- `{교수}`는 런타임에서 실제 교수 이름으로 치환한다.
- `{주인공}`은 작성용 라벨이며, 화면 표시용 이름으로 사용하지 않는다.

## 4. 서버 파싱 및 치환 규칙

### 4.1 파싱 대상

서버는 `Reference`의 원문 마크다운을 파싱해 아래 단위로 구조화한다.

- 구간
- 성격 타입
- 분기 키
- 씬
- 선택지
- 엔딩
- 스크립트 라인

### 4.2 런타임 치환 규칙

- `{교수}`는 선택된 교수의 실제 이름으로 치환한다.
- `{주인공}` 문자열은 응답에 포함하지 않는다.
- 교수 대사는 `speakerRole = professor`, `speakerName = 실제 이름`으로 변환한다.
- 주인공 대사는 `speakerRole = protagonist`로 변환하고 `speakerName` 키는 생략한다.
- `narration`, `stage_direction`, `inner_monologue` 라인은 `speakerRole`, `speakerName` 키를
  모두 생략한다.

## 5. 프론트엔드 전달 규칙

### 5.1 스크립트 라인 구조

```json
{
  "kind": "dialogue",
  "speakerRole": "professor",
  "speakerName": "김교수",
  "text": "오늘은 제법 성실했군."
}
```

허용 값:

- `kind`: `narration`, `dialogue`, `stage_direction`, `inner_monologue`
- `speakerRole`: `professor`, `protagonist`

### 5.2 렌더 규칙

- 프론트엔드는 `lines[]` 배열을 순서대로 렌더한다.
- `speakerRole = professor`면 교수 이름을 표시할 수 있다.
- `speakerRole = protagonist`면 화자 라벨 없이 대사 본문만 렌더한다.
- 화자 정보가 필요 없는 라인은 `speakerRole`, `speakerName` 키 자체를 보내지 않는다.
- `narration`, `stage_direction`, `inner_monologue`는 스타일만 다르게 처리하고, 마크다운 원문 라벨은 노출하지 않는다.

### 5.3 이벤트 응답 규칙

이벤트 본문은 단일 `body` 문자열이 아니라 아래 필드를 사용한다.

- `title`
- `rangeBand`
- `branchKey`
- `lines[]`
- `choices[]`

선택 결과는 단일 `resultText` 문자열이 아니라 아래 필드를 사용한다.

- `lines[]`
- `affectionDelta`
- `nextAffectionScore`
- `nextState`: `resolved`

## 6. 엔딩 규칙

- 엔딩 분기 타입은 최소 `happy`, `normal`, `bad`를 지원한다.
- 최종 결과 응답은 단일 문자열 묶음 대신 `script[]` 배열로 전달한다.
- 엔딩 스크립트도 일반 이벤트와 동일한 스크립트 라인 규칙을 따른다.

## 7. 동기화 규칙

- `Reference`의 구간 구조나 분기 파일명이 바뀌면 이 문서와 `Docs/developer-spec-v0.1.md`,
  `Docs/api-spec-v0.1.md`를 함께 갱신해야 한다.
- 프론트엔드가 기대하는 대사 렌더 구조가 바뀌면 `Reference` 작성 규칙과 API 응답 구조를
  같은 변경 세트에서 수정해야 한다.
