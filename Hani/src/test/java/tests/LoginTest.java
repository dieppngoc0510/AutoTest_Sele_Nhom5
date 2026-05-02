package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    // ✅ TC01 – Login thành công
    @Test
    public void FE02_TC01_loginSuccess() {

        LoginPage page = new LoginPage(driver);

        page.goToLoginPage();
        page.login("admin", "hani12345");

        Assert.assertTrue(page.isLoginSuccess(), "Không chuyển về trang chủ");
    }

    // ❌ TC02 – Sai mật khẩu
    @Test
    public void FE02_TC02_wrongPassword() {

        LoginPage page = new LoginPage(driver);

        page.goToLoginPage();
        page.login("testuser", "saimatkhau");

        String toast = driver.findElement(By.className("toast-message")).getText();

        Assert.assertTrue(toast.contains("không chính xác"));
    }

    // ❌ TC03 – Tài khoản không tồn tại
    @Test
    public void FE02_TC03_userNotExist() {

        LoginPage page = new LoginPage(driver);

        page.goToLoginPage();
        page.login("khongtontai", "123456");

        String toast = driver.findElement(By.className("toast-message")).getText();

        Assert.assertTrue(toast.contains("không tồn tại") || toast.contains("không chính xác"));
    }

    // ❌ TC04 – Bỏ trống username
    @Test
    public void FE02_TC04_emptyUsername() {

        LoginPage page = new LoginPage(driver);

        page.goToLoginPage();
        page.login("", "123456");

        String msg = driver.findElement(By.name("username"))
                .getAttribute("validationMessage");

        Assert.assertTrue(msg.length() > 0);
    }

    // ❌ TC05 – Bỏ trống password
    @Test
    public void FE02_TC05_emptyPassword() {

        LoginPage page = new LoginPage(driver);

        page.goToLoginPage();
        page.login("testuser", "");

        String msg = driver.findElement(By.name("password"))
                .getAttribute("validationMessage");

        Assert.assertTrue(msg.length() > 0);
    }

    // ❌ TC06 – Bỏ trống cả 2
    @Test
    public void FE02_TC06_emptyAll() {

        LoginPage page = new LoginPage(driver);

        page.goToLoginPage();
        page.login("", "");

        String msg = driver.findElement(By.name("username"))
                .getAttribute("validationMessage");

        Assert.assertTrue(msg.length() > 0);
    }

    // ❌ TC07 – Nhập khoảng trắng
    @Test
    public void FE02_TC07_whitespaceInput() {

        LoginPage page = new LoginPage(driver);

        page.goToLoginPage();
        page.login("   ", "   ");

        String toast = driver.findElement(By.className("toast-message")).getText();

        Assert.assertTrue(toast.contains("không chính xác"));
    }
}