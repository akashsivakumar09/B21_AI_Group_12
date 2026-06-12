package pages;

import com.microsoft.playwright.Page;

public class LoginDashboardPage {
    private final Page page;
    private final String baseUrl;

    public LoginDashboardPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void openLoginPage() {
        page.navigate(baseUrl + "/ui/login");
        page.waitForLoadState();
    }

    public void login(String username, String password) {
        openLoginPage();
        page.locator("input[name='username']").fill(username);
        page.locator("input[name='password']").fill(password);
        page.locator("button[type='submit'], button:has-text('Login')").click();
        page.waitForLoadState();
    }

    public void submitEmptyLoginForm() {
        openLoginPage();
        page.locator("button[type='submit'], button:has-text('Login')").click();
        page.waitForLoadState();
    }

    public void openDashboard() {
        page.navigate(baseUrl + "/ui/dashboard");
        page.waitForLoadState();
    }

    public void openDirectUrl(String path) {
        page.navigate(baseUrl + path);
        page.waitForLoadState();
    }

    public boolean isDashboardVisible() {
        String body = page.locator("body").innerText();
        return page.url().contains("/ui/dashboard") && body.contains("Dashboard");
    }

    public boolean hasText(String text) {
        return page.locator("body").innerText().contains(text);
    }

    public boolean isLoginFormVisible() {
        return page.locator("input[name='username']").isVisible()
                && page.locator("input[name='password']").isVisible();
    }

    public boolean hasAnyText(String... values) {
        String body = page.locator("body").innerText();
        for (String value : values) {
            if (body.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSidebarNavigation() {
        return hasAnyText("Dashboard")
                && hasAnyText("Categories")
                && hasAnyText("Plants")
                && hasAnyText("Sales")
                && hasAnyText("Logout");
    }

    public boolean hasDashboardModules() {
        return hasAnyText("Categories")
                && hasAnyText("Plants")
                && hasAnyText("Sales")
                && hasAnyText("Inventory");
    }

    public boolean hasReadOnlyDashboardLabelsForUser() {
        return hasText("View Categories")
                && hasText("View Plants")
                && !hasText("Manage Categories")
                && !hasText("Manage Plants");
    }

    public boolean isSidebarLinkActive(String linkText) {
        String classes = page.locator("a.nav-link:has-text('" + linkText + "')").first().getAttribute("class");
        return classes != null && classes.contains("active");
    }

    public boolean isAccessBlockedOrLoginPage() {
        String body = page.locator("body").innerText();
        return page.url().contains("/ui/login")
                || page.url().contains("/ui/403")
                || body.contains("Access Denied")
                || body.contains("Forbidden");
    }
}
