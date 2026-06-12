package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions"},
        tags = "@loginDashboard",
        plugin = {
                "pretty",
                "html:target/login-dashboard-cucumber.html",
                "json:target/login-dashboard-cucumber.json"
        },
        monochrome = true
)
public class LoginDashboardTestRunner extends AbstractTestNGCucumberTests {
}
