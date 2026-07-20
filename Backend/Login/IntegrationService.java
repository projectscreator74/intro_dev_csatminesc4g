import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class IntegrationService {
    private Connection conn;

    public IntegrationService() throws SQLException {
        conn = DbConfig.getConnection();
    }

    public void saveIntegration(int userId, String provider, String field1, String field2, String field3) throws SQLException {

        String insertIntCred = "INSERT INTO integration_credentials (user_id, provider, field_1, field_2, field_3) " +
                        "VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT (user_id, provider) " +
                        "DO UPDATE SET field_1 = EXCLUDED.field_1, field_2 = EXCLUDED.field_2, field_3 = EXCLUDED.field_3";

        try (PreparedStatement stmt = conn.prepareStatement(insertIntCred)) {
            stmt.setInt(1, userId);
            stmt.setString(2, provider);
            stmt.setString(3, field1);
            stmt.setString(4, field2);
            stmt.setString(5, field3);
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
        String sql = "SELECT field_1, field_2, field_3 FROM integration_credentials WHERE user_id = ? AND provider = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, provider);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JSONObject result = new JSONObject();
                result.put("field1", rs.getString("field_1"));
                result.put("field2", rs.getString("field_2"));
                result.put("field3", rs.getString("field_3") == null ? "" : rs.getString("field_3"));
                return result;
            }
        }

        return null;
    }

    public List<Integer> getAllConnectedUserIds(String provider) throws SQLException {
        List<Integer> result = new ArrayList<>();
        String sql = "SELECT user_id FROM integration_credentials WHERE provider = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, provider);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(rs.getInt("user_id"));
            }
        }

        return result;
    }
}
