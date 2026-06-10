package client;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class PlantApiClient {

    private final String baseUrl;
    private String authToken;

    // Constructor to initialize the client with the base URL
    public PlantApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    /**
     * Authenticates the user and stores the token internally for future requests.
     */
    public Response login(String username, String password) {
        String loginPayload = "{\n" +
                "  \"username\": \"" + username + "\",\n" +
                "  \"password\": \"" + password + "\"\n" +
                "}";

        Response response = given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .when()
                .post("/api/auth/login");

        // Extract and store the token if the login was successful
        if (response.statusCode() == 200) {
            this.authToken = response.jsonPath().getString("token");
        }

        return response;
    }

    /**
     * Helper method to build a request with the authorization header automatically attached.
     */
    private RequestSpecification getAuthorizedRequest() {
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalStateException("Client is not authenticated. Call login() first.");
        }
        return given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json");
    }

    /**
     * Fetches a specific plant by its ID.
     */
    public Response getPlantById(int id) {
        return getAuthorizedRequest()
                .pathParam("id", id)
                .when()
                .get("/api/plants/{id}");
    }

    /**
     * Fetches all plants in the system.
     */
    public Response getAllPlants() {
        return getAuthorizedRequest()
                .when()
                .get("/api/plants");
    }

    /**
     * Exposes the current auth token in case the test needs to validate it directly.
     */
    public String getAuthToken() {
        return this.authToken;
    }

    /**
     * Allows manually setting a token (useful for testing invalid token scenarios).
     */
    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void clearAuthToken() {
        this.authToken = null;
    }

    /**
     * Executes a GET request to a specific endpoint WITHOUT the Authorization header.
     */
    public Response getWithoutAuth(String endpoint) {
        return given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                // Notice: No Authorization header is attached here
                .when()
                .get(endpoint);
    }

    /**
     * Fetches a list of plants associated with a specific category ID.
     */
    public Response getPlantsByCategoryId(int categoryId) {
        return getAuthorizedRequest()
                .pathParam("categoryId", categoryId)
                .when()
                .get("/api/plants/category/{categoryId}");
    }

    public Response createPlant(int categoryId, String requestBody) {
        return getAuthorizedRequest()
                .pathParam("categoryId", categoryId)
                .body(requestBody)
                .when()
                .post("/api/plants/category/{categoryId}");
    }
    /**
     * Fetches the plant summary information (e.g., total counts, low stock).
     */
    public Response getPlantSummary() {
        return getAuthorizedRequest()
                .when()
                .get("/api/plants/summary");
    }

    public Response updatePlant(int id, String requestBody) {
        return getAuthorizedRequest()
                .pathParam("id", id)
                .body(requestBody)
                .when()
                .put("/api/plants/{id}");
    }

    /**
     * Deletes a specific plant from the system by its ID.
     */
    public Response deletePlant(int id) {
        return getAuthorizedRequest()
                .pathParam("id", id)
                .when()
                .delete("/api/plants/{id}");
    }
}
