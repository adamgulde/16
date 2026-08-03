# Project Plan

## Current State
- **Backend:** Spring Boot (Java 21), PostgreSQL, JPA/Hibernate.
- **Frontend:** React + TypeScript + Vite.
- **Functionality:** Basic authentication (Signin), displaying user graphs.

## Requirements & Status
1. **Persistent Data:** Update `docker-compose.yml` to use Docker volumes for the PostgreSQL database, ensuring data persists across container restarts. (Pending: Requires deployment to verify).
2. **Updated Authentication:**
    - [COMPLETED] Modify authentication logic to allow login for existing users using only their username (no password check).
    - [COMPLETED] Maintain password validation/requirement for new user registration.
3. **Validation & Testing:** 
    - [COMPLETED] Created integration test `AuthControllerIT.java` to verify new authentication flow.
    - (Pending: Cannot execute tests in current environment).
