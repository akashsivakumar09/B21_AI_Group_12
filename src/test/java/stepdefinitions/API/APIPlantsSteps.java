package stepdefinitions.API;

import api.clients.PlantApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

// Static imports make your assertions much cleaner
import static org.hamcrest.Matchers.*;

public class APIPlantsSteps {

    // ==========================================
    // CLASS STATE & INITIALIZATION
    // ==========================================
    private final PlantApiClient plantClient = new PlantApiClient("http://localhost:8080");
    private Response latestResponse;

    // Unified context variables for the current scenario
    private int contextPlantId;
    private int contextCategoryId;
    private String requestPayload;
    private String expectedPlantName;

    // ==========================================
    // AUTHENTICATION STEPS
    // ==========================================
    @Given("the user has logged in with valid credentials")
    public void userHasLoggedInWithValidCredentials() {
        authenticate("testuser", "test123");
    }

    @Given("the admin has logged in with valid credentials")
    public void adminHasLoggedInWithValidCredentials() {
        authenticate("admin", "admin123");
    }

    @Given("the user has a valid authorization token")
    @Given("the admin has a valid authorization token")
    public void hasAValidAuthorizationToken() {
        if (plantClient.getAuthToken() == null) {
            throw new IllegalStateException("Token is missing! Authentication step may have failed or was skipped.");
        }
    }

    @Given("authorization information is not given in the header")
    public void authorizationInformationIsNotGivenInTheHeader() {
        plantClient.clearAuthToken();
    }

    // ==========================================
    // DATA PREPARATION STEPS (@Given)
    // ==========================================
    @Given("a valid plant ID exists in the system")
    public void aValidPlantIdExistsInTheSystem() {
        Response allPlants = plantClient.getAllPlants();
        allPlants.then().statusCode(200);
        contextPlantId = allPlants.jsonPath().getInt("[0].id");
    }

    @Given("an invalid plant ID is prepared")
    public void anInvalidPlantIdIsPrepared() {
        contextPlantId = 999999; // Highly unlikely to exist
    }

    @Given("a valid category ID exists in the system")
    public void aValidCategoryIdExistsInTheSystem() {
        contextCategoryId = 2; // Hardcoded for testing; consider querying DB in the future
    }

    @Given("a non-existent category ID is prepared")
    public void aNonExistentCategoryIdIsPrepared() {
        contextCategoryId = 999999;
    }

    @Given("a valid plant request body is prepared")
    public void aValidPlantRequestBodyIsPrepared() {
        requestPayload = buildPlantPayload("Fern", 19.0f, 5, null);
    }

    @Given("a valid updated plant request body is prepared")
    public void aValidUpdatedPlantRequestBodyIsPrepared() {
        requestPayload = buildPlantPayload("Tulip", 35.0f, 20, 2);
    }

    @Given("a valid new plant request body is prepared")
    public void aValidNewPlantRequestBodyIsPrepared() {
        int randomNum = (int)(Math.random() * 9000) + 1000;
        expectedPlantName = "Monstera " + randomNum;
        requestPayload = buildPlantPayload(expectedPlantName, 45.0f, 15, 2);
    }

    @Given("a valid plant ID exists in the system for deletion")
    public void aValidPlantIdExistsInTheSystemForDeletion() {
        String dummyPlant = buildPlantPayload("Test Plant", 10.0f, 1, null);
        Response createResponse = plantClient.createPlant(2, dummyPlant);

        createResponse.then().statusCode(201);
        contextPlantId = createResponse.jsonPath().getInt("id");
        System.out.println("Created dummy plant for deletion with ID: " + contextPlantId);
    }

    // ==========================================
    // ACTION STEPS (@When)
    // ==========================================
    @When("the user executes a GET request to {string} using the valid plant ID")
    public void executeGetRequestUsingPreparedPlantId(String endpoint) {
        latestResponse = plantClient.getPlantById(contextPlantId);
    }

