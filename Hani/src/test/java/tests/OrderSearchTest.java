package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OrderSearchTest extends BaseTest {

    // ===== LOGIN =====
    public void login() {
        driver.get("http://localhost:8000/login/");

        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("hani12345");
        driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    }

    // ===== GO TO ORDER PAGE =====
    public void goToOrderPage() {
        driver.get("http://localhost:8000/panel/orders/");
    }

    // ===== SEARCH (FIXED) =====
    public void search(String keyword) {

        WebElement searchBox = driver.findElement(
                By.cssSelector(".topbar-search input[name='q']")
        );

        searchBox.click();
        searchBox.clear();
        searchBox.sendKeys(keyword);
        searchBox.sendKeys(Keys.ENTER);
    }

    // ✅ TC01 – Tìm theo mã đơn
    @Test
    public void FE12_TC01_searchExactOrderCode() {
        login();
        goToOrderPage();

        search("HD000000071");

        Assert.assertTrue(driver.getPageSource().contains("HD000000071"));
    }

    // ✅ TC02 – Không tồn tại
    @Test
    public void FE12_TC02_searchNotExist() {
        login();
        goToOrderPage();

        search("HD000000099");

        Assert.assertTrue(driver.getPageSource().contains("0 đơn hàng"));
    }

    // ✅ TC03 – Tên chính xác
    @Test
    public void FE12_TC03_searchExactName() {
        login();
        goToOrderPage();

        search("Lê Diệp Anh");

        Assert.assertTrue(driver.getPageSource().contains("Lê Diệp Anh"));
    }

    // ✅ TC04 – Gần đúng
    @Test
    public void FE12_TC04_searchPartialName() {
        login();
        goToOrderPage();

        search("Nguyễn");

        Assert.assertTrue(driver.getPageSource().contains("Nguyễn"));
    }

    // ✅ TC05 – Rỗng
    @Test
    public void FE12_TC05_emptySearch() {
        login();
        goToOrderPage();

        search("");

        Assert.assertTrue(driver.getPageSource().contains("đơn hàng"));
    }

    // ✅ TC06 – Ký tự đặc biệt
    @Test
    public void FE12_TC06_specialChar() {
        login();
        goToOrderPage();

        search("@@@###");

        Assert.assertTrue(driver.getPageSource().contains("0 đơn hàng"));
    }

    // ✅ TC07 – Khoảng trắng
    @Test
    public void FE12_TC07_whitespace() {
        login();
        goToOrderPage();

        search("   ");

        Assert.assertTrue(driver.getPageSource().contains("đơn hàng"));
    }

    // ✅ TC08 – Không phân biệt hoa thường
    @Test
    public void FE12_TC08_caseInsensitive() {
        login();
        goToOrderPage();

        search("phương linh");

        Assert.assertTrue(driver.getPageSource().toLowerCase().contains("phương linh"));
    }

    // ✅ TC09 – Filter + Search
    @Test
    public void FE12_TC09_searchWithStatus() {
        login();
        goToOrderPage();

        // click tab "Đang giao"
        driver.findElement(
                By.xpath("//a[contains(text(),'Đang giao')]")
        ).click();

        search("phương linh");

        Assert.assertTrue(driver.getPageSource().contains("Đang giao"));
    }
}