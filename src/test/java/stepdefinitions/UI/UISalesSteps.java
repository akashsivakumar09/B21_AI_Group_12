package stepdefinitions.UI;

import api.clients.AuthApiClient;
import api.clients.CategoryApiClient;
import api.clients.PlantApiClient;
import api.clients.SalesApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import pages.LoginPage;
import pages.SalesListPage;
import pages.SellPlantPage;
import stepdefinitions.Hooks;

import java.time.Instant;

public class UISalesSteps {
    private final String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");

    private int plantId;
    private String plantName;
    private int stockBefore;
    private int stockAfter;
    private boolean adminSellButtonWasVisible;

    private LoginPage loginPage() {
        return new LoginPage(Hooks.page, baseUrl);
    }

    private SalesListPage salesListPage() {
        return new SalesListPage(Hooks.page, baseUrl);
    }

    private SellPlantPage sellPlantPage() {
        return new SellPlantPage(Hooks.page, baseUrl);
    }

    @Given("the admin has logged in to the UI")
    public void adminHasLoggedInToUi() {
        loginPage().login("admin", "admin123");
        Assert.assertTrue(Hooks.page.url().contains("/ui/dashboard"));
    }

    @Given("the user has logged in to the UI")
    public void userHasLoggedInToUi() {
        loginPage().login("testuser", "test123");
        Assert.assertTrue(Hooks.page.url().contains("/ui/dashboard"));
    }

    @Given("sales records exist for UI testing")
    public void salesRecordsExistForUiTesting() {
        createPlantAndSale(8, 1);
    }

    @Given("a plant with stock exists for UI sale")
    public void plantWithStockExistsForUiSale() {
        createPlantOnly(5);
    }

    @Given("a sale record exists for UI deletion")
    public void saleRecordExistsForUiDeletion() {
        createPlantAndSale(5, 1);
    }

    @When("the admin opens the Sales list page")
    @When("the user opens the Sales list page")
    public void opensSalesListPage() {
        salesListPage().navigate();
    }

    @When("the admin opens the Sell Plant page")
    public void adminOpensSellPlantPage() {
        salesListPage().navigate();
        adminSellButtonWasVisible = salesListPage().isSellPlantButtonVisible();
        salesListPage().clickSellPlant();
    }

    @When("the admin submits the Sell Plant form without selecting a plant")
    public void adminSubmitsSellPlantFormWithoutSelectingPlant() {
        sellPlantPage().navigate();
        sellPlantPage().clearPlantSelection();
        sellPlantPage().enterQuantity("2");
        sellPlantPage().clickSell();
    }

    @When("the admin submits invalid quantity in the Sell Plant form")
    public void adminSubmitsInvalidQuantityInSellPlantForm() {
        sellPlantPage().navigate();
        sellPlantPage().selectPlant(plantId);
        sellPlantPage().enterQuantity("0");
        sellPlantPage().clickSell();
    }

    @When("the admin submits invalid Sell Plant form values")
    public void adminSubmitsInvalidSellPlantFormValues() {
        sellPlantPage().navigate();
        sellPlantPage().clearPlantSelection();
        sellPlantPage().enterQuantity("2");
        sellPlantPage().clickSell();
    }

    @When("the admin sells the plant with quantity {int}")
    public void adminSellsThePlantWithQuantity(int quantity) {
        sellPlantPage().navigate();
        stockBefore = sellPlantPage().stockForPlant(plantId);
        sellPlantPage().selectPlant(plantId);
        sellPlantPage().enterQuantity(String.valueOf(quantity));
        sellPlantPage().clickSell();
        sellPlantPage().navigate();
        stockAfter = sellPlantPage().stockForPlant(plantId);
    }

    @When("the admin cancels and then confirms sale deletion")
    public void adminCancelsAndThenConfirmsSaleDeletion() {
        salesListPage().navigate();
        salesListPage().deleteSaleForPlantAndDismiss(plantName);
        Assert.assertTrue(salesListPage().isSaleVisibleForPlant(plantName));
        salesListPage().deleteSaleForPlantAndAccept(plantName);
    }

    @When("the user directly opens the Sell Plant URL")
    public void userDirectlyOpensSellPlantUrl() {
        Hooks.page.navigate(baseUrl + "/ui/sales/new");
        Hooks.page.waitForLoadState();
    }

    @When("the user opens sorted Sales pages")
    public void userOpensSortedSalesPages() {
        String[] fields = {"plant.name", "quantity", "totalPrice", "soldAt"};
        for (String field : fields) {
            Hooks.page.navigate(baseUrl + "/ui/sales?page=0&sortField=" + field + "&sortDir=asc");
            Hooks.page.waitForLoadState();
            Assert.assertTrue(salesListPage().isDisplayed());
        }
    }

