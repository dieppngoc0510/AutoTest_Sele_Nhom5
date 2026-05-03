package org.example.tests;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CheckoutApiTest extends BaseTest {

    private static final String AUTH_TOKEN = "Token 870d1ea9be2e5d6c8eaef2803aea6e6ad204fbd9";

    private static int createdOrderId = 0;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://127.0.0.1:8000/api";
    }

    @Test(priority = 1)
    public void FE11_API01_CreateOrderSuccessfully() {
        System.out.println("\n--- Đang chạy: API01 - Tạo đơn hàng thành công (Luồng chính) ---");

        String requestBody = "{\n" +
                "  \"address\": \"50 Phan Tứ, Đà Nẵng\",\n" +
                "  \"shipping_method\": 1,\n" +
                "  \"payment_method\": \"COD\",\n" +
                "  \"total_amount\": 150000,\n" +
                "  \"items\": [{\"product_id\": 1, \"quantity\": 1}]\n" +
                "}";

        Response response = given()
                .header("Authorization", AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders/");

        System.out.println("Status Code: " + response.getStatusCode());
        if (response.getStatusCode() != 201) response.prettyPrint();

        Assert.assertEquals(response.getStatusCode(), 201, "Lỗi: Trạng thái không phải 201 Created");

        try {
            response.then().body("status", equalTo("pending"));
        } catch (AssertionError e) {
            System.out.println(" BUG LOGIC: Hệ thống tạo đơn thành công nhưng trạng thái sai (không phải pending).");
            throw e; // Ném lỗi để đánh FAILED test case này đúng như SRS yêu cầu
        }

        try { createdOrderId = response.jsonPath().getInt("id"); }
        catch(Exception e) { createdOrderId = response.jsonPath().getInt("order_id"); }
    }

    @Test(priority = 2)
    public void FE11_API02_CreateOrderMissingRequiredFields() {
        System.out.println("\n--- Đang chạy: API02 - Tạo đơn hàng thiếu trường bắt buộc (address) ---");

        String requestBody = "{\n" +
                "  \"shipping_method\": 1,\n" +
                "  \"payment_method\": \"COD\",\n" +
                "  \"total_amount\": 150000,\n" +
                "  \"items\": [{\"product_id\": 1, \"quantity\": 1}]\n" +
                "}";

        Response response = given()
                .header("Authorization", AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders/");

        System.out.println("Status Code: " + response.getStatusCode());
        if (response.getStatusCode() == 201) {
            System.out.println(" BUG NGHIÊM TRỌNG: Thiếu địa chỉ mà hệ thống vẫn cho tạo đơn thành công!");
        }
        Assert.assertEquals(response.getStatusCode(), 400, "Lỗi: Hệ thống không bắt lỗi (Không trả về 400 Bad Request)");
    }

    @Test(priority = 3)
    public void FE11_API03_CreateOrderWithoutAuthToken() {
        System.out.println("\n--- Đang chạy: API03 - Tạo đơn hàng không có Token xác thực ---");

        String requestBody = "{\n" +
                "  \"address\": \"50 Phan Tứ, Đà Nẵng\",\n" +
                "  \"payment_method\": \"COD\",\n" +
                "  \"total_amount\": 150000,\n" +
                "  \"items\": [{\"product_id\": 1, \"quantity\": 1}]\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders/");

        System.out.println("Status Code: " + response.getStatusCode());
        Assert.assertTrue(response.getStatusCode() == 401 || response.getStatusCode() == 403, "Lỗi: Hệ thống không chặn user chưa đăng nhập");
    }

    @Test(priority = 4)
    public void FE11_API04_CreateOrderWithEmptyItems() {
        System.out.println("\n--- Đang chạy: API05 - [Ngoại lệ] Tạo đơn với danh sách sản phẩm rỗng ---");

        String requestBody = "{\n" +
                "  \"address\": \"50 Phan Tứ, Đà Nẵng\",\n" +
                "  \"shipping_method\": 1,\n" +
                "  \"payment_method\": \"COD\",\n" +
                "  \"total_amount\": 150000,\n" +
                "  \"items\": []\n" +
                "}";

        Response response = given()
                .header("Authorization", AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders/");

        System.out.println("Status Code: " + response.getStatusCode());
        if (response.getStatusCode() == 201) {
            System.out.println(" BUG: Giỏ hàng trống rỗng mà vẫn tạo được đơn!");
        }
        Assert.assertEquals(response.getStatusCode(), 400, "Lỗi: Hệ thống không chặn đơn hàng rỗng.");
    }

    @Test(priority = 5)
    public void FE11_API05_CreateOrderWithOutOfStockItem() {
        System.out.println("\n--- Đang chạy: API06 - [Ngoại lệ] Tạo đơn chứa sản phẩm hết hàng ---");

        String requestBody = "{\n" +
                "  \"address\": \"50 Phan Tứ, Đà Nẵng\",\n" +
                "  \"shipping_method\": 1,\n" +
                "  \"payment_method\": \"COD\",\n" +
                "  \"total_amount\": 150000,\n" +
                "  \"items\": [{\"product_id\": 9999, \"quantity\": 1}]\n" +
                "}";

        Response response = given()
                .header("Authorization", AUTH_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders/");

        System.out.println("Status Code: " + response.getStatusCode());
        if (response.getStatusCode() == 201) {
            System.out.println(" BUG: Sản phẩm không tồn tại/hết hàng mà vẫn tạo được đơn!");
        }
        Assert.assertEquals(response.getStatusCode(), 400, "Lỗi: Hệ thống không chặn sản phẩm hết hàng.");
    }
}