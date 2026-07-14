import org.json.JSONArray;
import org.json.JSONObject;

public class SyncService {

    private final ClassService classService;
    private final CanvasService canvasService;
    private final SchoologyService schoologyService;

    public SyncService(ClassService classService, CanvasService canvasService, SchoologyService schoologyService) {
        this.classService = classService;
        this.canvasService = canvasService;
        this.schoologyService = schoologyService;
    }

    // ===== Network-calling methods (untestable without live credentials) =====

    public void syncCanvas(int userId) throws Exception {
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

    public void syncSchoology(int userId, String schoologyCourseId) throws Exception {
        JSONArray sections = schoologyService.getSections(schoologyCourseId);

        for (int i = 0; i < sections.length(); i++) {
            JSONObject section = sections.getJSONObject(i);
            int classId = syncSchoologySection(userId, section);

            JSONArray assignments = schoologyService.getAssignments(String.valueOf(section.getInt("id")));
            for (int j = 0; j < assignments.length(); j++) {
                syncSchoologyAssignment(userId, classId, assignments.getJSONObject(j));
            }
        }
    }

    // ===== Field-extraction methods (fully testable with fake JSON, no network needed) =====

    public int syncCanvasCourse(int userId, JSONObject course) throws Exception {
        String extId = "canvas-" + course.getInt("id");
        String name = course.getString("name");
        return classService.syncCourse(userId, extId, name);
    }

    public void syncCanvasAssignment(int userId, int classId, JSONObject assignment) throws Exception {
        String extId = "canvas-" + assignment.getInt("id");
        String title = assignment.getString("name");
        double totalPoints = assignment.optDouble("points_possible", 0);
        classService.syncAssignment(userId, classId, extId, title, totalPoints);
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
        classService.syncAssignment(userId, classId, extId, title, totalPoints);
    }
}