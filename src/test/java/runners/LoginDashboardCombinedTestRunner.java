package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions"},
        tags = "@loginDashboard or @loginDashboardBug",
        plugin = {
                "pretty",
                "html:target/login-dashboard-combined.html",
                "json:target/login-dashboard-combined.json"
        },
        monochrome = true
)
public class LoginDashboardCombinedTestRunner extends AbstractTestNGCucumberTests {
}
