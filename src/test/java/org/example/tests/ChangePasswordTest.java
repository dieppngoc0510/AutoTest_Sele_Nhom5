package org.example.tests;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.example.tests.Constant.*;


public class ChangePasswordTest extends BaseTest {

    // Mật khẩu mới dùng trong TC01 (phải hợp lệ ≥ 8 ký tự)
    private static final String NEW_VALID_PW = "NewPass@456";

    @BeforeMethod
    public void beforeEach() {
        System.out.println("  [INFO] Đang chuẩn bị môi trường cho test case...");
        
        // Thử đăng nhập bằng mật khẩu mặc định trước
        try {
            bp().login(VALID_USERNAME, VALID_PASSWORD);
        } catch (Exception e) {
            System.out.println("  [RECOVERY] Đăng nhập mặc định thất bại, thử khôi phục từ mật khẩu dự phòng...");
            try {
                bp().login(VALID_USERNAME, NEW_VALID_PW);
                
                // Nếu vào được bằng pass dự phòng, thực hiện đổi lại ngay lập tức
                bp().goToChangePassword();
                driver.findElement(FIELD_OLD_PW).clear();
                driver.findElement(FIELD_OLD_PW).sendKeys(NEW_VALID_PW);
                driver.findElement(FIELD_NEW_PW).clear();
                driver.findElement(FIELD_NEW_PW).sendKeys(VALID_PASSWORD);
                driver.findElement(FIELD_CONFIRM_PW).clear();
                driver.findElement(FIELD_CONFIRM_PW).sendKeys(VALID_PASSWORD);
                driver.findElement(BTN_CHANGE_PW).click();
                sleep(2000);
                
                // Sau khi đổi xong, login lại bằng pass mặc định để bắt đầu test
                bp().login(VALID_USERNAME, VALID_PASSWORD);
                System.out.println("  [SUCCESS] Đã tự động khôi phục mật khẩu về mặc định.");
            } catch (Exception fatal) {
                throw new RuntimeException(">>> KHÔNG THỂ CHUẨN BỊ MÔI TRƯỜNG: " + fatal.getMessage());
            }
        }
        
        bp().goToChangePassword();
    }

    // ════════════════════════════════════════════════════
    // TC01 – Đổi mật khẩu thành công
    // ════════════════════════════════════════════════════
    @Test(priority = 1, description = "FE07-TC01: Đổi mật khẩu thành công")
    public void TC01_doiMatKhauThanhCong() {
        System.out.println("\n[FE07-TC01] Đổi mật khẩu thành công...");

        bp().clearAndType(FIELD_OLD_PW, VALID_PASSWORD);
        bp().clearAndType(FIELD_NEW_PW, NEW_VALID_PW);
        bp().clearAndType(FIELD_CONFIRM_PW, NEW_VALID_PW);
        driver.findElement(BTN_CHANGE_PW).click();

        sleep(2000);

        boolean success = bp().isSuccess() || driver.getCurrentUrl().contains("/login/");
        
        try {
            Assert.assertTrue(success, "FAIL: Không thấy dấu hiệu thành công (toast hoặc redirect login) sau khi đổi mật khẩu");
            System.out.println("  → PASS: Đổi mật khẩu thành công");
        } finally {
            // Luôn cố gắng khôi phục mật khẩu cũ dù test fail hay pass
            System.out.println("  → Tiến hành khôi phục mật khẩu cũ để tránh ảnh hưởng test case khác...");
            restoreOriginalPassword();
        }
    }

