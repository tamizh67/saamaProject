package utils;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

	WebDriver driver;
	public Properties props;
	protected AdBlocker adBlocker;

	public BaseTest() {
		props = new Properties();
		try {
			FileInputStream fis = new FileInputStream("src/main/java/config.properties");
			props.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public WebDriver getDriver() {
        return driver;
    }
	
	protected WebDriver initializeDriver() {
		String browserName = props.getProperty("browser").toLowerCase();
		boolean isHeadless = Boolean.parseBoolean(props.getProperty("headless"));

		if ("chrome".equals(browserName)) {
			WebDriverManager.chromedriver().setup();
			ChromeOptions chromeOptions = new ChromeOptions();
			if(isHeadless) chromeOptions.addArguments("--headless");
			 chromeOptions.addArguments("--remote-allow-origins=*");
	            chromeOptions.addArguments("--no-sandbox");
	            chromeOptions.addArguments("--disable-dev-shm-usage");
	            chromeOptions.addArguments("--disable-gpu");
	            chromeOptions.addArguments("--ignore-certificate-errors");
	            chromeOptions.setPageLoadTimeout(Duration.ofSeconds(60));
			driver = new ChromeDriver(chromeOptions);
		}
		else if ("firefox".equals(browserName)) {
			WebDriverManager.firefoxdriver().setup();
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			if(isHeadless) firefoxOptions.addArguments("--headless");
			driver = new FirefoxDriver(firefoxOptions);
		}
		else if ("edge".equals(browserName)) {
			WebDriverManager.edgedriver().setup();
			EdgeOptions edgeOptions = new EdgeOptions();
			if(isHeadless) edgeOptions.addArguments("--headless");
			driver = new EdgeDriver(edgeOptions);
		}
		else {
			throw new RuntimeException("Unsupported browser: " + browserName);
		}

		// Common setup for all browsers
		driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

		return driver;
	}

	protected void quitDriver() {
		if(driver != null) {
			driver.quit();
			driver = null;
		}
	}
}

