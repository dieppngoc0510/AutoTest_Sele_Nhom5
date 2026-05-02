package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RegisterPage;

public class RegisterTest extends BaseTest {

    // ✅ TC01 – Đăng ký thành công
    @Test
    public void FE01_TC01_registerSuccess() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("hoaioanh.ng", "Nguyen Thi Hoai Oanh", "oanhhoai123@gmail.com", "0712345678", "12345678");

        Assert.assertTrue(page.getToastMessage().contains("thành công"));
    }

    // ❌ TC02 – Thiếu username
    @Test
    public void FE01_TC02_missingUsername() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("", "Nguyen Van A", "user@gmail.com", "0912345678", "12345678");

        Assert.assertTrue(page.getUsernameValidation().length() > 0);
    }

    // ❌ TC03 – Thiếu fullname
    @Test
    public void FE01_TC03_missingFullname() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("user1", "", "user@gmail.com", "0912345678", "12345678");

        Assert.assertTrue(page.getFullnameValidation().length() > 0);
    }

    // ❌ TC04 – Email sai format
    @Test
    public void FE01_TC04_invalidEmail() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("user1", "Nguyen Van A", "abc123", "0912345678", "12345678");

        Assert.assertTrue(page.getEmailValidation().contains("@"));
    }

    // ❌ TC05 – SĐT không đủ 10 số
    @Test
    public void FE01_TC05_phoneLessThan10() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("user1", "Nguyen Van A", "user@gmail.com", "12345", "12345678");

        Assert.assertTrue(page.getPhoneValidation().length() > 0);
    }

    // ❌ TC06 – SĐT chứa chữ
    @Test
    public void FE01_TC06_phoneContainsText() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("user1", "Nguyen Van A", "user@gmail.com", "abc1234567", "12345678");

        Assert.assertTrue(page.getPhoneValidation().length() > 0);
    }

    // ❌ TC07 – Thiếu password
    @Test
    public void FE01_TC07_missingPassword() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("user1", "Nguyen Van A", "user@gmail.com", "0912345678", "");

        Assert.assertTrue(page.getPasswordValidation().length() > 0);
    }

    // ❌ TC08 – Bỏ trống tất cả
    @Test
    public void FE01_TC08_emptyAll() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("", "", "", "", "");

        Assert.assertTrue(page.getUsernameValidation().length() > 0);
    }

    // ❌ TC09 – Username đã tồn tại
    @Test
    public void FE01_TC09_usernameExists() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("admin", "Nguyen Hoai Anh", "user@gmail.com", "0912345678", "12345678");

        Assert.assertTrue(page.getToastMessage().contains("đã tồn tại"));
    }

    // ❌ TC10 – Nhập khoảng trắng
    @Test
    public void FE01_TC10_whitespaceInput() {

        RegisterPage page = new RegisterPage(driver);

        page.goToRegisterPage();
        page.register("   ", "   ", "   ", "   ", "   ");

        Assert.assertTrue(page.getUsernameValidation().length() > 0);
    }
}