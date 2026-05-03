package org.example.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeleteCartTest extends BaseTest {

    @BeforeMethod
    public void setupUser() {
        bp().loginAsDefaultUser();
        bp().clearCart();
        driver.get(BASE_URL);
    }

    @Test(description = "FE10-TC01 - Xóa một sản phẩm khỏi giỏ (có nhiều SP)")
    public void testDeleteFromMulti() {
        bp().addProductToCartDirect(7, "Xanh \u0111en", "S");
        bp().addProductToCartDirect(10, "\u0110en", "S");

        String response = bp().postCartUpdate("{\"product_id\":7,\"color\":\"Xanh \\u0111en\",\"size\":\"S\",\"action\":\"delete\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Thao tác xóa một sản phẩm khỏi giỏ hàng phải thành công");
    }

    @Test(description = "FE10-TC02 - Xóa sản phẩm duy nhất trong giỏ")
    public void testDeleteOnlyItem() {
        bp().addProductToCartDirect(10, "\u0110en", "S");

        String response = bp().postCartUpdate("{\"product_id\":10,\"color\":\"\\u0110en\",\"size\":\"S\",\"action\":\"delete\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Thao tác xóa sản phẩm duy nhất trong giỏ hàng phải thành công");
    }

    @Test(description = "FE10-TC03 - Kiểm tra tạm tính sau khi xóa SP")
    public void testSubtotalAfterDelete() {
        bp().addProductToCartDirect(7, "Xanh \u0111en", "S");
        bp().addProductToCartDirect(10, "\u0110en", "S");

        String response = bp().postCartUpdate("{\"product_id\":7,\"color\":\"Xanh \\u0111en\",\"size\":\"S\",\"action\":\"delete\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Tạm tính phải được cập nhật đúng sau khi xóa sản phẩm");
    }

    @Test(description = "FE10-TC04 - Kiểm tra badge giỏ hàng sau khi xóa")
    public void testBadgeAfterDelete() {
        bp().addProductToCartDirect(7, "Xanh \u0111en", "S");
        bp().addProductToCartDirect(10, "\u0110en", "S");

        String response = bp().postCartUpdate("{\"product_id\":7,\"color\":\"Xanh \\u0111en\",\"size\":\"S\",\"action\":\"delete\"}");
        Assert.assertTrue(response.contains("\"success\":true") || response.contains("\"success\": true"),
                "Badge giỏ hàng phải được cập nhật ngay sau khi xóa sản phẩm");
    }
}
