---
description: "GitHub 연동을 위한 원격 저장소 URL을 감지한다"
---

# Git 원격 저장소 감지

GitHub 서비스 연동(예: 이슈 생성)에 필요한 Git 원격 저장소 URL을 감지한다.

## 선행 조건

- `git rev-parse --is-inside-work-tree 2>/dev/null`로 Git 사용 가능 여부를 확인한다.
- Git을 사용할 수 없으면 아래 경고를 출력하고 빈 결과를 반환한다.

```text
[specify] Warning: Git repository not detected; cannot determine remote URL
```

## 실행

원격 URL을 확인하려면 아래 명령을 실행한다.

```bash
git config --get remote.origin.url
```

## 출력

원격 URL을 파싱해 아래 정보를 판단한다.

1. **저장소 소유자**: URL에서 추출. 예: `https://github.com/github/spec-kit.git` -> `github`
2. **저장소 이름**: URL에서 추출. 예: `https://github.com/github/spec-kit.git` -> `spec-kit`
3. **GitHub 여부**: 원격이 실제로 GitHub 저장소를 가리키는지

지원하는 URL 형식:

- HTTPS: `https://github.com/<owner>/<repo>.git`
- SSH: `git@github.com:<owner>/<repo>.git`

> [!CAUTION]
> 원격 URL이 실제로 `github.com`을 가리킬 때만 GitHub 저장소라고 보고해야 한다.
> URL 형식이 비슷하다고 해서 GitHub라고 추정하면 안 된다.

## 점진적 저하 처리

Git이 설치되지 않았거나, 현재 디렉터리가 Git 저장소가 아니거나, 원격이 설정되지 않았으면:

- 빈 결과를 반환한다.
- 오류를 발생시키지 않는다. 다른 워크플로는 원격 정보 없이 계속 진행되어야 한다.
