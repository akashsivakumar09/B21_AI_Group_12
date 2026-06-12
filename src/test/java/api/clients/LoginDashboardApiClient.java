package api.clients;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class LoginDashboardApiClient {
    private final String baseUrl;
    private String token;

    public LoginDashboardApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response login(String username, String password) {
        String body = "{\n" +
                "  \"username\": \"" + username + "\",\n" +
                "  \"password\": \"" + password + "\"\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/auth/login");

        if (response.statusCode() == 200) {
            token = response.jsonPath().getString("token");
        }
        return response;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Response getWithToken(String endpoint) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .accept(ContentType.JSON)
                .get(endpoint);
    }

    public Response postWithToken(String endpoint, String body) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
                .post(endpoint);
    }

    public Response getWithoutToken(String endpoint) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .get(endpoint);
    }

    public Response getWithInvalidToken(String endpoint) {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer invalid.token.value")
                .accept(ContentType.JSON)
                .get(endpoint);
    }
}
