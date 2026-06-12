package stepdefinitions.API;

import api.clients.LoginDashboardApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

public class LoginDashboardApiSteps {
    private final String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");
    private final LoginDashboardApiClient client = new LoginDashboardApiClient(baseUrl);
    private Response response;
    private Response unauthorizedResponse;
    private Response invalidTokenResponse;

    @When("the login API is called with username {string} and password {string}")
    public void theLoginApiIsCalledWithUsernameAndPassword(String username, String password) {
        response = client.login(username, password);
    }

    @Given("the login dashboard API user is authenticated as admin")
    public void theLoginDashboardApiUserIsAuthenticatedAsAdmin() {
        response = client.login("admin", "admin123");
        response.then().statusCode(200);
    }

    @Given("the login dashboard API user is authenticated as user")
    public void theLoginDashboardApiUserIsAuthenticatedAsUser() {
        response = client.login("testuser", "test123");
        response.then().statusCode(200);
    }

    @When("the login dashboard API user sends a GET request to {string}")
    public void theLoginDashboardApiUserSendsAGetRequestTo(String endpoint) {
        response = client.getWithToken(endpoint);
    }

    @When("the login dashboard API user tries to create a category")
    public void theLoginDashboardApiUserTriesToCreateACategory() {
        String body = "{ \"name\": \"UD" + (System.currentTimeMillis() % 100000) + "\" }";
        response = client.postWithToken("/api/categories", body);
    }

    @When("protected dashboard related APIs are called without or with invalid token")
    public void protectedDashboardRelatedApisAreCalledWithoutOrWithInvalidToken() {
        unauthorizedResponse = client.getWithoutToken("/api/categories/summary");
        invalidTokenResponse = client.getWithInvalidToken("/api/plants/summary");
    }

    @Then("the login dashboard API status code should be {int}")
    public void theLoginDashboardApiStatusCodeShouldBe(int expectedStatus) {
        Assert.assertEquals(response.statusCode(), expectedStatus);
    }

    @Then("the response should contain a bearer token")
    public void theResponseShouldContainABearerToken() {
        Assert.assertNotNull(response.jsonPath().getString("token"));
        Assert.assertEquals(response.jsonPath().getString("tokenType"), "Bearer");
    }

    @Then("the response should contain data")
    public void theResponseShouldContainData() {
        Assert.assertFalse(response.asString().isBlank());
    }

    @Then("both unauthorized requests should return 401")
    public void bothUnauthorizedRequestsShouldReturn401() {
        Assert.assertEquals(unauthorizedResponse.statusCode(), 401);
        Assert.assertEquals(invalidTokenResponse.statusCode(), 401);
    }
}
