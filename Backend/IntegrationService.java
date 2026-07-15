import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

public class IntegrationService {
    private Connection conn;

    public IntegrationService() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/studystack?options=-c%20TimeZone=UTC";
        conn = DriverManager.getConnection(url, "postgres", "devpassword123");
    }

    public void saveIntegration(int userId, String provider, String field1, String field2) throws SQLException {

        String insertIntCred = "INSERT INTO integration_credentials (user_id, provider, field_1, field_2) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON CONFLICT (user_id, provider) " +
                        "DO UPDATE SET field_1 = EXCLUDED.field_1, field_2 = EXCLUDED.field_2";

        try (PreparedStatement stmt = conn.prepareStatement(insertIntCred)) {
            stmt.setInt(1, userId);
            stmt.setString(2, provider);
            stmt.setString(3, field1);
            stmt.setString(4, field2);
            stmt.executeUpdate();
        }
    }

    public JSONObject getIntegrationStatus(int userId) throws SQLException {
        JSONObject result = new JSONObject();
        result.put("canvas", false);
        result.put("schoology", false);
        result.put("google", false);

        String statusQuerySql = "SELECT provider FROM integration_credentials WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(statusQuerySql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.put(rs.getString("provider"), true);
            }
        }

        return result;
    }

    public JSONObject getCredentials(int userId, String provider) throws SQLException {
        String sql = "SELECT field_1, field_2 FROM integration_credentials WHERE user_id = ? AND provider = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, provider);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JSONObject result = new JSONObject();
                result.put("field1", rs.getString("field_1"));
                result.put("field2", rs.getString("field_2"));
                return result;
            }
        }

        return null;
    }
}