package org.example.tests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ApiSearchTest {

    // =======================================================
    // TC07: GỌI API TÌM KIẾM THEO TỪ KHÓA
    // =======================================================

    @Test
    public void FE05_API01_SearchProductsByKeyword() throws Exception {
        System.out.println("\nĐang chạy: FE05-API01 - Tìm kiếm API theo từ khóa");

        String searchKeyword = "sơ mi";
        String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        String apiUrl = "http://127.0.0.1:8000/api/products/?search=" + encodedKeyword;

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        Assert.assertEquals(statusCode, 200, "BUG API: Server không trả về mã 200 OK!");

        String responseBody = response.body().toLowerCase();

        // 1. Kiểm tra phải có chứa từ khóa
        Assert.assertTrue(responseBody.contains(searchKeyword.toLowerCase()),
                "BUG API: Dữ liệu JSON trả về rỗng hoặc không chứa '" + searchKeyword + "'");

        // 2. BƯỚC BẮT BUG: Kiểm tra ngược (Negative Check)
        boolean hasWrongProduct = responseBody.contains("váy") || responseBody.contains("quần");

        Assert.assertFalse(hasWrongProduct,
                "BUG (Đã bắt được): API trả về toàn bộ sản phẩm (lẫn lộn Váy/Quần) thay vì chỉ lọc từ khóa 'sơ mi'!");
    }

    // =======================================================
    // TC08: GỌI API TÌM KIẾM KẾT HỢP LỌC DANH MỤC
    // =======================================================

    @Test
    public void FE05_API02_SearchAndFilterCategory() throws Exception {
        System.out.println("\nĐang chạy: FE05-API02 - Tìm kiếm API kết hợp lọc danh mục");

        String searchKeyword = "áo";
        int targetCategoryId = 2;
        int wrongCategoryId = 1;

        String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        String apiUrl = "http://127.0.0.1:8000/api/products/?search=" + encodedKeyword + "&category=" + targetCategoryId;

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(response.statusCode(), 200, "BUG API: Server không trả về 200 OK!");

        String responseBody = response.body().toLowerCase();

        // 1. Kiểm tra cơ bản
        Assert.assertTrue(responseBody.contains(searchKeyword.toLowerCase()),
                "BUG API: Không tìm thấy sản phẩm nào chứa từ khóa '" + searchKeyword + "'");

        // 2. BƯỚC BẮT BUG: Lọc category=2 thì trong JSON tuyệt đối không được chứa category=1
        boolean hasWrongCategory = responseBody.contains("\"category_id\":" + wrongCategoryId)
                || responseBody.contains("\"category\":" + wrongCategoryId);

        Assert.assertFalse(hasWrongCategory,
                "BUG (Đã bắt được): API đang bỏ qua tham số lọc category, trả về toàn bộ sản phẩm của danh mục khác!");
    }
}