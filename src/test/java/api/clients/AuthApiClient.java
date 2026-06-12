package api.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthApiClient {
    private final String baseUrl;

    public AuthApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response login(String username, String password) {
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        return given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/auth/login");
    }
}
