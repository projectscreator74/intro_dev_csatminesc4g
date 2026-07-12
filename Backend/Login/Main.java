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

    public static void main(String[] args) throws IOException, SQLException {
        AccountService db = new AccountService();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        ProcessBuilder pb = new ProcessBuilder("python", "-m", "Backend.Login.email_verification");
        pb.inheritIO();
        Process flaskProcess = pb.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            flaskProcess.destroy();
            System.out.println("Flask server stopped.");
        }));

        server.createContext("/api/login", exchange -> handleLogin(exchange, db));
        server.createContext("/api/register", exchange -> handleRegisterStep1(exchange, db));
        server.createContext("/api/complete-profile", exchange -> handleRegisterStep2(exchange, db));
        server.createContext("/", Main::handleStaticFile);

        server.setExecutor(null);
        server.start();

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

    private static void handleStaticFile(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method not allowed", "text/plain");
            return;
        }

        Path frontendRoot = Path.of("..", "..", "Frontend", "Landing").toAbsolutePath().normalize();
        URI requestUri = exchange.getRequestURI();
        String requestedPath = requestUri.getPath().equals("/") ? "/login.html" : requestUri.getPath();
        Path filePath = frontendRoot.resolve(requestedPath.substring(1)).normalize();

        if (!filePath.startsWith(frontendRoot) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendText(exchange, 404, "Not found", "text/plain");
            return;
        }

        byte[] fileBytes = Files.readAllBytes(filePath);
        exchange.getResponseHeaders().set("Content-Type", contentType(filePath));
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
