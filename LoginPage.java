package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import utils.BaseTest;

public class LoginPage extends BaseTest {

	WebDriver driver;

	By usernameField = By.id("username");
	By passwordField = By.id("password");
	By loginButton = By.id("submit");
	By errorMessage = By.id("error");

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterUsername(String username) {
		driver.findElement(usernameField).clear();
		driver.findElement(usernameField).sendKeys(username);
	}

	public void enterPassword(String password) {
		driver.findElement(passwordField).clear();
		driver.findElement(passwordField).sendKeys(password);
	}

	public void clickLogin() {
		driver.findElement(loginButton).click();
	}

	public String getErrorMessage() {
		return driver.findElement(errorMessage).getText();
	}
}
