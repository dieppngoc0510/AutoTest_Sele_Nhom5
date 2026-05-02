package org.example.tests;

import org.example.tests.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;
import org.example.pages.CheckoutPage;

public class CheckoutTest {

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--remote-allow-origins=*");

        Constant.WEBDRIVER.set(new ChromeDriver(options));
        Constant.WEBDRIVER.get().manage().window().maximize();

        login("qnhu", "qnhu123");
    }

    @AfterMethod
    public void tearDown() {
        if (Constant.WEBDRIVER.get() != null) {
            Constant.WEBDRIVER.get().quit();
            Constant.WEBDRIVER.remove();
        }
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (Exception e) {}
    }

    private void login(String user, String pass) {
        Constant.WEBDRIVER.get().get("http://127.0.0.1:8000/login");
        sleep(1000);
        Constant.WEBDRIVER.get().findElement(By.name("username")).sendKeys(user);
        Constant.WEBDRIVER.get().findElement(By.name("password")).sendKeys(pass);
        Constant.WEBDRIVER.get().findElement(By.name("password")).submit();
        sleep(2000);

        Constant.WEBDRIVER.get().get("http://127.0.0.1:8000/product/9");
        sleep(1500);
        try {
            Constant.WEBDRIVER.get().findElement(By.xpath("//div[@class='color-options']//label[1]")).click();
            Constant.WEBDRIVER.get().findElement(By.xpath("//div[@class='size-options']//label[1]")).click();
        } catch (Exception e) {}

        Constant.WEBDRIVER.get().findElement(By.xpath("//button[contains(@class, 'btn-add-to-cart')]")).click();
        sleep(1500);
        try { Constant.WEBDRIVER.get().switchTo().alert().accept(); } catch (Exception e) {}
    }

    private void goToCheckout() {
        try {
            Constant.WEBDRIVER.get().findElement(By.xpath("//img[@alt='Cart']")).click();
            sleep(1500);
            Constant.WEBDRIVER.get().findElement(By.cssSelector("button.btn-cart-checkout")).click();
            sleep(2000);
        } catch (Exception e) {
            Constant.WEBDRIVER.get().get("http://127.0.0.1:8000/checkout");
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

        String currentUrl = Constant.WEBDRIVER.get().getCurrentUrl().toLowerCase();
        Assert.assertTrue(currentUrl.contains("order-success") || page.isOrderSuccess(),
                "BUG: Không chuyển hướng đến trang thành công. URL: " + currentUrl);
    }

    @Test
    public void FE11_TC02_SuccessfulQRCheckout() {
        System.out.println("\nĐang chạy: TC02 - Thanh toán bằng mã QR");
        goToCheckout();

        CheckoutPage page = new CheckoutPage();
        page.selectPaymentMethodQR();

        page.clickDatHang();
        sleep(3000);
        Assert.assertTrue(page.isQRCodeDisplayed(), "BUG: Đặt hàng QR thất bại, không hiển thị mã!");
    }

    @Test
    public void FE11_TC03_ApplyDiscountCode() {
        System.out.println("\nĐang chạy: TC03 - Áp dụng mã giảm giá");

        Constant.WEBDRIVER.get().findElement(By.xpath("//img[@alt='Cart']")).click();
        sleep(1500);

        CheckoutPage page = new CheckoutPage();
        page.applyDiscountCode("FLASH20");
        Constant.WEBDRIVER.get().findElement(By.cssSelector("button.btn-cart-checkout")).click();
        sleep(2000);

        boolean isApplied = Constant.WEBDRIVER.get().getPageSource().contains("99.800")
                || Constant.WEBDRIVER.get().getPageSource().contains("Đã áp dụng");
        Assert.assertTrue(isApplied, "BUG: Tiền giảm giá không được trừ ở trang Checkout!");
    }

    @Test
    public void FE11_TC04_VerifyAutoFillCustomerInfo() {
        System.out.println("\nĐang chạy: TC04 - Kiểm tra tính năng tự động điền thông tin");
        goToCheckout();

        String pageSource = Constant.WEBDRIVER.get().getPageSource();
        // Thay bằng thông tin thật của tài khoản 'qnhu'
        boolean hasPhone = pageSource.contains("0987654321");
        boolean hasName = pageSource.contains("Lê Thị Quỳnh Như");

        Assert.assertTrue(hasPhone && hasName, " BUG: Không tự động điền SĐT hoặc Tên từ Hồ sơ cá nhân!");
    }

    @Test
    public void FE11_TC05_EmptyDeliveryInfoValidation() {
        System.out.println("\nĐang chạy: TC05 - Kiểm tra tính bắt buộc của thông tin giao hàng");

        Constant.WEBDRIVER.get().get("http://127.0.0.1:8000/logout");
        sleep(1500);

        login("user_no_info", "pass123");
        goToCheckout();

        CheckoutPage page = new CheckoutPage();
        page.verifyOrderButtonStatus();
        sleep(1500);

        boolean hasWarning = Constant.WEBDRIVER.get().getPageSource().toLowerCase().contains("vui lòng")
                || Constant.WEBDRIVER.get().getCurrentUrl().contains("checkout");

        Assert.assertTrue(hasWarning, "BUG: Thông tin trống nhưng vẫn cho phép vượt qua màn hình Checkout!");
    }

    @Test
    public void FE11_TC07_VerifyTotalPriceWithDiscount() {
        System.out.println("\nĐang chạy: TC07 - Kiểm tra độ chính xác của Tổng tiền (Có Voucher)");

        Constant.WEBDRIVER.get().findElement(By.xpath("//img[@alt='Cart']")).click();
        sleep(1500);

        CheckoutPage page = new CheckoutPage();
        page.applyDiscountCode("FLASH20");
        sleep(1000);
        Constant.WEBDRIVER.get().findElement(By.cssSelector("button.btn-cart-checkout")).click();
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
            Constant.WEBDRIVER.get().findElement(By.xpath("//img[@alt='Cart']")).click();
            sleep(1500);

            WebElement selectAllInput = Constant.WEBDRIVER.get().findElement(By.id("select-all-cart"));

            if (selectAllInput.isSelected()) {
                WebElement selectAllCheckmark = Constant.WEBDRIVER.get().findElement(By.xpath("//input[@id='select-all-cart']/following-sibling::span[@class='checkmark']"));
                ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER.get()).executeScript("arguments[0].click();", selectAllCheckmark);
                sleep(1000);
            } else {
                java.util.List<WebElement> itemInputs = Constant.WEBDRIVER.get().findElements(By.xpath("//div[@class='cart-item-check']//input[@type='checkbox']"));
                for (WebElement input : itemInputs) {
                    if (input.isSelected()) {
                        WebElement checkmark = input.findElement(By.xpath("./following-sibling::span[@class='checkmark']"));
                        ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER.get()).executeScript("arguments[0].click();", checkmark);
                        sleep(300);
                    }
                }
            }
            System.out.println("Đã rà soát và đảm bảo giỏ hàng bị bỏ tick hoàn toàn!");

            Constant.WEBDRIVER.get().findElement(By.cssSelector("button.btn-cart-checkout")).click();
            sleep(2000);

        } catch (Exception e) {
            Assert.fail("Lỗi thao tác UI: Robot không thể bỏ tick hoặc không bấm được nút Đặt hàng! Chi tiết: " + e.getMessage());
        }

        String currentUrl = Constant.WEBDRIVER.get().getCurrentUrl().toLowerCase();
        String pageSource = Constant.WEBDRIVER.get().getPageSource().toLowerCase();

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

        Constant.WEBDRIVER.get().findElement(By.xpath("//img[@alt='Cart']")).click();
        sleep(1500);

        CheckoutPage page = new CheckoutPage();
        page.applyDiscountCode("NEWUSER50");
        sleep(1500);

        String pageSource = Constant.WEBDRIVER.get().getPageSource().toLowerCase();
        boolean hasErrorMsg = pageSource.contains("hết hạn") || pageSource.contains("kết thúc");
        long discountAmount = page.getDiscountAmount();

        Assert.assertTrue(hasErrorMsg || discountAmount == 0,
                "get().getCurrentUrl()BUG NGHIÊM TRỌNG: Mã giảm giá đã hết hạn nhưng hệ thống VẪN CHO ÁP DỤNG!");
    }
}