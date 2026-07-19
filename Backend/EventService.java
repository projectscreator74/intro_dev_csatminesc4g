import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;

public class EventService {
    private Connection conn;

    public EventService() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/studystack?options=-c%20TimeZone=UTC";
        conn = DriverManager.getConnection(url, "postgres", "devpassword123");
    }

    public JSONArray getEvents(int userId) throws SQLException {
        JSONArray result = new JSONArray();
        String sql = "SELECT event_id, title, notes, location, estimated_hours, start_time, end_time, class_id FROM event WHERE user_id = ? ORDER BY start_time";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JSONObject e = new JSONObject();
                e.put("id", rs.getInt("event_id"));
                e.put("title", rs.getString("title"));
                e.put("notes", rs.getString("notes") == null ? "" : rs.getString("notes"));
                e.put("location", rs.getString("location") == null ? "" : rs.getString("location"));

                double hours = rs.getDouble("estimated_hours");
                e.put("estimatedHours", rs.wasNull() ? JSONObject.NULL : hours);

                Timestamp start = rs.getTimestamp("start_time");
                e.put("startTime", start == null ? "" : start.toString());

                Timestamp end = rs.getTimestamp("end_time");
                e.put("endTime", end == null ? "" : end.toString());

                int classId = rs.getInt("class_id");
                e.put("classId", rs.wasNull() ? JSONObject.NULL : classId);

                result.put(e);
            }
        }
        return result;
    }

    public int addEvent(int userId, String title, String notes, String location, String startTime, String endTime, Integer classId) throws SQLException {
        String sql = "INSERT INTO event (user_id, title, notes, location, start_time, end_time, class_id) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING event_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, notes);
            stmt.setString(4, location);

            if (startTime == null || startTime.isBlank()) {
                stmt.setNull(5, java.sql.Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(startTime));
            }

            if (endTime == null || endTime.isBlank()) {
                stmt.setNull(6, java.sql.Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(6, Timestamp.valueOf(endTime));
            }

            if (classId == null) {
                stmt.setNull(7, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(7, classId);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("event_id");
            }
        }
        throw new SQLException("Failed to add event.");
    }

    public void updateEvent(int eventId, int userId, String title, String notes, String location, String startTime, String endTime) throws SQLException {
        String sql = "UPDATE event SET title = ?, notes = ?, location = ?, start_time = ?, end_time = ? WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, notes);
            stmt.setString(3, location);

            if (startTime == null || startTime.isBlank()) {
                stmt.setNull(4, java.sql.Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(startTime));
            }

            if (endTime == null || endTime.isBlank()) {
                stmt.setNull(5, java.sql.Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(endTime));
            }

            stmt.setInt(6, eventId);
            stmt.setInt(7, userId);
            stmt.executeUpdate();
        }
    }

    public void deleteEvent(int eventId, int userId) throws SQLException {
        String sql = "DELETE FROM event WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}