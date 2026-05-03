package org.example.tests;

import org.example.pages.CartSidebar;
import org.example.pages.ProductPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AddProductTest extends BaseTest {

    private static final String AO_GILE_LEN = "\u00c1o gile len c\u1ed5 ch\u1eef V";
    private static final String AO_TRE_VAI = "\u00c1o tr\u1ec5 vai nhung t\u0103m";
    private static final String SET_VAY_SUONG = "Set v\u00e1y su\u00f4ng tay d\u00e0i k\u00e8m n\u01a1";
    private static final String AO_KHOAC_DA_LON = "\u00c1o kho\u00e1c da l\u1ed9n d\u00e1ng ng\u1eafn";

    @BeforeMethod
    public void prepareUser() {
        bp().loginAsDefaultUser();
        bp().clearCart();
    }

    @Test(description = "FE08-TC01 - Thêm SP thành công khi chọn đủ màu và size")
    public void testAddProductSuccess() {
        bp().goToProduct(AO_GILE_LEN);
        ProductPage productPage = new ProductPage(driver);
        productPage.selectColor(0);
        productPage.selectSize(0);
        productPage.clickAddToCart();

        Assert.assertTrue(productPage.getToastMessage().contains("Đã thêm sản phẩm vào giỏ hàng"),
                "Không hiển thị thông báo thêm sản phẩm vào giỏ hàng thành công");
        Assert.assertTrue(productPage.isToastImageDisplayed(),
                "Không hiển thị ảnh thumbnail của sản phẩm trên thông báo thêm giỏ hàng");

        productPage.clickViewCartOnToast();
        CartSidebar cartSidebar = new CartSidebar(driver);
        Assert.assertEquals(cartSidebar.getCartItemCount(), 1,
                "Sản phẩm chưa được thêm vào giỏ hàng như mong đợi");
    }

    @Test(description = "FE08-TC02 - Thêm SP khi chưa chọn kích thước")
    public void testAddProductMissingSize() {
        bp().goToProduct(AO_GILE_LEN);
        ProductPage productPage = new ProductPage(driver);
        productPage.selectColor(0);
        productPage.clickAddToCart();

        Assert.assertTrue(productPage.isToastHidden(),
                "Không được hiển thị thông báo thêm thành công khi chưa chọn kích thước");
    }

    @Test(description = "FE08-TC03 - Thêm SP khi chưa chọn màu sắc")
    public void testAddProductMissingColor() {
        bp().goToProduct(AO_GILE_LEN);
        ProductPage productPage = new ProductPage(driver);
        productPage.selectSize(0);
        productPage.clickAddToCart();

        Assert.assertTrue(productPage.isToastHidden(),
                "Không được hiển thị thông báo thêm thành công khi chưa chọn màu sắc");
    }

    @Test(description = "FE08-TC04 - Thêm SP đã có trong giỏ hàng (cùng màu, cùng size)")
    public void testAddDuplicateProduct() {
        bp().goToProduct(AO_TRE_VAI);
        ProductPage productPage = new ProductPage(driver);
        productPage.addProductToCart(0, 0);
        Assert.assertTrue(productPage.isToastHidden(), "Thông báo thêm giỏ hàng lần đầu chưa tự động ẩn");

        productPage.clickAddToCart();
        productPage.waitForToast();
        productPage.clickViewCartOnToast();

        CartSidebar cartSidebar = new CartSidebar(driver);
        Assert.assertEquals(cartSidebar.getCartItemCount(), 1,
                "Sản phẩm trùng biến thể phải được gộp trên cùng một dòng giỏ hàng");
        Assert.assertEquals(cartSidebar.getQuantity(0), "2",
                "Số lượng sản phẩm phải tăng lên 2 sau khi thêm cùng biến thể lần hai");
    }

    @Test(description = "FE08-TC05 - Thêm nhiều sản phẩm khác nhau vào giỏ")
    public void testAddMultipleProducts() {
        bp().goToProduct(SET_VAY_SUONG);
        new ProductPage(driver).addProductToCart(0, 0);

        bp().goToProduct(AO_KHOAC_DA_LON);
        new ProductPage(driver).addProductToCart(0, 0);

        CartSidebar cartSidebar = new CartSidebar(driver);
        cartSidebar.openCartSidebar();
        Assert.assertEquals(cartSidebar.getCartItemCount(), 2,
                "Giỏ hàng phải hiển thị đủ hai sản phẩm khác nhau");
    }

    @Test(description = "FE08-TC06 - Kiểm tra giá hiển thị đúng khi thêm vào giỏ")
    public void testPriceConsistency() {
        bp().goToProduct(SET_VAY_SUONG);
        new ProductPage(driver).addProductToCart(0, 0);

        CartSidebar cartSidebar = new CartSidebar(driver);
        cartSidebar.openCartSidebar();
        Assert.assertFalse(cartSidebar.getSubtotal().isBlank(),
                "Phải hiển thị tạm tính sau khi thêm sản phẩm vào giỏ hàng");
    }

    @Test(description = "FE08-TC07 - Badge icon giỏ hàng hiển thị số lượng loại SP, không phải tổng số lượng")
    public void testBadgeCountByTypes() {
        bp().goToProduct(SET_VAY_SUONG);
        ProductPage productPage = new ProductPage(driver);
        productPage.addProductToCart(0, 0);
        Assert.assertTrue(productPage.isToastHidden(), "Thông báo thêm giỏ hàng lần đầu chưa tự động ẩn");

        productPage.clickAddToCart();
        productPage.waitForToast();

        CartSidebar cartSidebar = new CartSidebar(driver);
        Assert.assertFalse(cartSidebar.getCartBadgeCount().isBlank(),
                "Badge giỏ hàng phải hiển thị sau khi thêm sản phẩm");
    }

        @Test(description = "FE08-TC09 - Thông báo tự động ẩn sau một khoảng thời gian")
        public void testToastBehavior() {
            bp().goToProduct(AO_GILE_LEN);
            ProductPage productPage = new ProductPage(driver);
            productPage.addProductToCart(0, 0);

            Assert.assertTrue(productPage.getToastMessage().contains("Đã thêm"),
                    "Không hiển thị thông báo thêm sản phẩm vào giỏ hàng");
            Assert.assertTrue(productPage.isToastHidden(),
                    "Thông báo thêm giỏ hàng chưa tự động ẩn sau khi hiển thị");
        }

    @Test(description = "FE08-TC10 - Nhấn \"Xem giỏ hàng\" trên thông báo điều hướng đúng")
    public void testViewCartFromToast() {
        bp().goToProduct(SET_VAY_SUONG);
        ProductPage productPage = new ProductPage(driver);
        productPage.addProductToCart(0, 0);
        productPage.clickViewCartOnToast();

        Assert.assertTrue(driver.findElement(By.id("cart-sidebar")).getAttribute("class").contains("open"),
                "Sidebar giỏ hàng phải mở khi nhấn nút \"Xem giỏ hàng\" trên thông báo");
    }
}
