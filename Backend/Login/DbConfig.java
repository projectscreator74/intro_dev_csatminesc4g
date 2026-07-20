import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {

    public static Connection getConnection() throws SQLException {
        String host = getEnvOrDefault("DB_HOST", "localhost");
        String port = getEnvOrDefault("DB_PORT", "5432");
        String dbName = getEnvOrDefault("DB_NAME", "studystack");
        String user = getEnvOrDefault("DB_USER", "postgres");
        String password = getEnvOrDefault("DB_PASSWORD", "devpassword123");

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName + "?options=-c%20TimeZone=UTC";
        return DriverManager.getConnection(url, user, password);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}