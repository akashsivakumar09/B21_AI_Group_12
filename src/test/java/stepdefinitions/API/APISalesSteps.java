package stepdefinitions.API;

import api.clients.AuthApiClient;
import api.clients.CategoryApiClient;
import api.clients.PlantApiClient;
import api.clients.SalesApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class APISalesSteps {
    private final String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");
    private final AuthApiClient authClient = new AuthApiClient(baseUrl);
    private final SalesApiClient salesClient = new SalesApiClient(baseUrl);

    private String adminToken;
    private String userToken;
    private int plantId;
    private int saleId;
    private int stockBeforeSale;
    private int stockAfterSale;
    private Response latestResponse;

    @Given("the admin API token is available")
    public void adminApiTokenIsAvailable() {
        Response response = authClient.login("admin", "admin123");
        response.then().statusCode(200);
        adminToken = response.jsonPath().getString("token");
        salesClient.setToken(adminToken);
    }

    @Given("the user API token is available")
    public void userApiTokenIsAvailable() {
        Response response = authClient.login("testuser", "test123");
        response.then().statusCode(200);
        userToken = response.jsonPath().getString("token");
        salesClient.setToken(userToken);
    }

    @Given("a plant with stock exists for sales API testing")
    public void plantWithStockExistsForSalesApiTesting() {
        ensureAdminToken();
        long suffix = Instant.now().toEpochMilli() % 100000;
        CategoryApiClient categoryClient = new CategoryApiClient();
        int mainId = categoryClient.createCategory(adminToken, "/api/categories", "SA" + suffix)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
        int subId = categoryClient.createSubCategory(adminToken, "/api/categories", "SB" + suffix, mainId)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
        PlantApiClient plantClient = new PlantApiClient(baseUrl);
        plantClient.setAuthToken(adminToken);
        String plantBody = "{\n" +
                "  \"name\": \"SalesAuto" + suffix + "\",\n" +
                "  \"price\": 120.0,\n" +
                "  \"quantity\": 8\n" +
                "}";
        Response plantResponse = plantClient.createPlant(subId, plantBody);
        plantResponse.then().statusCode(201);
        plantId = plantResponse.jsonPath().getInt("id");
        stockBeforeSale = plantResponse.jsonPath().getInt("quantity");
    }

    @Given("an existing sale record is available for sales API testing")
    public void existingSaleRecordIsAvailableForSalesApiTesting() {
        plantWithStockExistsForSalesApiTesting();
        salesClient.setToken(adminToken);
        Response saleResponse = salesClient.sellPlant(plantId, 1);
        saleResponse.then().statusCode(201);
        saleId = saleResponse.jsonPath().getInt("id");
    }

    @When("the admin retrieves all sales through API")
    public void adminRetrievesAllSalesThroughApi() {
        salesClient.setToken(adminToken);
        latestResponse = salesClient.getAllSales();
    }

    @When("the admin retrieves paginated sales through API")
    public void adminRetrievesPaginatedSalesThroughApi() {
        salesClient.setToken(adminToken);
        latestResponse = salesClient.getSalesPage();
    }

    @When("the admin records a sale through API with quantity {int}")
    public void adminRecordsASaleThroughApiWithQuantity(int quantity) {
        salesClient.setToken(adminToken);
        latestResponse = salesClient.sellPlant(plantId, quantity);
        if (latestResponse.statusCode() == 201) {
            saleId = latestResponse.jsonPath().getInt("id");
            PlantApiClient plantClient = new PlantApiClient(baseUrl);
            plantClient.setAuthToken(adminToken);
            stockAfterSale = plantClient.getPlantById(plantId).jsonPath().getInt("quantity");
        }
    }

    @When("the admin sends invalid sales API requests")
    public void adminSendsInvalidSalesApiRequests() {
        salesClient.setToken(adminToken);
        Response zero = salesClient.sellPlant(plantId, 0);
        Response negative = salesClient.sellPlant(plantId, -1);
        Response invalidPlant = salesClient.sellPlant(999999999, 1);
        Response noToken = salesClient.sellPlantWithoutToken(plantId, 1);
        Response invalidToken = salesClient.getAllSalesWithInvalidToken();

        assertEquals(zero.statusCode(), 400);
        assertEquals(negative.statusCode(), 400);
        assertEquals(invalidPlant.statusCode(), 404);
        assertEquals(noToken.statusCode(), 401);
        assertEquals(invalidToken.statusCode(), 401);
        latestResponse = zero;
    }

    @When("the admin deletes the sale through API")
    public void adminDeletesTheSaleThroughApi() {
        salesClient.setToken(adminToken);
        latestResponse = salesClient.deleteSale(saleId);
    }

    @When("the user retrieves all sales through API")
    public void userRetrievesAllSalesThroughApi() {
        salesClient.setToken(userToken);
        latestResponse = salesClient.getAllSales();
    }

    @When("the user retrieves paginated sales through API")
    public void userRetrievesPaginatedSalesThroughApi() {
        salesClient.setToken(userToken);
        latestResponse = salesClient.getSalesPage();
    }

    @When("the user retrieves the sale by ID through API")
    public void userRetrievesTheSaleByIdThroughApi() {
        salesClient.setToken(userToken);
        latestResponse = salesClient.getSaleById(saleId);
    }

    @When("the user tries to record a sale through API")
    public void userTriesToRecordASaleThroughApi() {
        salesClient.setToken(userToken);
        latestResponse = salesClient.sellPlant(plantId, 1);
    }

    @When("the user tries to delete the sale through API")
    public void userTriesToDeleteTheSaleThroughApi() {
        salesClient.setToken(userToken);
        latestResponse = salesClient.deleteSale(saleId);
    }

    @Then("the API status code should be {int}")
    public void apiStatusCodeShouldBe(int statusCode) {
        latestResponse.then().statusCode(statusCode);
    }

    @Then("the sales response should be a list")
    public void salesResponseShouldBeAList() {
        List<?> sales = latestResponse.jsonPath().getList("$");
        assertNotNull(sales);
    }

    @Then("the sales page response should contain pagination details")
    public void salesPageResponseShouldContainPaginationDetails() {
        latestResponse.then()
                .body("$", hasKey("content"))
                .body("$", hasKey("totalElements"));
    }

    @Then("the sale should be recorded and plant stock should be reduced by {int}")
    public void saleShouldBeRecordedAndPlantStockShouldBeReducedBy(int quantity) {
        latestResponse.then()
                .body("id", notNullValue())
                .body("quantity", equalTo(quantity));
        assertEquals(stockAfterSale, stockBeforeSale - quantity);
    }

    @Then("the invalid sales API responses should be rejected")
    public void invalidSalesApiResponsesShouldBeRejected() {
        latestResponse.then().statusCode(400);
    }

    @Then("the deleted sale should not be retrievable through API")
    public void deletedSaleShouldNotBeRetrievableThroughApi() {
        salesClient.setToken(adminToken);
        salesClient.getSaleById(saleId).then().statusCode(404);
    }

    @Then("the sale response should contain the requested sale ID")
    public void saleResponseShouldContainTheRequestedSaleId() {
        latestResponse.then().body("id", equalTo(saleId));
    }

    @Then("the user sale action should be forbidden")
    public void userSaleActionShouldBeForbidden() {
        assertThat("ROLE_USER must not be allowed to create or delete sales", latestResponse.statusCode(), equalTo(403));
    }

    private void ensureAdminToken() {
        if (adminToken == null) {
            adminApiTokenIsAvailable();
        }
    }
}
