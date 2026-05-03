package org.example.tests;

import org.example.pages.LoginPage;
import org.example.pages.HomePage;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.Set;

public class LogoutTest extends BaseTest {

    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void prepareUser() {
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);

        // Đăng nhập với tài khoản người dùng mặc định
        bp().loginAsDefaultUser();
    }

    @Test(description = "FE03-TC01 - Đăng xuất thành công từ trang người dùng")
    public void testUserLogoutSuccess() {
        // 1. Mở menu người dùng và nhấn Đăng xuất
        homePage.openUserMenu();
        homePage.clickLogout();

        // 2. Kiểm tra chuyển hướng về trang chủ sau khi đăng xuất
        String expectedUrl = BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/";
        wait.until(d -> {
            String url = d.getCurrentUrl();
            return url.equals(expectedUrl) || url.equals(expectedUrl.substring(0, expectedUrl.length() - 1));
        });

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                currentUrl.equals(expectedUrl) || currentUrl.equals(expectedUrl.substring(0, expectedUrl.length() - 1)),
                "Hệ thống không chuyển hướng về trang chủ sau khi đăng xuất. URL hiện tại: " + currentUrl
        );
    }

    @Test(description = "FE03-TC02 - Menu người dùng hiển thị đúng khi nhấn vào tên/avatar")
    public void testUserMenuDisplay() {
        // 1. Nhấn vào menu người dùng
        homePage.openUserMenu();

        // 2. Kiểm tra menu dropdown và nút đăng xuất có hiển thị hay không
        Assert.assertTrue(homePage.isUserMenuDisplayed(), "Menu người dùng không hiển thị.");
    }

    @Test(description = "FE03-TC03 - Không thể truy cập trang cá nhân sau khi đăng xuất")
    public void testDirectAccessProfileAfterLogout() {
        // 1. Thực hiện đăng xuất
        homePage.openUserMenu();
        homePage.clickLogout();

        // 2. Xác nhận đã về trang chủ
        String expectedUrl = BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/";
        wait.until(d -> {
            String url = d.getCurrentUrl();
            return url.equals(expectedUrl) || url.equals(expectedUrl.substring(0, expectedUrl.length() - 1));
        });

        String homeUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                homeUrl.equals(expectedUrl) || homeUrl.equals(expectedUrl.substring(0, expectedUrl.length() - 1)),
                "Hệ thống không chuyển hướng về trang chủ sau khi đăng xuất. URL hiện tại: " + homeUrl
        );

        // 3. Thử truy cập trực tiếp URL trang thông tin cá nhân
        driver.get(BASE_URL + "profile/");

        // 4. Hệ thống phải đẩy về trang đăng nhập để bảo mật
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                "Người dùng vẫn truy cập được trang cá nhân sau khi đã logout.");
    }

    @Test(description = "FE03-TC04 - Nhấn Back trên trình duyệt sau khi logout không vào lại được phiên làm việc")
    public void testPressBackAfterUserLogout() {
        homePage.openUserMenu();
        homePage.clickLogout();

        // 1. Xác nhận đã về trang chủ
        String expectedUrl = BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/";
        wait.until(d -> {
            String url = d.getCurrentUrl();
            return url.equals(expectedUrl) || url.equals(expectedUrl.substring(0, expectedUrl.length() - 1));
        });

        String homeUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                homeUrl.equals(expectedUrl) || homeUrl.equals(expectedUrl.substring(0, expectedUrl.length() - 1)),
                "Hệ thống không chuyển hướng về trang chủ sau khi đăng xuất. URL hiện tại: " + homeUrl
        );

        // 2. Nhấn nút Back trên trình duyệt
        driver.navigate().back();
        sleep(1000);

        // 3. Không được thấy nội dung cũ của người dùng, phải ở trang login hoặc trang chủ (chưa đăng nhập)
        String urlAfterBack = driver.getCurrentUrl();
        String expectedHome = BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/";
        Assert.assertTrue(
                urlAfterBack.contains("/login")
                        || urlAfterBack.equals(expectedHome)
                        || urlAfterBack.equals(expectedHome.substring(0, expectedHome.length() - 1)),
                "Nút Back vẫn cho phép quay lại nội dung cá nhân sau khi đăng xuất. URL: " + urlAfterBack
        );
    }

    @Test(description = "FE03-TC05 - Session Cookie của người dùng bị xóa hoàn toàn")
    public void testUserSessionCleanup() {
        // 1. Kiểm tra cookie phiên làm việc tồn tại
        Set<Cookie> cookies = driver.manage().getCookies();
        boolean hasSession = cookies.stream().anyMatch(c ->
                c.getName().contains("session") || c.getName().equals("JSESSIONID")
        );
        Assert.assertTrue(hasSession, "Không tìm thấy session cookie của người dùng.");

        // 2. Đăng xuất
        homePage.openUserMenu();
        homePage.clickLogout();

        // 3. Xác nhận đã về trang chủ
        String expectedUrl = BASE_URL.endsWith("/") ? BASE_URL : BASE_URL + "/";
        wait.until(d -> {
            String url = d.getCurrentUrl();
            return url.equals(expectedUrl) || url.equals(expectedUrl.substring(0, expectedUrl.length() - 1));
        });

        String homeUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                homeUrl.equals(expectedUrl) || homeUrl.equals(expectedUrl.substring(0, expectedUrl.length() - 1)),
                "Hệ thống không chuyển hướng về trang chủ sau khi đăng xuất. URL hiện tại: " + homeUrl
        );

        // 4. Kiểm tra cookie đã bị xóa sạch
        Set<Cookie> cookiesAfter = driver.manage().getCookies();
        boolean sessionExists = cookiesAfter.stream().anyMatch(c ->
                c.getName().contains("session") || c.getName().equals("JSESSIONID")
        );
        Assert.assertFalse(sessionExists, "Session cookie vẫn tồn tại sau khi người dùng đăng xuất.");
    }
}
