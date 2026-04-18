---
description: "현재 브랜치가 기능 브랜치 네이밍 규칙을 따르는지 검증한다"
---

# 기능 브랜치 검증

현재 Git 브랜치가 기대하는 기능 브랜치 네이밍 규칙을 따르는지 검증한다.

## 선행 조건

- `git rev-parse --is-inside-work-tree 2>/dev/null`로 Git 사용 가능 여부를 확인한다.
- Git을 사용할 수 없으면 아래 경고를 출력하고 검증을 건너뛴다.

```text
[specify] Warning: Git repository not detected; skipped branch validation
```

## 검증 규칙

현재 브랜치 이름은 아래 명령으로 확인한다.

```bash
git rev-parse --abbrev-ref HEAD
```

브랜치 이름은 아래 패턴 중 하나와 일치해야 한다.

1. **순차 번호 방식**: `^[0-9]{3,}-` (예: `001-feature-name`, `042-fix-bug`, `1000-big-feature`)
2. **타임스탬프 방식**: `^[0-9]{8}-[0-9]{6}-` (예: `20260319-143022-feature-name`)

## 실행

현재 브랜치가 기능 브랜치 패턴과 일치하면:

- `✓ On feature branch: <branch-name>` 출력
- `specs/` 아래에 대응하는 스펙 디렉터리가 있는지 확인
  - 순차 번호 방식이면 `specs/<prefix>-*`
  - 타임스탬프 방식이면 `specs/<prefix>-*`
- 스펙 디렉터리가 있으면 `✓ Spec directory found: <path>` 출력
- 스펙 디렉터리가 없으면 `⚠ No spec directory found for prefix <prefix>` 출력

기능 브랜치가 아니면:

- `✗ Not on a feature branch. Current branch: <branch-name>` 출력
- `Feature branches should be named like: 001-feature-name or 20260319-143022-feature-name` 출력

## 점진적 저하 처리

Git이 설치되지 않았거나 현재 디렉터리가 Git 저장소가 아니면:

- 대체 수단으로 `SPECIFY_FEATURE` 환경 변수를 확인한다.
- 값이 있으면 같은 패턴으로 검증한다.
- 값이 없으면 경고를 출력하고 검증을 건너뛴다.
