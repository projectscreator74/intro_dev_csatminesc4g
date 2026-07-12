import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class AccountService {
    private Connection conn;

    public AccountService() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/studystack?options=-c%20TimeZone=UTC";
        conn = DriverManager.getConnection(url, "postgres", "devpassword123");
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
}