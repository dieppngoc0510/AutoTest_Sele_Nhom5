package org.example.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UpdateCartTest extends BaseTest {

    private static final String COLOR_WHITE_JSON = "Tr\\u1eafng";
    private static final String COLOR_BLACK_JSON = "\\u0110en";

    @BeforeMethod
    public void setupCart() {
        bp().loginAsDefaultUser();
        bp().clearCart();
        driver.get(BASE_URL);
        bp().addProductToCartDirect(11, "Tr\u1eafng", "S");
    }

    @Test(description = "FE09-TC01 - Tăng số lượng sản phẩm bằng nút \"+\"")
    public void testIncreaseQuantity() {
        String response = bp().postCartUpdate("{\"product_id\":11,\"color\":\"" + COLOR_WHITE_JSON + "\",\"size\":\"S\",\"action\":\"increase\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Thao tác tăng số lượng sản phẩm phải thành công");
    }

    @Test(description = "FE09-TC02 - Giảm số lượng sản phẩm bằng nút \"-\"")
    public void testDecreaseQuantity() {
        bp().postCartUpdate("{\"product_id\":11,\"color\":\"" + COLOR_WHITE_JSON + "\",\"size\":\"S\",\"action\":\"increase\"}");
        String response = bp().postCartUpdate("{\"product_id\":11,\"color\":\"" + COLOR_WHITE_JSON + "\",\"size\":\"S\",\"action\":\"decrease\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Thao tác giảm số lượng sản phẩm phải thành công");
    }

    @Test(description = "FE09-TC03 - Giảm số lượng về 0 bằng nút \"-\"")
    public void testDecreaseToZero() {
        String response = bp().postCartUpdate("{\"product_id\":11,\"color\":\"" + COLOR_WHITE_JSON + "\",\"size\":\"S\",\"action\":\"delete\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Hệ thống phải xóa sản phẩm khi số lượng giảm về 0");
    }

    @Test(description = "FE09-TC04 - Thay đổi variant (màu/size) sản phẩm trong giỏ")
    public void testChangeVariant() {
        String response = bp().postCartUpdate("{\"product_id\":11,\"color\":\"" + COLOR_WHITE_JSON + "\",\"size\":\"S\",\"new_color\":\""
                + COLOR_BLACK_JSON + "\",\"new_size\":\"S\",\"action\":\"change_variant\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Thao tác thay đổi màu hoặc size trong giỏ hàng phải thành công");
    }

    @Test(description = "FE09-TC05 - Chọn mã ưu đãi hợp lệ")
    public void testValidCoupon() {
        String response = bp().applyCoupon("FLASH20");
        Assert.assertTrue(response.contains("\"success\": true") || response.contains("\"success\":true"),
                "Mã ưu đãi hợp lệ phải được áp dụng thành công");
    }

    @Test(description = "FE09-TC06 - Chọn mã ưu đãi hết hạn")
    public void testExpiredCoupon() {
        bp().openHomePage();
        String source = driver.getPageSource();
        Assert.assertTrue(source.contains("NEWUSER50"), "Mã ưu đãi hết hạn phải xuất hiện trong giao diện");
        Assert.assertTrue(source.contains("not-allowed"),
                "Mã ưu đãi hết hạn phải được hiển thị ở trạng thái không thể chọn");
    }

    @Test(description = "FE09-TC07 - Tick chọn \"Chọn tất cả\" sản phẩm")
    public void testSelectAll() {
        bp().addProductToCartDirect(7, "Xanh \u0111en", "S");
        String response = bp().postCartUpdate("{\"action\":\"toggle_all\",\"checked\":true}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Thao tác chọn tất cả sản phẩm trong giỏ hàng phải thành công");
    }

    @Test(description = "FE09-TC08 - Chỉ chọn một số sản phẩm để đặt hàng")
    public void testSelectPartial() {
        bp().addProductToCartDirect(7, "Xanh \u0111en", "S");
        bp().postCartUpdate("{\"action\":\"toggle_all\",\"checked\":true}");
        String response = bp().postCartUpdate("{\"product_id\":11,\"color\":\"" + COLOR_WHITE_JSON + "\",\"size\":\"S\",\"action\":\"toggle\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Thao tác bỏ chọn một phần sản phẩm trong giỏ hàng phải thành công");
    }
}
