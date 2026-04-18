# Feature Specification: [FEATURE NAME]

**Feature ID**: `[FEATURE_ID]`  
**Working Branch**: `[WORKING_BRANCH]`  
**Created**: [DATE]  
**Status**: Draft  
**Input**: User description: "$ARGUMENTS"

**Note**: `issueNum` in branch names means the numeric part of a GitHub issue,
for example `123` from `#123`.

## User Scenarios & Testing *(mandatory)*

User stories MUST be independently testable, independently reviewable, and explicit about
frontend/backend boundaries.

### User Story 1 - [Brief Title] (Priority: P1)

[Describe this user journey in plain language]

**Why this priority**: [Explain the user/business value]

**Independent Test**: [Describe the end-to-end verification path]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]
2. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 2 - [Brief Title] (Priority: P2)

[Describe this user journey in plain language]

**Why this priority**: [Explain the user/business value]

**Independent Test**: [Describe the end-to-end verification path]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

### User Story 3 - [Brief Title] (Priority: P3)

[Describe this user journey in plain language]

**Why this priority**: [Explain the user/business value]

**Independent Test**: [Describe the end-to-end verification path]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]

## Edge Cases

- What happens when [frontend validation failure or empty state]?
- What happens when [backend contract mismatch, timeout, or concurrent update]?
- What happens when FE and BE are developed in parallel and one side is ahead of the agreed contract?

## Frontend / Backend Boundary *(mandatory)*

### Frontend Responsibilities

- [UI scope, routes, state, validation, rendering ownership]

### Backend Responsibilities

- [domain rules, persistence, API, auth, async processing ownership]

### Shared Contract

- [API endpoints / payloads / error codes / docs to update in `Docs/`]

### Parallel Work Plan

- [How FE and BE can work independently once the contract is frozen]

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST [specific capability]
- **FR-002**: System MUST [specific capability]
- **FR-003**: Frontend MUST [specific UX/client behavior]
- **FR-004**: Backend MUST [specific domain/API behavior]
- **FR-005**: System MUST update or create versioned documents in `Docs/` when the
  feature changes contracts, behavior, or cross-team assumptions.

### Key Entities *(include if feature involves data)*

- **[Entity 1]**: [What it represents, key attributes without implementation]
- **[Entity 2]**: [What it represents, relationships to other entities]

## Documentation & Communication Impact *(mandatory)*

- **Docs to Create/Update**: [e.g., `Docs/api-spec-v0.2.md`, `Docs/FE-comment-v1.0.md`]
- **API / Contract Changes**: [Describe payload or contract changes]
- **Frontend Open Questions**: [List unresolved FE -> BE questions or `None`]
- **Backend Open Questions**: [List unresolved BE -> FE questions or `None`]
- **Versioning Plan**: [How document versions will be incremented]

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: [Measurable user or business metric]
- **SC-002**: [Measurable performance or reliability metric]
- **SC-003**: [Measurable delivery or usability metric]
- **SC-004**: [Measurable collaboration metric, e.g., FE and BE can integrate without
  undocumented contract changes]

## Assumptions

- [Assumption about target users]
- [Assumption about scope boundaries]
- [Assumption about environment or deployment]
- [Assumption about contract stability or existing system dependencies]
