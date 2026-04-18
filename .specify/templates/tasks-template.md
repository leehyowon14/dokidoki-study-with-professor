---

description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[FEATURE_ID]/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/
**Tests**: Every user story MUST start with failing tests per TDD.
**Organization**: Tasks are grouped by user story and split across `frontend/`,
`backend/`, and `Docs/` so FE/BE can work in parallel without hidden dependencies.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Frontend**: `frontend/src/`, `frontend/tests/`
- **Backend**: `backend/src/main/`, `backend/src/test/`
- **Docs**: `Docs/`
- **GitHub workflow**: `.github/`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish the feature branch context, versioned docs, and project scaffolding

- [ ] T001 Create or verify feature workspace structure for `frontend/`, `backend/`, and `Docs/`
- [ ] T002 Create or update versioned contract docs in `Docs/` for this feature scope
- [ ] T003 [P] Link the feature to a GitHub issue and confirm required labels
- [ ] T004 [P] Identify FE/BE ownership boundaries in the plan and spec artifacts

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Freeze contracts and shared foundations before feature work begins

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Finalize API/interface contract in `Docs/api-spec-vX.Y.md` or `specs/[FEATURE_ID]/contracts/`
- [ ] T006 [P] Establish frontend service/client layer entry points in `frontend/src/services/`
- [ ] T007 [P] Establish backend controller/service/module entry points in `backend/src/main/`
- [ ] T008 [P] Configure or update CI checks required for this feature
- [ ] T009 Define cross-team blocker handling via `Docs/FE-comment-vX.Y.md` and `Docs/BE-comment-vX.Y.md`

**Checkpoint**: Contract and shared foundations are ready; FE and BE work can now proceed in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 🎯 MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1 (MANDATORY - FAIL FIRST)

- [ ] T010 [P] [US1] Add frontend test coverage in `frontend/tests/` for the primary user flow
- [ ] T011 [P] [US1] Add backend test coverage in `backend/src/test/` for the primary API/domain flow

### Implementation for User Story 1

- [ ] T012 [P] [US1] Implement frontend UI/state changes in `frontend/src/`
- [ ] T013 [P] [US1] Implement backend domain/API changes in `backend/src/main/`
- [ ] T014 [US1] Integrate frontend with the agreed API contract
- [ ] T015 [US1] Integrate backend validation, error handling, and observability
- [ ] T016 [US1] Update versioned docs in `Docs/` to reflect the delivered contract/behavior
- [ ] T017 [US1] Record unresolved cross-team issues in `Docs/FE-comment-vX.Y.md` or `Docs/BE-comment-vX.Y.md` if needed

**Checkpoint**: User Story 1 is fully functional, documented, and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 (MANDATORY - FAIL FIRST)

- [ ] T018 [P] [US2] Add frontend tests in `frontend/tests/`
- [ ] T019 [P] [US2] Add backend tests in `backend/src/test/`

### Implementation for User Story 2

- [ ] T020 [P] [US2] Implement frontend changes in `frontend/src/`
- [ ] T021 [P] [US2] Implement backend changes in `backend/src/main/`
- [ ] T022 [US2] Validate FE/BE integration against the frozen contract
- [ ] T023 [US2] Update affected versioned docs in `Docs/`

**Checkpoint**: User Stories 1 and 2 both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 (MANDATORY - FAIL FIRST)

- [ ] T024 [P] [US3] Add frontend tests in `frontend/tests/`
- [ ] T025 [P] [US3] Add backend tests in `backend/src/test/`

### Implementation for User Story 3

- [ ] T026 [P] [US3] Implement frontend changes in `frontend/src/`
- [ ] T027 [P] [US3] Implement backend changes in `backend/src/main/`
- [ ] T028 [US3] Update contracts, docs, and comments in `Docs/` as needed

**Checkpoint**: All user stories are independently functional and documented

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Clean up duplicated FE/BE code paths without violating ownership boundaries
- [ ] TXXX [P] Update versioned docs and remove stale contract notes in `Docs/`
- [ ] TXXX Validate CI/CD behavior for the target branch
- [ ] TXXX Run full FE and BE test suites and capture evidence for PR
- [ ] TXXX Verify GitHub issue/PR checklist compliance

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Setup completion and freezes contracts
- **User Stories (Phase 3+)**: Depend on Foundational completion
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- User stories can proceed in parallel once the contract and foundations are locked
- Each user story MUST remain independently testable and releasable

### Within Each User Story

- Tests MUST be written and fail before implementation
- Frontend and backend tasks can run in parallel after shared contract tasks complete
- Docs updates MUST land with the implementation, not later
- Unresolved FE/BE questions MUST be recorded in the comment docs before merge

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- Foundational FE and BE entry-point tasks can run in parallel after contract drafting begins
- Frontend and backend implementation tasks for the same user story can run in parallel once tests and contracts are in place
- Different user stories can run in parallel when staffing and dependency boundaries allow it

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup
2. Complete Foundational
3. Complete User Story 1 tests first, then implementation
4. Validate FE/BE integration and docs updates
5. Deploy or demo from the constitution-approved target branch

### Incremental Delivery

1. Freeze the contract
2. Deliver User Story 1 with tests and docs
3. Deliver User Story 2 with tests and docs
4. Deliver User Story 3 with tests and docs
5. Keep each increment mergeable through the standardized PR template

### Parallel Team Strategy

With multiple developers:

1. Finalize shared contract and foundations together
2. Split FE and BE ownership per user story after contract freeze
3. Use `Docs/FE-comment-vX.Y.md` and `Docs/BE-comment-vX.Y.md` for blocker capture
4. Reintegrate through contract validation and CI before merge

---

## Notes

- [P] tasks = different files, no dependencies
- Each user story should remain independently completable and testable
- Contract changes must update versioned docs in `Docs/`
- Avoid hidden cross-team assumptions; document them or remove them