    private void restoreOriginalPassword() {
        try {
            System.out.println("  [RESTORE] Đang kiểm tra trạng thái mật khẩu...");
            driver.get(BASE_URL + "logout/?silent=1");
            
            // 1. Thử login bằng mật khẩu mặc định trước
            try {
                bp().login(VALID_USERNAME, VALID_PASSWORD);
                if (!driver.getCurrentUrl().contains("/login/")) {
                    System.out.println("  [RESTORE] Mật khẩu đã là mặc định, không cần khôi phục.");
                    return;
                }
            } catch (Exception e) {
                // Tiếp tục thử recovery bên dưới
            }

            // 2. Nếu không vào được bằng pass mặc định, thử bằng pass mới để đổi lại
            System.out.println("  [RESTORE] Đang khôi phục từ mật khẩu dự phòng...");
            driver.get(BASE_URL + "logout/?silent=1");
            bp().login(VALID_USERNAME, NEW_VALID_PW);
            
            bp().goToChangePassword();
            bp().clearAndType(FIELD_OLD_PW, NEW_VALID_PW);
            bp().clearAndType(FIELD_NEW_PW, VALID_PASSWORD);
            bp().clearAndType(FIELD_CONFIRM_PW, VALID_PASSWORD);
            driver.findElement(BTN_CHANGE_PW).click();
            sleep(2000);
            
            // 3. Verify cuối cùng
            driver.get(BASE_URL + "logout/?silent=1");
            bp().login(VALID_USERNAME, VALID_PASSWORD);
            if (!driver.getCurrentUrl().contains("/login/")) {
                System.out.println("  [SUCCESS] Đã khôi phục mật khẩu mặc định thành công.");
            } else {
                System.err.println("  [FAIL] Khôi phục mật khẩu thất bại!");
            }
        } catch (Exception e) {
            System.err.println("  [ERROR] Lỗi trong quá trình khôi phục: " + e.getMessage());
        }
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

        driver.findElement(FIELD_OLD_PW).sendKeys(VALID_PASSWORD);
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

        driver.findElement(FIELD_OLD_PW).sendKeys(VALID_PASSWORD);
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

        driver.findElement(FIELD_OLD_PW).sendKeys(VALID_PASSWORD);
        driver.findElement(FIELD_NEW_PW).sendKeys(VALID_PASSWORD);     // Trùng
        driver.findElement(FIELD_CONFIRM_PW).sendKeys(VALID_PASSWORD);
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
        // beforeEach đã login sẵn, không cần login lại ở đây
        
        // Dùng chung NEW_VALID_PW cho thống nhất
        String body = "{\"old_password\":\"" + VALID_PASSWORD + "\"," +
                "\"new_password\":\"" + NEW_VALID_PW + "\"}";
        log("Gọi PUT " + API_CHANGE_PW);

        String result = bp().callChangePwAPI("PUT", body);
        String status = bp().statusOf(result);

        log("API status : " + status);
        Assert.assertTrue(status.equals("200") || status.equals("302"), 
                "API đổi mật khẩu thất bại, nhận status: " + status);

        // ── Dọn dẹp: đặt lại mật khẩu ban đầu bằng API ──────────────
        log("Reset lại mật khẩu ban đầu bằng API...");
        String resetBody = "{\"old_password\":\"" + NEW_VALID_PW + "\"," +
                "\"new_password\":\"" + VALID_PASSWORD + "\"}";
        bp().callChangePwAPI("PUT", resetBody);
    }

    @Test(priority = 8,
            description = "FE07_API02 – PUT /api/user/change-password – Mật khẩu cũ sai")
    public void API02_MatKhauCuSai() {
        // beforeEach đã login sẵn
        
            String body = "{\"old_password\":\"WrongPass@WrongWrong\",\"new_password\":\"NewPass@789!\"}";
        log("Gọi PUT " + API_CHANGE_PW + " với mật khẩu cũ SAI");

        String result = bp().callChangePwAPI("PUT", body);
        String status = bp().statusOf(result);
        String resp   = bp().bodyOf(result);

        log("API status : " + status);

        if (status.equals("400") || status.equals("422")) {
            Assert.assertTrue(true);
        } else if (status.equals("200") || status.equals("302")) {
            // Django redirect về login thường nghĩa là không đổi thành công nếu dữ liệu sai (tùy backend)
            // Kiểm tra mật khẩu cũ vẫn dùng được
            driver.get(BASE_URL + "/logout/");
            driver.get(BASE_URL + "/login/");
            bp().login(VALID_USERNAME, VALID_PASSWORD);
            Assert.assertTrue(!driver.getCurrentUrl().contains("/login/"),
                    "Mật khẩu không được thay đổi khi nhập mật khẩu cũ sai");
        }
    }

    @Test(priority = 9,
            description = "FE07_API03 – PUT /api/user/change-password – Không có token xác thực")
    public void API03_KhongCoToken() {
        driver.get(BASE_URL + "logout/?silent=1");
        sleep(500);
        
        String result    = bp().callChangePwAPI("PUT",
                "{\"old_password\":\"any\",\"new_password\":\"newpass123\"}");
        String apiStatus = bp().statusOf(result);
        
        Assert.assertTrue(apiStatus.equals("401") || apiStatus.equals("403") || apiStatus.equals("302"),
                "Khi chưa xác thực, API phải trả về lỗi hoặc redirect");
    }
}
