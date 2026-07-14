import org.json.JSONArray;

public class TestCanvasParsing {
    public static void main(String[] args) {
        String sampleJson = "[{\"id\":1,\"name\":\"AP Calculus BC\",\"course_code\":\"CALC101\"}," +
                             "{\"id\":2,\"name\":\"AP Chemistry\",\"course_code\":\"CHEM101\"}]";

        org.json.JSONArray courses = new org.json.JSONArray(sampleJson);

        for (int i = 0; i < courses.length(); i++) {
            org.json.JSONObject course = courses.getJSONObject(i);
            System.out.println("Course ID: " + course.getInt("id"));
            System.out.println("Name: " + course.getString("name"));
            System.out.println("Code: " + course.getString("course_code"));
            System.out.println("---");
        }
    }
}

