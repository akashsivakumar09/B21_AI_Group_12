package stepdefinitions.API;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;
import org.json.JSONArray;
import org.json.JSONObject;
import api.clients.CategoryApiClient;
import io.restassured.response.Response;

public class APICategoriesSteps {
    private CategoryApiClient apiClient = new CategoryApiClient();
    private String token;
    private Response response;
    private String generatedCategoryName;

    @Given("the API user has authenticated as {string}")
    public void the_api_user_has_authenticated_as(String role) throws Exception {
        String username = role.equals("admin") ? "admin" : "testuser";
        String password = role.equals("admin") ? "admin123" : "test123";

        // Authenticate and retrieve token via the API client model
        token = apiClient.authenticate(username, password);
    }

    @When("the API user sends a GET request to {string}")
    public void the_api_user_sends_a_get_request_to(String endpoint) throws Exception {
        response = apiClient.getCategories(token, endpoint);
    }

    @When("the API user sends a POST request to {string} with name {string}")
    public void the_api_user_sends_a_post_request_to_with_name(String endpoint, String name) throws Exception {
        if (name.contains("[timestamp]")) {
            name = name.replace("[timestamp]", String.valueOf(System.currentTimeMillis() % 100000));
        }
        generatedCategoryName = name;
        response = apiClient.createCategory(token, endpoint, name);
    }

    @When("the API user sends a POST request to {string} with name {string} and parent category ID {int}")
    public void the_api_user_sends_a_post_request_to_with_name_and_parent_id(String endpoint, String name, int parentId) throws Exception {
        if (name.contains("[timestamp]")) {
            name = name.replace("[timestamp]", String.valueOf(System.currentTimeMillis() % 100000));
        }
        generatedCategoryName = name;
        response = apiClient.createSubCategory(token, endpoint, name, parentId);
    }

    @When("the API user sends an unauthenticated GET request to {string}")
    public void the_api_user_sends_an_unauthenticated_get_request_to(String endpoint) throws Exception {
        response = apiClient.getCategoriesUnauthenticated(endpoint);
    }

    @Then("the API response status code should be {int}")
    public void the_api_response_status_code_should_be(int expectedStatus) {
        Assert.assertEquals(response.statusCode(), expectedStatus, "API response status mismatch!");
    }

    @Then("the API response should contain a list of categories")
    public void the_api_response_should_contain_a_list_of_categories() {
        JSONArray array = new JSONArray(response.asString());
        Assert.assertTrue(array.length() >= 0, "Response body should be a categories list!");
    }

    @Then("the API response should confirm the category was created")
    public void the_api_response_should_confirm_the_category_was_created() {
        Assert.assertNotNull(response.jsonPath().get("id"), "Created category response should contain an ID!");
        Assert.assertEquals(response.jsonPath().getString("name"), generatedCategoryName, "Created category name mismatch!");
    }

    @Then("the API response should confirm the sub-category was created")
    public void the_api_response_should_confirm_the_sub_category_was_created() {
        Assert.assertNotNull(response.jsonPath().get("id"), "Created sub-category response should contain an ID!");
        Assert.assertEquals(response.jsonPath().getString("name"), generatedCategoryName, "Created sub-category name mismatch!");
    }

    @Then("the API response should contain mainCategories and subCategories counts")
    public void the_api_response_should_contain_main_and_sub_counts() {
        Assert.assertNotNull(response.jsonPath().get("mainCategories"), "Response should have mainCategories count!");
        Assert.assertNotNull(response.jsonPath().get("subCategories"), "Response should have subCategories count!");
    }
}
