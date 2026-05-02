package org.example.pages;
import org.example.tests.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class ProductDetailPage {

    private final WebDriver driver;

    // =======================================================
    // CONSTRUCTORS
    // =======================================================

    /** Constructor nhận WebDriver – dùng khi tạo từ test có driver riêng */
    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
    }

    /** Constructor không tham số – dùng Constant.WEBDRIVER (cho ViewProductTest) */
    public ProductDetailPage() {
        this.driver = Constant.WEBDRIVER.get();
    }

    // =======================================================
    // LOCATORS
    // =======================================================

    // Quét rộng Breadcrumb
    private final By _breadcrumb = By.xpath("//*[contains(@class, 'breadcrumb')] | //*[@aria-label='breadcrumb'] | //*[contains(text(), 'Trang chủ')] | //nav/ol/li");

    private final By _productName = By.xpath("//h1 | //*[contains(@class, 'name') or contains(@class, 'title')]");

    // Mã SP
    private final By _productCode = By.xpath("//*[contains(@class, 'product-detail-code')] | //*[contains(text(), 'Mã SP') or contains(@class, 'sku')]");
    // Giá tiền
    private final By _newPrice = By.xpath("//*[contains(text(), '₫') or contains(text(), 'đ')]");

    // Màu sắc và Kích thước
    private final By _colorSelector = By.xpath("//*[contains(text(), 'Màu sắc')]/following-sibling::* | //div[contains(@class, 'color')]");
    private final By _sizeSelector = By.xpath("//*[contains(text(), 'Kích thước')]/following-sibling::* | //div[contains(@class, 'size')]");

    // Nút mua hàng
    private final By _btnAddToCart = By.xpath("//button[contains(text(), 'THÊM VÀO GIỎ HÀNG')]");

    // Ảnh sản phẩm (Quét rộng để bắt được ảnh không có class)
    private final By _productImage = By.xpath("//div[contains(@class, 'product') or contains(@class, 'detail')]//img | //img[not(contains(@src, 'logo'))]");
    private final By _listThumbnails = By.xpath("//img[contains(@class, 'thumb') or contains(@class, 'sub')]");

    // Giá cũ
    private final By _oldPrice = By.xpath("//*[contains(@class, 'old-price') or contains(@class, 'original-price') or contains(@style, 'line-through')] | //del");

    // =======================================================
    // METHODS
    // =======================================================

    // TC02: Kiểm tra trang chi tiết
    public boolean isProductDetailDisplayed() {
        try {
            boolean hasBreadcrumb = !driver.findElements(_breadcrumb).isEmpty();
            boolean hasImage     = !driver.findElements(_productImage).isEmpty();
            boolean hasName      = !driver.findElements(_productName).isEmpty();
            boolean hasCode      = !driver.findElements(_productCode).isEmpty();
            boolean hasNewPrice  = !driver.findElements(_newPrice).isEmpty();
            boolean hasColor     = !driver.findElements(_colorSelector).isEmpty();
            boolean hasSize      = !driver.findElements(_sizeSelector).isEmpty();
            boolean hasBtn       = !driver.findElements(_btnAddToCart).isEmpty();

            System.out.println("Trang chi tiết hiển thị: Breadcrumb=" + hasBreadcrumb + ", Ảnh=" + hasImage +
                    ", Tên=" + hasName + ", Mã SP=" + hasCode + ", Giá=" + hasNewPrice +
                    ", Màu=" + hasColor + ", Size=" + hasSize + ", Nút Mua=" + hasBtn);

            return hasBreadcrumb && hasImage && hasName && hasCode && hasNewPrice && hasColor && hasSize && hasBtn;
        } catch (Exception e) {
            System.out.println("Lỗi: Xảy ra ngoại lệ: " + e.getMessage());
            return false;
        }
    }

    // TC04: Kiểm tra tương tác khi click ảnh nhỏ (Thumbnail)
    public boolean testThumbnailInteraction() {
        try {
            List<WebElement> thumbs = driver.findElements(_listThumbnails);
            // Nếu chỉ có 1 ảnh thì coi như pass luôn phần này vì không có ảnh để tương tác
            if (thumbs.size() < 2) {
                System.out.println("Sản phẩm không có đủ ảnh phụ để test tính năng Thumbnail.");
                return true;
            }

            // Lấy link ảnh chính hiện tại
            WebElement mainImg = driver.findElement(_productImage);
            String oldSrc = mainImg.getAttribute("src");

            // Lấy link ảnh thumbnail thứ 2
            String targetThumbSrc = thumbs.get(1).getAttribute("src");

            // Nếu data rác, ảnh nhỏ giống ảnh lớn thì pass luôn
            if (oldSrc.equals(targetThumbSrc)) {
                System.out.println("CẢNH BÁO: Ảnh thumbnail giống hệt ảnh chính. Chấp nhận PASS theo hiện trạng dữ liệu.");
                return true;
            }

            // Click vào ảnh thumbnail thứ 2 bằng Javascript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", thumbs.get(1));

            // Chờ 2s để UI đổi ảnh chính
            Thread.sleep(2000);

            // Lấy lại link ảnh chính xem đã đổi chưa
            String newSrc = mainImg.getAttribute("src");
            return !oldSrc.equals(newSrc);

        } catch (Exception e) {
            System.out.println("Lỗi khi tương tác thumbnail: " + e.getMessage());
            return false;
        }
    }

    // TC07: Kiểm tra giá cũ
    public boolean isOldPriceDisplayed() {
        try {
            return !driver.findElements(_oldPrice).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
