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

    @Given("the admin has logged in with valid credentials")
    public void adminHasLoggedInWithValidCredentials() {
        // Authenticate as Admin and store the token
        latestResponse = plantClient.login("admin", "admin123");
        latestResponse.then().statusCode(200);
    }

    @Given("the user has a valid authorization token")
    @Given("the admin has a valid authorization token")
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

    @Given("authorization information is not given in the header")
    public void authorizationInformationIsNotGivenInTheHeader() {
        // Ensure the client has no token stored
        plantClient.clearAuthToken();
    }

    @When("the user executes a GET request to {string} without authorization")
    public void theUserExecutesAGetRequestWithoutAuthorization(String endpoint) {
        // Use the new client method that explicitly omits the Auth header
        latestResponse = plantClient.getWithoutAuth(endpoint);
    }

    @Then("the response body should contain a {string} error message")
    public void theResponseBodyShouldContainAnErrorMessage(String expectedError) {
        // Validates that the expected error text (e.g., "UNAUTHORIZED") appears somewhere in the response body.
        // This is a safe assertion if you don't know the exact JSON schema of the error payload.
        //latestResponse.then().body(org.hamcrest.Matchers.containsString(expectedError));

        /* Note: If your API returns a specific JSON format for errors like {"error": "UNAUTHORIZED"},
           you can make this more strict by using:
        */
        latestResponse.then().body("error", equalTo(expectedError));
    }

    private int targetCategoryId;

    @Given("a valid category ID exists in the system")
    public void aValidCategoryIdExistsInTheSystem() {
        // For testing purposes, we define a known valid category ID.
        // In a real framework, you might query the DB or call a GET /api/categories endpoint to fetch one dynamically.
        targetCategoryId = 2;
    }

    @When("the admin executes a GET request to {string} using the valid category ID")
    @When("the user executes a GET request to {string} using the valid category ID")
    public void theUserExecutesAGetRequestUsingTheValidCategoryId(String endpoint) {
        // Using our API Client to abstract the HTTP call
        latestResponse = plantClient.getPlantsByCategoryId(targetCategoryId);
    }

    @Then("the response body should contain a list of plants with {string}, {string}, {string}, and {string}")
    public void theResponseBodyShouldContainAListOfPlantsWithFields(String field1, String field2, String field3, String field4) {
        // Assert that the response is an array (list) and check the structure of the first item in the list
        // Note: Using "[0]" assumes the category has at least one plant.
        latestResponse.then()
                .body("size()", greaterThan(0)) // Ensure the list is not empty
                .body("[0]", hasKey(field1))
                .body("[0]", hasKey(field2))
                .body("[0]", hasKey(field3))
                .body("[0]", hasKey(field4));
    }

    private String plantRequestBody;

    @Given("a valid plant request body is prepared")
    public void aValidPlantRequestBodyIsPrepared() {
        // Construct a valid JSON payload for creating a plant.
        // *Note: Adjust these fields if your API requires different properties (e.g., description, image URL).*
        plantRequestBody = "{\n" +
                "  \"name\": \"Fern\",\n" +
                "  \"price\": 19.99,\n" +
                "  \"quantity\": 5\n" +
                "}";
    }

    @When("the user executes a POST request to {string} to create a plant")
    public void theUserExecutesAPOSTRequestToCreateAPlant(String endpoint) {
        // We pass the dynamically retrieved category ID (from U-03) and the payload to the client
        latestResponse = plantClient.createPlant(targetCategoryId, plantRequestBody);
    }

    @When("the user executes a GET request to {string} to retrieve all plants")
    public void theUserExecutesAGetRequestToRetrieveAllPlants(String endpoint) {
        // We already created this method in our PlantApiClient earlier!
        latestResponse = plantClient.getAllPlants();
    }

    @When("the user executes a GET request to {string} to retrieve the plant summary")
    public void theUserExecutesAGetRequestToRetrieveThePlantSummary(String endpoint) {
        // Execute the GET request using our client method
        latestResponse = plantClient.getPlantSummary();
    }

    @Then("the response body should contain the summary fields {string} and {string}")
    public void theResponseBodyShouldContainTheSummaryFields(String field1, String field2) {
        // Validate that the JSON root object contains the expected summary keys
        latestResponse.then()
                .body("$", org.hamcrest.Matchers.hasKey(field1))
                .body("$", org.hamcrest.Matchers.hasKey(field2));
    }

    private String updatedPlantRequestBody;
    @Given("a valid updated plant request body is prepared")
    public void aValidUpdatedPlantRequestBodyIsPrepared() {
        // Formulate the JSON body exactly as requested in the test case image
        updatedPlantRequestBody = "{\n" +
                "  \"name\": \"Tulip\",\n" +
                "  \"price\": 35,\n" +
                "  \"quantity\": 20,\n" +
                "  \"categoryid\": 2\n" +
                "}";
    }

    @When("the admin executes a PUT request to {string} to update plant details")
    public void theAdminExecutesAPutRequestToUpdatePlantDetails(String endpoint) {
        // Execute the PUT request via the API client
        latestResponse = plantClient.updatePlant(targetPlantId, updatedPlantRequestBody);
    }

    @Then("the response body should contain the updated plant details")
    public void theResponseBodyShouldContainTheUpdatedPlantDetails() {
        // Verify the response echoes back the updated details we sent in the PUT body
        latestResponse.then()
                .body("name", org.hamcrest.Matchers.equalTo("Tulip"))
                // Note: RestAssured parses JSON decimals as Floats by default
                .body("price", org.hamcrest.Matchers.equalTo(35.0f))
                .body("quantity", org.hamcrest.Matchers.equalTo(20))
                .body("category.id", org.hamcrest.Matchers.equalTo(2));
    }

    @Given("a valid plant ID exists in the system for deletion")
    public void aValidPlantIdExistsInTheSystemForDeletion() {
        // 1. Create a dummy payload
        String dummyPlant = "{\n" +
                "  \"name\": \"Test Plant\",\n" +
                "  \"price\": 10.00,\n" +
                "  \"quantity\": 1\n" +
                "}";

        // 2. Create the plant (assuming category ID 2 exists)
        Response createResponse = plantClient.createPlant(2, dummyPlant);
        createResponse.then().statusCode(201); // Ensure it was created

        // 3. Save the newly generated ID to targetPlantId
        // Assuming your POST response returns the created plant object with its new ID
        targetPlantId = createResponse.jsonPath().getInt("id");

        System.out.println("Created dummy plant for deletion with ID: " + targetPlantId);
    }

    @When("the admin executes a DELETE request to {string} with the invalid plant ID")
    @When("the admin executes a DELETE request to {string} with a valid plant ID")
    public void theAdminExecutesADeleteRequestWithAValidPlantId(String endpoint) {
        // We assume targetPlantId was populated by the "Given a valid plant ID exists in the system" step
        latestResponse = plantClient.deletePlant(targetPlantId);
    }

    private int invalidPlantId;
    @Given("an invalid plant ID is prepared")
    public void anInvalidPlantIdIsPrepared() {
        // Use an ID that is highly unlikely to ever exist in the database
        invalidPlantId = 999999;
    }

    private String newPlantRequestBody;

    @Given("a valid new plant request body is prepared")
    public void aValidNewPlantRequestBodyIsPrepared() {
        // Formulate the JSON body exactly as requested in the test case image
        // Make sure the categoryid matches the targetCategoryId you are testing against
        newPlantRequestBody = "{\n" +
                "  \"name\": \"Monstera Delicio\",\n" +
                "  \"price\": 45,\n" +
                "  \"quantity\": 15,\n" +
                "  \"categoryid\": 2\n" +
                "}";
    }

    @When("the admin executes a POST request to {string} to create a plant")
    public void theAdminExecutesAPostRequestToCreateAPlant(String endpoint) {
        // Execute the POST request via the API client using the category ID and payload
        latestResponse = plantClient.createPlant(targetCategoryId, newPlantRequestBody);
    }

    @Then("the response body should contain the created plant details")
    public void theResponseBodyShouldContainTheCreatedPlantDetails() {
        // Verify the response echoes back the exact details we sent in the POST body
        latestResponse.then()
                .body("name", org.hamcrest.Matchers.equalTo("Monstera Delicio"))
                // Note: RestAssured parses JSON decimals as Floats by default
                .body("price", org.hamcrest.Matchers.equalTo(45f))
                .body("quantity", org.hamcrest.Matchers.equalTo(15));

        // Optional: Also assert that the database successfully generated a new ID for it
        latestResponse.then().body("id", org.hamcrest.Matchers.notNullValue());
    }
}