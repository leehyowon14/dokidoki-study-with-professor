# AGENTS.md

이 문서는 이 저장소를 처음 받는 다른 환경의 에이전트도 바로 읽고 작업할 수 있도록 만든
운영 가이드다. 특정 개인 로컬 환경이 아니라, 저장소 구조와 현재 협업 규칙을 기준으로
판단한다.

## 공통

### 먼저 읽을 문서

작업 전 아래 순서로 읽는다.

1. `.specify/memory/constitution.md`
2. `Docs/README.md`
3. `Docs/api-spec-v0.1.md`
4. `Docs/developer-spec-v0.1.md`
5. `Docs/dialogue-content-spec-v0.1.md`

헌법은 브랜치, TDD, 문서 계약, 이슈/PR 추적 규칙의 최상위 기준이다. 계약과 구현이
충돌하면 코드보다 문서를 먼저 본다. 문서를 바꿔야 하는 변경이면 같은 변경 세트에서
함께 갱신한다.

### 저장소 개요

- 백엔드 코드 루트: `Backend/`
- 문서 루트: `Docs/`
- 스펙/계획 루트: `specs/001-backend-mvp/`
- 교수/대사 원문 자산: `Backend/Reference/`
- 백엔드 CI: `.github/workflows/backend-ci.yml`

현재 저장소의 구현 중심은 Spring Boot 백엔드 MVP다. 프론트엔드는 별도 트랙이며, 이
저장소에서는 주로 계약 문서와 협업 규칙으로 연결된다.

### 작업 원칙

- TDD 우선: 구현 전에 실패하는 테스트를 먼저 만든다.
- 계약 우선: API 요청/응답이 바뀌면 `Docs/api-spec-v0.1.md`와
  `specs/001-backend-mvp/contracts/backend-api.openapi.yaml`을 함께 갱신한다.
- 콘텐츠 동기화: 대사 구조, 호감도 구간, 이벤트 포맷이 바뀌면
  `Docs/dialogue-content-spec-v0.1.md`와 `Backend/Reference/`를 함께 갱신한다.
- 작은 변경 세트 유지: 코드, 테스트, 문서 동기화를 같은 논리 단위로 묶는다.

### 브랜치와 협업 규칙

기준 브랜치:

- `main`: 운영 기준
- `dev/back-end`: 백엔드 개발 서버 기준
- `dev/front-end`: 프론트엔드 개발 서버 기준

브랜치 네이밍:

- 백엔드 기능: `feat/back-end/<issueNum>-<slug>`
- 백엔드 긴급 수정: `hotfix/back-end/<issueNum>-<slug>`
- 프론트엔드 기능은 이 저장소 문서 기준으로 `feat/front-end/<issueNum>-<slug>`를 사용한다.

`issueNum`은 GitHub 이슈 `#123`의 숫자 `123`이다.

### Git 작업 주의사항

- `Backend/build/`, `Backend/.gradle/` 등 gitignore 대상은 커밋하지 않는다.
- `git add -f`로 gitignore 대상 파일이나 폴더를 강제로 올리지 않는다.
- pull 시 로컬 변경을 먼저 보존해야 하면 충돌 전략을 명시적으로 선택한다.
- 사용자가 요구하지 않은 reset, checkout, clean 같은 파괴적 명령은 피한다.

### 브라우저 자동화 주의사항

- 브라우저 자동화를 수행한 뒤에는 반드시 브라우저 프로세스를 종료한다.
- 브라우저를 띄운 채로 작업을 끝내지 않는다.

### 작업 시작 전 체크리스트

- 현재 브랜치를 확인했다.
- `git status`로 로컬 변경 상태를 확인했다.
- 관련 스펙과 계약 문서를 읽었다.
- 필요한 실행 환경과 도구 상태를 확인했다.

### 작업 종료 전 체크리스트

- 변경한 코드나 문서에 맞는 검증을 수행했다. 못 했다면 이유를 남긴다.
- 문서 동기화 대상이 있으면 함께 반영했다.
- 생성물과 캐시가 git에 섞이지 않았는지 확인했다.
- 브라우저 자동화를 썼다면 브라우저를 종료했다.

## BE

### 먼저 읽을 문서

백엔드 작업 전 아래 순서로 읽는다.

1. `specs/001-backend-mvp/plan.md`
2. `specs/001-backend-mvp/quickstart.md`
3. `specs/001-backend-mvp/tasks.md`
4. `Docs/api-spec-v0.1.md`
5. `Docs/developer-spec-v0.1.md`
6. `Docs/dialogue-content-spec-v0.1.md`

### BE 범위

현재 이 저장소에서 실제 코드 작업의 중심은 `Backend/`다. `auth`, `professor`, `study`,
`dialogue`, `result`, `common` 구조로 확장하는 모듈형 모놀리스 계획을 따른다.

### 필요한 환경

최소 요구사항:

- Git
- Java 21
- Docker Desktop 또는 Docker Engine
- PostgreSQL 16
- zsh 또는 bash

권장:

- `psql`
- `curl`
- IntelliJ IDEA 또는 VS Code Java 확장

중요:

- Gradle은 전역 설치하지 않아도 된다. 항상 `Backend/gradlew`를 사용한다.
- 테스트는 Testcontainers를 사용하므로 Docker가 켜져 있어야 안정적으로 돌아간다.
- 런타임 DB는 로컬 PostgreSQL 16 기준이다.

