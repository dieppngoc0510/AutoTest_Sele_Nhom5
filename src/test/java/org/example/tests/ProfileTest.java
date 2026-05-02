package org.example.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.example.tests.Constant.*;


public class ProfileTest extends BaseTest {

    @BeforeMethod
    public void beforeEach() {
        login();
        goToProfile();
    }

    // ════════════════════════════════════════════════════
    // TC01 – Xem thông tin cá nhân
    // ════════════════════════════════════════════════════
    @Test(priority = 1, description = "FE06-TC01: Xem thông tin cá nhân")
    public void TC01_xemThongTinCaNhan() {
        System.out.println("\n[FE06-TC01] Xem thông tin cá nhân...");

        String[] names  = {"Họ tên", "Email", "SĐT", "Ngày sinh", "Địa chỉ"};
        By[]     fields = {FIELD_FULLNAME, FIELD_EMAIL, FIELD_PHONE,
                FIELD_BIRTHDATE, FIELD_ADDRESS};

        for (int i = 0; i < fields.length; i++) {
            WebElement el = driver.findElement(fields[i]);
            Assert.assertTrue(el.isDisplayed(),
                    "FAIL: Trường '" + names[i] + "' không hiển thị!");
        }

        // Kiểm tra radio group giới tính có mặt
        Assert.assertFalse(
                driver.findElements(By.cssSelector(".s-form-control.s-radio-group")).isEmpty(),
                "FAIL: Khu vực chọn giới tính không hiển thị!"
        );

        System.out.println("  → PASS: Tất cả trường hiển thị đầy đủ");
    }

