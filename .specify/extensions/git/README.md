# Git 브랜치 워크플로 확장

Spec Kit을 위한 Git 저장소 초기화, 기능 브랜치 생성, 번호 부여(순차/타임스탬프),
브랜치 검증, 원격 저장소 감지, 자동 커밋 기능을 제공한다.

## 개요

이 확장은 Git 작업을 선택적으로 사용할 수 있는 독립 모듈로 제공한다. 관리 대상은 아래와 같다.

- 설정 가능한 커밋 메시지를 사용하는 **저장소 초기화**
- 순차 번호(`001-feature-name`) 또는 타임스탬프(`20260319-143022-feature-name`) 기반의 **기능 브랜치 생성**
- 브랜치 네이밍 규칙 준수를 확인하는 **브랜치 검증**
- GitHub 연동용 **원격 저장소 감지** (예: 이슈 생성)
- 핵심 명령 뒤에 실행되는 **자동 커밋** (명령별 켜기/끄기 및 메시지 설정 가능)

## 명령 목록

| 명령 | 설명 |
|---------|-------------|
| `speckit.git.initialize` | 설정 가능한 초기 커밋 메시지로 Git 저장소를 초기화한다 |
| `speckit.git.feature` | 순차 번호 또는 타임스탬프를 붙인 기능 브랜치를 만든다 |
| `speckit.git.validate` | 현재 브랜치가 기능 브랜치 규칙을 따르는지 검증한다 |
| `speckit.git.remote` | GitHub 연동용 원격 저장소 URL을 감지한다 |
| `speckit.git.commit` | 변경 사항을 자동 커밋한다 (명령별 켜기/끄기 및 메시지 설정 가능) |

## 훅

| 이벤트 | 명령 | 선택 여부 | 설명 |
|-------|---------|----------|-------------|
| `before_constitution` | `speckit.git.initialize` | 필수 | 헌장 설정 전에 Git 저장소를 초기화한다 |
| `before_specify` | `speckit.git.feature` | 필수 | 명세 생성 전에 기능 브랜치를 만든다 |
| `before_clarify` | `speckit.git.commit` | 선택 | 명세 보완 전에 미커밋 변경 사항을 커밋한다 |
| `before_plan` | `speckit.git.commit` | 선택 | 계획 수립 전에 미커밋 변경 사항을 커밋한다 |
| `before_tasks` | `speckit.git.commit` | 선택 | 작업 목록 생성 전에 미커밋 변경 사항을 커밋한다 |
| `before_implement` | `speckit.git.commit` | 선택 | 구현 전에 미커밋 변경 사항을 커밋한다 |
| `before_checklist` | `speckit.git.commit` | 선택 | 체크리스트 생성 전에 미커밋 변경 사항을 커밋한다 |
| `before_analyze` | `speckit.git.commit` | 선택 | 분석 전에 미커밋 변경 사항을 커밋한다 |
| `before_taskstoissues` | `speckit.git.commit` | 선택 | 이슈 동기화 전에 미커밋 변경 사항을 커밋한다 |
| `after_constitution` | `speckit.git.commit` | 선택 | 헌장 갱신 후 자동 커밋한다 |
| `after_specify` | `speckit.git.commit` | 선택 | 명세 생성 후 자동 커밋한다 |
| `after_clarify` | `speckit.git.commit` | 선택 | 명세 보완 후 자동 커밋한다 |
| `after_plan` | `speckit.git.commit` | 선택 | 계획 수립 후 자동 커밋한다 |
| `after_tasks` | `speckit.git.commit` | 선택 | 작업 목록 생성 후 자동 커밋한다 |
| `after_implement` | `speckit.git.commit` | 선택 | 구현 후 자동 커밋한다 |
| `after_checklist` | `speckit.git.commit` | 선택 | 체크리스트 생성 후 자동 커밋한다 |
| `after_analyze` | `speckit.git.commit` | 선택 | 분석 후 자동 커밋한다 |
| `after_taskstoissues` | `speckit.git.commit` | 선택 | 이슈 동기화 후 자동 커밋한다 |

## 설정

설정은 `.specify/extensions/git/git-config.yml`에 저장한다.

```yaml
# 브랜치 번호 전략: "sequential" 또는 "timestamp"
branch_numbering: sequential

# git init 시 사용할 커밋 메시지
init_commit_message: "[Spec Kit] Initial commit"

# 명령별 자동 커밋 설정 (기본값은 모두 비활성화)
# 예: specify 이후 자동 커밋 활성화
auto_commit:
  default: false
  after_specify:
    enabled: true
    message: "[Spec Kit] Add specification"
```

## 설치

```bash
# 번들된 git 확장을 설치한다. 네트워크는 필요 없다.
specify extension add git
```

## 비활성화

```bash
# git 확장을 비활성화한다. 스펙 생성은 브랜치 생성 없이 계속된다.
specify extension disable git

# 다시 활성화한다.
specify extension enable git
```

## 점진적 저하 처리

Git이 설치되지 않았거나 현재 디렉터리가 Git 저장소가 아니면:

- 스펙 디렉터리는 계속 `specs/` 아래에 생성된다.
- 브랜치 생성은 경고와 함께 건너뛴다.
- 브랜치 검증은 경고와 함께 건너뛴다.
- 원격 저장소 감지는 빈 결과를 반환한다.

## 스크립트

이 확장은 아래와 같은 크로스플랫폼 스크립트를 포함한다.

- `scripts/bash/create-new-feature.sh` — Bash 구현
- `scripts/bash/git-common.sh` — 공통 Git 유틸리티 (Bash)
- `scripts/powershell/create-new-feature.ps1` — PowerShell 구현
- `scripts/powershell/git-common.ps1` — 공통 Git 유틸리티 (PowerShell)
