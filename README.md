FlowState – Focus Session Management System
Spring Boot 3.2 (Java 17, Maven, MySQL 8, JWT/RBAC) backend + React 18 / Redux Toolkit frontend, generated from FlowState_SRS.docx.

flowstate/
├── backend/    Spring Boot API   (http://localhost:8080)
└── frontend/   React app         (http://localhost:3000)
Run it
1. Backend (needs JDK 17+, Maven, a running MySQL 8)

cd backend
# optional – defaults are root / root
export DB_USERNAME=root DB_PASSWORD=yourpassword
export JWT_SECRET="at-least-32-characters-long-secret-value"
mvn spring-boot:run
The flowstate_db database is created automatically (createDatabaseIfNotExist=true, ddl-auto=update). Tests run on H2 (src/test/resources/application.properties).

2. Frontend (needs Node 18+)

cd frontend
npm install
npm start
Demo accounts (seeded on first start)
Role	Username	Password
PLATFORM_ADMIN	admin	Password123!
FLOW_COACH	coach	Password123!
PRACTITIONER	maya	Password123!
You can also register PRACTITIONER / FLOW_COACH users from the login screen.

Notes on the SRS
Implemented as specified: all 6 entities, repositories, services, controllers, JWT (HS256, 15 min access / 7 day refresh), RBAC, CORS, GlobalExceptionHandler, focus-score formula, Redux slices, Axios interceptors and the components in section 11.

Small, deliberate deviations / additions:

findWeeklyDistractionTrend uses COUNT(dl.id) – COUNT(dl) is JPQL syntax and is invalid in native MySQL SQL.
The SRS defines totalFocusMinutes as SUM(focusScore) (findTotalFocusMinutes). That is implemented literally; if you want real minutes, sum actualEnd - actualStart over completed sessions instead.
Focus score is also capped at 100 (SRS: "0–100"). The score operators ×50 / ×20 were lost in the document text and are implemented as multiplication.
Extra endpoints needed by the UI: PUT /api/sessions/{id} (Edit button) and GET /api/users/practitioners (coach picks a recipient).
Refresh tokens carry a type=refresh claim and are rejected as API access tokens.
Redux slices + services exist for goals and distractions (per the project structure), but the SRS defines no screens for them, so they are not wired into the UI yet.