    // ════════════════════════════════════════════════════
    // TC02 – Cập nhật họ tên hợp lệ
    // ════════════════════════════════════════════════════
    @Test(priority = 2, description = "FE06-TC02: Cập nhật họ tên hợp lệ")
    public void TC02_capNhatHoTenHopLe() {
        System.out.println("\n[FE06-TC02] Cập nhật họ tên hợp lệ...");

        clearAndType(FIELD_FULLNAME, "Nguyễn Văn An");
        submitProfileForm();

        Assert.assertTrue(isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi cập nhật họ tên");
        System.out.println("  → PASS: Cập nhật họ tên thành công");
    }

    // ════════════════════════════════════════════════════
    // TC03 – Cập nhật Email hợp lệ
    // ════════════════════════════════════════════════════
    @Test(priority = 3, description = "FE06-TC03: Cập nhật email hợp lệ")
    public void TC03_capNhatEmailHopLe() {
        System.out.println("\n[FE06-TC03] Cập nhật email hợp lệ...");

        clearAndType(FIELD_EMAIL, NEW_EMAIL);
        submitProfileForm();
        Assert.assertTrue(isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi cập nhật email");
        System.out.println("  → PASS: Cập nhật email thành công");
    }

    // ════════════════════════════════════════════════════
    // TC04 – Cập nhật SĐT hợp lệ (10 chữ số)
    // ════════════════════════════════════════════════════
    @Test(priority = 4, description = "FE06-TC04: Cập nhật SĐT hợp lệ")
    public void TC04_capNhatSDTHopLe() {
        System.out.println("\n[FE06-TC04] Cập nhật SĐT hợp lệ (10 số)...");

        clearAndType(FIELD_PHONE, "0912345678");
        submitProfileForm();

        Assert.assertTrue(isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi cập nhật SĐT");
        System.out.println("  → PASS: Cập nhật SĐT thành công");
    }

    // ════════════════════════════════════════════════════
    // TC05 – Cập nhật địa chỉ hợp lệ
    // ════════════════════════════════════════════════════
    @Test(priority = 5, description = "FE06-TC05: Cập nhật địa chỉ hợp lệ")
    public void TC05_capNhatDiaChiHopLe() {
        System.out.println("\n[FE06-TC05] Cập nhật địa chỉ hợp lệ...");

        clearAndType(FIELD_ADDRESS, "123 Đường Lê Lợi, Quận 1, TP.HCM");
        submitProfileForm();

        Assert.assertTrue(isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi cập nhật địa chỉ");
        System.out.println("  → PASS: Cập nhật địa chỉ thành công");
    }

    // ════════════════════════════════════════════════════
    // TC06 – Cập nhật ngày sinh hợp lệ
    // ════════════════════════════════════════════════════
    @Test(priority = 6, description = "FE06-TC06: Cập nhật ngày sinh hợp lệ")
    public void TC06_capNhatNgaySinhHopLe() {
        System.out.println("\n[FE06-TC06] Cập nhật ngày sinh hợp lệ...");

        // type="date" → dùng JavaScript để set value vì sendKeys thường không ổn
        WebElement dobField = driver.findElement(FIELD_BIRTHDATE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '2000-01-01';", dobField);
        submitProfileForm();

        Assert.assertTrue(isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi cập nhật ngày sinh");
        System.out.println("  → PASS: Cập nhật ngày sinh thành công");
    }

    // ════════════════════════════════════════════════════
    // TC07 – Cập nhật giới tính (chọn Nữ)
    // ════════════════════════════════════════════════════
    @Test(priority = 7, description = "FE06-TC07: Cập nhật giới tính")
    public void TC07_capNhatGioiTinh() {
        System.out.println("\n[FE06-TC07] Cập nhật giới tính (chọn Nữ)...");

        // Radio group: 3 label.s-radio-btn (Nữ / Nam / Khác)
        // Click vào label chứa chữ "Nữ"
        try {
            WebElement labelNu = driver.findElement(By.xpath(
                    "//div[contains(@class,'s-radio-group')]//label[contains(.,'Nữ')]"));
            labelNu.click();
        } catch (Exception e) {
            // Fallback: click trực tiếp radio input đầu tiên
            driver.findElement(By.xpath(
                            "//div[contains(@class,'s-radio-group')]//input[@type='radio']"))
                    .click();
        }

        submitProfileForm();

        Assert.assertTrue(isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi cập nhật giới tính");
        System.out.println("  → PASS: Cập nhật giới tính thành công");
    }


    // ════════════════════════════════════════════════════
    // TC08 – Họ tên để trống → lỗi
    // ════════════════════════════════════════════════════
    @Test(priority = 8, description = "FE06-TC08: Họ tên để trống")
    public void TC08_hoTenDetrong() {
        System.out.println("\n[FE06-TC09] Họ tên để trống...");

        driver.findElement(FIELD_FULLNAME).clear();
        submitProfileForm();
        sleep(1000);

        // Không được có thông báo thành công
        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù họ tên trống!");

        // Phải có thông báo lỗi (HTML5 required hoặc toast lỗi)
        Assert.assertTrue(
                pageContainsError("không được để trống", "required", "bắt buộc",
                        "vui lòng nhập", "error", "invalid"),
                "FAIL: Không hiển thị lỗi khi họ tên để trống"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi khi họ tên trống");
    }

    // ════════════════════════════════════════════════════
    // TC09 – Email sai định dạng (thiếu @)
    // ════════════════════════════════════════════════════
    @Test(priority = 9, description = "FE06-TC09: Email sai định dạng")
    public void TC09_emailSaiDinhDang() {
        System.out.println("\n[FE06-TC10] Email sai định dạng (thiếu @)...");

        clearAndType(FIELD_EMAIL, "usergmail.com");
        submitProfileForm();
        sleep(1000);

        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù email sai định dạng!");

        Assert.assertTrue(
                pageContainsError("email không hợp lệ", "định dạng", "invalid email",
                        "không đúng", "error", "invalid"),
                "FAIL: Không hiển thị lỗi khi email sai định dạng"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi khi email sai định dạng");
    }

    // ════════════════════════════════════════════════════
    // TC10 – Email đã tồn tại trong hệ thống
    // ════════════════════════════════════════════════════
    @Test(priority = 10, description = "FE06-TC10: Email đã tồn tại")
    public void TC10_emailDaTonTai() {
        System.out.println("\n[FE06-TC11] Email đã tồn tại trong hệ thống...");

        // ⚠️ SỬA: nhập email của tài khoản KHÁC đã có trong hệ thống
        clearAndType(FIELD_EMAIL, "nhom@gmail.com");
        submitProfileForm();
        sleep(1500);

        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù email đã tồn tại!");

        Assert.assertTrue(
                pageContainsError("đã được sử dụng", "đã tồn tại", "already exists",
                        "already used", "email exists", "error"),
                "FAIL: Không hiển thị lỗi khi email đã tồn tại"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi khi email đã tồn tại");
    }

    // ════════════════════════════════════════════════════
    // TC12 – SĐT chứa chữ cái → lỗi
    // ════════════════════════════════════════════════════
    @Test(priority = 11, description = "FE06-TC11: SĐT chứa chữ cái")
    public void TC11_sdtSaiDinhDang() {
        System.out.println("\n[FE06-TC12] SĐT chứa chữ cái...");

        // pattern="[0-9]{10}" → browser sẽ chặn trước khi submit
        clearAndType(FIELD_PHONE, "09abc12345");
        submitProfileForm();
        sleep(1000);

        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù SĐT chứa chữ cái!");

        // HTML5 validation: title="Số điện thoại phải bao gồm đúng 10 chữ số"
        Assert.assertTrue(
                pageContainsError("10 chữ số", "không hợp lệ", "invalid",
                        "error", "phone", "pattern"),
                "FAIL: Không hiển thị lỗi khi SĐT chứa chữ cái"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi khi SĐT chứa chữ cái");
    }

    // ════════════════════════════════════════════════════
    // TC13 – SĐT không đủ 10 chữ số
    // ════════════════════════════════════════════════════
    @Test(priority = 12, description = "FE06-TC12: SĐT không đủ 10 số")
    public void TC12_sdtKhongDuSo() {
        System.out.println("\n[FE06-TC13] SĐT không đủ 10 chữ số...");

        // minlength="10" → browser chặn khi submit
        clearAndType(FIELD_PHONE, "0912345");   // chỉ 7 số
        submitProfileForm();
        sleep(1000);

        Assert.assertFalse(isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù SĐT chỉ 7 số!");

        // HTML5: title="Số điện thoại phải bao gồm đúng 10 chữ số"
        Assert.assertTrue(
                pageContainsError("10 chữ số", "không hợp lệ", "invalid",
                        "minlength", "error"),
                "FAIL: Không hiển thị lỗi khi SĐT không đủ 10 số"
        );
        System.out.println("  → PASS: Hệ thống báo lỗi khi SĐT không đủ 10 số");
    }


    private void submitProfileForm() {
        try {
            WebElement btn = driver.findElement(BTN_SAVE);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
            // Fallback: submit form trực tiếp qua JS
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelector('form.shopee-form-section').submit();");
        }
        sleep(1500);
    }
}

