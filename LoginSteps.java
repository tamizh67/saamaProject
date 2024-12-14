package stepDefenitions;

import static org.testng.Assert.assertTrue;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.LoginPage;
import utils.BaseTest;

public class LoginSteps extends BaseTest{
	WebDriver driver;
	LoginPage loginPage;

	@Given("I navigate to the login page")
	public void i_navigate_to_the_login_page() {
		driver.get(props.getProperty("base.url"));
		loginPage = new LoginPage(driver);
	}

	@When("I enter valid credentials")
	public void i_enter_valid_credentials() {
		loginPage.enterUsername("student");
		loginPage.enterPassword("Password123");
		loginPage.clickLogin();
	}

	@Then("I should be redirected to the success page")
	public void i_should_be_redirected_to_the_success_page() {
		String currentUrl = driver.getCurrentUrl();
		assertTrue(currentUrl.contains("practicetestautomation.com/logged-in-successfully/"));
	}

	@Then("I should see a logout button")
	public void i_should_see_a_logout_button() {
		Assert.assertTrue(driver.findElement(By.linkText("Log out")).isDisplayed());
		driver.quit();
	}

	@When("I enter invalid username")
	public void i_enter_invalid_username() {
		loginPage.enterUsername("incorrectUser");
		loginPage.enterPassword("Password123");
		loginPage.clickLogin();
	}

	@Then("I should see an error message for invalid username")
	public void i_should_see_an_error_message_for_invalid_username() {
		String errorMessage = driver.findElement(By.id("error")).getText();
		Assert.assertTrue(errorMessage.contains("Your username is invalid!"));
		driver.quit();
	}

	@When("I enter valid username")
	public void i_enter_valid_username() {
		loginPage.enterUsername("student");
		loginPage.enterPassword("incorrectPassword");
		loginPage.clickLogin();
	}

	@Then("I should see an error message for invalid password")
	public void i_should_see_an_error_message_for_invalid_password() {
		String errorMessage = driver.findElement(By.id("error")).getText();
		assertTrue(errorMessage.contains("Your password is invalid!"));
		driver.quit();
	}
}


