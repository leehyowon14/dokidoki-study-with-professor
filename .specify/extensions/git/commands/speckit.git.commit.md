---
description: "Spec Kit 명령 완료 후 변경 사항을 자동 커밋한다"
---

# 자동 커밋

Spec Kit 명령이 끝난 뒤 모든 변경 사항을 자동으로 스테이징하고 커밋한다.

## 동작 방식

이 명령은 핵심 명령의 전후 훅으로 호출된다. 동작 순서는 아래와 같다.

1. 훅 컨텍스트에서 이벤트 이름을 확인한다.
   예: `after_specify` 훅에서 호출되면 이벤트는 `after_specify`, `before_plan`이면 `before_plan`
2. `.specify/extensions/git/git-config.yml`의 `auto_commit` 섹션을 확인한다.
3. 해당 이벤트 키에 자동 커밋이 활성화되어 있는지 조회한다.
4. 이벤트별 키가 없으면 `auto_commit.default` 값을 사용한다.
5. 명령별 `message`가 설정되어 있으면 그 값을, 없으면 기본 메시지를 사용한다.
6. 자동 커밋이 활성화되어 있고 미커밋 변경이 있으면 `git add .`와 `git commit`을 실행한다.

## 실행

이 명령을 호출한 훅에서 이벤트 이름을 확인한 뒤 아래 스크립트를 실행한다.

- **Bash**: `.specify/extensions/git/scripts/bash/auto-commit.sh <event_name>`
- **PowerShell**: `.specify/extensions/git/scripts/powershell/auto-commit.ps1 <event_name>`

`<event_name>`에는 실제 훅 이벤트 이름을 넣는다. 예: `after_specify`, `before_plan`, `after_implement`

## 설정

`.specify/extensions/git/git-config.yml` 예시:

```yaml
auto_commit:
  default: false          # 전체 명령의 기본값. true면 전부 활성화
  after_specify:
    enabled: true         # 명령별 재정의
    message: "[Spec Kit] Add specification"
  after_plan:
    enabled: false
    message: "[Spec Kit] Add implementation plan"
```

## 점진적 저하 처리

- Git을 사용할 수 없거나 현재 디렉터리가 저장소가 아니면 경고를 내고 건너뛴다.
- 설정 파일이 없으면 비활성화 상태로 간주하고 건너뛴다.
- 커밋할 변경 사항이 없으면 메시지를 남기고 건너뛴다.
