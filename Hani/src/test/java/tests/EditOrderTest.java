package tests;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class EditOrderTest extends BaseTest {

    // ===== LOGIN =====
    public void login() {
        driver.get("http://localhost:8000/login/");
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("hani12345");
        driver.findElement(By.cssSelector(".btn-auth-submit")).click();
    }

    public void goToOrderPage() {
        driver.get("http://localhost:8000/panel/orders/");
    }

    // mở modal edit (click icon sửa)
    public void openEdit() {
        driver.findElement(By.cssSelector(".fa-pencil-alt")).click();
    }

    // submit form
    public void submit() {
        driver.findElement(By.cssSelector("#admin-edit-form button[type='submit']")).click();
    }

    // ✅ TC01 - Update hợp lệ
    @Test
    public void FE14_TC01_validUpdate() {
        login(); goToOrderPage(); openEdit();

        driver.findElement(By.id("edit-cust-name")).clear();
        driver.findElement(By.id("edit-cust-name")).sendKeys("Test User");

        driver.findElement(By.id("edit-cust-phone")).clear();
        driver.findElement(By.id("edit-cust-phone")).sendKeys("0901234567");

        submit();

        Assert.assertTrue(driver.getPageSource().contains("thành công"));
    }

    // ❌ TC02 - Bỏ trống tên
    @Test
    public void FE14_TC02_emptyName() {
        login(); goToOrderPage(); openEdit();

        driver.findElement(By.id("edit-cust-name")).clear();
        submit();

        Assert.assertTrue(driver.getPageSource().contains("tên"));
    }

    // ❌ TC03 - SĐT sai
    @Test
    public void FE14_TC03_invalidPhone() {
        login(); goToOrderPage(); openEdit();

        driver.findElement(By.id("edit-cust-phone")).clear();
        driver.findElement(By.id("edit-cust-phone")).sendKeys("abc123");

        submit();

        Assert.assertTrue(driver.getPageSource().contains("điện thoại"));
    }

    // ❌ TC04 - Không có sản phẩm
    @Test
    public void FE14_TC04_noProduct() {
        login(); goToOrderPage(); openEdit();

        // xóa tất cả
        By deleteBtn = By.cssSelector("#edit-order-tbody .fa-trash");

        while (!driver.findElements(deleteBtn).isEmpty()) {
            driver.findElements(deleteBtn).get(0).click();
        }

        submit();

        // handle alert
        Alert alert = driver.switchTo().alert();

        String alertText = alert.getText();

        Assert.assertTrue(alertText.contains("ít nhất 1 sản phẩm"));

        alert.accept();
    }

    // ✅ TC05 - Thêm sản phẩm
    @Test
    public void FE14_TC05_addProduct() {
        login(); goToOrderPage(); openEdit();

        driver.findElement(By.id("edit-product-search-input"))
                .sendKeys("Váy");

        driver.findElement(By.cssSelector("#edit-product-search-results div"))
                .click();

        Assert.assertTrue(driver.getPageSource().contains("Váy"));
    }

    // ✅ TC06 - Xóa sản phẩm
    @Test
    public void FE14_TC06_deleteProduct() {
        login(); goToOrderPage(); openEdit();

        // đợi có sản phẩm
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.findElements(By.cssSelector("#edit-order-tbody tr")).size() > 0);

        // số dòng trước
        int before = driver.findElements(By.cssSelector("#edit-order-tbody tr")).size();

        // chọn 1 dòng
        WebElement row = driver.findElement(By.cssSelector("#edit-order-tbody tr"));

        // click xoá
        row.findElement(By.cssSelector(".fa-trash")).click();

        // đợi update DOM
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.findElements(By.cssSelector("#edit-order-tbody tr")).size() < before);

        // số dòng sau
        int after = driver.findElements(By.cssSelector("#edit-order-tbody tr")).size();

        // verify
        Assert.assertTrue(after < before);
    }

    // ❌ TC07 - SL vượt tồn kho
    @Test
    public void FE14_TC07_overStock() {
        login(); goToOrderPage(); openEdit();

        // đợi có sản phẩm
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.findElements(By.cssSelector("#edit-order-tbody tr")).size() > 0);

        WebElement row = driver.findElement(By.cssSelector("#edit-order-tbody tr"));

        // nút +
        WebElement plusBtn = row.findElements(By.cssSelector(".stepper-btn")).get(1);

        // click nhiều lần để vượt tồn kho
        for (int i = 0; i < 30; i++) {
            plusBtn.click();
        }

        submit();

        Assert.assertTrue(driver.getPageSource().contains("tồn kho"));
    }

    // ✅ TC08 - Đổi size
    @Test
    public void FE14_TC08_changeSize() {
        login(); goToOrderPage(); openEdit();

        WebElement row = driver.findElement(By.cssSelector("#edit-order-tbody tr"));

        WebElement sizeSelect = row.findElement(By.cssSelector("select[name='sizes[]']"));

        Select select = new Select(sizeSelect);

        String before = select.getFirstSelectedOption().getText();

        select.selectByVisibleText("L");

        String after = select.getFirstSelectedOption().getText();

        Assert.assertNotEquals(before, after);

        submit();
    }

    // ✅ TC09 - Đổi màu
    @Test
    public void FE14_TC9_changeColor() {
        login(); goToOrderPage(); openEdit();

        // đợi có sản phẩm
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.findElements(By.cssSelector("#edit-order-tbody tr")).size() > 0);

        WebElement row = driver.findElement(By.cssSelector("#edit-order-tbody tr"));

        // dropdown màu
        WebElement colorSelectEl = row.findElement(By.cssSelector("select[name='colors[]']"));
        Select colorSelect = new Select(colorSelectEl);

        // lấy giá trị trước
        String before = colorSelect.getFirstSelectedOption().getText();

        // đổi màu (ví dụ: Trắng)
        colorSelect.selectByVisibleText("Trắng");

        // lấy giá trị sau
        String after = colorSelect.getFirstSelectedOption().getText();

        // verify đã đổi
        Assert.assertNotEquals(before, after);

        submit();
    }

    // ✅ TC10 - Payment method
    @Test
    public void FE14_TC10_paymentMethod() {
        login(); goToOrderPage(); openEdit();

        driver.findElements(By.name("payment")).get(1).click();

        submit();

        Assert.assertTrue(driver.getPageSource().contains("thành công"));
    }

    // ✅ TC11 - Discount
    @Test
    public void FE14_TC11_discount() {
        login(); goToOrderPage(); openEdit();

        driver.findElement(By.id("edit-discount-selector"))
                .sendKeys("WELCOME10");

        submit();

        Assert.assertTrue(driver.getPageSource().contains("thành công"));
    }

    // ✅ TC12 - Total auto update
    @Test
    public void FE14_TC12_totalChange() {
        login(); goToOrderPage(); openEdit();

        // đợi có sản phẩm
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.findElements(By.cssSelector("#edit-order-tbody tr")).size() > 0);

        WebElement row = driver.findElement(By.cssSelector("#edit-order-tbody tr"));

        // lấy total trước
        WebElement totalEl = driver.findElement(By.id("edit-txt-total"));
        String before = totalEl.getText();

        // click +
        WebElement plusBtn = row.findElements(By.cssSelector(".stepper-btn")).get(1);
        plusBtn.click();

        // đợi total thay đổi
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> !d.findElement(By.id("edit-txt-total")).getText().equals(before));

        String after = driver.findElement(By.id("edit-txt-total")).getText();

        // verify
        Assert.assertNotEquals(before, after);
    }

    // ✅ TC13 - Không thay đổi
    @Test
    public void FE14_TC13_noChange() {
        login(); goToOrderPage(); openEdit();

        submit();

        Assert.assertTrue(driver.getPageSource().contains("thành công"));
    }

    // ❌ TC14 - Hủy
    @Test
    public void FE14_TC14_cancel() {
        login(); goToOrderPage(); openEdit();

        WebElement modal = driver.findElement(By.id("edit-order-modal"));

        // click nút Huỷ
        modal.findElement(By.xpath(".//button[contains(text(),'Huỷ')]")).click();

        // đợi modal ẩn
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> !modal.isDisplayed());

        Assert.assertFalse(modal.isDisplayed());
    }
}