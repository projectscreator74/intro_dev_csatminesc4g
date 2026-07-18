import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final int PORT = 8080;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\"email\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("\"password\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("\"username\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern DISPLAY_NAME_PATTERN = Pattern.compile("\"displayName\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("\"userId\"\\s*:\\s*(\\d+)");
    private static final Pattern NOTIFICATIONS_PATTERN = Pattern.compile("\"notifications\"\\s*:\\s*(true|false)");
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern PERIOD_PATTERN = Pattern.compile("\"period\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern CLASS_ID_PATTERN = Pattern.compile("\"classId\"\\s*:\\s*(\\d+)");
    private static final Pattern TITLE_PATTERN = Pattern.compile("\"title\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern DUE_PATTERN = Pattern.compile("\"due\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern ASSIGNMENT_ID_PATTERN = Pattern.compile("\"assignmentId\"\\s*:\\s*(\\d+)");
    private static final Pattern GRADE_PATTERN = Pattern.compile("\"grade\"\\s*:\\s*([0-9.]+)");
    private static final Pattern PROVIDER_PATTERN = Pattern.compile("\"provider\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern FIELD1_PATTERN = Pattern.compile("\"field1\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern FIELD2_PATTERN = Pattern.compile("\"field2\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern GOAL_ID_PATTERN = Pattern.compile("\"goalId\"\\s*:\\s*(\\d+)");
    private static final Pattern STATUS_PATTERN = Pattern.compile("\"status\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern NOTES_PATTERN = Pattern.compile("\"notes\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern FILE_ID_PATTERN = Pattern.compile("\"fileId\"\\s*:\\s*(\\d+)");
    private static final Pattern TYPE_PATTERN = Pattern.compile("\"type\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("\"category\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern NOTIFICATION_ID_PATTERN = Pattern.compile("\"notificationId\"\\s*:\\s*(\\d+)");
    private static final Pattern EVENT_ID_PATTERN = Pattern.compile("\"eventId\"\\s*:\\s*(\\d+)");
    private static final Pattern LOCATION_PATTERN = Pattern.compile("\"location\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern START_TIME_PATTERN = Pattern.compile("\"startTime\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern END_TIME_PATTERN = Pattern.compile("\"endTime\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern SCHOOLOGY_COURSE_ID_PATTERN = Pattern.compile("\"schoologyCourseId\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern FIELD3_PATTERN = Pattern.compile("\"field3\"\\s*:\\s*\"([^\"]*)\"");

    public static void main(String[] args) throws IOException, SQLException {
        AccountService db = new AccountService();
        ClassService classService = new ClassService();
        IntegrationService integrationService = new IntegrationService();
        GoalService goalService = new GoalService();
        EventService eventService = new EventService();
        SyncService syncService = new SyncService(classService, integrationService);
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        String[] candidates = {"python", "py", "python3"};
        String pythonCommand = null;

        for (String candidate : candidates) {
            try {
                new ProcessBuilder(candidate, "--version").start().waitFor();
                pythonCommand = candidate;
                break;
            } catch (IOException e) {
                // try next candidate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (pythonCommand == null) {
            throw new IOException("Could not find a working Python installation (tried: python, py, python3).");
        }

        ProcessBuilder pb = new ProcessBuilder(pythonCommand, "-m", "Backend.Login.email_verification");
        pb.inheritIO();
        Process flaskProcess = pb.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            flaskProcess.destroy();
            System.out.println("Flask server stopped.");
        }));

        server.createContext("/api/login", exchange -> handleLogin(exchange, db));
        server.createContext("/api/register", exchange -> handleRegisterStep1(exchange, db));
        server.createContext("/api/complete-profile", exchange -> handleRegisterStep2(exchange, db));
        server.createContext("/api/settings/save", exchange -> handleSettingsSave(exchange, db));
        server.createContext("/api/settings/get", exchange -> handleSettingsGet(exchange, db));
        server.createContext("/api/classes/list", exchange -> handleClassesList(exchange, db, classService));
        server.createContext("/api/classes/add", exchange -> handleClassesAdd(exchange, db, classService));
        server.createContext("/api/classes/remove", exchange -> handleClassesRemove(exchange, db, classService));
        server.createContext("/api/assignments/add", exchange -> handleAssignmentsAdd(exchange, db, classService));
        server.createContext("/api/assignments/remove",
                exchange -> handleAssignmentsRemove(exchange, db, classService));
        server.createContext("/api/assignments/grade", exchange -> handleAssignmentsGrade(exchange, db, classService));
        server.createContext("/api/assignments/complete",
                exchange -> handleAssignmentsComplete(exchange, db, classService));
        server.createContext("/api/integrations/save",
                exchange -> handleIntegrationsSave(exchange, db, integrationService));
        server.createContext("/api/integrations/status",
                exchange -> handleIntegrationsStatus(exchange, db, integrationService));
        server.createContext("/api/account/delete", exchange -> handleAccountDelete(exchange, db));
        server.createContext("/api/goals/list", exchange -> handleGoalsList(exchange, db, goalService));
        server.createContext("/api/goals/add", exchange -> handleGoalsAdd(exchange, db, goalService));
        server.createContext("/api/goals/update", exchange -> handleGoalsUpdate(exchange, db, goalService));
        server.createContext("/api/goals/complete", exchange -> handleGoalsComplete(exchange, db, goalService));
        server.createContext("/api/goals/remove", exchange -> handleGoalsRemove(exchange, db, goalService));
        server.createContext("/api/files/list", exchange -> handleFilesList(exchange, db, goalService));
        server.createContext("/api/files/add", exchange -> handleFilesAdd(exchange, db, goalService));
        server.createContext("/api/files/rename", exchange -> handleFilesRename(exchange, db, goalService));
        server.createContext("/api/files/remove", exchange -> handleFilesRemove(exchange, db, goalService));
        server.createContext("/api/notifications/list", exchange -> handleNotificationsList(exchange, db, goalService));
        server.createContext("/api/notifications/read", exchange -> handleNotificationsRead(exchange, db, goalService));
        server.createContext("/api/notifications/dismiss", exchange -> handleNotificationsDismiss(exchange, db, goalService));
        server.createContext("/api/notifications/clear", exchange -> handleNotificationsClear(exchange, db, goalService));
        server.createContext("/api/events/list", exchange -> handleEventsList(exchange, db, eventService));
        server.createContext("/api/events/add", exchange -> handleEventsAdd(exchange, db, eventService));
        server.createContext("/api/events/update", exchange -> handleEventsUpdate(exchange, db, eventService));
        server.createContext("/api/events/remove", exchange -> handleEventsRemove(exchange, db, eventService));
        server.createContext("/api/sync/canvas", exchange -> handleSyncCanvas(exchange, db, syncService));
        server.createContext("/api/sync/schoology", exchange -> handleSyncSchoology(exchange, db, syncService));
        server.createContext("/", Main::handleStaticFile);

        server.setExecutor(null);
        server.start();

        java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (int uid : integrationService.getAllConnectedUserIds("canvas")) {
                    try {
                        syncService.syncCanvas(uid);
                        System.out.println("Auto-synced Canvas for user " + uid);
                    } catch (Exception e) {
                        System.out.println("Canvas auto-sync failed for user " + uid + ": " + e.getMessage());
                    }
                }
                for (int uid : integrationService.getAllConnectedUserIds("schoology")) {
                    try {
                        syncService.syncSchoology(uid, null);
                        System.out.println("Auto-synced Schoology for user " + uid);
                    } catch (Exception e) {
                        System.out.println("Schoology auto-sync failed for user " + uid + ": " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 30, 30, java.util.concurrent.TimeUnit.SECONDS);

        System.out.println("StudyStack is running at http://localhost:" + PORT + "/login.html");
    }

    private static void handleLogin(HttpExchange exchange, AccountService db) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false,\"message\":\"Use POST for login.\"}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String password = getJsonValue(body, PASSWORD_PATTERN);
        try {
            if (db.login(email, password)) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Login successful.\"}");
            } else {
                sendJson(exchange, 401, "{\"success\":false,\"message\":\"Invalid email or password.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error.\"}");
        }
    }

    private static void handleRegisterStep1(HttpExchange exchange, AccountService db) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false,\"message\":\"Use POST for registration.\"}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String password = getJsonValue(body, PASSWORD_PATTERN);
        try {
            int userId = db.createAccount(email, password);
            sendJson(exchange, 200, "{\"success\":true,\"userId\":" + userId + "}");
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, 0);
            sendJson(exchange, 500,
                    "{\"success\":false,\"message\":\"Registration failed. Email may already be in use.\"}");
        }
    }

    private static void handleRegisterStep2(HttpExchange exchange, AccountService db) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false,\"message\":\"Use POST for this step.\"}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String userIdStr = getJsonValue(body, USER_ID_PATTERN);
        String username = getJsonValue(body, USERNAME_PATTERN);
        String displayName = getJsonValue(body, DISPLAY_NAME_PATTERN);
        try {
            int userId = Integer.parseInt(userIdStr);
            db.completeProfile(userId, username, displayName);
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Profile completed.\"}");
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Failed to complete profile.\"}");
        } catch (NumberFormatException e) {
            sendJson(exchange, 400, "{\"success\":false,\"message\":\"Invalid user ID.\"}");
        }
    }

    private static void handleSettingsGet(HttpExchange exchange, AccountService db) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            sendJson(exchange, 200, db.getSettings(userId).toString());
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleSettingsSave(HttpExchange exchange, AccountService db) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String displayName = getJsonValue(body, DISPLAY_NAME_PATTERN);
        String notificationsStr = getJsonValue(body, NOTIFICATIONS_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            db.saveSettings(userId, displayName, Boolean.parseBoolean(notificationsStr));
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleIntegrationsSave(HttpExchange exchange, AccountService db,
            IntegrationService integrationService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String provider = getJsonValue(body, PROVIDER_PATTERN);
        String field1 = getJsonValue(body, FIELD1_PATTERN);
        String field2 = getJsonValue(body, FIELD2_PATTERN);
        String field3 = getJsonValue(body, FIELD3_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            integrationService.saveIntegration(userId, provider, field1, field2, field3);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Failed to save integration.\"}");
        }
    }

    private static void handleIntegrationsStatus(HttpExchange exchange, AccountService db,
            IntegrationService integrationService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            sendJson(exchange, 200, integrationService.getIntegrationStatus(userId).toString());
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleAccountDelete(HttpExchange exchange, AccountService db) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            if (userId == -1) {
                sendJson(exchange, 404, "{\"success\":false,\"message\":\"User not found.\"}");
                return;
            }
            db.deleteAccount(userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Failed to delete account.\"}");
        }
    }

    private static void handleClassesList(HttpExchange exchange, AccountService db, ClassService classService)
            throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            if (userId == -1) {
                sendJson(exchange, 404, "{\"success\":false,\"message\":\"User not found.\"}");
                return;
            }
            sendJson(exchange, 200, classService.getClassesWithAssignments(userId).toString());
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error.\"}");
        }
    }

    private static void handleClassesAdd(HttpExchange exchange, AccountService db, ClassService classService)
            throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String name = getJsonValue(body, NAME_PATTERN);
        String period = getJsonValue(body, PERIOD_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            if (userId == -1) {
                sendJson(exchange, 404, "{\"success\":false,\"message\":\"User not found.\"}");
                return;
            }
            int classId = classService.addClass(userId, name, period);
            sendJson(exchange, 200, "{\"success\":true,\"classId\":" + classId + "}");
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error.\"}");
        }
    }

    private static void handleClassesRemove(HttpExchange exchange, AccountService db, ClassService classService)
            throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String classIdStr = getJsonValue(body, CLASS_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int classId = Integer.parseInt(classIdStr);
            classService.removeClass(classId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error.\"}");
        }
    }

    private static void handleAssignmentsAdd(HttpExchange exchange, AccountService db, ClassService classService)
            throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String classIdStr = getJsonValue(body, CLASS_ID_PATTERN);
        String title = getJsonValue(body, TITLE_PATTERN);
        String due = getJsonValue(body, DUE_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int classId = Integer.parseInt(classIdStr);
            int assignmentId = classService.addAssignment(userId, classId, title, due);
            sendJson(exchange, 200, "{\"success\":true,\"assignmentId\":" + assignmentId + "}");
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error.\"}");
        }
    }

    private static void handleAssignmentsRemove(HttpExchange exchange, AccountService db, ClassService classService)
            throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String assignmentIdStr = getJsonValue(body, ASSIGNMENT_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int assignmentId = Integer.parseInt(assignmentIdStr);
            classService.removeAssignment(assignmentId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error.\"}");
        }
    }

    private static void handleAssignmentsGrade(HttpExchange exchange, AccountService db, ClassService classService)
            throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String assignmentIdStr = getJsonValue(body, ASSIGNMENT_ID_PATTERN);
        String gradeStr = getJsonValue(body, GRADE_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int assignmentId = Integer.parseInt(assignmentIdStr);
            double grade = Double.parseDouble(gradeStr);
            classService.setAssignmentGrade(assignmentId, userId, grade);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error.\"}");
        }
    }

    private static void handleAssignmentsComplete(HttpExchange exchange, AccountService db, ClassService classService)
            throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String assignmentIdStr = getJsonValue(body, ASSIGNMENT_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int assignmentId = Integer.parseInt(assignmentIdStr);
            classService.toggleAssignmentComplete(assignmentId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Server error.\"}");
        }
    }

    private static void handleGoalsList(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            sendJson(exchange, 200, goalService.getGoals(userId).toString());
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleGoalsAdd(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String title = getJsonValue(body, TITLE_PATTERN);
        String due = getJsonValue(body, DUE_PATTERN);
        String notes = getJsonValue(body, NOTES_PATTERN);
        String status = getJsonValue(body, STATUS_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int goalId = goalService.addGoal(userId, title, due, notes, status);
            sendJson(exchange, 200, "{\"success\":true,\"goalId\":" + goalId + "}");
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleGoalsUpdate(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String goalIdStr = getJsonValue(body, GOAL_ID_PATTERN);
        String title = getJsonValue(body, TITLE_PATTERN);
        String due = getJsonValue(body, DUE_PATTERN);
        String notes = getJsonValue(body, NOTES_PATTERN);
        String status = getJsonValue(body, STATUS_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int goalId = Integer.parseInt(goalIdStr);
            goalService.updateGoal(goalId, userId, title, due, notes, status);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleGoalsComplete(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String goalIdStr = getJsonValue(body, GOAL_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int goalId = Integer.parseInt(goalIdStr);
            goalService.completeGoal(goalId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleGoalsRemove(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String goalIdStr = getJsonValue(body, GOAL_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int goalId = Integer.parseInt(goalIdStr);
            goalService.deleteGoal(goalId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleFilesList(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            sendJson(exchange, 200, goalService.getFiles(userId).toString());
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleFilesAdd(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String name = getJsonValue(body, NAME_PATTERN);
        String type = getJsonValue(body, TYPE_PATTERN);
        String category = getJsonValue(body, CATEGORY_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int fileId = goalService.addFile(userId, name, type, category);
            sendJson(exchange, 200, "{\"success\":true,\"fileId\":" + fileId + "}");
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleFilesRename(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String fileIdStr = getJsonValue(body, FILE_ID_PATTERN);
        String name = getJsonValue(body, NAME_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int fileId = Integer.parseInt(fileIdStr);
            goalService.renameFile(fileId, userId, name);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleFilesRemove(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String fileIdStr = getJsonValue(body, FILE_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int fileId = Integer.parseInt(fileIdStr);
            goalService.deleteFile(fileId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleNotificationsList(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            sendJson(exchange, 200, goalService.getNotifications(userId).toString());
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleNotificationsRead(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String notificationIdStr = getJsonValue(body, NOTIFICATION_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int notificationId = Integer.parseInt(notificationIdStr);
            goalService.markNotificationRead(notificationId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleNotificationsDismiss(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String notificationIdStr = getJsonValue(body, NOTIFICATION_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int notificationId = Integer.parseInt(notificationIdStr);
            goalService.dismissNotification(notificationId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleNotificationsClear(HttpExchange exchange, AccountService db, GoalService goalService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            goalService.clearAllNotifications(userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleEventsList(HttpExchange exchange, AccountService db, EventService eventService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            sendJson(exchange, 200, eventService.getEvents(userId).toString());
        } catch (SQLException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleEventsAdd(HttpExchange exchange, AccountService db, EventService eventService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String title = getJsonValue(body, TITLE_PATTERN);
        String notes = getJsonValue(body, NOTES_PATTERN);
        String location = getJsonValue(body, LOCATION_PATTERN);
        String startTime = getJsonValue(body, START_TIME_PATTERN);
        String endTime = getJsonValue(body, END_TIME_PATTERN);
        String classIdStr = getJsonValue(body, CLASS_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            Integer classId = classIdStr.isBlank() ? null : Integer.parseInt(classIdStr);
            int eventId = eventService.addEvent(userId, title, notes, location, startTime, endTime, classId);
            sendJson(exchange, 200, "{\"success\":true,\"eventId\":" + eventId + "}");
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleEventsUpdate(HttpExchange exchange, AccountService db, EventService eventService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String eventIdStr = getJsonValue(body, EVENT_ID_PATTERN);
        String title = getJsonValue(body, TITLE_PATTERN);
        String notes = getJsonValue(body, NOTES_PATTERN);
        String location = getJsonValue(body, LOCATION_PATTERN);
        String startTime = getJsonValue(body, START_TIME_PATTERN);
        String endTime = getJsonValue(body, END_TIME_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int eventId = Integer.parseInt(eventIdStr);
            eventService.updateEvent(eventId, userId, title, notes, location, startTime, endTime);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    private static void handleEventsRemove(HttpExchange exchange, AccountService db, EventService eventService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String eventIdStr = getJsonValue(body, EVENT_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            int eventId = Integer.parseInt(eventIdStr);
            eventService.deleteEvent(eventId, userId);
            sendJson(exchange, 200, "{\"success\":true}");
        } catch (SQLException | NumberFormatException e) {
            sendJson(exchange, 500, "{\"success\":false}");
        }
    }

    
    private static void handleSyncCanvas(HttpExchange exchange, AccountService db, SyncService syncService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            syncService.syncCanvas(userId);
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Canvas sync completed.\"}");
        } catch (IllegalStateException e) {
            sendJson(exchange, 400, "{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Canvas sync failed.\"}");
        }
    }

    private static void handleSyncSchoology(HttpExchange exchange, AccountService db, SyncService syncService) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"success\":false}");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String email = getJsonValue(body, EMAIL_PATTERN);
        String schoologyCourseId = getJsonValue(body, SCHOOLOGY_COURSE_ID_PATTERN);
        try {
            int userId = db.getUserIdByEmail(email);
            syncService.syncSchoology(userId, schoologyCourseId);
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Schoology sync completed.\"}");
        } catch (IllegalStateException e) {
            sendJson(exchange, 400, "{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{\"success\":false,\"message\":\"Schoology sync failed.\"}");
        }
    }

    private static void handleStaticFile(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method not allowed", "text/plain");
            return;
        }
        Path frontendRoot = Path.of("Frontend", "Landing").toAbsolutePath().normalize();
        URI requestUri = exchange.getRequestURI();
        String requestedPath = requestUri.getPath().equals("/") ? "/login.html" : requestUri.getPath();
        Path filePath = frontendRoot.resolve(requestedPath.substring(1)).normalize();
        if (!filePath.startsWith(frontendRoot) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendText(exchange, 404, "Not found", "text/plain");
            return;
        }
        byte[] fileBytes = Files.readAllBytes(filePath);
        String ct = contentType(filePath);
        exchange.getResponseHeaders().set("Content-Type", ct);
        if (ct.startsWith("text/html") || ct.startsWith("application/javascript") || ct.startsWith("text/css")) {
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
        }
        exchange.sendResponseHeaders(200, fileBytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(fileBytes);
        }
    }

    private static String getJsonValue(String json, Pattern pattern) {
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        sendText(exchange, statusCode, json, "application/json");
    }

    private static void sendText(HttpExchange exchange, int statusCode, String text, String contentType)
            throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static String contentType(Path path) {
        String fileName = path.getFileName().toString();
        if (fileName.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (fileName.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (fileName.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        }
        return "application/octet-stream";
    }
}


