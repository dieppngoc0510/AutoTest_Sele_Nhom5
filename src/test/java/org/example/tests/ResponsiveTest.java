package org.example.tests;

import org.example.pages.ResponsivePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ResponsiveTest extends BaseTest {

    private static final int MOBILE_WIDTH = 375;
    private static final int MOBILE_HEIGHT = 812;
    private static final int LAPTOP_WIDTH = 1280;
    private static final int LAPTOP_HEIGHT = 900;
    private static final By SEARCH_INPUT = By.cssSelector(".search-bar input[name='q']");
    private ResponsivePage responsivePage;

    @BeforeMethod
    public void resetViewport() {
        responsivePage = new ResponsivePage(driver);
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
    }

    @Test(description = "RES01 - Form đăng ký hiển thị đúng trên điện thoại")
    public void testRegisterFormOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        getUrl(BASE_URL + "register/");
        responsivePage.waitForVisible(By.cssSelector(".auth-container"));

        responsivePage.assertNoHorizontalOverflow("Trang đăng ký không được tràn ngang trên điện thoại.");
        responsivePage.assertTopLevelWidthFits(".auth-container", 16, "Form đăng ký phải vừa với chiều rộng màn hình điện thoại.");
        responsivePage.assertElementsStackVertically(".auth-form .form-group", 2,
                "Các trường đăng ký phải xếp dọc trên điện thoại.");
        responsivePage.assertWidthNearContainer(".btn-auth-submit", ".auth-form", 0.9,
                "Nút đăng ký phải đủ rộng để thao tác chạm trên điện thoại.");
    }

    @Test(description = "RES02 - Form đăng ký hiển thị đúng trên máy tính")
    public void testRegisterFormOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        getUrl(BASE_URL + "register/");
        responsivePage.waitForVisible(By.cssSelector(".auth-container"));

        responsivePage.assertNoHorizontalOverflow("Trang đăng ký không được tràn ngang trên máy tính.");
        responsivePage.assertCenteredWithinViewport(".auth-container", 120,
                "Form đăng ký phải nằm giữa trên máy tính.");
        responsivePage.assertContainerNotTooWide(".auth-container", 0.75,
                "Form đăng ký không được giãn quá rộng trên máy tính.");
    }

    @Test(description = "RES03 - Form đăng nhập hiển thị đúng trên điện thoại")
    public void testLoginFormOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        getUrl(BASE_URL + "login/");
        responsivePage.waitForVisible(By.cssSelector(".auth-container"));

        responsivePage.assertNoHorizontalOverflow("Trang đăng nhập không được tràn ngang trên điện thoại.");
        responsivePage.assertTopLevelWidthFits(".auth-container", 16, "Form đăng nhập phải vừa với chiều rộng màn hình điện thoại.");
        responsivePage.assertElementsStackVertically(".auth-form .form-group", 2,
                "Các trường đăng nhập phải xếp dọc trên điện thoại.");
        responsivePage.assertWidthNearContainer(".btn-auth-submit", ".auth-form", 0.9,
                "Nút đăng nhập phải dễ bấm trên điện thoại.");
    }

    @Test(description = "RES04 - Form đăng nhập hiển thị đúng trên máy tính")
    public void testLoginFormOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        getUrl(BASE_URL + "login/");
        responsivePage.waitForVisible(By.cssSelector(".auth-container"));

        responsivePage.assertNoHorizontalOverflow("Trang đăng nhập không được tràn ngang trên máy tính.");
        responsivePage.assertCenteredWithinViewport(".auth-container", 120,
                "Form đăng nhập phải nằm giữa trên máy tính.");
        responsivePage.assertContainerNotTooWide(".auth-container", 0.75,
                "Form đăng nhập không được giãn quá rộng trên máy tính.");
    }

    @Test(description = "RES05 - Menu đăng xuất truy cập được trên điện thoại")
    public void testLogoutMenuOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        loginAsDefaultUser();
        responsivePage.waitForVisible(By.id("userMenuToggle"));

        WebElement toggle = driver.findElement(By.id("userMenuToggle"));
        toggle.click();
        WebElement logoutLink = responsivePage.waitForVisible(By.cssSelector("#userMenuDropdown .logout-link"));

        responsivePage.assertNoHorizontalOverflow("Menu người dùng không được tràn ngang trên điện thoại.");
        Assert.assertTrue(logoutLink.isDisplayed(), "Tùy chọn đăng xuất phải hiển thị trên điện thoại.");
        Assert.assertTrue(logoutLink.getText().contains("Đăng xuất"),
                "Nội dung tùy chọn đăng xuất phải dễ đọc trên điện thoại.");
    }

    @Test(description = "RES06 - Menu đăng xuất hiển thị đúng trên máy tính")
    public void testLogoutMenuOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        loginAsDefaultUser();
        responsivePage.waitForVisible(By.id("userMenuToggle"));

        driver.findElement(By.id("userMenuToggle")).click();
        WebElement logoutLink = responsivePage.waitForVisible(By.cssSelector("#userMenuDropdown .logout-link"));

        responsivePage.assertNoHorizontalOverflow("Menu người dùng không được tràn ngang trên máy tính.");
        Assert.assertTrue(logoutLink.isDisplayed(), "Tùy chọn đăng xuất phải hiển thị trên máy tính.");
        Assert.assertTrue(responsivePage.getElementWidth("#userMenuDropdown") > 0,
                "Dropdown người dùng phải mở đúng trên máy tính.");
    }

    @Test(description = "RES07 - Danh sách sản phẩm dùng lưới gọn trên điện thoại")
    public void testProductListOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        openHomePage();

        responsivePage.assertNoHorizontalOverflow("Danh sách sản phẩm không được tràn ngang trên điện thoại.");
        int columns = responsivePage.countDistinctLefts(".product-grid .product-card", 4);
        Assert.assertTrue(columns >= 1 && columns <= 2,
                "Danh sách sản phẩm phải hiển thị 1 hoặc 2 cột trên điện thoại, nhưng hiện tại là " + columns + " cột.");
        responsivePage.assertTopLevelWidthFits("#new-products-grid", 20, "Lưới sản phẩm phải vừa với chiều rộng màn hình điện thoại.");
    }

    @Test(description = "RES08 - Danh sách sản phẩm dùng lưới nhiều cột trên máy tính")
    public void testProductListOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        openHomePage();

        responsivePage.assertNoHorizontalOverflow("Danh sách sản phẩm không được tràn ngang trên máy tính.");
        int columns = responsivePage.countDistinctLefts(".product-grid .product-card", 4);
        Assert.assertTrue(columns >= 3,
                "Danh sách sản phẩm phải hiển thị tối thiểu 3 cột trên máy tính, nhưng hiện tại là " + columns + " cột.");
    }

    @Test(description = "RES09 - Chi tiết sản phẩm xếp dọc trên điện thoại")
    public void testProductDetailOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        getUrl(BASE_URL + "product/7/");
        responsivePage.waitForVisible(By.cssSelector(".product-detail-container"));

        responsivePage.assertNoHorizontalOverflow("Trang chi tiết sản phẩm không được tràn ngang trên điện thoại.");
        responsivePage.assertElementsStackVertically(".product-detail-container > div", 2,
                "Ảnh sản phẩm và thông tin sản phẩm phải xếp dọc trên điện thoại.");
        responsivePage.assertTopLevelWidthFits(".product-detail-container", 20,
                "Nội dung chi tiết sản phẩm phải vừa với chiều rộng màn hình điện thoại.");
    }

    @Test(description = "RES10 - Chi tiết sản phẩm hiển thị 2 cột trên máy tính")
    public void testProductDetailOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        getUrl(BASE_URL + "product/7/");
        responsivePage.waitForVisible(By.cssSelector(".product-detail-container"));

        responsivePage.assertNoHorizontalOverflow("Trang chi tiết sản phẩm không được tràn ngang trên máy tính.");
        responsivePage.assertElementsShareRow(".product-detail-container > div", 2,
                "Ảnh sản phẩm và thông tin sản phẩm phải hiển thị cạnh nhau trên máy tính.");
    }

    @Test(description = "RES11 - Thanh tìm kiếm hoạt động trên điện thoại")
    public void testSearchBarOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        openHomePage();
        WebElement searchInput = responsivePage.waitForVisible(SEARCH_INPUT);

        responsivePage.assertNoHorizontalOverflow("Giao diện tìm kiếm không được tràn ngang trên điện thoại.");
        Assert.assertTrue(searchInput.isDisplayed(), "Ô tìm kiếm phải hiển thị trên điện thoại.");
        searchInput.sendKeys("váy");
        searchInput.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.urlContains("/search/"));
        Assert.assertTrue(driver.getCurrentUrl().contains("q="), "Từ khóa tìm kiếm phải được gửi trên điện thoại.");
    }

    @Test(description = "RES12 - Thanh tìm kiếm hiển thị đúng trên máy tính")
    public void testSearchBarOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        openHomePage();
        WebElement searchInput = responsivePage.waitForVisible(SEARCH_INPUT);

        responsivePage.assertNoHorizontalOverflow("Giao diện tìm kiếm không được tràn ngang trên máy tính.");
        Assert.assertTrue(searchInput.isDisplayed(), "Ô tìm kiếm phải hiển thị trên máy tính.");
        Assert.assertTrue(responsivePage.getElementWidth(".search-bar") > 180,
                "Thanh tìm kiếm phải giữ được chiều rộng đủ dùng trên máy tính.");
    }

    @Test(description = "RES13 - Trang hồ sơ hiển thị đúng trên điện thoại")
    public void testProfilePageOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        loginAsDefaultUser();
        getUrl(BASE_URL + "profile/");
        responsivePage.waitForVisible(By.cssSelector(".shopee-container"));

        responsivePage.assertNoHorizontalOverflow("Trang hồ sơ không được tràn ngang trên điện thoại.");
        responsivePage.assertElementsStackVertically(".shopee-container > div", 2,
                "Thanh bên và nội dung chính của hồ sơ phải xếp dọc trên điện thoại.");
        responsivePage.assertElementsStackVertically(".shopee-form-section .s-form-row", 2,
                "Các hàng trong form hồ sơ phải xếp dọc trên điện thoại.");
        responsivePage.assertWidthNearContainer(".s-btn-save", ".shopee-form-section", 0.9,
                "Nút lưu phải đủ rộng và dễ bấm trên điện thoại.");
    }

    @Test(description = "RES14 - Trang hồ sơ hiển thị đúng trên máy tính")
    public void testProfilePageOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        loginAsDefaultUser();
        getUrl(BASE_URL + "profile/");
        responsivePage.waitForVisible(By.cssSelector(".shopee-container"));

        responsivePage.assertNoHorizontalOverflow("Trang hồ sơ không được tràn ngang trên máy tính.");
        responsivePage.assertElementsShareRow(".shopee-container > div", 2,
                "Thanh bên và nội dung chính của hồ sơ phải hiển thị cạnh nhau trên máy tính.");
        responsivePage.assertWidthRatioBetween(".s-btn-save", ".shopee-form-section", 0.08, 0.3,
                "Nút lưu phải giữ tỉ lệ cân đối trên máy tính.");
    }

    @Test(description = "RES15 - Form đổi mật khẩu hiển thị đúng trên điện thoại")
    public void testChangePasswordOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        loginAsDefaultUser();
        getUrl(BASE_URL + "change-password/");
        responsivePage.waitForVisible(By.cssSelector(".shopee-container"));

        responsivePage.assertNoHorizontalOverflow("Trang đổi mật khẩu không được tràn ngang trên điện thoại.");
        responsivePage.assertElementsStackVertically(".shopee-form-section .s-form-row", 3,
                "Các trường mật khẩu phải xếp dọc trên điện thoại.");
        responsivePage.assertWidthNearContainer(".s-btn-save", ".shopee-form-section", 0.9,
                "Nút xác nhận phải đủ rộng trên điện thoại.");
    }

    @Test(description = "RES16 - Form đổi mật khẩu hiển thị đúng trên máy tính")
    public void testChangePasswordOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        loginAsDefaultUser();
        getUrl(BASE_URL + "change-password/");
        responsivePage.waitForVisible(By.cssSelector(".shopee-container"));

        responsivePage.assertNoHorizontalOverflow("Trang đổi mật khẩu không được tràn ngang trên máy tính.");
        responsivePage.assertElementsShareRow(".shopee-container > div", 2,
                "Thanh bên và nội dung đổi mật khẩu phải hiển thị cạnh nhau trên máy tính.");
        responsivePage.assertContainerNotTooWide(".shopee-form-section", 0.7,
                "Form đổi mật khẩu không được giãn quá rộng trên máy tính.");
    }

    @Test(description = "RES17 - Sidebar giỏ hàng hiển thị đúng trên điện thoại")
    public void testCartLayoutOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        loginAsDefaultUser();
        clearCart();
        openHomePage();
        addProductToCartDirect(7, "Xanh đen", "S");
        responsivePage.openResponsiveCartAndWaitForItems(1);

        responsivePage.assertNoHorizontalOverflow("Sidebar giỏ hàng không được tràn ngang trên điện thoại.");
        responsivePage.assertTopLevelWidthFits("#cart-sidebar", 0, "Sidebar giỏ hàng phải vừa với chiều rộng màn hình điện thoại.");
        Assert.assertEquals(getCartItems().size(), 1, "Giỏ hàng phải hiển thị sản phẩm đã thêm trên điện thoại.");
    }

    @Test(description = "RES18 - Sidebar giỏ hàng hiển thị đúng trên máy tính")
    public void testCartLayoutOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        loginAsDefaultUser();
        clearCart();
        openHomePage();
        addProductToCartDirect(7, "Xanh đen", "S");
        responsivePage.openResponsiveCartAndWaitForItems(1);

        responsivePage.assertNoHorizontalOverflow("Sidebar giỏ hàng không được tràn ngang trên máy tính.");
        Assert.assertTrue(responsivePage.getElementWidth("#cart-sidebar") >= 320,
                "Sidebar giỏ hàng phải giữ được chiều rộng rõ ràng, dễ dùng trên máy tính.");
        Assert.assertEquals(getCartItems().size(), 1, "Giỏ hàng phải hiển thị sản phẩm đã thêm trên máy tính.");
    }

    @Test(description = "RES19 - Trang thanh toán hiển thị đúng trên điện thoại")
    public void testCheckoutOnMobile() {
        responsivePage.setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        loginAsDefaultUser();
        clearCart();
        openHomePage();
        addProductToCartDirect(7, "Xanh đen", "S");
        getUrl(BASE_URL + "checkout/");
        responsivePage.waitForVisible(By.cssSelector(".checkout-container"));

        responsivePage.assertNoHorizontalOverflow("Trang thanh toán không được tràn ngang trên điện thoại.");
        responsivePage.assertElementsStackVertically(".checkout-container > div", 2,
                "Các khu vực thanh toán phải xếp dọc trên điện thoại.");
        responsivePage.assertTopLevelWidthFits(".checkout-container", 20,
                "Nội dung thanh toán phải vừa với chiều rộng màn hình điện thoại.");
    }

    @Test(description = "RES20 - Trang thanh toán hiển thị đúng trên máy tính")
    public void testCheckoutOnLaptop() {
        responsivePage.setViewport(LAPTOP_WIDTH, LAPTOP_HEIGHT);
        loginAsDefaultUser();
        clearCart();
        openHomePage();
        addProductToCartDirect(7, "Xanh đen", "S");
        getUrl(BASE_URL + "checkout/");
        responsivePage.waitForVisible(By.cssSelector(".checkout-container"));

        responsivePage.assertNoHorizontalOverflow("Trang thanh toán không được tràn ngang trên máy tính.");
        responsivePage.assertElementsShareRow(".checkout-container > div", 2,
                "Form thanh toán và tóm tắt đơn hàng phải hiển thị cạnh nhau trên máy tính.");
    }
}