### 최초 확인 명령

```bash
git clone <repo-url>
cd April
chmod +x Backend/gradlew
java -version
docker --version
```

`java -version` 결과가 21이 아니면 작업을 시작하지 않는다.

### 환경 변수

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/april
export SPRING_DATASOURCE_USERNAME=april
export SPRING_DATASOURCE_PASSWORD=april
export NANOBANANA_API_KEY=change-me
export NANOBANANA_BASE_URL=https://api.nanobanana.example
```

- `SPRING_DATASOURCE_*`: 로컬 개발 DB 연결 정보
- `NANOBANANA_API_KEY`: 교수 캐릭터 에셋 생성 연동 키
- `NANOBANANA_BASE_URL`: 에셋 생성 API 기본 URL

에셋 생성 플로우를 건드리면 `NANOBANANA_*` 유무를 반드시 확인한다.

### 데이터베이스 준비

런타임은 로컬 PostgreSQL 16, 테스트는 Testcontainers가 기준이다.

로컬 PostgreSQL 예시:

```bash
createdb april
createuser april
```

Docker 예시:

```bash
docker run --name april-postgres \
  -e POSTGRES_DB=april \
  -e POSTGRES_USER=april \
  -e POSTGRES_PASSWORD=april \
  -p 5432:5432 \
  -d postgres:16
```

테스트 설정은 `Backend/src/test/resources/application-test.yml`에서
`jdbc:tc:postgresql:16.4:///april_test`를 사용한다. 즉, 테스트는 로컬 DB보다 Docker
가용성이 더 중요하다.

### 자주 쓰는 명령

모든 백엔드 명령은 `Backend/` 기준이다.

```bash
cd Backend
./gradlew clean assemble
./gradlew contractTest
./gradlew integrationTest
./gradlew unitTest
./gradlew check
./gradlew bootRun
```

기본 확인 순서:

1. `./gradlew clean assemble`
2. `./gradlew contractTest`
3. `./gradlew integrationTest`
4. `./gradlew unitTest`

### 현재 코드 구조 기준

```text
Backend/
├── build.gradle.kts
├── gradlew
├── src/main/java/com/animalleague/april/
│   └── AprilApplication.java
├── src/main/resources/
│   └── application.yml
└── src/test/resources/
    └── application-test.yml
```

목표 구조:

```text
Backend/src/main/java/com/animalleague/april/
├── auth/
├── professor/
├── study/
├── dialogue/
├── result/
└── common/
```

새 코드는 가능하면 이 목표 구조를 따른다.

### CI 기준

백엔드 CI는 `.github/workflows/backend-ci.yml` 기준이다.

현재 CI는 다음을 수행한다.

1. Java 21 설정
2. `Backend/`에서 Gradle 실행 권한 부여
3. `./gradlew --no-daemon clean assemble`
4. `./gradlew --no-daemon contractTest`
5. `./gradlew --no-daemon integrationTest`
6. `./gradlew --no-daemon unitTest`

로컬 검증도 이 순서를 우선한다.

### BE 문제 해결 힌트

- `./gradlew` 실행 권한 오류: `chmod +x Backend/gradlew`
- Testcontainers 실패: Docker 실행 여부 확인
- DB 연결 실패: `SPRING_DATASOURCE_*`와 PostgreSQL 16 상태 확인
- 에셋 생성 호출 실패: `NANOBANANA_API_KEY`, `NANOBANANA_BASE_URL` 확인
- 계약 불일치: 코드보다 `Docs/api-spec-v0.1.md`와 OpenAPI 문서를 먼저 확인

## FE

### FE 범위

현재 저장소에는 프론트엔드 애플리케이션 코드 루트가 없다. FE 관련 작업은 주로 아래
문서와 협업 인터페이스를 다룬다.

- `Docs/api-spec-v0.1.md`
- `Docs/developer-spec-v0.1.md`
- `Docs/dialogue-content-spec-v0.1.md`
- `Docs/FE-comment-v1.0.md`
- `Docs/BE-comment-v1.0.md`

### FE 작업 시 기준

- 프론트엔드 요구가 계약과 다르면 바로 구현 가정으로 밀지 말고 문서 기준을 먼저 확인한다.
- FE가 백엔드에 요청하거나 질문할 내용은 `Docs/FE-comment-v1.0.md`에 남긴다.
- BE에서 FE로 전달할 계약/응답 구조 이슈는 `Docs/BE-comment-v1.0.md`에 남긴다.
- API 형식 변경은 문서 계약과 OpenAPI를 먼저 맞춘 뒤 구현 트랙에 전달한다.

### FE 브랜치 기준

- FE 기능 작업: `feat/front-end/<issueNum>-<slug>`
- 개발 서버 기준 브랜치: `dev/front-end`

프론트엔드 코드가 다른 저장소에 있더라도, 이 저장소 문서를 기준선으로 삼는다.

## AGENTS.md 갱신 기준

아래가 바뀌면 `AGENTS.md`도 갱신 대상이다.

- 필수 개발 도구 버전
- 실행 명령
- 브랜치 전략
- 백엔드 루트 경로
- 테스트/CI 파이프라인
- FE/BE 문서 협업 방식
- 외부 연동 필수 환경 변수
