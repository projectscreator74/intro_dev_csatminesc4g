# intro_dev_csatminesc4g

## Run StudyStack

This project uses one simple Java server for both the backend login API and the frontend pages.

```bash
cd Backend/Login
javac Main.java User.java UserDatabase.java
cd ../..
java -cp Backend/Login Main
```

Then open:

```text
http://localhost:8080/login.html
```

Try this test account:

```text
email: student@school.com
password: student123
```
