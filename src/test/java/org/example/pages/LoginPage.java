package org.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    @FindBy(css = "input[placeholder='Tên đăng nhập hoặc Số điện thoại']")
    private WebElement usernameField;

    @FindBy(css = "input[placeholder='Mật khẩu']")
    private WebElement passwordField;

    @FindBy(css = "button.btn-auth-submit")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        wait.until(ExpectedConditions.visibilityOf(usernameField)).sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
    }
}
