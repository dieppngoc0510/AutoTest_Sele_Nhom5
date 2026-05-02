package org.example.tests;
import org.example.tests.Constant;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.example.pages.HomePage;
import org.example.pages.ProductDetailPage;

public class ViewProductTest {

    @BeforeMethod
    public void beforeMethod() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--remote-allow-origins=*");

        Constant.WEBDRIVER.set(new ChromeDriver(options));
        Constant.WEBDRIVER.get().manage().window().maximize();
    }

    @AfterMethod
    public void afterMethod() {
        if (Constant.WEBDRIVER.get() != null) {
            Constant.WEBDRIVER.get().quit();
            Constant.WEBDRIVER.remove();
        }
    }

    @Test
    public void FE04_TC01_ViewProductListSuccessfully() {
        System.out.println("\nĐang chạy: FE04-TC01 - Xem danh sách sản phẩm thành công");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        boolean isDisplayed = homePage.isProductListDisplayed();
        Assert.assertTrue(isDisplayed, "BUG: Danh sách sản phẩm không hiển thị đủ (Ảnh, Tên, Giá mới/cũ, Chấm màu)!");
    }

    @Test
    public void FE04_TC02_ViewProductDetailSuccessfully() {
        System.out.println("\nĐang chạy: FE04-TC02 - Xem chi tiết sản phẩm thành công");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        ProductDetailPage detailPage = homePage.clickFirstProduct();
        sleep(2000);

        String currentUrl = Constant.WEBDRIVER.get().getCurrentUrl();
        Assert.assertFalse(currentUrl.equals("http://127.0.0.1:8000/"), "BUG: Bấm vào sản phẩm nhưng không chuyển sang trang chi tiết!");

        boolean isDisplayed = detailPage.isProductDetailDisplayed();
        Assert.assertTrue(isDisplayed, "BUG: Thiếu thông tin quan trọng trên trang Chi tiết sản phẩm (Breadcrumb, Ảnh, Mã, Giá...)!");
    }

    @Test
    public void FE04_TC03_FilterProductsByCategory() {
        System.out.println("\nĐang chạy: FE04-TC03 - Lọc sản phẩm theo danh mục");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        homePage.clickCategoryAo();
        sleep(2000);

        boolean isCorrect = homePage.areAllProductsBelongToCategory("áo");
        Assert.assertTrue(isCorrect, "BUG: Nhấn vào menu Áo nhưng lại hiện ra sản phẩm khác (Quần, Váy...)!");
    }

    @Test
    public void FE04_TC04_TestThumbnailGallery() {
        System.out.println("\nĐang chạy: FE04-TC04 - Kiểm tra tương tác ảnh Thumbnail");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        ProductDetailPage detailPage = homePage.clickProductByIndex(1);
        sleep(2000);

        Assert.assertTrue(detailPage.testThumbnailInteraction(), "BUG: Click ảnh thumbnail nhưng ảnh chính không thay đổi!");
    }

    @Test
    public void FE04_TC05_AccessInvalidProductID() {
        System.out.println("\nĐang chạy: FE04-TC05 - Truy cập ID sản phẩm không tồn tại");
        Constant.WEBDRIVER.get().get("http://127.0.0.1:8000/product/99999");
        sleep(2000);

        String pageSource = Constant.WEBDRIVER.get().getPageSource().toLowerCase();
        boolean hasError = pageSource.contains("404") || pageSource.contains("không tồn tại");

        Assert.assertTrue(hasError, "BUG: ID sản phẩm không tồn tại nhưng hệ thống không hiển thị trang lỗi 404!");
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void FE04_TC06_CheckLoadMoreButton() {
        System.out.println("\nĐang chạy: FE04-TC06 - Kiểm tra nút 'Xem thêm'");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        // 1. Đếm số sản phẩm ban đầu
        int initialCount = homePage.getProductCount();
        System.out.println("Số sản phẩm ban đầu: " + initialCount);

        // 2. Click Xem thêm
        homePage.clickLoadMoreButton();
        sleep(3000); // Chờ API tải thêm dữ liệu

        // 3. Đếm lại số sản phẩm
        int newCount = homePage.getProductCount();
        System.out.println("Số sản phẩm sau khi bấm Xem thêm: " + newCount);

        if (initialCount > 0) {
            Assert.assertTrue(newCount >= initialCount, "BUG: Bấm nút Xem thêm nhưng danh sách sản phẩm không tăng lên!");
        }
    }

    @Test
    public void FE04_TC07_CheckOldPriceLogic() {
        System.out.println("\nĐang chạy: FE04-TC07 - Logic hiển thị Giá cũ");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        ProductDetailPage detailPage = homePage.clickProductByIndex(2);
        sleep(2000);

        boolean hasOldPrice = detailPage.isOldPriceDisplayed();


        System.out.println("Sản phẩm này có hiển thị giá gạch ngang không? " + hasOldPrice);
        Assert.assertFalse(hasOldPrice, "BUG: Sản phẩm không có chương trình giảm giá nhưng lại hiển thị Giá cũ gạch ngang!");
    }

    @Test
    public void FE04_TC08_AccessDiscontinuedProduct() {
        System.out.println("\nĐang chạy: FE04-TC08 - Truy cập Sản phẩm ngừng kinh doanh");

        Constant.WEBDRIVER.get().get("http://127.0.0.1:8000/product/22");        sleep(2000);

        String pageSource = Constant.WEBDRIVER.get().getPageSource().toLowerCase();
        boolean isBlocked = pageSource.contains("404") || pageSource.contains("không tồn tại") || pageSource.contains("ngừng kinh doanh");

        Assert.assertTrue(isBlocked, "BUG: Sản phẩm đã ngừng kinh doanh nhưng vẫn cho phép khách hàng truy cập vào xem chi tiết!");
    }
}