package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions"},
        tags = "@loginDashboardBug",
        plugin = {
                "pretty",
                "html:target/login-dashboard-bugs.html",
                "json:target/login-dashboard-bugs.json"
        },
        monochrome = true
)
public class LoginDashboardBugTestRunner extends AbstractTestNGCucumberTests {
}
