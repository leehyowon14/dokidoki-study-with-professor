# 백엔드 코멘트 로그

문서 버전: v1.0

## 목적

백엔드 작업 중 발견한 프론트엔드 계약 불일치, UI/API 사용 질문, 오류, 차단 이슈를
버전이 고정된 문서로 남긴다.

## 기록 규칙

- 이 문서는 추가 기록 전용 방식으로 관리한다.
- 각 항목은 날짜, 관련 이슈 번호, 작업 브랜치, 상태를 반드시 포함한다.
- 해결되면 동일 항목에 해소 내용을 갱신한다.

## 항목 템플릿

### 2026-04-18 / 이슈 #000 / 상태: 열림

- 작업 브랜치:
- 관련 문서/API:
- 질문 또는 문제:
- 기대하는 프론트엔드 조치:
- 해결 내용:

### 2026-04-19 / 이슈 #1 / 상태: 해소

- 작업 브랜치: `feat/back-end/1-common-platform-ci-init`
- 관련 문서/API: `specs/001-backend-mvp/quickstart.md`, `.github/workflows/backend-ci.yml`
- 질문 또는 문제: 백엔드 초기 구현 기준에서 실행 명령과 필수 환경 변수가 문서와 실제 프로젝트 골격에 맞춰져 있어야 함
- 기대하는 프론트엔드 조치: 없음
- 해결 내용: `Backend/gradlew`, `clean assemble contractTest integrationTest unitTest` 1회 실행 기준으로 CI와 로컬 검증 절차를 맞추고 `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `NANOBANANA_API_KEY`, `NANOBANANA_BASE_URL`를 필수 확인 환경 변수로 명시. DB 계정 정보 fallback은 제거하고 세션 타임아웃은 `server.servlet.session.timeout` 기준으로 고정
