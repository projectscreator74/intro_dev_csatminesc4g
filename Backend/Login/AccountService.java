import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

public class AccountService {
    private Connection conn;

    public AccountService() throws SQLException {
        conn = DbConfig.getConnection();
    }

    public boolean login(String email, String password) throws SQLException {
        String sql = "SELECT password FROM account WHERE email_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
        }
        return false;
    }

    public int getUserIdByEmail(String email) throws SQLException {
        String sql = "SELECT user_id FROM account WHERE email_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        return -1;
    }

    public int createAccount(String email, String password) throws SQLException {
        String sql = "INSERT INTO account (email_id, password) VALUES (?, ?) RETURNING user_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
        throw new SQLException("Account creation failed.");
    }

    public void completeProfile(int userId, String username, String displayName) throws SQLException {
        String updateAccountSql = "UPDATE account SET user_name = ? WHERE user_id = ?";
        String insertProfileSql = "INSERT INTO profile (user_id, display_name) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(updateAccountSql)) {
            stmt.setString(1, username);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertProfileSql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, displayName);
            stmt.executeUpdate();
        }
    }

    public JSONObject getSettings(int userId) throws SQLException {
        String sql = "SELECT display_name, notify_enabled FROM profile WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JSONObject result = new JSONObject();
                result.put("displayName", rs.getString("display_name"));
                result.put("notifications", rs.getBoolean("notify_enabled"));
                return result;
            }
        }
        JSONObject fallback = new JSONObject();
        fallback.put("displayName", "Student");
        fallback.put("notifications", true);
        return fallback;
    }

    public void saveSettings(int userId, String displayName, boolean notifications) throws SQLException {
        String sql = "UPDATE profile SET display_name = ?, notify_enabled = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, displayName);
            stmt.setBoolean(2, notifications);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        }
    }

    public Double getGradeBenchmark(int userId) throws SQLException {
        String sql = "SELECT grade_benchmark FROM profile WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double value = rs.getDouble("grade_benchmark");
                if (rs.wasNull()) {
                    return null;
                }
                return value;
            }
        }
        return null;
    }

    public void saveGradeBenchmark(int userId, Double benchmark) throws SQLException {
        String sql = "UPDATE profile SET grade_benchmark = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (benchmark == null) {
                stmt.setNull(1, java.sql.Types.NUMERIC);
            } else {
                stmt.setDouble(1, benchmark);
            }
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void deleteAccount(int userId) throws SQLException {
        String[] deleteStatements = {
                "DELETE FROM assignment WHERE user_id = ?",
                "DELETE FROM event WHERE user_id = ?",
                "DELETE FROM exam WHERE user_id = ?",
                "DELETE FROM goals WHERE user_id = ?",
                "DELETE FROM notifications WHERE user_id = ?",
                "DELETE FROM file WHERE user_id = ?",
                "DELETE FROM integration_credentials WHERE user_id = ?",
                "DELETE FROM class WHERE user_id = ?",
                "DELETE FROM profile WHERE user_id = ?",
                "DELETE FROM account WHERE user_id = ?"
        };

        for (String sql : deleteStatements) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
        }
    }
}
