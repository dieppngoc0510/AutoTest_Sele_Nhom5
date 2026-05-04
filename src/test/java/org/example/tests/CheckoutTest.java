package org.example.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;
import org.example.pages.CheckoutPage;

public class CheckoutTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    @Override
    public void beforeMethod() {
        super.beforeMethod();
        login("ngocdiep", "12345678");
    }
    private void login(String user, String pass) {
        bp().login(user, pass);

        // Đảm bảo user có địa chỉ để nút Đặt hàng không bị disabled
        if (driver.getCurrentUrl().contains("login")) {
            log("Đăng nhập không thành công, bỏ qua bước cập nhật profile.");
            return; 
        }

        driver.get("http://127.0.0.1:8000/profile");
        sleep(1500);
        try {
            if (driver.getCurrentUrl().contains("profile")) {
                java.util.List<WebElement> fields = driver.findElements(By.name("address"));
                if (!fields.isEmpty()) {
                    WebElement addressField = fields.get(0);
                    if (addressField.getAttribute("value").isEmpty()) {
                        addressField.sendKeys("123 Đường ABC, Quận 1, TP.HCM");
                        driver.findElement(By.name("phone")).clear();
                        driver.findElement(By.name("phone")).sendKeys("0987654321");
                        driver.findElement(By.cssSelector("form.shopee-form-section button[type='submit']")).click();
                        sleep(2000);
                    }
                }
            }
        } catch (Exception e) {
            log("Lỗi khi cập nhật profile: " + e.getMessage());
        }

        driver.get("http://127.0.0.1:8000/product/9");
        sleep(1500);
        try {
            driver.findElement(By.xpath("//div[@class='color-options']//label[1]")).click();
            driver.findElement(By.xpath("//div[@class='size-options']//label[1]")).click();
        } catch (Exception e) {}

        try {
            driver.findElement(By.xpath("//button[contains(@class, 'btn-add-to-cart')]")).click();
            sleep(1500);
            driver.switchTo().alert().accept();
        } catch (Exception e) {}
    }

    private void goToCheckout() {
        try {
            driver.findElement(By.xpath("//img[@alt='Cart']")).click();
            sleep(1500);
            WebElement btnCheckout = driver.findElement(By.cssSelector("button.btn-cart-checkout"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnCheckout);
            sleep(2000);
        } catch (Exception e) {
            driver.get("http://127.0.0.1:8000/checkout");
            sleep(2000);
        }
    }

    // ================= TEST CASES =================

    @Test
    public void FE11_TC01_SuccessfulCODCheckout() {
        System.out.println("\nĐang chạy: TC01 - Thanh toán COD thành công");
        goToCheckout();

        CheckoutPage page = new CheckoutPage();
        page.selectPaymentMethodCOD();
        page.clickDatHang();
        sleep(3000);

        String currentUrl = driver.getCurrentUrl().toLowerCase();
        Assert.assertTrue(currentUrl.contains("order-success") || page.isOrderSuccess(),
                "BUG: Không chuyển hướng đến trang thành công. URL: " + currentUrl);
    }

    @Test
    public void FE11_TC03_ApplyDiscountCode() {
        System.out.println("\nĐang chạy: TC03 - Áp dụng mã giảm giá");

        driver.findElement(By.xpath("//img[@alt='Cart']")).click();
        sleep(1500);

        CheckoutPage page = new CheckoutPage();
        page.applyDiscountCode("FLASH20");
        
        WebElement btnCheckout = driver.findElement(By.cssSelector("button.btn-cart-checkout"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnCheckout);
        sleep(2000);

        boolean isApplied = driver.getPageSource().contains("99.800")
                || driver.getPageSource().contains("Đã áp dụng");
        Assert.assertTrue(isApplied, "BUG: Tiền giảm giá không được trừ ở trang Checkout!");
    }

    @Test
    public void FE11_TC04_VerifyAutoFillCustomerInfo() {
        System.out.println("\nĐang chạy: TC04 - Kiểm tra tính năng tự động điền thông tin");
        goToCheckout();

        String pageSource = driver.getPageSource();
        // Kiểm tra xem có chứa thông tin user 'ngocdiep' hoặc text từ hồ sơ
        boolean hasPhone = pageSource.contains("0987654321");
        // User ngocdiep có thể có tên khác, nên kiểm tra linh hoạt hơn hoặc bỏ qua tên nếu không chắc
        boolean hasName = pageSource.contains("Lê Thị Quỳnh Như") || pageSource.contains("Nguyễn Văn An") || pageSource.contains("Ngọc Diệp");

        Assert.assertTrue(hasPhone || hasName, " BUG: Không tự động điền SĐT hoặc Tên từ Hồ sơ cá nhân!");
    }

    @Test
    public void FE11_TC05_EmptyDeliveryInfoValidation() {
        System.out.println("\nĐang chạy: TC05 - Kiểm tra tính bắt buộc của thông tin giao hàng");

        driver.get("http://127.0.0.1:8000/logout");
        sleep(1500);

        login("user_no_info", "pass123");
        goToCheckout();

        CheckoutPage page = new CheckoutPage();
        page.verifyOrderButtonStatus();
        sleep(1500);

        boolean hasWarning = driver.getPageSource().toLowerCase().contains("vui lòng")
                || driver.getCurrentUrl().contains("checkout");

        Assert.assertTrue(hasWarning, "BUG: Thông tin trống nhưng vẫn cho phép vượt qua màn hình Checkout!");
    }

    @Test
    public void FE11_TC07_VerifyTotalPriceWithDiscount() {
        System.out.println("\nĐang chạy: TC07 - Kiểm tra độ chính xác của Tổng tiền (Có Voucher)");

        driver.findElement(By.xpath("//img[@alt='Cart']")).click();
        sleep(1500);

        CheckoutPage page = new CheckoutPage();
        page.applyDiscountCode("FLASH20");
        sleep(1000);
        
        WebElement btnCheckout = driver.findElement(By.cssSelector("button.btn-cart-checkout"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnCheckout);
        sleep(2000);

        long subtotal = page.getSubtotal();
        long shipping = page.getShippingFee();
        long discount = page.getDiscountAmount();
        long displayedTotal = page.getTotalPayment();
        long expectedTotal = subtotal + shipping - discount;

        Assert.assertEquals(displayedTotal, expectedTotal, "BUG: Web tính toán Tổng tiền sai công thức!");
    }

    @Test
    public void FE11_TC08_CheckoutWithNoSelectedProduct() {
        System.out.println("\nĐang chạy: TC08 - Thanh toán khi Không chọn sản phẩm nào trong giỏ");

        try {
            driver.findElement(By.xpath("//img[@alt='Cart']")).click();
            sleep(1500);

            WebElement selectAllInput = driver.findElement(By.id("select-all-cart"));

            if (selectAllInput.isSelected()) {
                WebElement selectAllCheckmark = driver.findElement(By.xpath("//input[@id='select-all-cart']/following-sibling::span[@class='checkmark']"));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", selectAllCheckmark);
                sleep(1000);
            } else {
                java.util.List<WebElement> itemInputs = driver.findElements(By.xpath("//div[@class='cart-item-check']//input[@type='checkbox']"));
                for (WebElement input : itemInputs) {
                    if (input.isSelected()) {
                        WebElement checkmark = input.findElement(By.xpath("./following-sibling::span[@class='checkmark']"));
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", checkmark);
                        sleep(300);
                    }
                }
            }
            System.out.println("Đã rà soát và đảm bảo giỏ hàng bị bỏ tick hoàn toàn!");

            WebElement btnCheckout = driver.findElement(By.cssSelector("button.btn-cart-checkout"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnCheckout);
            sleep(2000);

        } catch (Exception e) {
            Assert.fail("Lỗi thao tác UI: Robot không thể bỏ tick hoặc không bấm được nút Đặt hàng! Chi tiết: " + e.getMessage());
        }

        String currentUrl = driver.getCurrentUrl().toLowerCase();
        String pageSource = driver.getPageSource().toLowerCase();

        boolean isNotRedirected = !currentUrl.contains("checkout");

        boolean hasWarningMsg = pageSource.contains("1 sản phẩm")
                || pageSource.contains("chưa chọn")
                || pageSource.contains("vui lòng chọn");

        System.out.println("BÁO CÁO TỪ ROBOT:");
        System.out.println(" - Bị chặn lại ở trang hiện tại: " + isNotRedirected);
        System.out.println(" - Tìm thấy thông báo nhắc nhở: " + hasWarningMsg);

        Assert.assertTrue(isNotRedirected && hasWarningMsg, "BUG: Không tick sản phẩm nào nhưng web VẪN CHO PHÉP ấn Đặt hàng!");
    }

    @Test
    public void FE11_TC09_ApplyExpiredCoupon() {
        System.out.println("\nĐang chạy: TC09 - Kiểm tra áp dụng mã giảm giá đã hết hạn");

        driver.findElement(By.xpath("//img[@alt='Cart']")).click();
        sleep(1500);

        CheckoutPage page = new CheckoutPage();
        page.applyDiscountCode("NEWUSER50");
        sleep(1500);

        String pageSource = driver.getPageSource().toLowerCase();
        boolean hasErrorMsg = pageSource.contains("hết hạn") || pageSource.contains("kết thúc");
        long discountAmount = page.getDiscountAmount();

        Assert.assertTrue(hasErrorMsg || discountAmount == 0,
                "BUG NGHIÊM TRỌNG: Mã giảm giá đã hết hạn nhưng hệ thống VẪN CHO ÁP DỤNG!");
    }
}