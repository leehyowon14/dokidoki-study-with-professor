# 교수 시나리오 파일 구성

## 목적

교수 연애 시나리오 중 `85-100` 고호감도 구간만 실제 대사 기준으로 정리한 인덱스
문서다. 이 문서에서 가리키는 파일들은 요약이 아니라, 사용자가 제공한 원문 대사와
이벤트 진행을 그대로 담는 것을 기준으로 한다.

## 현재 범위

- `85-100/`만 사용
- `50-84/`, `20-49/`, `0-19/` 시나리오 파일은 현재 범위에서 제외

## 파일 네이밍 규칙

- `85-100/` 형식: `common.md` + `professor-{type}/branch-{name}.md`
- `type`, `name`은 ASCII slug 사용

## 시나리오 목록

### 85-100

- [common.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/common.md)
  - 고호감도 공통 진입, 공통 대사/이벤트, 공통 마무리와 엔딩 조건
- [professor-gentle](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-gentle)
  - 다정한 교수님 분기 폴더
  - [branch-special-consultation.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-gentle/branch-special-consultation.md)
  - [branch-random-check-in.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-gentle/branch-random-check-in.md)
  - [branch-midnight-lab.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-gentle/branch-midnight-lab.md)
- [professor-tsundere](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-tsundere)
  - 냉미남 교수님 분기 폴더
  - [branch-special-consultation.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-tsundere/branch-special-consultation.md)
  - [branch-random-check-in.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-tsundere/branch-random-check-in.md)
  - [branch-perfect-control.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-tsundere/branch-perfect-control.md)
- [professor-english-mix](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-english-mix)
  - 영어 섞어쓰는 교수님 분기 폴더
  - [branch-special-consultation.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-english-mix/branch-special-consultation.md)
  - [branch-random-check-in.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-english-mix/branch-random-check-in.md)
  - [branch-the-golden-cage.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-english-mix/branch-the-golden-cage.md)
- [professor-shy](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-shy)
  - 수줍은 교수님 분기 폴더
  - [branch-special-consultation.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-shy/branch-special-consultation.md)
  - [branch-random-check-in.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-shy/branch-random-check-in.md)
  - [branch-secret-curriculum.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/85-100/professor-shy/branch-secret-curriculum.md)

## 함께 보는 파일

- [professor-character-spec.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/professor-character-spec.md)
- [professor-warning-dialogues.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/professor-warning-dialogues.md)
- [professor-encouragement-dialogues.md](/Users/wonny/src/Project/Team/AnimalLeague/April/Backend/Reference/professor-encouragement-dialogues.md)

## 운용 메모

- `85-100/`은 `common.md` + `professor-{type}/branch-{name}.md` 구조를 사용
- `common.md`에는 공통 루트, 공통 선택지, 공통 엔딩을 저장
- 각 교수 폴더에는 공통 루트 분기와 교수 전용 이벤트를 저장
- 이름, 성별, 외모 치환은 모든 파일에서 `{교수}`와 외부 입력값 기준으로 처리
