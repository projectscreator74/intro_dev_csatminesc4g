public class Main {

    public static void main(String[] args) {

        UserDatabase db = new UserDatabase();

        boolean success = db.login(
                "students@school.com",
                "student123"
        );

        if (success) {
            System.out.println("Login Successful");
        } else {
            System.out.println("Invalid Login");
        }

    }

}