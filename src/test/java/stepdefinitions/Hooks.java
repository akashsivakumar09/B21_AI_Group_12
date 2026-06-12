package stepdefinitions;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;

public class Hooks {

    // HEAVY OBJECTS: Initialized once per test run
    public static Playwright playwright;
    public static Browser browser;

    // LIGHTWEIGHT OBJECTS: Initialized fresh for every scenario
    public static BrowserContext context;
    public static Page page;

    // Executes before EACH UI scenario. API scenarios do not need a browser.
    @Before("not @api")
    public void startScenario() {
        if (playwright == null) {
            playwright = Playwright.create();
        }
        if (browser == null) {
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(Boolean.parseBoolean(System.getProperty("headless", "false"))));
        }
        // Create an isolated incognito-like session
        context = browser.newContext();
        // Open a new tab in that isolated session
        page = context.newPage();
    }

    // Executes after EACH UI scenario
    @After("not @api")
    public void endScenario() {
        // Close the tab and wipe the session data (cookies, cache)
        if (page != null) page.close();
        if (context != null) context.close();
    }

    // 4. Executes exactly ONCE after all tests are completely finished
    @AfterAll
    public static void closeBrowser() {
        // Shut down the physical browser and Playwright engine
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
