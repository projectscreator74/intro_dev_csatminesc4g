import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SchoologyService {

    private final String consumerKey;
    private final String consumerSecret;
    private final String baseURL = "https://api.schoology.com/v1";
    private final HttpClient client;

    public SchoologyService(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.client = HttpClient.newHttpClient();
    }

    public JSONArray getCourses() throws Exception {
        String json = get("/courses");
        JSONObject parsed = new JSONObject(json);
        return parsed.getJSONArray("course");
    }

    public JSONArray getSections(String courseId) throws Exception {
        String json = get("/courses/" + courseId + "/sections");
        JSONObject parsed = new JSONObject(json);
        return parsed.getJSONArray("section");
    }

    public JSONArray getAssignments(String sectionId) throws Exception {
        String json = get("/sections/" + sectionId + "/assignments");
        JSONObject parsed = new JSONObject(json);
        return parsed.getJSONArray("assignment");
    }

    public JSONArray getGrades(String sectionId) throws Exception {
        String json = get("/sections/" + sectionId + "/grades");
        JSONObject parsed = new JSONObject(json);
        return parsed.getJSONArray("grades");
    }

    private String get(String path) throws Exception {
        String fullUrl = baseURL + path;
        String authHeader = buildOAuthHeader("GET", fullUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Authorization", authHeader)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Schoology API error: " + response.statusCode() + " - " + response.body());
        }

        return response.body();
    }

    private String buildOAuthHeader(String method, String url) throws Exception {
        String nonce = generateNonce();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        java.util.TreeMap<String, String> params = new java.util.TreeMap<>();
        params.put("oauth_consumer_key", consumerKey);
        params.put("oauth_token", "");
        params.put("oauth_signature_method", "HMAC-SHA1");
        params.put("oauth_timestamp", timestamp);
        params.put("oauth_nonce", nonce);
        params.put("oauth_version", "1.0");

        String signature = generateSignature(method, url, params);
        params.put("oauth_signature", signature);

        StringBuilder header = new StringBuilder("OAuth ");
        boolean first = true;
        for (java.util.Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) header.append(", ");
            header.append(entry.getKey()).append("=\"").append(urlEncode(entry.getValue())).append("\"");
            first = false;
        }

        return header.toString();
    }

    private String generateSignature(String method, String url, java.util.TreeMap<String, String> params) throws Exception {
        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (java.util.Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) paramString.append("&");
            paramString.append(urlEncode(entry.getKey())).append("=").append(urlEncode(entry.getValue()));
            first = false;
        }

        String baseString = method.toUpperCase() + "&" + urlEncode(url) + "&" + urlEncode(paramString.toString());
        String signingKey = urlEncode(consumerSecret) + "&";

        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(signingKey.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signatureBytes = mac.doFinal(baseString.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        return java.util.Base64.getEncoder().encodeToString(signatureBytes);
    }

    private String generateNonce() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return java.util.Base64.getEncoder().encodeToString(bytes).replaceAll("[^a-zA-Z0-9]", "");
    }

    private String urlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }
}