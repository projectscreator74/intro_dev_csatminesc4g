import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import org.json.JSONArray;
import org.json.JSONObject;

public class GoalService {
    private Connection conn;

    public GoalService() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/studystack?options=-c%20TimeZone=UTC";
        conn = DriverManager.getConnection(url, "postgres", "devpassword123");
    }

    private int getStatusId(String statusName) throws SQLException {
        String normalized = statusName.toLowerCase().replace(" ", "_");
        String sql = "SELECT status_id FROM status WHERE status_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalized);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("status_id");
            }
        }
        return -1;
    }

    private String getStatusName(int statusId) throws SQLException {
        String sql = "SELECT status_name FROM status WHERE status_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status_name");
            }
        }
        return "not_started";
    }

    // ===== Goals =====

    public JSONArray getGoals(int userId) throws SQLException {
        JSONArray result = new JSONArray();
        String sql = "SELECT goal_id, title, timedate, notes, status_id FROM goals WHERE user_id = ? ORDER BY goal_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject g = new JSONObject();
                g.put("id", rs.getInt("goal_id"));
                g.put("title", rs.getString("title"));
                Date due = rs.getDate("timedate");
                g.put("date", due == null ? "" : due.toString());
                g.put("notes", rs.getString("notes") == null ? "" : rs.getString("notes"));
                g.put("status", getStatusName(rs.getInt("status_id")));
                result.put(g);
            }
        }
        return result;
    }

    public int addGoal(int userId, String title, String due, String notes, String status) throws SQLException {
        int statusId = getStatusId(status);
        String sql = "INSERT INTO goals (user_id, title, timedate, notes, status_id) VALUES (?, ?, ?, ?, ?) RETURNING goal_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            if (due == null || due.isBlank()) {
                stmt.setNull(3, java.sql.Types.DATE);
            } else {
                stmt.setDate(3, Date.valueOf(due));
            }
            stmt.setString(4, notes);
            stmt.setInt(5, statusId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("goal_id");
            }
        }
        throw new SQLException("Failed to add goal.");
    }

    public void updateGoal(int goalId, int userId, String title, String due, String notes, String status) throws SQLException {
        int statusId = getStatusId(status);
        String sql = "UPDATE goals SET title = ?, timedate = ?, notes = ?, status_id = ? WHERE goal_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            if (due == null || due.isBlank()) {
                stmt.setNull(2, java.sql.Types.DATE);
            } else {
                stmt.setDate(2, Date.valueOf(due));
            }
            stmt.setString(3, notes);
            stmt.setInt(4, statusId);
            stmt.setInt(5, goalId);
            stmt.setInt(6, userId);
            stmt.executeUpdate();
        }
    }

    public void completeGoal(int goalId, int userId) throws SQLException {
        int statusId = getStatusId("completed");
        String sql = "UPDATE goals SET status_id = ? WHERE goal_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            stmt.setInt(2, goalId);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        }
    }

    public void deleteGoal(int goalId, int userId) throws SQLException {
        String sql = "DELETE FROM goals WHERE goal_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, goalId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    // ===== Files =====

    public JSONArray getFiles(int userId) throws SQLException {
        JSONArray result = new JSONArray();
        String sql = "SELECT file_id, file_name, file_type, category FROM file WHERE user_id = ? ORDER BY file_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject f = new JSONObject();
                f.put("id", rs.getInt("file_id"));
                f.put("name", rs.getString("file_name"));
                f.put("type", rs.getString("file_type"));
                f.put("category", rs.getString("category"));
                result.put(f);
            }
        }
        return result;
    }

    public int addFile(int userId, String name, String type, String category) throws SQLException {
        String sql = "INSERT INTO file (user_id, file_name, file_type, category) VALUES (?, ?, ?, ?) RETURNING file_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, type);
            stmt.setString(4, category);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("file_id");
            }
        }
        throw new SQLException("Failed to add file.");
    }

    public void renameFile(int fileId, int userId, String newName) throws SQLException {
        String sql = "UPDATE file SET file_name = ? WHERE file_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, fileId);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        }
    }

    public void deleteFile(int fileId, int userId) throws SQLException {
        String sql = "DELETE FROM file WHERE file_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fileId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    // ===== Notifications =====

    public JSONArray getNotifications(int userId) throws SQLException {
        JSONArray result = new JSONArray();
        String sql = "SELECT notification_id, title, notes, notif_color, is_read, timedate FROM notifications WHERE user_id = ? ORDER BY notification_id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject n = new JSONObject();
                n.put("id", rs.getInt("notification_id"));
                n.put("title", rs.getString("title"));
                n.put("message", rs.getString("notes") == null ? "" : rs.getString("notes"));
                n.put("type", rs.getString("notif_color") == null ? "blue" : rs.getString("notif_color"));
                n.put("read", rs.getBoolean("is_read"));
                java.sql.Timestamp ts = rs.getTimestamp("timedate");
                n.put("time", ts == null ? "" : ts.toString());
                result.put(n);
            }
        }
        return result;
    }

    public void markNotificationRead(int notificationId, int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = true WHERE notification_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void dismissNotification(int notificationId, int userId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE notification_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void clearAllNotifications(int userId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}