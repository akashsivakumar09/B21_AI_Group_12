package api.clients;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.json.JSONObject;

public class CategoryApiClient {
    private static final String BASE_URL = "http://localhost:8080";

    /**
     * Authenticates a user and returns their JWT token.
     */
    public String authenticate(String username, String password) {
        String loginPayload = new JSONObject()
                .put("username", username)
                .put("password", password)
                .toString();

        Response response = RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .post("/api/auth/login");

        response.then().statusCode(200);
        return response.jsonPath().getString("token");
    }

    /**
     * Sends an authorized GET request.
     */
    public Response getCategories(String token, String endpoint) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .accept(ContentType.JSON)
                .get(endpoint);
    }

    /**
     * Sends an authorized POST request to create a main category.
     */
    public Response createCategory(String token, String endpoint, String name) {
        String payload = new JSONObject()
                .put("name", name)
                .toString();

        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(payload)
                .post(endpoint);
    }

    /**
     * Sends an authorized POST request to create a sub-category under a parent ID.
     */
    public Response createSubCategory(String token, String endpoint, String name, int parentId) {
        String payload = new JSONObject()
                .put("name", name)
                .put("parent", new JSONObject().put("id", parentId))
                .toString();

        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(payload)
                .post(endpoint);
    }

    /**
     * Sends an unauthenticated GET request.
     */
    public Response getCategoriesUnauthenticated(String endpoint) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .accept(ContentType.JSON)
                .get(endpoint);
    }
}
