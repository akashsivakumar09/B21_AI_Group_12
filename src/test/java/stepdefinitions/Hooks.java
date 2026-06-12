package stepdefinitions;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

public class Hooks {

    public static Playwright playwright;
    public static Browser browser;
    public static BrowserContext context;
    public static Page page;

    @BeforeAll
    public static void startBrowser() {
        playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(false);
        browser = playwright.chromium().launch(options);
        // --- FIREFOX ---
        //browser = playwright.firefox().launch(options);

        // --- WEBKIT (Safari) ---
        //browser = playwright.webkit().launch(options);
    }

    // This hook will ONLY execute for scenarios tagged with @ui
    @Before("@ui")
    public void startScenario() {
        context = browser.newContext();
        page = context.newPage();
    }

    // This hook will ONLY execute for scenarios tagged with @ui
    @After("@ui")
    public void endScenario() {
        if (context != null) context.close();
    }

    @AfterAll
    public static void closeBrowser() {
        if (playwright != null) playwright.close();
    }
}