    @Then("the Sales list should be displayed with sorting and pagination")
    public void salesListShouldBeDisplayedWithSortingAndPagination() {
        Assert.assertTrue(salesListPage().isDisplayed());
        Assert.assertTrue(salesListPage().hasSalesRows());
        Assert.assertTrue(salesListPage().hasSortingLinks());
        Assert.assertTrue(salesListPage().hasPaginationEvidence());
    }

    @Then("the Sell Plant button and form should be available to admin")
    public void sellPlantButtonAndFormShouldBeAvailableToAdmin() {
        Assert.assertTrue(adminSellButtonWasVisible);
        Assert.assertTrue(sellPlantPage().isDisplayed());
        Assert.assertTrue(sellPlantPage().dropdownContainsStockValues());
    }

    @Then("the Plant required validation message should be displayed")
    public void plantRequiredValidationMessageShouldBeDisplayed() {
        Assert.assertTrue(sellPlantPage().hasMessage("Plant is required"));
    }

    @Then("the Quantity validation should be enforced")
    public void quantityValidationShouldBeEnforced() {
        boolean hasApplicationMessage = sellPlantPage().hasMessage("Quantity must be greater than 0");
        boolean browserValidationBlockedSubmit = !sellPlantPage().isQuantityInputValid();
        Assert.assertTrue(hasApplicationMessage || browserValidationBlockedSubmit);
    }

    @Then("the Sell Plant validation messages should be displayed")
    public void sellPlantValidationMessagesShouldBeDisplayed() {
        Assert.assertTrue(sellPlantPage().hasMessage("Plant is required"));
    }

    @Then("the sale should appear in the UI and stock should be reduced by {int}")
    public void saleShouldAppearInUiAndStockShouldBeReducedBy(int quantity) {
        Assert.assertTrue(Hooks.page.url().contains("/ui/sales"));
        salesListPage().navigate();
        Assert.assertTrue(salesListPage().isSaleVisibleForPlant(plantName));
        Assert.assertEquals(stockAfter, stockBefore - quantity);
    }

    @Then("the sale should be removed from the UI")
    public void saleShouldBeRemovedFromUi() {
        Assert.assertFalse(salesListPage().isSaleVisibleForPlant(plantName));
    }

    @Then("the user should be able to view the Sales list")
    public void userShouldBeAbleToViewSalesList() {
        Assert.assertTrue(salesListPage().isDisplayed());
        Assert.assertTrue(salesListPage().hasSalesRows());
    }

    @Then("the Sell Plant button should not be visible to the user")
    public void sellPlantButtonShouldNotBeVisibleToUser() {
        Assert.assertFalse(salesListPage().isSellPlantButtonVisible());
    }

    @Then("the user should be blocked from the Sell Plant page")
    public void userShouldBeBlockedFromSellPlantPage() {
        Assert.assertTrue(Hooks.page.url().contains("/ui/403") || Hooks.page.locator("body").innerText().contains("Access Denied"));
    }

    @Then("the Delete action should not be visible to the user")
    public void deleteActionShouldNotBeVisibleToUser() {
        Assert.assertFalse(salesListPage().hasAnyDeleteAction());
    }

    @Then("the user should be able to view sorting and pagination")
    public void userShouldBeAbleToViewSortingAndPagination() {
        Assert.assertTrue(salesListPage().hasSortingLinks());
        Assert.assertTrue(salesListPage().hasPaginationEvidence());
    }

    private void createPlantOnly(int quantity) {
        String adminToken = new AuthApiClient(baseUrl)
                .login("admin", "admin123")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
        long suffix = Instant.now().toEpochMilli() % 100000;
        CategoryApiClient categoryClient = new CategoryApiClient();
        int mainId = categoryClient.createCategory(adminToken, "/api/categories", "UA" + suffix)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
        int subId = categoryClient.createSubCategory(adminToken, "/api/categories", "UB" + suffix, mainId)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
        plantName = "UISales" + suffix;
        PlantApiClient plantClient = new PlantApiClient(baseUrl);
        plantClient.setAuthToken(adminToken);
        String plantBody = "{\n" +
                "  \"name\": \"" + plantName + "\",\n" +
                "  \"price\": 150.0,\n" +
                "  \"quantity\": " + quantity + "\n" +
                "}";
        Response plant = plantClient.createPlant(subId, plantBody);
        plant.then().statusCode(201);
        plantId = plant.jsonPath().getInt("id");
    }

    private void createPlantAndSale(int quantity, int saleQuantity) {
        createPlantOnly(quantity);
        String adminToken = new AuthApiClient(baseUrl)
                .login("admin", "admin123")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
        SalesApiClient salesClient = new SalesApiClient(baseUrl);
        salesClient.setToken(adminToken);
        salesClient.sellPlant(plantId, saleQuantity).then().statusCode(201);
    }
}
