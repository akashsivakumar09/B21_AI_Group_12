package pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;
    private final String baseUrl;

    public LoginPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void navigate() {
        page.navigate(baseUrl + "/ui/login");
    }

    public void login(String username, String password) {
        navigate();
        page.locator("input[name='username']").fill(username);
        page.locator("input[name='password']").fill(password);
        page.locator("button[type='submit']").click();
        page.waitForLoadState();
    }
}
