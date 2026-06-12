package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions"},
        //tags = "@TC-SALES-UI-007",
        plugin = {
                "pretty",
                "html:target/cucumber.html",
                "json:target/cucumber.json"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

}
