package org.example.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.StringJoiner;

public class CartApiTest {

    private static final String BASE_URL = "http://127.0.0.1:8000";
    private static final String LOGIN_URL = BASE_URL + "/login/";
    private static final String PRIMARY_PRODUCT_ID = "10";
    private static final String SECONDARY_PRODUCT_ID = "7";
    private static final String PRIMARY_COLOR = "\u0110en";
    private static final String SECONDARY_COLOR = "Xanh \u0111en";
    private static final String SIZE = "S";
    private static final String COUPON_CODE = "FLASH20";

    private CookieManager cookieManager;
    private HttpClient httpClient;
    private String csrfToken;

    @BeforeMethod
    public void initializeSession() throws Exception {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder(URI.create(LOGIN_URL))
                        .GET()
                        .timeout(Duration.ofSeconds(10))
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        Assert.assertEquals(response.statusCode(), 200,
                "Không thể khởi tạo phiên đăng nhập để chạy test API giỏ hàng");

        csrfToken = cookieManager.getCookieStore()
                .get(URI.create(BASE_URL))
                .stream()
                .filter(cookie -> "csrftoken".equals(cookie.getName()))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(csrfToken, "Không lấy được cookie CSRF để chạy test API giỏ hàng");
        Assert.assertFalse(csrfToken.isBlank(), "CSRF token không được để trống");
    }

    @Test(description = "FE08-API01 - [API] Thêm sản phẩm vào giỏ hàng thành công")
    public void testAddCartItemApi() throws Exception {
        HttpResponse<String> response = sendFormPost("/cart/add/", Map.of(
                "product_id", PRIMARY_PRODUCT_ID,
                "color", PRIMARY_COLOR,
                "size", SIZE
        ));

        assertSuccessResponse(response);
    }

    @Test(description = "API - Đọc dữ liệu giỏ hàng hiện tại")
    public void testReadCartDataApi() throws Exception {
        HttpResponse<String> response = httpClient.send(
                baseRequest("/cart/data/").GET().build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        Assert.assertEquals(response.statusCode(), 200, "API đọc dữ liệu giỏ hàng phải trả về mã trạng thái 200");
    }

    @Test(description = "API - Thêm một biến thể sản phẩm hợp lệ khác vào giỏ hàng")
    public void testAddSecondCartItemApi() throws Exception {
        HttpResponse<String> response = sendFormPost("/cart/add/", Map.of(
                "product_id", SECONDARY_PRODUCT_ID,
                "color", SECONDARY_COLOR,
                "size", SIZE
        ));

        assertSuccessResponse(response);
    }

    @Test(description = "FE09-API01 - [API] Cập nhật số lượng sản phẩm trong giỏ hàng")
    public void testUpdateQuantityApi() throws Exception {
        ensurePrimaryItemExists();

        HttpResponse<String> response = sendJsonPost("/cart/update/",
                "{\"product_id\":10,\"color\":\"\\u0110en\",\"size\":\"S\",\"action\":\"increase\"}");

        assertSuccessResponse(response);
    }

    @Test(description = "API - Bật hoặc tắt trạng thái chọn của một sản phẩm trong giỏ hàng")
    public void testToggleSelectionApi() throws Exception {
        ensurePrimaryItemExists();

        HttpResponse<String> response = sendJsonPost("/cart/update/",
                "{\"product_id\":10,\"color\":\"\\u0110en\",\"size\":\"S\",\"action\":\"toggle\"}");

        assertSuccessResponse(response);
    }

    @Test(description = "API - Chọn tất cả sản phẩm trong giỏ hàng")
    public void testToggleAllApi() throws Exception {
        ensurePrimaryItemExists();

        HttpResponse<String> response = sendJsonPost("/cart/update/",
                "{\"action\":\"toggle_all\",\"checked\":true}");

        assertSuccessResponse(response);
    }

    @Test(description = "API - Áp dụng mã ưu đãi cho giỏ hàng")
    public void testApplyCouponApi() throws Exception {
        ensurePrimaryItemExists();

        HttpResponse<String> response = sendJsonPost("/apply-coupon/",
                "{\"code\":\"" + COUPON_CODE + "\"}");

        assertSuccessResponse(response);
    }

    @Test(description = "FE10-API01 - [API] Xóa sản phẩm khỏi giỏ hàng thành công")
    public void testDeleteCartItemApi() throws Exception {
        ensurePrimaryItemExists();

        HttpResponse<String> response = sendJsonPost("/cart/update/",
                "{\"product_id\":10,\"color\":\"\\u0110en\",\"size\":\"S\",\"action\":\"delete\"}");

        assertSuccessResponse(response);
    }

    private void ensurePrimaryItemExists() throws Exception {
        HttpResponse<String> response = sendFormPost("/cart/add/", Map.of(
                "product_id", PRIMARY_PRODUCT_ID,
                "color", PRIMARY_COLOR,
                "size", SIZE
        ));

        assertSuccessResponse(response);
    }

    private HttpResponse<String> sendFormPost(String path, Map<String, String> formValues) throws IOException, InterruptedException {
        StringJoiner body = new StringJoiner("&");
        formValues.forEach((key, value) -> body.add(encode(key) + "=" + encode(value)));

        return httpClient.send(
                baseRequest(path)
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
    }

    private HttpResponse<String> sendJsonPost(String path, String json) throws IOException, InterruptedException {
        return httpClient.send(
                baseRequest(path)
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json, text/plain, */*")
                .header("Origin", BASE_URL)
                .header("Referer", LOGIN_URL)
                .header("X-CSRFToken", csrfToken);
    }

    private void assertSuccessResponse(HttpResponse<String> response) {
        Assert.assertEquals(response.statusCode(), 200, "Phản hồi API không đúng mong đợi: " + response.body());
        Assert.assertTrue(
                response.body().contains("\"success\": true") || response.body().contains("\"success\":true"),
                "Nội dung phản hồi API không thể hiện thao tác thành công: " + response.body()
        );
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
