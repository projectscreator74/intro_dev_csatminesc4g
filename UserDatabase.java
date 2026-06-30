import java.util.ArrayList;

public class UserDatabase {

    private final ArrayList<User> users = new ArrayList<>();

    public UserDatabase() {

        users.add(new User("teacher@school.com", "teacher123"));
        users.add(new User("student@school.com", "student123"));
        users.add(new User("admin@school.com", "admin123"));

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
}