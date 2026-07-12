package Backend;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CanvasService {

    private final String domain;
    private final String token;
    private final HttpClient client;

    public CanvasService() {
        this.domain = System.getenv("CANVAS_DOMAIN");
        this.token = System.getenv("CANVAS_TOKEN");
        this.client = HttpClient.newHttpClient();
    }

    private String get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + domain + path))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Canvas API error: " + response.statusCode() + " - " + response.body());
        }

        return response.body();
    }

    public JSONArray getCourses() throws Exception {
        String json = get("/api/v1/courses");
        return new JSONArray(json);
    }

    public JSONArray getAssignments(long courseId) throws Exception {
        String json = get("/api/v1/courses/" + courseId + "/assignments");
        return new JSONArray(json);
    }

    public JSONArray getGrades(long courseId) throws Exception {
        String json = get("/api/v1/courses/" + courseId + "/enrollments?user_id=self");
        return new JSONArray(json);
    }
}
