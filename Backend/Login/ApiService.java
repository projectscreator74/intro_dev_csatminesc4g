import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiService {
    private static final String API_KEY = System.getenv("MY_API_KEY");

    public void makeApiCall() throws Exception {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("API key is missing from environment variables!");
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://example.com"))
                .header("Authorization", "Bearer " + API_KEY)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
