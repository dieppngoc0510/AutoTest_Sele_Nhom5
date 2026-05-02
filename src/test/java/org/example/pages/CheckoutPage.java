package org.example.pages;
import org.example.tests.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import java.util.List;

public class CheckoutPage {

    private final By _btnDatHang = By.cssSelector("button.btn-place-order");
    private final By _lblSuccessMessage = By.xpath("//h2[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'thành công')]");

    public void selectPaymentMethodCOD() {
        try {
            WebElement codLabel = Constant.WEBDRIVER.get().findElement(By.xpath("//label[contains(text(), 'COD') or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'khi nhận hàng')]"));
            codLabel.click();
        } catch (Exception e) {
            System.out.println("PT Thanh toán COD có thể đã được chọn mặc định.");
        }
    }

    public void selectPaymentMethodQR() {
        try {
            WebElement qrLabel = Constant.WEBDRIVER.get().findElement(By.xpath("//label[contains(text(), 'Chuyển khoản') or contains(text(), 'QR')]"));
            qrLabel.click();
            System.out.println("Đã chọn phương thức: Chuyển khoản / Mã QR");
        } catch (Exception e) {
            Assert.fail("BUG (Đã bắt được): Hệ thống chỉ cho phép thanh toán COD, hoàn toàn KHÔNG CÓ tùy chọn Chuyển khoản/QR trên UI!");
        }
    }

    public void clickDatHang() {
        try {
            WebElement btn = Constant.WEBDRIVER.get().findElement(_btnDatHang);
            ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);
            Thread.sleep(500);
            btn.click();
        } catch (Exception e) {
            try {
                WebElement btn = Constant.WEBDRIVER.get().findElement(_btnDatHang);
                ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].click();", btn);
            } catch (Exception ex) {
                System.out.println("KHÔNG TÌM THẤY nút Đặt hàng trên màn hình!");
            }
        }
    }

    public boolean isOrderSuccess() {
        try {
            return Constant.WEBDRIVER.get().findElement(_lblSuccessMessage).isDisplayed();
        } catch (Exception e) {
            return Constant.WEBDRIVER.get().getPageSource().toLowerCase().contains("thành công");
        }
    }

    public boolean isQRCodeDisplayed() {
        try {
            return !Constant.WEBDRIVER.get().findElements(By.xpath("//img[contains(@src, 'qr')] | //div[contains(@class, 'qr')]")).isEmpty();
        } catch (Exception e) {
            return Constant.WEBDRIVER.get().getPageSource().toLowerCase().contains("mã qr");
        }
    }

    public void applyDiscountCode(String couponCode) {
        try {
            Constant.WEBDRIVER.get().findElement(By.id("coupon-toggle-row")).click();
            Thread.sleep(1000);

            String xpathCoupon = String.format("//div[@id='cart-coupon-list']//div[contains(@class, 'coupon-item-mini')]//span[text()='%s']", couponCode);
            Constant.WEBDRIVER.get().findElement(By.xpath(xpathCoupon)).click();

            System.out.println("Đã áp dụng mã giảm giá: " + couponCode);
            Thread.sleep(1500);
        } catch (Exception e) {
            System.out.println("Không thể áp dụng mã giảm giá: " + couponCode);
        }
    }

    public boolean isDeliveryInfoEmpty() {
        try {
            String phone = Constant.WEBDRIVER.get().findElement(By.name("phone_number")).getAttribute("value").trim();
            String address = Constant.WEBDRIVER.get().findElement(By.name("address")).getAttribute("value").trim();
            return phone.isEmpty() || address.isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    public void verifyOrderButtonStatus() {
        try {
            WebElement btnDatHang = Constant.WEBDRIVER.get().findElement(_btnDatHang);
            boolean isDisabled = btnDatHang.getAttribute("disabled") != null;
            if (isDisabled) {
                System.out.println("Nút ĐẶT HÀNG đã bị khóa an toàn do thiếu thông tin.");
            } else {
                btnDatHang.click();
            }
        } catch (Exception e) {
            System.out.println("Không tìm thấy nút ĐẶT HÀNG.");
        }
    }

    private long parseCurrency(String text) {
        if (text == null || text.isEmpty()) return 0;
        String lowerText = text.toLowerCase();
        if (lowerText.contains("miễn phí") || lowerText.contains("free") || lowerText.contains("0đ")) return 0;
        try {
            return Long.parseLong(text.replaceAll("[^0-9]", ""));
        } catch (Exception e) { return 0; }
    }

    public long getSubtotal() {
        try { return parseCurrency(Constant.WEBDRIVER.get().findElement(By.xpath("//span[text()='Tổng tiền hàng']/following-sibling::span")).getText()); } catch (Exception e) { return 0; }
    }

    public long getShippingFee() {
        try { return parseCurrency(Constant.WEBDRIVER.get().findElement(By.xpath("//span[text()='Tổng tiền phí vận chuyển']/following-sibling::span")).getText()); } catch (Exception e) { return 0; }
    }

    public long getDiscountAmount() {
        try { return parseCurrency(Constant.WEBDRIVER.get().findElement(By.xpath("//span[text()='Tổng cộng voucher giảm giá']/following-sibling::span")).getText()); } catch (Exception e) { return 0; }
    }

    public long getTotalPayment() {
        try { return parseCurrency(Constant.WEBDRIVER.get().findElement(By.className("total-amount")).getText()); } catch (Exception e) { return 0; }
    }
}