package stepdefinitions.UI;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;
import pages.CategoriesPage;
import stepdefinitions.Hooks;
import java.util.List;

public class UICategoriesSteps {
    private CategoriesPage categoriesPage = new CategoriesPage(Hooks.page);
    private String generatedCategoryName;

    @Given("the admin has logged in")
    public void the_admin_has_logged_in() {
        Hooks.page.navigate("http://localhost:8080/ui/login");
        Hooks.page.locator("input[name='username']").fill("admin");
        Hooks.page.locator("input[name='password']").fill("admin123");
        Hooks.page.locator("button[type='submit'], button:has-text('Login')").click();
    }

    @Given("the user has logged in")
    public void the_user_has_logged_in() {
        Hooks.page.navigate("http://localhost:8080/ui/login");
        Hooks.page.locator("input[name='username']").fill("testuser");
        Hooks.page.locator("input[name='password']").fill("test123");
        Hooks.page.locator("button[type='submit'], button:has-text('Login')").click();
    }

    @Given("he navigates to the Categories page")
    public void he_navigates_to_the_categories_page() {
        categoriesPage.navigateToCategories();
    }

    @Then("the Add Category button should be visible")
    public void the_add_category_button_should_be_visible() {
        Assert.assertTrue(categoriesPage.isAddCategoryButtonVisible(), "Add Category button is not visible!");
    }

    @When("the user clicks the Add Category button")
    public void the_user_clicks_the_add_category_button() {
        categoriesPage.clickAddCategoryButton();
    }

    @Then("the Add Category page should be displayed")
    public void the_add_category_page_should_be_displayed() {
        Hooks.page.waitForURL("**/ui/categories/add");
        Assert.assertTrue(Hooks.page.url().contains("/ui/categories/add"), "Not on Add Category page!");
    }

    @Given("the user navigates to the Add Category page")
    public void the_user_navigates_to_the_add_category_page() {
        categoriesPage.navigateToCategories();
        categoriesPage.clickAddCategoryButton();
        Hooks.page.waitForURL("**/ui/categories/add");
    }

    @When("the user enters category name {string}")
    public void the_user_enters_category_name(String name) {
        if (name.contains("[timestamp]")) {
            generatedCategoryName = name.replace("[timestamp]", String.valueOf(System.currentTimeMillis() % 100000));
        } else {
            generatedCategoryName = name;
        }
        categoriesPage.enterCategoryName(generatedCategoryName);
    }

    @When("selects parent category {string} from the parent dropdown")
    public void selects_parent_category_from_the_parent_dropdown(String parentName) {
        categoriesPage.selectParentCategory(parentName);
    }

    @When("clicks the Save button on the Add Category form")
    public void clicks_the_save_button_on_the_add_category_form() {
        categoriesPage.clickSave();
    }

    @Then("the system should redirect to the Categories list page")
    public void the_system_should_redirect_to_the_categories_list_page() {
        Hooks.page.waitForURL("**/ui/categories");
        Assert.assertTrue(Hooks.page.url().endsWith("/ui/categories"), "Not redirected to Categories list!");
    }

    @Then("the new category should be visible in the list")
    public void the_new_category_should_be_visible_in_the_list() {
        Assert.assertTrue(categoriesPage.isCategoryVisibleInTable(generatedCategoryName), "New category is not visible in table!");
    }

    @Then("a validation error message {string} should be displayed for the name field")
    public void a_validation_error_message_should_be_displayed_for_the_name_field(String expectedError) {
        Assert.assertTrue(categoriesPage.isNameErrorVisible(), "Validation error message is not visible!");
        Assert.assertEquals(categoriesPage.getNameErrorText(), expectedError, "Validation error message text mismatch!");
    }

    @Then("the Add Category button should not be visible")
    public void the_add_category_button_should_not_be_visible() {
        Assert.assertFalse(categoriesPage.isAddCategoryButtonVisible(), "Add Category button is visible for non-admin!");
    }

    @Then("the Edit and Delete actions should be disabled or hidden")
    public void the_edit_and_delete_actions_should_be_disabled_or_hidden() {
        int editDisabledCount = Hooks.page.locator("a.disabled:has-text('Edit'), a[disabled='disabled']:has-text('Edit')").count();
        int deleteDisabledCount = Hooks.page.locator("a.disabled:has-text('Delete'), a[disabled='disabled']:has-text('Delete')").count();
        Assert.assertTrue(editDisabledCount > 0 || deleteDisabledCount > 0, "Actions are not disabled!");
    }

    @When("the user clicks the Cancel button on the Add Category form")
    public void the_user_clicks_the_cancel_button_on_the_add_category_form() {
        categoriesPage.clickCancel();
    }

    @Then("the parent category dropdown should contain {string}")
    public void the_parent_category_dropdown_should_contain(String expectedCategory) {
        List<String> options = categoriesPage.getParentSelectOptions();
        Assert.assertTrue(options.contains(expectedCategory), "Dropdown does not contain parent category: " + expectedCategory);
    }
}
