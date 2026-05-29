package stepdefinitions.API;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APICategoriesSteps {
    private HttpClient client = HttpClient.newHttpClient();
    private String baseUrl = "http://localhost:8080";
    private String token;
    private HttpResponse<String> response;

    @Given("the API user has authenticated as {string}")
    public void the_api_user_has_authenticated_as(String role) throws Exception {
        String username = role.equals("admin") ? "admin" : "testuser";
        String password = role.equals("admin") ? "admin123" : "test123";

        String loginPayload = new JSONObject()
                .put("username", username)
                .put("password", password)
                .toString();

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginPayload))
                .build();

        HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(loginResponse.statusCode(), 200, "API Login failed!");

        JSONObject body = new JSONObject(loginResponse.body());
        token = body.getString("token");
    }

    @When("the API user sends a GET request to {string}")
    public void the_api_user_sends_a_get_request_to(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @When("the API user sends a POST request to {string} with name {string}")
    public void the_api_user_sends_a_post_request_to_with_name(String endpoint, String name) throws Exception {
        if (name.contains("[timestamp]")) {
            name = name.replace("[timestamp]", String.valueOf(System.currentTimeMillis() % 100000));
        }

        String payload = new JSONObject()
                .put("name", name)
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @When("the API user sends a POST request to {string} with name {string} and parent category ID {int}")
    public void the_api_user_sends_a_post_request_to_with_name_and_parent_id(String endpoint, String name, int parentId) throws Exception {
        if (name.contains("[timestamp]")) {
            name = name.replace("[timestamp]", String.valueOf(System.currentTimeMillis() % 100000));
        }

        String payload = new JSONObject()
                .put("name", name)
                .put("parent", new JSONObject().put("id", parentId))
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @When("the API user sends an unauthenticated GET request to {string}")
    public void the_api_user_sends_an_unauthenticated_get_request_to(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Then("the API response status code should be {int}")
    public void the_api_response_status_code_should_be(int expectedStatus) {
        Assert.assertEquals(response.statusCode(), expectedStatus, "API response status mismatch!");
    }

    @Then("the API response should contain a list of categories")
    public void the_api_response_should_contain_a_list_of_categories() {
        JSONArray array = new JSONArray(response.body());
        Assert.assertTrue(array.length() >= 0, "Response body should be a categories list!");
    }

    @Then("the API response should confirm the category was created")
    public void the_api_response_should_confirm_the_category_was_created() {
        JSONObject obj = new JSONObject(response.body());
        Assert.assertNotNull(obj.get("id"), "Created category response should contain an ID!");
    }

    @Then("the API response should confirm the sub-category was created")
    public void the_api_response_should_confirm_the_sub_category_was_created() {
        JSONObject obj = new JSONObject(response.body());
        Assert.assertNotNull(obj.get("id"), "Created sub-category response should contain an ID!");
        Assert.assertNotNull(obj.getJSONObject("parent"), "Created sub-category should have parent info!");
    }

    @Then("the API response should contain mainCategories and subCategories counts")
    public void the_api_response_should_contain_main_and_sub_counts() {
        JSONObject obj = new JSONObject(response.body());
        Assert.assertTrue(obj.has("mainCategories"), "Response should have mainCategories count!");
        Assert.assertTrue(obj.has("subCategories"), "Response should have subCategories count!");
    }
}
