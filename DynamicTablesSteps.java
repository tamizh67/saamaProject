package stepDefenitions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.BaseTest;

public class DynamicTablesSteps extends BaseTest{

	WebDriver driver;

    @Given("I navigate to the DataTables example page")
    public void i_navigate_to_the_data_tables_example_page() {
        driver.get(props.getProperty("base.url1"));
    }

    @When("I filter the table by {string}")
    public void i_filter_the_table_by(String filterText) {
        driver.findElement(By.id("example_filter")).findElement(By.tagName("input")).sendKeys(filterText);
    }

    @Then("I should see {string} in the table results")
    public void i_should_see_in_the_table_results(String expectedText) {
        String tableData = driver.findElement(By.xpath("//table[@id='example']//tbody")).getText();
        Assert.assertTrue(tableData.contains(expectedText));
    }

    @Then("I should see the total number of entries is {int}")
    public void i_should_see_the_total_number_of_entries_is(int expectedCount) {
        String totalEntriesText = driver.findElement(By.id("example_info")).getText();
        String[] parts = totalEntriesText.split(" ");
        int actualCount = Integer.parseInt(parts[5]); 
        Assert.assertEquals(expectedCount, actualCount);
        driver.quit();
    }
}

