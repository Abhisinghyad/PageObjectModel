package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.ExcelUtils;  // Add this import

public class LoginPage {

    WebDriver driver;
    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    // Locators
    private By usernameField = By.name("username");
    private By passwordField = By.name("password");
    private By loginButton = By.xpath("/html/body/div[1]/div/div/div[2]/div/div/div[1]/div[1]/div/section/main/article/div[2]/div[1]/div[2]/div/form/div[1]/div[3]");
    private By errorMessage = By.xpath("//*[contains(text(),'Sorry, your password was incorrect.')]");

    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // Actions
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

    public boolean isPasswordIncorrect() {
        try {
            WebElement error = driver.findElement(errorMessage);
            return error.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void loginForMultipleUsers(String[][] credentials) {
        for (String[] user : credentials) {
            String username = user[0];
            String password = user[1];

            logger.info("Trying with Username: " + username);

            // Enter login details
            enterUsername(username);
            enterPassword(password);
            clickLogin();

            // Explicit wait for login to process
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                // Wait for an element that signifies the user is logged in
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//some_logged_in_element_xpath")));
                logger.info("Login successful with Username: " + username);
                System.out.println(" Correct Credentials: Username = " + username + " | Password = " + password);

                // Write the correct credentials into Excel
                String projectPath = System.getProperty("user.dir");
                String outputExcelPath = projectPath + "\\src\\test\\resources\\ValidLogin.xlsx";
                ExcelUtils.writeValidCredentials(username, password, outputExcelPath);

                break; // Stop further attempts after a successful login
            } catch (Exception e) {
                if (isPasswordIncorrect()) {
                    logger.error("Incorrect password for Username: " + username);
                } else {
                    logger.error("Login failed for Username: " + username);
                }
                continue; // Try next credentials if login fails
            }
        }
    }

}
