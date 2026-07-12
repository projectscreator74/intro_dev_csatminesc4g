import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

public class ClassService {
    private Connection conn;

    public ClassService() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/studystack?options=-c%20TimeZone=UTC";
        conn = DriverManager.getConnection(url, "postgres", "devpassword123");
    }

    public void syncCourse(int userId, JSONObject canvasCourse) throws SQLException {
        String canvasId = String.valueOf(canvasCourse.getInt("id"));
        String name = canvasCourse.getString("name");

        String checkSql = "SELECT class_id FROM class WHERE ext_id = ? AND user_id = ?";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, canvasId);
            check.setInt(2, userId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                return;
            }
        }

        String insertSql = "INSERT INTO class (user_id, class_name, ext_id) VALUES (?, ?, ?)";
        try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
            insert.setInt(1, userId);
            insert.setString(2, name);
            insert.setString(3, canvasId);
            insert.executeUpdate();
        }
    }

    public int getClassIdByExtId(String extId, int userId) throws SQLException {
        String sql = "SELECT class_id FROM class WHERE ext_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, extId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("class_id");
            }
        }
        return -1;
    }

    public void syncAssignment(int userId, int classId, JSONObject canvasAssignment) throws SQLException {
        String canvasId = String.valueOf(canvasAssignment.getInt("id"));
        String title = canvasAssignment.getString("name");
        double totalPoints = canvasAssignment.optDouble("points_possible", 0);

        String checkSql = "SELECT assignment_id FROM assignment WHERE ext_id = ? AND user_id = ?";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, canvasId);
            check.setInt(2, userId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                return;
            }
        }

        String insertSql = "INSERT INTO assignment (user_id, class_id, title, total_points, ext_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
            insert.setInt(1, userId);
            insert.setInt(2, classId);
            insert.setString(3, title);
            insert.setDouble(4, totalPoints);
            insert.setString(5, canvasId);
            insert.executeUpdate();
        }
    }
}