package stepdefinitions.UI;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.DashboardPage;
import stepdefinitions.Hooks;

public class LoginDashboardUISteps {
    private final String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");
    private boolean categoriesSidebarActive;
    private boolean plantsSidebarActive;

    private DashboardPage loginDashboardPage() {
        return new DashboardPage(Hooks.page, baseUrl);
    }

    @When("the login dashboard UI user logs in with username {string} and password {string}")
    public void theLoginDashboardUiUserLogsInWithUsernameAndPassword(String username, String password) {
        loginDashboardPage().login(username, password);
    }

    @Given("the login dashboard UI user logs in as admin")
    public void theLoginDashboardUiUserLogsInAsAdmin() {
        loginDashboardPage().login("admin", "admin123");
        Assert.assertTrue(loginDashboardPage().isDashboardVisible());
    }

    @Given("the login dashboard UI user logs in as user")
    public void theLoginDashboardUiUserLogsInAsUser() {
        loginDashboardPage().login("testuser", "test123");
        Assert.assertTrue(loginDashboardPage().isDashboardVisible());
    }

    @When("the login dashboard UI user submits empty login fields")
    public void theLoginDashboardUiUserSubmitsEmptyLoginFields() {
        loginDashboardPage().submitEmptyLoginForm();
    }

    @When("the login dashboard UI user opens admin-only pages directly")
    public void theLoginDashboardUiUserOpensAdminOnlyPagesDirectly() {
        loginDashboardPage().openDirectUrl("/ui/categories/add");
        Assert.assertTrue(loginDashboardPage().isAccessBlockedOrLoginPage());
        loginDashboardPage().openDirectUrl("/ui/sales/new");
    }

    @When("an unauthenticated UI user opens the dashboard directly")
    public void anUnauthenticatedUiUserOpensTheDashboardDirectly() {
        loginDashboardPage().openDashboard();
    }

    @When("the authenticated user opens the Login page again")
    public void theAuthenticatedUserOpensTheLoginPageAgain() {
        loginDashboardPage().openLoginPage();
    }

    @When("the admin checks active sidebar highlighting on Categories and Plants pages")
    public void theAdminChecksActiveSidebarHighlightingOnCategoriesAndPlantsPages() {
        loginDashboardPage().openDirectUrl("/ui/categories");
        categoriesSidebarActive = loginDashboardPage().isSidebarLinkActive("Categories");
        loginDashboardPage().openDirectUrl("/ui/plants");
        plantsSidebarActive = loginDashboardPage().isSidebarLinkActive("Plants");
    }

    @Then("the dashboard page should be visible")
    public void theDashboardPageShouldBeVisible() {
        Assert.assertTrue(loginDashboardPage().isDashboardVisible());
    }

    @Then("the invalid login message should be visible")
    public void theInvalidLoginMessageShouldBeVisible() {
        Assert.assertTrue(loginDashboardPage().hasAnyText("Invalid username or password", "Invalid credentials"));
    }

    @Then("username and password validation messages should be visible")
    public void usernameAndPasswordValidationMessagesShouldBeVisible() {
        Assert.assertTrue(loginDashboardPage().hasText("Username is required"));
        Assert.assertTrue(loginDashboardPage().hasText("Password is required"));
    }

    @Then("the dashboard sidebar navigation should be visible")
    public void theDashboardSidebarNavigationShouldBeVisible() {
        Assert.assertTrue(loginDashboardPage().hasSidebarNavigation());
    }

    @Then("dashboard module areas should be visible")
    public void dashboardModuleAreasShouldBeVisible() {
        Assert.assertTrue(loginDashboardPage().hasDashboardModules());
    }

    @Then("admin-only direct access should be blocked")
    public void adminOnlyDirectAccessShouldBeBlocked() {
        Assert.assertTrue(loginDashboardPage().isAccessBlockedOrLoginPage());
    }

    @Then("unauthenticated dashboard access should be blocked")
    public void unauthenticatedDashboardAccessShouldBeBlocked() {
        Assert.assertTrue(loginDashboardPage().isAccessBlockedOrLoginPage());
    }

    @Then("the authenticated user should be redirected to the Dashboard page")
    public void theAuthenticatedUserShouldBeRedirectedToTheDashboardPage() {
        Assert.assertTrue(loginDashboardPage().isDashboardVisible(), "Authenticated user was not redirected to dashboard.");
        Assert.assertFalse(loginDashboardPage().isLoginFormVisible(), "Login form is visible for an already authenticated user.");
    }

    @Then("the user dashboard category and plant buttons should show read-only labels")
    public void theUserDashboardCategoryAndPlantButtonsShouldShowReadOnlyLabels() {
        Assert.assertTrue(loginDashboardPage().hasReadOnlyDashboardLabelsForUser(),
                "User dashboard should show View Categories and View Plants, not Manage Categories and Manage Plants.");
    }

    @Then("the Categories and Plants sidebar links should be highlighted")
    public void theCategoriesAndPlantsSidebarLinksShouldBeHighlighted() {
        Assert.assertTrue(categoriesSidebarActive && plantsSidebarActive,
                "Expected Categories and Plants sidebar links to have active highlight. "
                        + "Categories active: " + categoriesSidebarActive
                        + ", Plants active: " + plantsSidebarActive);
    }
}
