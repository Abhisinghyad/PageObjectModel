package tests;

import com.aventstack.extentreports.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;
import pages.LoginPage;
import project.ExcelUtils;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

public class LoginTest {

    WebDriver driver;
    LoginPage loginPage;
    ExtentReports extent;
    ExtentTest test;
    private static final Logger logger = LogManager.getLogger(LoginTest.class);

    @BeforeSuite
    public void setupReport() {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + "/src/test/resources/log4j2.xml");
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(
                System.getProperty("user.dir") + "/test-output/ExtentReports/LoginTestReport.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        logger.info("===== Test Suite Started =====");
    }

    @BeforeMethod
    public void setup() {
    	   logger.info("Launching Chrome Browser in Headless Mode...");
    	    
    	    ChromeOptions options = new ChromeOptions();
    	    options.addArguments("--headless");    //  Headless mode
    	    options.addArguments("--window-size=1920,1080");  //  Recommended window size
    	    options.addArguments("--disable-gpu"); //  (for Windows systems sometimes required)
    	    options.addArguments("--remote-allow-origins=*"); //  (for Chrome latest versions)
    	    
    	    driver = new ChromeDriver(options);
    	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    	    driver.manage().window().maximize();
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {
        logger.info("Fetching Login Data from Excel...");
        String projectDir = System.getProperty("user.dir");
        String excelPath = projectDir + "\\src\\test\\resources\\LoginData.xlsx";
        return ExcelUtils.getTestData(excelPath, "Sheet1");
    }

    @Test(dataProvider = "loginData")
    public void loginTest(String username, String password) throws IOException {
        logger.info("Starting Login Test for user: " + username);

        driver.get("https://www.instagram.com/");
        test = extent.createTest("Login Test for user: " + username +" Password: "+password);

        loginPage = new LoginPage(driver);
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        try {
            Thread.sleep(3000); // Use explicit wait later
        } catch (InterruptedException e) {
            logger.error("Interrupted Exception", e);
        }

        if (loginPage.isPasswordIncorrect()) {
            logger.error(" Login failed for Username: " + username);
            System.out.println(" Login failed for Username: " + username);
            test.fail(" Login failed for Username: " + username);

            String screenshotPath = takeScreenshot(username);
            test.addScreenCaptureFromPath(screenshotPath);
        } else {
            logger.info(" Login successful for Username: " + username +" Password: "+password);
            System.out.println("Login successful for Username: " + username +" Password: "+password);
            test.pass(" Login successful for Username: " + username);
            // Write successful login credentials to ValidData file
            String validDataFilePath = System.getProperty("user.dir") + "/src/test/resources/ValidData.xlsx";
            ExcelUtils.writeValidCredentials(username, password, validDataFilePath);
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            logger.info("Closing Browser...");
            driver.quit();
        }
    }

    @AfterSuite
    public void tearDownReport() {
        logger.info("Flushing Extent Report...");
        extent.flush();
        
        logger.info("Sending email with Test Report...");
        String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReports/LoginTestReport.html";
        String toEmail = "abhi.singhyad@gmail.com";  
        String subject = "Automated Test Report - Login Module";
        String body = "Hello,\n\nPlease find attached the test execution report.\n\nRegards,\n Abhishek Singh";

        project.Email.sendEmailWithAttachment(toEmail, subject, body, reportPath);

        logger.info("===== Test Suite Finished =====");
    }

    // Utility method
    public String takeScreenshot(String username) {
        logger.info("Taking Screenshot for user: " + username);
        String screenshotDir = System.getProperty("user.dir") + "/test-output/Screenshots/";
        String screenshotPath = screenshotDir + username + "_" + System.currentTimeMillis() + ".png";

        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotPath);
            Files.createDirectories(Paths.get(screenshotDir));
            Files.copy(scrFile.toPath(), destFile.toPath());
        } catch (IOException e) {
            logger.error("Error while taking screenshot", e);
        }
        return screenshotPath;
    }
}
