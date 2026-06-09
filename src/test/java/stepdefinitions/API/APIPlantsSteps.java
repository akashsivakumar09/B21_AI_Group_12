package stepdefinitions.API;
import client.PlantApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;
import client.PlantApiClient;

public class APIPlantsSteps {

    // Initialize the client
    private PlantApiClient plantClient = new PlantApiClient("http://localhost:8080");
    private Response latestResponse;
    private int targetPlantId;

    @Given("the user has logged in with valid credentials")
    public void userHasLoggedInWithValidCredentials() {
        // The client handles the payload and token extraction automatically
        latestResponse = plantClient.login("testuser", "test123");
        latestResponse.then().statusCode(200);
    }

    @Given("the user has a valid authorization token")
    public void userHasAValidAuthorizationToken() {
        // Handled entirely by the client during the login step
        if (plantClient.getAuthToken() == null) {
            throw new IllegalStateException("Token is missing!");
        }
    }

    @Given("a valid plant ID exists in the system")
    public void aValidPlantIdExistsInTheSystem() {
        // Use the client to get all plants and fetch the first ID
        Response allPlants = plantClient.getAllPlants();
        allPlants.then().statusCode(200);
        targetPlantId = allPlants.jsonPath().getInt("[0].id");
    }

    @When("the user executes a GET request to {string} using the valid plant ID")
    public void userExecutesAGetRequestToUsingTheValidPlantId(String endpoint) {
        // The client abstracts the Rest Assured given/when/then logic away
        latestResponse = plantClient.getPlantById(targetPlantId);
    }

    @Then("the HTTP status code should be {int}")
    public void httpStatusCodeShouldBe(int expectedStatusCode) {
        latestResponse.then().statusCode(expectedStatusCode);
    }

    @Then("the response body should contain {string}, {string}, and {string}")
    public void responseBodyShouldContainFields(String field1, String field2, String field3) {
        latestResponse.then()
                .body("$", hasKey(field1))
                .body("$", hasKey(field2))
                .body("$", hasKey(field3));
    }
}