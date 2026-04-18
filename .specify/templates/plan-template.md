# Implementation Plan: [FEATURE]

**Feature ID**: `[FEATURE_ID]` | **Working Branch**: `[WORKING_BRANCH]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[FEATURE_ID]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. The working branch
MUST follow the project constitution, not the default Spec Kit numbered branch convention.
`issueNum` means the numeric part of a GitHub issue such as `123` from `#123`.

## Summary

[Extract from feature spec: primary requirement + FE/BE split + technical approach]

## Technical Context

**Frontend**: [React version / app router / state model or NEEDS CLARIFICATION]  
**Backend**: [Spring Boot version / module layout / API style or NEEDS CLARIFICATION]  
**Storage**: [e.g., PostgreSQL, Redis, S3, local files or NEEDS CLARIFICATION]  
**Testing**: [Frontend test stack] / [Backend test stack]  
**Target Platform**: [mobile web / HTTP API / admin console / batch worker]  
**Project Type**: Web application with strict frontend/backend separation  
**Docs Impact**: [versioned Docs files to create or update in `Docs/`]  
**Deployment Targets**: `main` -> production, `dev/front-end` -> FE dev server, `dev/back-end` -> BE dev server  
**Constraints**: TDD first, versioned docs mandatory, FE/BE contract-first communication, CI/CD required  
**Scale/Scope**: [expected users, pages, endpoints, jobs, integrations]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [ ] FE/BE responsibility boundary is explicit and file ownership is clear.
- [ ] Versioned contract/docs updates in `Docs/` are identified.
- [ ] TDD order is explicit: failing tests first, implementation second.
- [ ] Backend design allows 2-person parallel collaboration without implicit coupling.
- [ ] Working branch and deployment target match the constitution.
- [ ] GitHub issue/PR template and label impacts are identified if workflow changes.

## Project Structure

### Documentation (repository root)

```text
Docs/
├── product-plan-vX.Y.md
├── developer-spec-vX.Y.md
├── api-spec-vX.Y.md
├── FE-comment-vX.Y.md
└── BE-comment-vX.Y.md

specs/[FEATURE_ID]/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
└── tasks.md
```

### Source Code (repository root)

```text
frontend/
├── src/
│   ├── app/
│   ├── components/
│   ├── features/
│   ├── services/
│   └── types/
└── tests/
    ├── integration/
    └── unit/

backend/
├── src/main/
│   ├── java/ or kotlin/
│   └── resources/
└── src/test/

.github/
├── ISSUE_TEMPLATE/
└── pull_request_template.md
```

**Structure Decision**: [Document actual directories used by this feature and why the
split supports parallel FE/BE execution]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., temporary shared DTO package] | [current need] | [why strict split alone was insufficient] |
