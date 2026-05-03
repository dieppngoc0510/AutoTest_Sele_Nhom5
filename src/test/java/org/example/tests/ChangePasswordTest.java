package org.example.tests;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.example.tests.Constant.*;


public class ChangePasswordTest extends BaseTest {

    // Mật khẩu mới dùng trong TC01 (phải hợp lệ ≥ 8 ký tự)
    private static final String NEW_VALID_PW = "NewPass@456";

    @BeforeMethod
    public void beforeEach() {
        bp().login();
        bp().goToChangePassword();
    }

    // ════════════════════════════════════════════════════
    // TC01 – Đổi mật khẩu thành công
    // ════════════════════════════════════════════════════
    @Test(priority = 1, description = "FE07-TC01: Đổi mật khẩu thành công")
    public void TC01_doiMatKhauThanhCong() {
        System.out.println("\n[FE07-TC01] Đổi mật khẩu thành công...");

        driver.findElement(FIELD_OLD_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(FIELD_NEW_PW).sendKeys(NEW_VALID_PW);
        driver.findElement(FIELD_CONFIRM_PW).sendKeys(NEW_VALID_PW);
        driver.findElement(BTN_CHANGE_PW).click();

        sleep(2000);

        Assert.assertTrue(bp().isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi đổi mật khẩu");
        System.out.println("  \u2192 PASS: Đổi mật khẩu thành công");

        // Đổi lại mật khẩu cũ để không ảnh hưởng TC khác
        System.out.println("  \u2192 Khôi phục mật khẩu cũ...");
        sleep(1000); // chờ server redirect về /login/
        // Server đã redirect sang /login/, dùng LoginPage trực tiếp
        org.example.pages.LoginPage lp = new org.example.pages.LoginPage(driver);
        lp.login(TEST_USERNAME, NEW_VALID_PW);
        wait.until(d -> !d.getCurrentUrl().contains("login/"));
        bp().goToChangePassword();
        driver.findElement(FIELD_OLD_PW).sendKeys(NEW_VALID_PW);
        driver.findElement(FIELD_NEW_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(FIELD_CONFIRM_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(BTN_CHANGE_PW).click();
        sleep(2000);
        System.out.println("  \u2192 Đã khôi phục mật khẩu cũ");
    }

    // ════════════════════════════════════════════════════
    // TC02 – Sai mật khẩu hiện tại
    // ════════════════════════════════════════════════════
    @Test(priority = 2, description = "FE07-TC02: Sai mật khẩu hiện tại")
    public void TC02_saiMatKhauHienTai() {
        System.out.println("\n[FE07-TC02] Sai mật khẩu hiện tại...");

        driver.findElement(FIELD_OLD_PW).sendKeys("SaiHoanToan@999");
        driver.findElement(FIELD_NEW_PW).sendKeys(NEW_VALID_PW);
        driver.findElement(FIELD_CONFIRM_PW).sendKeys(NEW_VALID_PW);
        driver.findElement(BTN_CHANGE_PW).click();
        sleep(1500);

        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống báo thành công dù sai mật khẩu hiện tại!");

        Assert.assertTrue(
                bp().pageContainsError("không đúng", "incorrect", "wrong",
                        "mật khẩu hiện tại", "current password", "error"),
                "FAIL: Không hiển thị lỗi khi sai mật khẩu hiện tại"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi đúng khi sai mật khẩu hiện tại");
    }

    // ════════════════════════════════════════════════════
    // TC03 – Mật khẩu mới và xác nhận không khớp
    // ════════════════════════════════════════════════════
    @Test(priority = 3, description = "FE07-TC03: Xác nhận mật khẩu không khớp")
    public void TC03_xacNhanKhongKhop() {
        System.out.println("\n[FE07-TC03] Xác nhận mật khẩu không khớp...");

        driver.findElement(FIELD_OLD_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(FIELD_NEW_PW).sendKeys("NewPass@456");
        driver.findElement(FIELD_CONFIRM_PW).sendKeys("NewPass@999"); // Khác
        driver.findElement(BTN_CHANGE_PW).click();
        sleep(1500);

        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống báo thành công dù xác nhận không khớp!");

        Assert.assertTrue(
                bp().pageContainsError("không khớp", "does not match", "mismatch",
                        "xác nhận", "confirm", "error"),
                "FAIL: Không hiển thị lỗi khi xác nhận mật khẩu không khớp"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi đúng khi xác nhận không khớp");
    }

    // ════════════════════════════════════════════════════
    // TC04 – Mật khẩu mới quá ngắn (< 8 ký tự)
    // ════════════════════════════════════════════════════
    @Test(priority = 4, description = "FE07-TC04: Mật khẩu mới quá ngắn")
    public void TC04_matKhauQuaNgan() {
        System.out.println("\n[FE07-TC04] Mật khẩu mới quá ngắn (3 ký tự)...");

        driver.findElement(FIELD_OLD_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(FIELD_NEW_PW).sendKeys("123");
        driver.findElement(FIELD_CONFIRM_PW).sendKeys("123");
        driver.findElement(BTN_CHANGE_PW).click();
        sleep(1500);

        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống báo thành công dù mật khẩu chỉ 3 ký tự!");

        Assert.assertTrue(
                bp().pageContainsError("ít nhất 8", "at least 8", "minimum",
                        "too short", "quá ngắn", "error", "invalid"),
                "FAIL: Không hiển thị lỗi khi mật khẩu quá ngắn"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi đúng khi mật khẩu quá ngắn");
    }

    // ════════════════════════════════════════════════════
    // TC05 – Mật khẩu mới trùng mật khẩu cũ
    // ════════════════════════════════════════════════════
    @Test(priority = 5, description = "FE07-TC05: Mật khẩu mới trùng mật khẩu cũ")
    public void TC05_matKhauTrungCu() {
        System.out.println("\n[FE07-TC05] Mật khẩu mới trùng mật khẩu cũ...");

        driver.findElement(FIELD_OLD_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(FIELD_NEW_PW).sendKeys(TEST_PASSWORD);     // Trùng
        driver.findElement(FIELD_CONFIRM_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(BTN_CHANGE_PW).click();
        sleep(1500);

        boolean hasError = bp().pageContainsError(
                "không được trùng", "same as current", "must be different",
                "phải khác", "error"
        );

        if (hasError) {
            System.out.println("  → PASS: Hệ thống báo lỗi khi mật khẩu mới trùng cũ");
        } else {
            // Ghi nhận – một số hệ thống không có rule này
            System.out.println("  → LƯU Ý (FAIL): Website KHÔNG chặn mật khẩu mới trùng cũ");
            System.out.println("     → Ghi FAIL vào cột Kết Quả trong file Excel!");
        }
    }

    // ════════════════════════════════════════════════════
    // TC06 – Để trống tất cả trường
    // ════════════════════════════════════════════════════
    @Test(priority = 6, description = "FE07-TC06: Để trống tất cả trường")
    public void TC06_deTrongTatCaTruong() {
        System.out.println("\n[FE07-TC06] Để trống tất cả trường...");

        // Không nhập gì, nhấn thẳng nút Xác nhận
        driver.findElement(BTN_CHANGE_PW).click();
        sleep(1500);

        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống báo thành công dù không nhập gì!");

        Assert.assertTrue(
                bp().pageContainsError("vui lòng nhập", "required", "bắt buộc",
                        "không được để trống", "error", "invalid"),
                "FAIL: Không hiển thị lỗi khi để trống tất cả trường"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi đúng khi để trống tất cả trường");
    }

    @Test(priority = 7,
            description = "FE07_API01 – PUT /api/user/change-password – Đổi mật khẩu thành công")
    public void API01_DoiMatKhauThanhCong() {
        driver.get(BASE_URL + "logout/?silent=1");
        bp().loginAsUser();
        String body = "{\"old_password\":\"" + VALID_PASSWORD + "\"," +
                "\"new_password\":\"NewPass@456_api!\"}";
        log("Gọi PUT " + API_CHANGE_PW);
        log("Request body: " + body);

        String result = bp().callChangePwAPI("PUT", body);
        String status = bp().statusOf(result);
        String resp   = bp().bodyOf(result);

        log("API status : " + status);
        log("API body   : " + resp);

        // Chấp nhận 200 OK / 302 Redirect
        boolean success = status.equals("200") || status.equals("302");
        Assert.assertTrue(success,
                "API PUT /change-password phải trả về 200/302, nhận: " + status);

        if (status.equals("200")) {
            boolean hasSuccessHint = resp.contains("thành công") || resp.contains("success") ||
                    resp.contains("Password changed") || resp.contains("updated");
            log("Body chứa dấu hiệu thành công: " + hasSuccessHint);
        }

        // ── Dọn dẹp: đặt lại mật khẩu ban đầu ──────────────
        log("Reset lại mật khẩu ban đầu");
        String resetBody = "{\"old_password\":\"NewPass@456_api!\"," +
                "\"new_password\":\"" + VALID_PASSWORD + "\"}";
        String resetResult = bp().callChangePwAPI("PUT", resetBody);
        log("Reset status: " + bp().statusOf(resetResult));
    }

    @Test(priority = 8,
            description = "FE07_API02 – PUT /api/user/change-password – Mật khẩu cũ sai")
    public void API02_MatKhauCuSai() {
        driver.get(BASE_URL + "logout/?silent=1");
        bp().loginAsUser();
        String body = "{\"old_password\":\"WrongPass@WrongWrong\",\"new_password\":\"NewPass@789!\"}";
        log("Gọi PUT " + API_CHANGE_PW + " với mật khẩu cũ SAI");
        log("Request body: " + body);

        String result = bp().callChangePwAPI("PUT", body);
        String status = bp().statusOf(result);
        String resp   = bp().bodyOf(result);

        log("API status : " + status);
        log("API body   : " + resp);

        if (status.equals("400") || status.equals("422")) {
            // ── REST API chuẩn ────────────────────────────────
            boolean hasErrMsg = resp.contains("không đúng")    || resp.contains("không chính xác") ||
                    resp.contains("incorrect")      || resp.contains("invalid") ||
                    resp.contains("old_password")   || resp.contains("current_password");
            Assert.assertTrue(hasErrMsg,
                    "Response 400 phải chứa thông báo lỗi mật khẩu cũ sai");
        } else if (status.equals("200") || status.equals("302")) {
            // ── Django truyền thống ────────────────────────────
            log("API trả " + status + " – kiểm tra mật khẩu không bị thay đổi qua UI");
            // Thử đăng nhập lại bằng VALID_PASSWORD – nếu vẫn đăng nhập được thì mật khẩu chưa đổi
            driver.get(BASE_URL + "/logout/");
            driver.get(BASE_URL + "/login/");
            waitFor(By.name("username")).sendKeys(VALID_USERNAME);
            driver.findElement(By.name("password")).sendKeys(VALID_PASSWORD);
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
            boolean stillLoggedIn = !driver.getCurrentUrl().contains("/login/");
            log("Đăng nhập lại bằng mật khẩu cũ: " + (stillLoggedIn ? "THÀNH CÔNG ✓" : "THẤT BẠI ✗"));
            Assert.assertTrue(stillLoggedIn,
                    "Mật khẩu không được thay đổi khi nhập mật khẩu cũ sai");
        } else {
            Assert.fail("API trả trạng thái không mong đợi: " + status + " | body: " + resp);
        }
    }

    @Test(priority = 9,
            description = "FE07_API03 – PUT /api/user/change-password – Không có token xác thực")
    public void API03_KhongCoToken() {
        // KHÔNG đăng nhập - logout trước
        driver.get(BASE_URL + "logout/?silent=1");
        sleep(500);
        log("Truy cập " + CHANGE_PW_URL + " khi CHƯA đăng nhập");
        driver.get(CHANGE_PW_URL);

        boolean redirectLogin = driver.getCurrentUrl().contains("/login/") ||
                driver.getCurrentUrl().contains("/signin/");
        boolean showsLogin    = !driver.findElements(
                By.cssSelector("input[name='username'], input[name='password']")).isEmpty();

        log("Redirect login: " + redirectLogin);
        log("Hiện form login: " + showsLogin);

        // Gọi API trực tiếp không có session
        String result    = bp().callChangePwAPI("PUT",
                "{\"old_password\":\"any\",\"new_password\":\"newpass123\"}");
        String apiStatus = bp().statusOf(result);
        String apiBody   = bp().bodyOf(result);
        boolean apiBlocked = apiStatus.equals("401") || apiStatus.equals("403") ||
                apiStatus.equals("302");

        log("API (no session) – status: " + apiStatus + " | body: " + apiBody);

        Assert.assertTrue(redirectLogin || showsLogin || apiBlocked,
                "Khi chưa xác thực, phải redirect về login hoặc API trả 401/403");

        if (apiStatus.equals("401") || apiStatus.equals("403")) {
            log("API trả đúng " + apiStatus + " Unauthorized/Forbidden ✓");
            boolean hasUnauthorized = apiBody.contains("Unauthorized") ||
                    apiBody.contains("unauthorized")  ||
                    apiBody.contains("Authentication") ||
                    apiBody.contains("login");
            log("Body chứa thông báo xác thực: " + hasUnauthorized);
        }
    }
}
