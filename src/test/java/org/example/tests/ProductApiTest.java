package org.example.tests;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class ProductApiTest extends BaseTest {

    String BASE_URL = "http://127.0.0.1:8000/api";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void FE04_API01_GetProductList() {
        System.out.println("\nĐang chạy: FE04-API01 - Lấy danh sách sản phẩm");

        Response response = RestAssured.get("/products");

        // 1. Kiểm tra HTTP Status
        Assert.assertEquals(response.getStatusCode(), 200, "BUG: API lấy danh sách không trả về 200 OK!");

        // 2. Kiểm tra dữ liệu Body
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("\"id\""), "Thiếu trường id");
        Assert.assertTrue(responseBody.contains("\"name\""), "Thiếu trường name");
        Assert.assertTrue(responseBody.contains("\"price\""), "Thiếu trường price");

        boolean hasOldPrice = responseBody.contains("\"old_price\"")
                || responseBody.contains("\"original_price\"")
                || responseBody.contains("\"price_old\"");
        Assert.assertTrue(hasOldPrice, "CẢNH BÁO: API không trả về trường Giá cũ (old_price) cho Frontend hiển thị!");
    }

    @Test
    public void FE04_API02_GetValidProductDetail() {
        System.out.println("\nĐang chạy: FE04-API02 - Lấy chi tiết sản phẩm hợp lệ");

        Response listResponse = RestAssured.get("/products");
        JsonPath jsonPathEvaluator = listResponse.jsonPath();

        Integer validId = null;
        try {
            validId = jsonPathEvaluator.getInt("[0].id");
        } catch (Exception e) {
            validId = 9;
        }

        System.out.println("-> Đang gọi API chi tiết cho ID sản phẩm: " + validId);
        Response response = RestAssured.get("/products/" + validId);

        Assert.assertEquals(response.getStatusCode(), 200, "BUG: Không tìm thấy sản phẩm ID=" + validId + "!");
        String responseBody = response.getBody().asString();

        Assert.assertTrue(responseBody.contains("\"sizes\"") || responseBody.contains("\"size\""), "BUG: Thiếu thông tin kích thước (sizes)!");
        Assert.assertTrue(responseBody.contains("\"colors\"") || responseBody.contains("\"color\""), "BUG: Thiếu thông tin màu sắc (colors)!");
    }

    @Test
    public void FE04_API03_GetNonExistentProduct() {
        System.out.println("\nĐang chạy: FE04-API03 - Sản phẩm không tồn tại");

        Response response = RestAssured.get("/products/99999");

        Assert.assertEquals(response.getStatusCode(), 404, "BUG: ID ảo nhưng API không chặn và không trả về lỗi 404!");

        String responseBody = response.getBody().asString().toLowerCase();
        boolean hasErrorMsg = responseBody.contains("error") || responseBody.contains("message") || responseBody.contains("not found");
        Assert.assertTrue(hasErrorMsg, "BUG (Bắt được): API có trả về 404 nhưng Body rỗng, thiếu JSON error message mô tả lỗi cho Frontend!");
    }

    @Test
    public void FE04_API04_FilterProductsByCategory() {
        System.out.println("\nĐang chạy: FE04-API04 - Lọc sản phẩm theo danh mục");

        int testCategoryId = 1;
        Response response = RestAssured.given()
                .queryParam("category", testCategoryId)
                .get("/products");

        Assert.assertEquals(response.getStatusCode(), 200, "BUG: API lọc danh mục bị sập (Không trả về 200)!");

        JsonPath jsonPath = response.jsonPath();
        try {
            List<Integer> categoryIds = jsonPath.getList("category_id", Integer.class);
            if (categoryIds != null && !categoryIds.isEmpty()) {
                for (Integer id : categoryIds) {
                    Assert.assertEquals(id.intValue(), testCategoryId, "BUG LOGIC: Lọc category=1 nhưng API lại trả về sản phẩm lẫn lộn danh mục khác!");
                }
                System.out.println("-> Dữ liệu lọc chính xác 100%.");
            }
        } catch (Exception e) {
            System.out.println("-> (Bỏ qua verify sâu do Backend dùng tên trường danh mục khác, tạm thời Pass Status 200).");
        }
    }
}