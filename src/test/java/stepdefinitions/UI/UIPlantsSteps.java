package stepdefinitions.UI;

import api.clients.PlantApiClient;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.cucumber.java.en.*;
import org.testng.Assert;
import pages.AddPlantPage;
import pages.EditPlantPage;
import pages.PlantsPage;
import stepdefinitions.Hooks;
import com.microsoft.playwright.Locator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class UIPlantsSteps {

    PlantsPage plantsPage = new PlantsPage(Hooks.page);
    String searchKeyword;
    AddPlantPage addPlantPage = new AddPlantPage(Hooks.page);
    EditPlantPage editPlantPage = new EditPlantPage(Hooks.page);

    @Given("he navigates to the Plant page")
    public void the_user_navigates_to_the_plant_page() {
        plantsPage.navigateToPlantPage();
    }

    @When("types the plant name {string} in the search plant field")
    public void types_the_plant_name_in_the_search_plant_field(String plantName) {
        this.searchKeyword = plantName;
        plantsPage.enterPlantName(plantName);
    }

    @When("clicks the Search button")
    public void clicks_the_search_button() {
        plantsPage.clickSearch();
    }

    // UPDATED: Uses the API client to seed data if it is missing, then refreshes the UI.
    @Given("Plant {string} records exist in the system")
    public void plant_records_exist_in_the_system(String plantName) {
        // Initialize the API Client and Login
        PlantApiClient apiClient = new PlantApiClient("http://localhost:8080"); // Ensure base URL is correct
        Response loginResponse = apiClient.login("admin", "admin123");

        if (loginResponse.statusCode() != 200) {
            throw new RuntimeException("API Setup Error: Failed to login for test data setup.");
        }

        // Fetch all plants to check if the requested plant already exists
        Response allPlantsResponse = apiClient.getAllPlants();
        List<String> plantNames = allPlantsResponse.jsonPath().getList("name");

        // If the plant does not exist, create it via the API
        if (plantNames == null || !plantNames.contains(plantName)) {
            int defaultCategoryId = 2; // Must be a valid category ID in your DB

            String newPlantPayload = "{\n" +
                    "  \"name\": \"" + plantName + "\",\n" +
                    "  \"description\": \"A fragrant flower\",\n" +
                    "  \"price\": 15.99,\n" +
                    "  \"quantity\": 100\n" + // <--- Updated to match your API requirements
                    "}";

            Response createResponse = apiClient.createPlant(defaultCategoryId, newPlantPayload);

            if (createResponse.statusCode() != 201 && createResponse.statusCode() != 200) {
                throw new RuntimeException("API Setup Error: Failed to seed plant data via API. Response: " + createResponse.getBody().asString());
            }
            System.out.println("Test Data Setup: Successfully created plant '" + plantName + "' via API.");
        } else {
            System.out.println("Test Data Setup: Plant '" + plantName + "' already exists in the backend.");
        }

        // CRITICAL FIX: Reload the page so the UI table updates with the newly injected DB data
        Hooks.page.reload();
    }

    // UPDATED: Isolated the UI assertion to just the @Then step
    @Then("the Plant table should contain {string}")
    public void the_Plant_table_should_contain(String plantName) {
        Locator matchingRow = plantsPage.getTable()
                .locator("tbody tr")
                .filter(new Locator.FilterOptions().setHasText(plantName));
        boolean isFound = matchingRow.count() > 0;
        Assert.assertTrue(isFound, "Expected to find plant: '" + plantName + "' in the table, but it was not there.");
    }

    @Given("category {string} exists in the system")
    public void categoryExistsInTheSystem(String expectedCategoryName) {
        boolean categoryExists = plantsPage.doesCategoryExist(expectedCategoryName);
        Assert.assertTrue(categoryExists,
                "Setup Failure: The category '" + expectedCategoryName + "' does not exist in the system.");
    }

    @When("user selects category {string} from the category dropdown")
    public void userSelectsCategoryFromDropdown(String categoryName) {
        plantsPage.selectCategory(categoryName);
    }

    @Then("all displayed plants should belong to category {string}")
    public void allDisplayedPlantsShouldBelongToCategory(String expectedCategoryName) {
        List<String> actualCategories = plantsPage.getAllDisplayedCategories(2);

        Assert.assertFalse(actualCategories.isEmpty(),
                "Expected plants to be displayed, but the list was empty.");

        for (String actualCategory : actualCategories) {
            Assert.assertEquals(actualCategory.trim(), expectedCategoryName,
                    "Found a plant that does not match the expected category filter.");
        }
    }

    @Given("more than one plant records exist in the system")
    public void more_than_one_plant_records_exist_in_the_system() {
        int rowCount = plantsPage.getTable().locator("tbody tr").count();
        Assert.assertTrue(rowCount > 1,
                "Pre-condition failed: Need at least 2 plants in the table to verify sorting functionality.");
    }

    @When("the user clicks on the Name column header")
    public void the_user_clicks_on_the_name_column_header() {
        plantsPage.clickNameColumnHeader();
    }

    @Then("the Plant list should be sorted in {string} alphabetical order by Name")
    public void the_plant_list_should_be_sorted_in_alphabetical_order_by_name(String expectedDirection) {
        List<String> actualNames = plantsPage.getAllDisplayedPlantNames();
        List<String> expectedSortedNames = new ArrayList<>(actualNames);
        expectedSortedNames.sort(String.CASE_INSENSITIVE_ORDER);

        if (expectedDirection.equalsIgnoreCase("descending")) {
            Collections.reverse(expectedSortedNames);
        }

        Assert.assertEquals(actualNames, expectedSortedNames,
                "Toggle Failed: The plant list did not successfully sort in " + expectedDirection + " order after clicking.");
    }

    @Given("at least one plant record exists with a stock quantity below {int}")
    public void at_least_one_plant_record_exists_with_a_stock_quantity_below(Integer threshold) {
        boolean hasRecords = plantsPage.hasLowStockRecords(4, threshold);
        Assert.assertTrue(hasRecords, "Pre-condition failed: No plant records found with stock below " + threshold);
    }

    @Then("a {string} badge should be displayed near the stock quantity number for plants with less than {int} stock")
    public void a_badge_should_be_displayed_near_the_stock_quantity_number(String badgeText, Integer threshold) {
        boolean isBadgeCorrect = plantsPage.verifyLowBadgeForLowStock(4, badgeText, threshold);
        Assert.assertTrue(isBadgeCorrect, "Validation Failed: The '" + badgeText + "' badge was missing on one or more items with stock below " + threshold);
    }

    @Given("no plant records exist in the system")
    public void there_are_plant_records_existing_in_the_system() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "http://localhost:8080";

        String loginPayload = new JSONObject()
                .put("username", "admin")
                .put("password", "admin123")
                .toString();

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginPayload))
                .build();

        HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(loginResponse.statusCode(), 200, "API Error: Login failed. Status code: " + loginResponse.statusCode());

        JSONObject loginResponseBody = new JSONObject(loginResponse.body());
        String token = loginResponseBody.getString("token");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/plants"))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(getResponse.statusCode(), 200, "API Error: Failed to fetch plants.");

        JSONArray plantsArray = new JSONArray(getResponse.body());

        Assert.assertTrue(plantsArray.length() != 0,
                "There are Plant found in the system");
    }

    @Then("the Plant list should display a {string} message")
    public void the_plant_list_should_display_a_message(String expectedMessage) {
        String actualMessage = plantsPage.getEmptyTableMessage();

        Assert.assertNotNull(actualMessage,
                "The empty state row was not visible on the page.");

        Assert.assertEquals(actualMessage, expectedMessage,
                "The empty state message did not match the expected text.");
    }

    @Then("the Add Plant button should be visible")
    public void the_add_plant_button_should_be_visible() {
        boolean isVisible = plantsPage.isAddPlantButtonVisible();
        Assert.assertTrue(isVisible, "Validation Failed: The 'Add a Plant' button is not visible to the admin on the Plant page.");
    }

    @When("the user clicks the Add Plant button")
    public void the_user_clicks_the_add_plant_button() {
        plantsPage.clickAddPlantButton();
    }

    @Then("the Add Plant page should be displayed")
    public void the_add_plant_page_should_be_displayed() {
        boolean isNavigatedSuccessfully = plantsPage.isAddPlantPageDisplayed();

        Assert.assertTrue(isNavigatedSuccessfully,
                "Navigation Failed: Expected to land on the Add Plant page (URL containing '/ui/plants/add'), but did not.");
    }

    @Given("the user navigates to the Add Plant page")
    public void the_user_navigates_to_the_add_plant_page() {
        plantsPage.clickAddPlantButton();
        Hooks.page.waitForURL("**/ui/plants/add");
    }

    @When("clicks the Save button on the Add Plant form")
    @When("the user clicks the Save button without entering information")
    public void the_user_clicks_the_save_button_without_entering_information() {
        addPlantPage.clickSave();
    }

    @Then("validation error messages should be displayed for the required fields")
    public void validation_error_messages_should_be_displayed() {
        boolean errorsDisplayed = addPlantPage.areValidationMessagesDisplayed();

        Assert.assertTrue(errorsDisplayed,
                "Validation Failed: Expected to see error messages when saving an empty form, but none were displayed.");
    }

    @When("the user enters {string} into the quantity field")
    public void the_user_enters_into_the_quantity_field(String quantity) {
        addPlantPage.enterQuantity(quantity);
    }

    @Then("a validation error message {string} should be displayed for the quantity field")
    public void a_validation_error_message_should_be_displayed_for_the_quantity_field(String expectedMessage) {
        String actualMessage = addPlantPage.getQuantityErrorMessage();

        Assert.assertNotNull(actualMessage,
                "Validation Failed: The quantity error message element was not found or visible on the page.");

        Assert.assertEquals(actualMessage, expectedMessage,
                "Validation Failed: The displayed error message did not match the expected text.");
    }

    @When("the user clicks the Cancel button on the Add Plant form")
    public void the_user_clicks_the_cancel_button_on_the_add_plant_form() {
        addPlantPage.clickCancel();
    }

    @Then("the system should redirect to the Plant list page")
    public void the_system_should_redirect_to_the_plant_list_page() {
        boolean isNavigatedSuccessfully = false;
        try {
            Hooks.page.waitForURL("**/ui/plants");
            isNavigatedSuccessfully = Hooks.page.url().endsWith("/ui/plants");
        } catch (Exception e) {
            System.err.println("Timed out waiting for Plant list page URL: " + e.getMessage());
        }

        Assert.assertTrue(isNavigatedSuccessfully,
                "Navigation Failed: Expected to redirect to the Plant list page after clicking Cancel, but did not.");
    }

    @When("the user clicks the delete icon for plant {string} and confirms the deletion")
    public void the_user_clicks_the_delete_icon_for_plant_and_confirms_the_deletion(String plantName) {
        plantsPage.deletePlantAndConfirm(plantName);
    }

    @Then("the Plant table should not contain {string}")
    public void the_plant_table_should_not_contain(String plantName) {
        boolean isFound = plantsPage.isPlantVisibleInTable(plantName);

        Assert.assertFalse(isFound,
                "Deletion Failed: Expected plant '" + plantName + "' to be removed, but it was still found in the table.");
    }

    @When("the user clicks the Edit button for plant {string}")
    public void the_user_clicks_the_edit_button_for_plant(String plantName) {
        plantsPage.clickEditButtonForPlant(plantName);
    }

    @When("updates the Category to {string}")
    public void updates_the_category_to(String newCategory) {
        editPlantPage.selectCategory(newCategory);
    }

    @When("clicks the Save button on the Edit Plant form")
    public void clicks_the_save_button_on_the_edit_plant_form() {
        editPlantPage.clickSave();
    }

    @Then("the Plant table should display {string} with the category {string}")
    public void the_plant_table_should_display_with_the_category(String plantName, String expectedCategory) {
        String actualCategory = plantsPage.getCategoryForPlant(plantName);

        Assert.assertEquals(actualCategory, expectedCategory,
                "Update Failed: The category for plant '" + plantName + "' did not match the expected updated value.");
    }
}