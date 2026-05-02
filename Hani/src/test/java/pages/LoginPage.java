package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // ===== LOCATORS =====
    By loginLink = By.linkText("Đăng nhập");
    By username = By.name("username");
    By password = By.name("password");
    By loginBtn = By.cssSelector(".btn-auth-submit");

    By usernameDisplay = By.className("username-display");
    By toast = By.className("toast-message");

    // ===== ACTION =====
    public void goToLoginPage() {
        driver.findElement(loginLink).click();
    }

    public void login(String user, String pass) {
        driver.findElement(username).clear();
        driver.findElement(password).clear();

        driver.findElement(username).sendKeys(user);
        driver.findElement(password).sendKeys(pass);
        driver.findElement(loginBtn).click();
    }

    // ===== VERIFY =====
    public boolean isLoginSuccess() {
        return driver.findElements(usernameDisplay).size() > 0;
    }

    public boolean isLoginFail() {
        return driver.findElements(toast).size() > 0;
    }
}