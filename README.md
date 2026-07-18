# intro_dev_csatminesc4g

This project uses one simple Java server to connect the frontend and backend.
Frontend is mostly in CSS, HTML, and JS, while the Backend was coded in Python and Java.
Everything is wired to the database, meaning that changes are user-based and persistent.

## Run StudyStack

# Prerequisites
- Java 21+
- Python 3
- Docker Desktop

# First-time setup
```powershell
docker compose up -d
psql -h localhost -U postgres -d studystack -f schema.sql
```
(Password: `devpassword123`)

# Running the app

**Windows (PowerShell):**
```powershell
docker compose up -d
javac -cp "Backend/lib/*" Backend/Login/*.java Backend/GoalService.java Backend/EventService.java
$env:JAVA_TOOL_OPTIONS = "-Duser.timezone=UTC"
java -cp "Backend/lib/*;Backend;Backend/Login" Main
```

**Mac/Linux (bash/zsh):**
```bash
docker compose up -d
javac -cp "Backend/lib/*" Backend/Login/*.java Backend/GoalService.java Backend/EventService.java
export JAVA_TOOL_OPTIONS="-Duser.timezone=UTC"
java -cp "Backend/lib/*:Backend:Backend/Login" Main
```

Then open `http://localhost:8080/login.html` in your browser.

### Notes
- Test account: `teststudent@school.com` / `temporarypassword`
