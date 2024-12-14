package reporting;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.BaseTest;
import utils.EmailSender;
import java.util.HashSet;
import java.util.Set;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;

public class TestListener implements ITestListener {
    
    private static ExtentReports extent = ExtentReportManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();
    private String reportPath;
    private String screenshotsDir;
    // Add a Set to track failed tests
    private Set<String> failedTests = new HashSet<>();

    @Override
    public void onStart(ITestContext context) {
        String baseDir = System.getProperty("user.dir");
        reportPath = baseDir + File.separator + "test-output" + File.separator + "ExtentReport.html";
        screenshotsDir = baseDir + File.separator + "test-output" + File.separator + "screenshots";
        new File(screenshotsDir).mkdirs();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        ExtentTest extentTest = extent.createTest(methodName);
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Check if we've already handled this failure
        String testName = result.getName();
        if (failedTests.contains(testName)) {
            return;
        }
        
        failedTests.add(testName);
        test.get().fail(result.getThrowable());

        try {
            WebDriver driver = ((BaseTest)result.getInstance()).getDriver();
            if (driver != null) {
                String screenshotPath = takeScreenshot(driver, testName);
                if (screenshotPath != null) {
                    test.get().addScreenCaptureFromPath(screenshotPath);
                    System.out.println("Screenshot added to report: " + screenshotPath);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to capture/attach screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        
        try {
            // Wait for report to be generated
            Thread.sleep(1000);
            
            // Clear the screenshots directory of old files before sending email
            cleanScreenshotsDirectory();
            
            System.out.println("Sending email with test report and screenshots...");
            EmailSender.sendTestReport(reportPath, screenshotsDir);
            
        } catch (Exception e) {
            System.out.println("Failed to send email report: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clear the failed tests set
            failedTests.clear();
        }
    }

    private String takeScreenshot(WebDriver driver, String testName) {
        try {
            // Create a unique filename using just the test name
            String fileName = testName + ".png";
            String fullPath = screenshotsDir + File.separator + fileName;
            
            // Delete existing screenshot if it exists
            File existingFile = new File(fullPath);
            if (existingFile.exists()) {
                existingFile.delete();
            }
            
            // Take new screenshot
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(fullPath);
            FileUtils.copyFile(source, destination);
            
            System.out.println("Screenshot saved at: " + fullPath);
            return fullPath;
            
        } catch (Exception e) {
            System.out.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void cleanScreenshotsDirectory() {
        File screenshotsFolder = new File(screenshotsDir);
        if (screenshotsFolder.exists() && screenshotsFolder.isDirectory()) {
            // Delete all files except the ones for current failed tests
            File[] files = screenshotsFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean shouldKeep = false;
                    for (String testName : failedTests) {
                        if (file.getName().startsWith(testName)) {
                            shouldKeep = true;
                            break;
                        }
                    }
                    if (!shouldKeep) {
                        file.delete();
                    }
                }
            }
        }
    }
}