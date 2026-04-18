---
description: "순차 번호 또는 타임스탬프를 붙인 기능 브랜치를 생성한다"
---

# 기능 브랜치 생성

주어진 명세를 위한 새 Git 기능 브랜치를 만들고 그 브랜치로 전환한다. 이 명령은 **브랜치 생성만** 담당하며, 스펙 디렉터리와 파일은 핵심 `/speckit.specify` 워크플로가 생성한다.

## 사용자 입력

```text
$ARGUMENTS
```

입력이 비어 있지 않다면 반드시 고려해야 한다.

## 환경 변수 우선 규칙

사용자가 `GIT_BRANCH_NAME`을 명시적으로 제공했다면(환경 변수, 인자, 요청문 등),
스크립트를 호출하기 전에 `GIT_BRANCH_NAME` 환경 변수로 전달한다. 이 값이 설정되면:

- 스크립트는 그 값을 브랜치 이름으로 그대로 사용한다.
- prefix/suffix 생성 과정을 모두 건너뛴다.
- `--short-name`, `--number`, `--timestamp` 옵션은 무시한다.
- 이름이 숫자 접두사로 시작하면 `FEATURE_NUM`은 그 숫자를, 아니면 전체 브랜치 이름을 사용한다.

## 선행 조건

- `git rev-parse --is-inside-work-tree 2>/dev/null`로 Git 사용 가능 여부를 확인한다.
- Git을 사용할 수 없으면 사용자에게 경고하고 브랜치 생성을 건너뛴다.

## 브랜치 번호 전략

번호 부여 방식은 아래 순서로 확인한다.

1. `.specify/extensions/git/git-config.yml`의 `branch_numbering`
2. `.specify/init-options.json`의 `branch_numbering` (하위 호환)
3. 둘 다 없으면 `sequential` 기본값 사용

## 실행

브랜치용 짧은 이름(2~4단어)을 간결하게 만든다.

- 기능 설명에서 가장 의미 있는 키워드를 추출한다.
- 가능하면 동사-명사 형식을 사용한다. 예: `add-user-auth`, `fix-payment-bug`
- OAuth2, API, JWT 같은 기술 용어와 약어는 그대로 유지한다.

플랫폼에 맞는 스크립트를 아래와 같이 실행한다.

- **Bash**: `.specify/extensions/git/scripts/bash/create-new-feature.sh --json --short-name "<short-name>" "<feature description>"`
- **Bash (timestamp)**: `.specify/extensions/git/scripts/bash/create-new-feature.sh --json --timestamp --short-name "<short-name>" "<feature description>"`
- **PowerShell**: `.specify/extensions/git/scripts/powershell/create-new-feature.ps1 -Json -ShortName "<short-name>" "<feature description>"`
- **PowerShell (timestamp)**: `.specify/extensions/git/scripts/powershell/create-new-feature.ps1 -Json -Timestamp -ShortName "<short-name>" "<feature description>"`

**중요**:

- `--number`는 전달하지 않는다. 스크립트가 다음 번호를 자동으로 계산한다.
- 출력 파싱 안정성을 위해 반드시 JSON 플래그를 포함한다.
- 기능 하나당 이 스크립트는 한 번만 실행해야 한다.
- JSON 출력에는 `BRANCH_NAME`과 `FEATURE_NUM`이 포함된다.

## 점진적 저하 처리

Git이 설치되지 않았거나 현재 디렉터리가 Git 저장소가 아니면:

- 경고와 함께 브랜치 생성을 건너뛴다.
  - `[specify] Warning: Git repository not detected; skipped branch creation`
- 그래도 호출자는 참조할 수 있도록 `BRANCH_NAME`과 `FEATURE_NUM`을 출력한다.

## 출력

스크립트는 아래 JSON 필드를 출력한다.

- `BRANCH_NAME`: 생성된 브랜치 이름. 예: `003-user-auth`, `20260319-143022-user-auth`
- `FEATURE_NUM`: 숫자 또는 타임스탬프 접두사
