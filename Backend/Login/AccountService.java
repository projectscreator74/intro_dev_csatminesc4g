import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountService {
    private Connection conn;

    public AccountService() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/studystack";
        conn = DriverManager.getConnection(url, "postgres", "devpassword123");
    }

    public boolean login(String email, String password) throws SQLException {
        System.out.println("!!!!! LOGIN METHOD WAS CALLED !!!!!");
        String sql = "SELECT password FROM account WHERE email_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                System.out.println("Stored password: [" + storedPassword + "]");
                System.out.println("Given password: [" + password + "]");
                System.out.println("Match: " + storedPassword.equals(password));
                return storedPassword.equals(password);
            } else {
                System.out.println("No account found for email: [" + email + "]");
            }
        }

        return false;
    }
}