package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {

    WebDriver driver;
    WebDriverWait wait;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ===== LOCATORS =====
    By registerLink = By.linkText("Đăng ký");

    By username = By.name("username");
    By fullname = By.name("fullname");
    By email = By.name("email");
    By phone = By.name("phone");
    By password = By.name("password");

    By submitBtn = By.cssSelector(".btn-auth-submit");

    By toastMessage = By.className("toast-message");

    // ===== ACTION =====

    public void goToRegisterPage() {
        wait.until(ExpectedConditions.elementToBeClickable(registerLink)).click();
    }

    public void enterUsername(String user) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(username)).clear();
        driver.findElement(username).sendKeys(user);
    }

    public void enterFullname(String name) {
        driver.findElement(fullname).clear();
        driver.findElement(fullname).sendKeys(name);
    }

    public void enterEmail(String mail) {
        driver.findElement(email).clear();
        driver.findElement(email).sendKeys(mail);
    }

    public void enterPhone(String phoneNum) {
        driver.findElement(phone).clear();
        driver.findElement(phone).sendKeys(phoneNum);
    }

    public void enterPassword(String pass) {
        driver.findElement(password).clear();
        driver.findElement(password).sendKeys(pass);
    }

    public void clickRegister() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(submitBtn));

        // scroll tới button
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);

        // wait clickable
        wait.until(ExpectedConditions.elementToBeClickable(btn));

        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            // fallback: click bằng JS
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void register(String user, String name, String mail, String phoneNum, String pass) {
        enterUsername(user);
        enterFullname(name);
        enterEmail(mail);
        enterPhone(phoneNum);
        enterPassword(pass);
        clickRegister();
    }

    // ===== VERIFY =====

    public String getToastMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(toastMessage)).getText();
    }

    public boolean isToastDisplayed() {
        return driver.findElements(toastMessage).size() > 0;
    }

    // ===== VALIDATION =====

    public String getUsernameValidation() {
        return driver.findElement(username).getAttribute("validationMessage");
    }

    public String getFullnameValidation() {
        return driver.findElement(fullname).getAttribute("validationMessage");
    }

    public String getEmailValidation() {
        return driver.findElement(email).getAttribute("validationMessage");
    }

    public String getPhoneValidation() {
        return driver.findElement(phone).getAttribute("validationMessage");
    }

    public String getPasswordValidation() {
        return driver.findElement(password).getAttribute("validationMessage");
    }
}