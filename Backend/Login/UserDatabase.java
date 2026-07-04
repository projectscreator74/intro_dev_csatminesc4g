import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDatabase {

    private final ArrayList<User> users = new ArrayList<>();
    private static final Pattern USER_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\{\\s*\"password\"\\s*:\\s*\"([^\"]+)\"");

    public UserDatabase() {
        loadUsersFromJson();

        if (users.isEmpty()) {
            users.add(new User("teacher@school.com", "teacher123"));
            users.add(new User("student@school.com", "student123"));
            users.add(new User("admin@school.com", "admin123"));
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

     public boolean login(String email, String password) {

        for (User user : users) {

            if (user.getEmail().equals(email)
                    && user.getPassword().equals(password)) {
                return true;
            }

        }

        return false;
    }

    private void loadUsersFromJson() {
        Path usersPath = findUsersFile();

        if (usersPath == null) {
            return;
        }

        try {
            String json = Files.readString(usersPath, StandardCharsets.UTF_8);
            Matcher matcher = USER_PATTERN.matcher(json);

            while (matcher.find()) {
                users.add(new User(matcher.group(1), matcher.group(2)));
            }
        } catch (Exception error) {
            users.clear();
        }
    }

    private Path findUsersFile() {
        Path[] paths = {
                Path.of("Backend", "Login", "users.json"),
                Path.of("users.json")
        };

        for (Path path : paths) {
            if (Files.exists(path)) {
                return path;
            }
        }

        return null;
    }
}
