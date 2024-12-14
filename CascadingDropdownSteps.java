package stepDefenitions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.BaseTest;

public class CascadingDropdownSteps extends BaseTest{

	WebDriver driver;

    @Given("I navigate to the W3Schools dropdown example page")
    public void i_navigate_to_the_w3schools_dropdown_example_page() {
        driver.get(props.getProperty("base.url2"));
    }

    @When("I select {string} from the country dropdown")
    public void i_select_from_the_country_dropdown(String country) {
        driver.findElement(By.id("country")).sendKeys(country);
    }

    @Then("I should see {string} in the city dropdown")
    public void i_should_see_in_the_city_dropdown(String city) {
        String cityDropdownOptions = driver.findElement(By.id("city")).getText();
        Assert.assertTrue(cityDropdownOptions.contains(city));
        driver.quit();
    }
}