    @When("the user executes a GET request to {string} without authorization")
    public void executeGetRequestWithoutAuthorization(String endpoint) {
        latestResponse = plantClient.getWithoutAuth(endpoint);
    }

    @When("the admin executes a GET request to {string} using the non-existent category ID")
    @When("the admin executes a GET request to {string} using the valid category ID")
    @When("the user executes a GET request to {string} using the valid category ID")
    public void executeGetRequestUsingPreparedCategoryId(String endpoint) {
        latestResponse = plantClient.getPlantsByCategoryId(contextCategoryId);
    }

    @When("the admin executes a POST request to {string} to create a plant")
    @When("the user executes a POST request to {string} to create a plant")
    public void executePostRequestToCreatePlant(String endpoint) {
        latestResponse = plantClient.createPlant(contextCategoryId, requestPayload);
    }

    @When("the user executes a GET request to {string} to retrieve all plants")
    public void executeGetRequestToRetrieveAllPlants(String endpoint) {
        latestResponse = plantClient.getAllPlants();
    }

    @When("the user executes a GET request to {string} to retrieve the plant summary")
    public void executeGetRequestToRetrievePlantSummary(String endpoint) {
        latestResponse = plantClient.getPlantSummary();
    }

    @When("the admin executes a PUT request to {string} to update plant details")
    public void executePutRequestToUpdatePlantDetails(String endpoint) {
        latestResponse = plantClient.updatePlant(contextPlantId, requestPayload);
    }

    @When("the admin executes a DELETE request to {string} with the invalid plant ID")
    @When("the admin executes a DELETE request to {string} with a valid plant ID")
    public void executeDeleteRequestWithPreparedPlantId(String endpoint) {
        latestResponse = plantClient.deletePlant(contextPlantId);
    }

    // ==========================================
    // ASSERTION STEPS (@Then)
    // ==========================================
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

    @Then("the response body should contain a {string} error message")
    public void responseBodyShouldContainAnErrorMessage(String expectedError) {
        latestResponse.then().body("error", equalTo(expectedError));
    }

    @Then("the response body should contain a list of plants with {string}, {string}, {string}, and {string}")
    public void responseBodyShouldContainAListOfPlantsWithFields(String field1, String field2, String field3, String field4) {
        latestResponse.then()
                .body("size()", greaterThan(0))
                .body("[0]", hasKey(field1))
                .body("[0]", hasKey(field2))
                .body("[0]", hasKey(field3))
                .body("[0]", hasKey(field4));
    }

    @Then("the response body should contain the summary fields {string} and {string}")
    public void responseBodyShouldContainTheSummaryFields(String field1, String field2) {
        latestResponse.then()
                .body("$", hasKey(field1))
                .body("$", hasKey(field2));
    }

    @Then("the response body should contain the updated plant details")
    public void responseBodyShouldContainTheUpdatedPlantDetails() {
        latestResponse.then()
                .body("name", equalTo("Tulip"))
                .body("price", equalTo(35.0f))
                .body("quantity", equalTo(20))
                .body("category.id", equalTo(2));
    }

    @Then("the response body should contain the created plant details")
    public void responseBodyShouldContainTheCreatedPlantDetails() {
        latestResponse.then()
                .body("name", equalTo(expectedPlantName))
                .body("price", equalTo(45.0f))
                .body("quantity", equalTo(15))
                .body("id", notNullValue());
    }

    // ==========================================
    // PRIVATE HELPER METHODS
    // ==========================================
    private void authenticate(String username, String password) {
        latestResponse = plantClient.login(username, password);
        latestResponse.then().statusCode(200);
    }

    /**
     * Helper method to generate JSON payloads cleanly without repeating string concatenations.
     */
    private String buildPlantPayload(String name, float price, int quantity, Integer categoryId) {
        String categoryJson = (categoryId != null) ? ",\n  \"categoryid\": " + categoryId : "";
        return String.format("{\n  \"name\": \"%s\",\n  \"price\": %s,\n  \"quantity\": %d%s\n}",
                name, price, quantity, categoryJson);
    }
}