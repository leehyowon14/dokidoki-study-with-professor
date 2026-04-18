---
description: "초기 커밋과 함께 Git 저장소를 초기화한다"
---

# Git 저장소 초기화

현재 프로젝트 디렉터리에 Git 저장소가 없으면 새로 초기화한다.

## 실행

프로젝트 루트에서 플랫폼에 맞는 스크립트를 실행한다.

- **Bash**: `.specify/extensions/git/scripts/bash/initialize-repo.sh`
- **PowerShell**: `.specify/extensions/git/scripts/powershell/initialize-repo.ps1`

확장 스크립트를 찾지 못하면 아래 명령으로 대체한다.

- **Bash**: `git init && git add . && git commit -m "Initial commit from Specify template"`
- **PowerShell**: `git init; git add .; git commit -m "Initial commit from Specify template"`

스크립트는 아래 검사를 내부에서 처리한다.

- Git 사용 가능 여부 확인
- 이미 Git 저장소 안인지 확인
- `git init`, `git add .`, `git commit` 실행

## 사용자 정의

프로젝트별 초기화 단계를 추가하려면 스크립트를 교체하거나 확장한다.

- 커스텀 `.gitignore` 템플릿
- 기본 브랜치 이름 설정 (`git config init.defaultBranch`)
- Git LFS 설정
- Git hooks 설치
- 커밋 서명 설정
- Git Flow 초기화

## 출력

성공 시:

- `✓ Git repository initialized`

## 점진적 저하 처리

Git이 설치되지 않았으면:

- 사용자에게 경고한다.
- 저장소 초기화를 건너뛴다.
- Git이 없어도 프로젝트는 계속 동작하며, 스펙은 `specs/` 아래에 생성될 수 있다.

Git은 설치되어 있지만 `git init`, `git add .`, `git commit` 중 하나가 실패하면:

- 오류를 사용자에게 그대로 보여준다.
- 부분적으로 초기화된 상태로 진행하지 않고 명령을 중단한다.
