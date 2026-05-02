package org.example.tests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.example.tests.Constant.*;


public class ChangePasswordTest extends BaseTest {

    // Mật khẩu mới dùng trong TC01 (phải hợp lệ ≥ 8 ký tự)
    private static final String NEW_VALID_PW = "NewPass@456";

    @BeforeMethod
    public void beforeEach() {
        login();
        goToChangePassword();
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

        Assert.assertTrue(isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi đổi mật khẩu");
        System.out.println("  → PASS: Đổi mật khẩu thành công");

        // Đổi lại mật khẩu cũ để không ảnh hưởng TC khác
        System.out.println("  → Khôi phục mật khẩu cũ...");
        login(TEST_USERNAME, NEW_VALID_PW);
        goToChangePassword();
        driver.findElement(FIELD_OLD_PW).sendKeys(NEW_VALID_PW);
        driver.findElement(FIELD_NEW_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(FIELD_CONFIRM_PW).sendKeys(TEST_PASSWORD);
        driver.findElement(BTN_CHANGE_PW).click();
        sleep(2000);
        System.out.println("  → Đã khôi phục mật khẩu cũ");
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

        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống báo thành công dù sai mật khẩu hiện tại!");

        Assert.assertTrue(
                pageContainsError("không đúng", "incorrect", "wrong",
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

        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống báo thành công dù xác nhận không khớp!");

        Assert.assertTrue(
                pageContainsError("không khớp", "does not match", "mismatch",
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

        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống báo thành công dù mật khẩu chỉ 3 ký tự!");

        Assert.assertTrue(
                pageContainsError("ít nhất 8", "at least 8", "minimum",
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

        boolean hasError = pageContainsError(
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

        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống báo thành công dù không nhập gì!");

        Assert.assertTrue(
                pageContainsError("vui lòng nhập", "required", "bắt buộc",
                        "không được để trống", "error", "invalid"),
                "FAIL: Không hiển thị lỗi khi để trống tất cả trường"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi đúng khi để trống tất cả trường");
    }
}
