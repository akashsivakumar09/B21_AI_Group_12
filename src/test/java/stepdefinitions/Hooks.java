package stepdefinitions;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

public class Hooks {

    // HEAVY OBJECTS: Initialized once per test run
    public static Playwright playwright;
    public static Browser browser;

    // LIGHTWEIGHT OBJECTS: Initialized fresh for every scenario
    public static BrowserContext context;
    public static Page page;

    // 1. Executes exactly ONCE before any tests start
    @BeforeAll
    public static void startBrowser() {
        playwright = Playwright.create();
        // Launch the physical browser executable just once
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
    }

    // 2. Executes before EACH scenario
    @Before
    public void startScenario() {
        // Create an isolated incognito-like session
        context = browser.newContext();
        // Open a new tab in that isolated session
        page = context.newPage();
    }

    // 3. Executes after EACH scenario
    @After
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