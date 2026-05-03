package org.example.tests;

import org.example.tests.Constant;
import org.example.pages.HomePage;
import org.example.pages.SearchResultPage;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    @Override
    public void beforeMethod() {
        super.beforeMethod();
    }

    @Test
    public void FE05_TC01_SearchWithValidKeyword() {
        System.out.println("\nĐang chạy: FE05-TC01 - Tìm kiếm với từ khóa hợp lệ (Tên SP)");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        SearchResultPage resultPage = homePage.searchProduct("sơ mi");
        sleep(2000);

        // 1. Kiểm tra danh sách có chứa chữ 'sơ mi' không
        boolean isCorrect = resultPage.areProductsFoundContain("sơ mi");
        Assert.assertTrue(isCorrect, "BUG: Có sản phẩm hiển thị không chứa từ khóa 'sơ mi'");

        // 2. Bổ sung: Kiểm tra UI thẻ sản phẩm
        boolean isUIValid = resultPage.isProductListUIValid();
        Assert.assertTrue(isUIValid, "BUG: Cấu trúc thẻ sản phẩm tìm kiếm bị lỗi (Thiếu Ảnh/Tên/Giá)!");
    }

    @Test
    public void FE05_TC02_SearchWithNoResults() {
        System.out.println("\nĐang chạy: FE05-TC02 - Tìm kiếm không có kết quả");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        SearchResultPage resultPage = homePage.searchProduct("xyzabc123");
        sleep(2000);

        int count = resultPage.getProductCount();
        Assert.assertEquals(count, 0, "BUG: Từ khóa sai nhưng vẫn trả về sản phẩm!");

        String message = resultPage.getSearchMessage().toLowerCase();
        Assert.assertTrue(message.contains("không tìm thấy"), "BUG: Không hiện thông báo lỗi tìm kiếm rỗng!");
    }

    @Test
    public void FE05_TC03_SearchWithEmptyInput() {
        System.out.println("\nĐang chạy: FE05-TC03 - Tìm kiếm với ô trống");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        SearchResultPage resultPage = homePage.searchProduct("");
        sleep(2000);

        int count = resultPage.getProductCount();
        Assert.assertTrue(count > 0, "BUG: Web không hiển thị gì hoặc bị crash khi tìm ô trống!");
    }

    @Test
    public void FE05_TC04_SearchCaseInsensitive() {
        System.out.println("\nĐang chạy: FE05-TC04 - Tìm kiếm không phân biệt hoa/thường");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        SearchResultPage resultPage = homePage.searchProduct("SƠ mI");
        sleep(2000);

        boolean isCorrect = resultPage.areProductsFoundContain("sơ mi");
        Assert.assertTrue(isCorrect, "BUG: Hệ thống phân biệt hoa thường, kết quả không chính xác!");
    }

    @Test
    public void FE05_TC05_SearchByValidProductCode() {
        System.out.println("\nĐang chạy: FE05-TC05 - Tìm kiếm theo Mã sản phẩm hợp lệ");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        String validCode = "SP000000033";
        SearchResultPage resultPage = homePage.searchProduct(validCode);
        sleep(2000);

        int count = resultPage.getProductCount();
        Assert.assertEquals(count, 1, "BUG: Nhập mã sản phẩm cụ thể nhưng trả về khác 1 sản phẩm!");

        Assert.assertTrue(resultPage.isProductListUIValid(), "BUG: Thẻ sản phẩm hiển thị thiếu Ảnh/Tên/Giá!");
    }

    @Test
    public void FE05_TC06_SearchWithSpecialCharacters() {
        System.out.println("\nĐang chạy: FE05-TC06 - Tìm kiếm với ký tự đặc biệt");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        SearchResultPage resultPage = homePage.searchProduct("!@#$%");
        sleep(2000);

        int count = resultPage.getProductCount();
        Assert.assertEquals(count, 0, "BUG: XSS bị lọt hoặc trả về sản phẩm!");

        String message = resultPage.getSearchMessage().toLowerCase();
        Assert.assertTrue(message.contains("không tìm thấy"), "BUG: Crash XSS hoặc không hiện thông báo!");
    }

    @Test
    public void FE05_TC07_SearchDiscontinuedProduct() {
        System.out.println("\nĐang chạy: FE05-TC07 - Tìm kiếm Sản phẩm ngừng kinh doanh");
        HomePage homePage = new HomePage();
        homePage.open();
        sleep(2000);

        SearchResultPage resultPage = homePage.searchProduct("Áo test lỗi");
        sleep(2000);

        int count = resultPage.getProductCount();
        Assert.assertEquals(count, 0, "BUG: Sản phẩm đã bị ngừng kinh doanh/ẩn nhưng vẫn hiển thị ở kết quả tìm kiếm!");

        String message = resultPage.getSearchMessage().toLowerCase();
        Assert.assertTrue(message.contains("không tìm thấy"), "BUG: Không hiện thông báo chặn kết quả tìm kiếm!");
    }


}