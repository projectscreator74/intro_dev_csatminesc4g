import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;

public class ClassService {
  private Connection conn;

  public ClassService() throws SQLException {
    String url = "jdbc:postgresql://localhost:5432/studystack?options=-c%20TimeZone=UTC";
    conn = DriverManager.getConnection(url, "postgres", "devpassword123");
  }

  // ===== Generic sync methods (used by Canvas and Schoology alike) =====

  public int syncCourse(int userId, String extId, String name) throws SQLException {
    String checkSql = "SELECT class_id FROM class WHERE ext_id = ? AND user_id = ?";
    try (PreparedStatement check = conn.prepareStatement(checkSql)) {
      check.setString(1, extId);
      check.setInt(2, userId);
      ResultSet rs = check.executeQuery();
      if (rs.next()) {
        return rs.getInt("class_id");
      }
    }

    String insertSql = "INSERT INTO class (user_id, class_name, ext_id) VALUES (?, ?, ?) RETURNING class_id";
    try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
      insert.setInt(1, userId);
      insert.setString(2, name);
      insert.setString(3, extId);
      ResultSet rs = insert.executeQuery();
      if (rs.next()) {
        return rs.getInt("class_id");
      }
    }

    throw new SQLException("Failed to sync course.");
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

    public void syncAssignment(int userId, int classId, String extId, String title, double totalPoints, String dueTimestamp) throws SQLException {
        String checkSql = "SELECT assignment_id FROM assignment WHERE ext_id = ? AND user_id = ?";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, extId);
            check.setInt(2, userId);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                return;
            }
        }

        String insertSql = "INSERT INTO assignment (user_id, class_id, title, total_points, ext_id, timedate) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
            insert.setInt(1, userId);
            insert.setInt(2, classId);
            insert.setString(3, title);
            insert.setDouble(4, totalPoints);
            insert.setString(5, extId);

            if (dueTimestamp == null || dueTimestamp.isBlank()) {
                insert.setNull(6, java.sql.Types.TIMESTAMP);
            } else {
                insert.setTimestamp(6, Timestamp.valueOf(dueTimestamp));
            }

            insert.executeUpdate();
        }
    }

  // ===== Frontend-facing CRUD methods =====

  public JSONArray getClassesWithAssignments(int userId) throws SQLException {
    JSONArray result = new JSONArray();

    String classSql = "SELECT class_id, class_name, period FROM class WHERE user_id = ? ORDER BY class_id";
    try (PreparedStatement stmt = conn.prepareStatement(classSql)) {
      stmt.setInt(1, userId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        JSONObject cls = new JSONObject();
        int classId = rs.getInt("class_id");
        cls.put("id", classId);
        cls.put("name", rs.getString("class_name"));
        cls.put("period", rs.getString("period") == null ? "" : rs.getString("period"));
        cls.put("assignments", getAssignmentsForClass(classId));
        result.put(cls);
      }
    }

    return result;
  }

  private JSONArray getAssignmentsForClass(int classId) throws SQLException {
    JSONArray assignments = new JSONArray();
    String sql = "SELECT assignment_id, title, timedate, due_label, user_score, completed FROM assignment WHERE class_id = ? ORDER BY assignment_id";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, classId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        JSONObject a = new JSONObject();
        a.put("id", rs.getInt("assignment_id"));
        a.put("title", rs.getString("title"));

        String dueLabel = rs.getString("due_label");
        if (dueLabel != null && !dueLabel.isBlank()) {
          a.put("due", dueLabel);
        } else {
          Timestamp ts = rs.getTimestamp("timedate");
          a.put("due", ts == null ? "" : ts.toString());
        }

        double score = rs.getDouble("user_score");
        a.put("grade", rs.wasNull() ? JSONObject.NULL : score);

        a.put("completed", rs.getBoolean("completed"));
        assignments.put(a);
      }
    }

    return assignments;
  }

  public int addClass(int userId, String name, String period) throws SQLException {
    String sql = "INSERT INTO class (user_id, class_name, period) VALUES (?, ?, ?) RETURNING class_id";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, userId);
      stmt.setString(2, name);
      stmt.setString(3, period);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("class_id");
      }
    }
    throw new SQLException("Failed to add class.");
  }

  public void removeClass(int classId, int userId) throws SQLException {
    String deleteAssignments = "DELETE FROM assignment WHERE class_id = ? AND user_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(deleteAssignments)) {
      stmt.setInt(1, classId);
      stmt.setInt(2, userId);
      stmt.executeUpdate();
    }

    String deleteClass = "DELETE FROM class WHERE class_id = ? AND user_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(deleteClass)) {
      stmt.setInt(1, classId);
      stmt.setInt(2, userId);
      stmt.executeUpdate();
    }
  }

  public int addAssignment(int userId, int classId, String title, String due) throws SQLException {
    String sql = "INSERT INTO assignment (user_id, class_id, title, due_label, total_points, completed) VALUES (?, ?, ?, ?, 100, false) RETURNING assignment_id";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, userId);
      stmt.setInt(2, classId);
      stmt.setString(3, title);
      stmt.setString(4, due);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt("assignment_id");
      }
    }
    throw new SQLException("Failed to add assignment.");
  }

  public void removeAssignment(int assignmentId, int userId) throws SQLException {
    String sql = "DELETE FROM assignment WHERE assignment_id = ? AND user_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, assignmentId);
      stmt.setInt(2, userId);
      stmt.executeUpdate();
    }
  }

  public void setAssignmentGrade(int assignmentId, int userId, double grade) throws SQLException {
    String sql = "UPDATE assignment SET user_score = ? WHERE assignment_id = ? AND user_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setDouble(1, grade);
      stmt.setInt(2, assignmentId);
      stmt.setInt(3, userId);
      stmt.executeUpdate();
    }
  }

  public void toggleAssignmentComplete(int assignmentId, int userId) throws SQLException {
    String sql = "UPDATE assignment SET completed = NOT completed WHERE assignment_id = ? AND user_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, assignmentId);
      stmt.setInt(2, userId);
      stmt.executeUpdate();
    }
  }
}