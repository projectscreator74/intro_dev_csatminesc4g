import org.json.JSONArray;
import org.json.JSONObject;

public class SyncService {

    private final ClassService classService;
    private final IntegrationService integrationService;

    public SyncService(ClassService classService, IntegrationService integrationService) {
        this.classService = classService;
        this.integrationService = integrationService;
    }

    public void syncCanvas(int userId) throws Exception {
        JSONObject creds = integrationService.getCredentials(userId, "canvas");
        if (creds == null) {
            throw new IllegalStateException("Canvas is not connected for this user.");
        }

        CanvasService canvasService = new CanvasService(creds.getString("field1"), creds.getString("field2"));
        JSONArray courses = canvasService.getCourses();

        for (int i = 0; i < courses.length(); i++) {
            JSONObject course = courses.getJSONObject(i);
            int classId = syncCanvasCourse(userId, course);

            JSONArray assignments = canvasService.getAssignments(course.getLong("id"));
            for (int j = 0; j < assignments.length(); j++) {
                syncCanvasAssignment(userId, classId, assignments.getJSONObject(j));
            }
        }
    }

    public void syncSchoology(int userId, String schoologyCourseIdOverride) throws Exception {
        JSONObject creds = integrationService.getCredentials(userId, "schoology");
        if (creds == null) {
            throw new IllegalStateException("Schoology is not connected for this user.");
        }

        String courseId = (schoologyCourseIdOverride != null && !schoologyCourseIdOverride.isBlank())
                ? schoologyCourseIdOverride
                : creds.getString("field3");

        if (courseId == null || courseId.isBlank()) {
            throw new IllegalStateException("No Schoology course ID is set for this user.");
        }

        SchoologyService schoologyService = new SchoologyService(creds.getString("field1"), creds.getString("field2"));
        JSONArray sections = schoologyService.getSections(courseId);

        for (int i = 0; i < sections.length(); i++) {
            JSONObject section = sections.getJSONObject(i);
            int classId = syncSchoologySection(userId, section);

            JSONArray assignments = schoologyService.getAssignments(String.valueOf(section.getInt("id")));
            for (int j = 0; j < assignments.length(); j++) {
                syncSchoologyAssignment(userId, classId, assignments.getJSONObject(j));
            }
        }
    }

    public int syncCanvasCourse(int userId, JSONObject course) throws Exception {
        String extId = "canvas-" + course.getInt("id");
        String name = course.getString("name");
        return classService.syncCourse(userId, extId, name);
    }

    public void syncCanvasAssignment(int userId, int classId, JSONObject assignment) throws Exception {
        String extId = "canvas-" + assignment.getInt("id");
        String title = assignment.getString("name");
        double totalPoints = assignment.optDouble("points_possible", 0);

        String dueAt = assignment.optString("due_at", null);
        String normalizedDue = null;
        if (dueAt != null && !dueAt.isBlank()) {
            normalizedDue = dueAt.replace("T", " ").replace("Z", "");
        }

        classService.syncAssignment(userId, classId, extId, title, totalPoints, normalizedDue);
    }

    public int syncSchoologySection(int userId, JSONObject section) throws Exception {
        String extId = "schoology-" + section.getInt("id");
        String name = section.optString("section_title", section.optString("course_title", "Untitled Course"));
        return classService.syncCourse(userId, extId, name);
    }

    public void syncSchoologyAssignment(int userId, int classId, JSONObject assignment) throws Exception {
        String extId = "schoology-" + assignment.getInt("id");
        String title = assignment.getString("title");
        double totalPoints = assignment.optDouble("max_points", 0);
        String due = assignment.optString("due", null);

        classService.syncAssignment(userId, classId, extId, title, totalPoints, due);
    }
}