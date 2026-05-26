package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/features/UI",
        glue = {"stepdefinitions"},
        tags = "@user"
)
public class TestRunner extends AbstractTestNGCucumberTests {

}