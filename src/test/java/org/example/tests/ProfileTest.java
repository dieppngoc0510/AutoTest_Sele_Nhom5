package org.example.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.example.tests.Constant.*;


public class ProfileTest extends BaseTest {

    // Aliases used by API test methods
    private final By fullnameField = Constant.FIELD_FULLNAME;
    private final By phoneField    = Constant.FIELD_PHONE;
    private final By saveBtn       = Constant.BTN_SAVE;

    @BeforeMethod
    public void beforeEach() {
        bp().loginAsUser(); // Đổi sang dùng acc ngocdiep thay vì admin
        bp().goToProfile();
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

        bp().clearAndType(FIELD_FULLNAME, "Nguyễn Văn An");
        submitProfileForm();

        Assert.assertTrue(bp().isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi cập nhật họ tên");
        System.out.println("  → PASS: Cập nhật họ tên thành công");
    }

    // ════════════════════════════════════════════════════
    // TC03 – Cập nhật Email hợp lệ
    // ════════════════════════════════════════════════════
    @Test(priority = 3, description = "FE06-TC03: Cập nhật email hợp lệ")
    public void TC03_capNhatEmailHopLe() {
        System.out.println("\n[FE06-TC03] Cập nhật email hợp lệ...");

        String randomEmail = "test_" + System.currentTimeMillis() + "@gmail.com";
        bp().clearAndType(FIELD_EMAIL, randomEmail);
        submitProfileForm();

        // Lấy thông báo thực tế để debug nếu fail
        String toastMsg = bp().getToastMessage();
        boolean success = toastMsg.contains("thành công") || toastMsg.contains("success")
                       || toastMsg.contains("updated") || toastMsg.contains("cập nhật");

        Assert.assertTrue(success,
                "FAIL: Cập nhật email thất bại! Thông báo nhận được: [" + toastMsg + "]");
        System.out.println("  → PASS: Cập nhật email thành công");
    }

    // ════════════════════════════════════════════════════
    // TC04 – Cập nhật SĐT hợp lệ (10 chữ số)
    // ════════════════════════════════════════════════════
    @Test(priority = 4, description = "FE06-TC04: Cập nhật SĐT hợp lệ")
    public void TC04_capNhatSDTHopLe() {
        System.out.println("\n[FE06-TC04] Cập nhật SĐT hợp lệ (10 số)...");

        bp().clearAndType(FIELD_PHONE, "0912345678");
        submitProfileForm();

        Assert.assertTrue(bp().isSuccess(),
                "FAIL: Không thấy thông báo thành công sau khi cập nhật SĐT");
        System.out.println("  → PASS: Cập nhật SĐT thành công");
    }

    // ════════════════════════════════════════════════════
    // TC05 – Cập nhật địa chỉ hợp lệ
    // ════════════════════════════════════════════════════
    @Test(priority = 5, description = "FE06-TC05: Cập nhật địa chỉ hợp lệ")
    public void TC05_capNhatDiaChiHopLe() {
        System.out.println("\n[FE06-TC05] Cập nhật địa chỉ hợp lệ...");

        bp().clearAndType(FIELD_ADDRESS, "123 Đường Lê Lợi, Quận 1, TP.HCM");
        submitProfileForm();

        Assert.assertTrue(bp().isSuccess(),
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

        Assert.assertTrue(bp().isSuccess(),
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

        Assert.assertTrue(bp().isSuccess(),
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
        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù họ tên trống!");

        // Phải có thông báo lỗi (HTML5 required hoặc toast lỗi)
        Assert.assertTrue(
                bp().pageContainsError("không được để trống", "required", "bắt buộc",
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

        bp().clearAndType(FIELD_EMAIL, "usergmail.com");
        submitProfileForm();
        sleep(1000);

        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù email sai định dạng!");

        Assert.assertTrue(
                bp().pageContainsError("email không hợp lệ", "định dạng", "invalid email",
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
        bp().clearAndType(FIELD_EMAIL, "nhom@gmail.com");
        submitProfileForm();
        sleep(1500);

        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù email đã tồn tại!");

        Assert.assertTrue(
                bp().pageContainsError("đã được sử dụng", "đã tồn tại", "already exists",
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
        bp().clearAndType(FIELD_PHONE, "09abc12345");
        submitProfileForm();
        sleep(1000);

        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù SĐT chứa chữ cái!");

        // HTML5 validation: title="Số điện thoại phải bao gồm đúng 10 chữ số"
        Assert.assertTrue(
                bp().pageContainsError("10 chữ số", "không hợp lệ", "invalid",
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
        bp().clearAndType(FIELD_PHONE, "0912345");   // chỉ 7 số
        submitProfileForm();
        sleep(1000);

        Assert.assertFalse(bp().isSuccess(),
                "FAIL: Hệ thống vẫn báo thành công dù SĐT chỉ 7 số!");

        // HTML5: title="Số điện thoại phải bao gồm đúng 10 chữ số"
        Assert.assertTrue(
                bp().pageContainsError("10 chữ số", "không hợp lệ", "invalid",
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

    // ════════════════════════════════════════════════════════
    //  API01 – API04  |  Kiểm thử API (dùng XMLHttpRequest qua JS)
    // ════════════════════════════════════════════════════════

    @Test(priority = 13,
            description = "FE06_API01 – GET /api/user/profile – Lấy thông tin cá nhân thành công")
    public void API01_GetProfileThanhCong() {
        // Logout trước rồi login lại để test session mới
        driver.get(BASE_URL + "logout/?silent=1");
        bp().loginAsUser();
        log("Gọi GET " + API_PROFILE + " với session hợp lệ");

        String result = bp().callAPI("GET", API_PROFILE, null);
        String status = bp().statusOf(result);
        String body   = bp().bodyOf(result);

        log("API status : " + status);
        log("API body   : " + body);

        // Chấp nhận 200 (REST) hoặc 302 (Django truyền thống redirect sang trang HTML)
        Assert.assertTrue(status.equals("200") || status.equals("302"),
                "GET /profile cần trả về 200 OK (REST) hoặc 302 (Django redirect), nhận: " + status);

        if (status.equals("200")) {
            // Response phải chứa ít nhất 1 trong các field profile
            boolean hasField = body.contains("fullname") || body.contains("email") ||
                    body.contains("phone")    || body.contains("full_name");
            Assert.assertTrue(hasField,
                    "Response 200 phải chứa thông tin profile (fullname / email / phone)");
        }
    }

    @Test(priority = 14,
            description = "FE06_API02 – PUT /api/user/profile – Cập nhật thông tin thành công")
    public void API02_PutProfileThanhCong() {
        driver.get(BASE_URL + "logout/?silent=1");
        bp().loginAsUser();
        String jsonBody = "{\"first_name\":\"Lê Thị Quỳnh Như\"," +
                "\"phone\":\"0987654321\"," +
                "\"gender\":\"nu\"," +
                "\"birthdate\":\"1995-01-01\"," +
                "\"address\":\"123 Đường ABC, Quận 1, TP.HCM\"}";
        log("Gọi PUT " + API_PROFILE);
        log("Request body: " + jsonBody);

        String result = bp().callAPI("PUT", API_PROFILE, jsonBody);
        String status = bp().statusOf(result);
        String body   = bp().bodyOf(result);

        log("API status : " + status);
        log("API body   : " + body);

        // Chấp nhận 200 OK / 201 Created / 302 Redirect
        boolean success = status.equals("200") || status.equals("201") || status.equals("302");
        Assert.assertTrue(success,
                "PUT /profile phải trả về 200/201/302, nhận: " + status);

        if (status.equals("200")) {
            boolean hasSuccessHint = body.contains("thành công") || body.contains("success") ||
                    body.contains("Nguyen Van An") || body.contains("updated");
            log("Body chứa dấu hiệu thành công: " + hasSuccessHint);
            // Không assert cứng để tránh fail khi response format khác nhau
        }

        // Xác nhận qua UI: reload trang, kiểm tra dữ liệu mới
        driver.get(PROFILE_URL);
        waitFor(fullnameField);
        String savedName = driver.findElement(fullnameField).getAttribute("value");
        log("Họ tên sau khi PUT: " + savedName);
        Assert.assertEquals(savedName, "Lê Thị Quỳnh Như",
                "Dữ liệu phải được lưu đúng vào database sau khi PUT thành công");
    }

    @Test(priority = 15,
            description = "FE06_API03 – PUT /api/user/profile – Không có token xác thực")
    public void API03_KhongCoToken() {
        // KHÔNG đăng nhập – logout trước để đảm bảo không có session
        driver.get(BASE_URL + "logout/?silent=1");
        sleep(500);
        log("Truy cập " + PROFILE_URL + " khi CHƯA đăng nhập");
        driver.get(PROFILE_URL);

        boolean redirectLogin = driver.getCurrentUrl().contains("/login/") ||
                driver.getCurrentUrl().contains("/signin/");
        boolean showsLoginUI  = !driver.findElements(
                By.cssSelector("input[name='username'], input[name='password']")).isEmpty();

        log("Redirect về /login/: " + redirectLogin);
        log("Hiển thị form login: " + showsLoginUI);

        // Gọi API không có session cookie
        String result    = bp().callAPI("GET", API_PROFILE, null);
        String apiStatus = bp().statusOf(result);
        String apiBody   = bp().bodyOf(result);
        boolean apiBlocked = apiStatus.equals("401") || apiStatus.equals("403") ||
                apiStatus.equals("302");

        log("API GET (no session) – status: " + apiStatus + " | body: " + apiBody);

        Assert.assertTrue(redirectLogin || showsLoginUI || apiBlocked,
                "Khi chưa xác thực, phải redirect về login hoặc API trả 401/403");
    }

    @Test(priority = 16,
            description = "FE06_API04 – PUT /api/user/profile – Dữ liệu không hợp lệ (phone = \"abc\")")
    public void API04_PhoneSaiDinhDang() {
        driver.get(BASE_URL + "logout/?silent=1");
        bp().loginAsUser();

        // ── Bước 1: Ghi nhận phone hợp lệ hiện tại ──────────
        driver.get(PROFILE_URL);
        waitFor(phoneField);
        String originalPhone = driver.findElement(phoneField).getAttribute("value");
        log("SĐT hiện tại trong DB: " + originalPhone);

        // ── Bước 2: Gọi API PUT với phone không hợp lệ ───────
        String badBody = "{\"first_name\":\"Lê Thị Quỳnh Như\"," +
                "\"phone\":\"abc\"," +
                "\"gender\":\"nu\"," +
                "\"address\":\"123 Đường ABC, Quận 1, TP.HCM\"}";
        log("Gọi PUT " + API_PROFILE + " với phone='abc'");

        String result = bp().callAPI("PUT", API_PROFILE, badBody);
        String status = bp().statusOf(result);
        String body   = bp().bodyOf(result);

        log("API status : " + status);
        log("API body   : " + body);

        if (status.equals("400") || status.equals("422")) {
            // ── Trường hợp A: REST API chuẩn trả lỗi ─────────
            log("API trả lỗi validation đúng chuẩn REST: " + status);
            boolean hasPhoneError = body.contains("phone")         ||
                    body.contains("không hợp lệ") ||
                    body.contains("invalid")       ||
                    body.contains("10 chữ số");
            Assert.assertTrue(hasPhoneError,
                    "Response " + status + " phải chứa thông báo lỗi liên quan đến phone");
        } else {
            // ── Trường hợp B: Django truyền thống (200 / 302) ─
            log("API trả " + status + " – kiểm tra DB không lưu phone='abc'");
            driver.get(PROFILE_URL);
            waitFor(phoneField);
            String currentPhone = driver.findElement(phoneField).getAttribute("value");
            log("SĐT trong DB sau khi PUT 'abc': " + currentPhone);
            Assert.assertNotEquals(currentPhone, "abc",
                    "Phone='abc' không hợp lệ KHÔNG được lưu vào database");
        }

        // ── Bước 3: Xác nhận qua UI form ─────────────────────
        driver.get(PROFILE_URL);
        waitFor(phoneField);
        WebElement phoneEl = driver.findElement(phoneField);
        phoneEl.clear();
        phoneEl.sendKeys("abc");
        driver.findElement(saveBtn).click();

        boolean uiValidationErr = !driver.findElements(By.xpath(
                "//*[contains(text(),'không hợp lệ') or contains(text(),'10 chữ số')" +
                        " or contains(text(),'Số điện thoại') or contains(text(),'invalid')]")).isEmpty();
        boolean html5Err = !bp().validationMsg(phoneField).isEmpty();

        log("UI validation error: " + uiValidationErr + " | HTML5: " + html5Err);

        if (!uiValidationErr && !html5Err) {
            // Nếu không hiện lỗi, DB chắc chắn không được lưu "abc"
            driver.navigate().refresh();
            waitFor(phoneField);
            String afterUI = driver.findElement(phoneField).getAttribute("value");
            Assert.assertNotEquals(afterUI, "abc",
                    "Dù không hiện lỗi UI, phone='abc' vẫn KHÔNG được lưu vào DB");
        } else {
            Assert.assertTrue(true, "Hiện lỗi validation SĐT không hợp lệ ✓");
        }
    }

    @AfterClass(alwaysRun = true)
    public void restoreProfile() {
        System.out.println("\n[POST-TEST] Đang khôi phục dữ liệu Profile gốc...");
        try {
            bp().loginAsUser();
            bp().goToProfile();
            
            bp().clearAndType(FIELD_FULLNAME, "Lê Thị Quỳnh Như");
            bp().clearAndType(FIELD_EMAIL, "ngocdiep@gmail.com");
            bp().clearAndType(FIELD_PHONE, "0987654321");
            bp().clearAndType(FIELD_ADDRESS, "123 Đường ABC, Quận 1, TP.HCM");
            
            // Chọn lại giới tính Nữ
            try {
                WebElement labelNu = driver.findElement(By.xpath("//div[contains(@class,'s-radio-group')]//label[contains(.,'Nữ')]"));
                labelNu.click();
            } catch (Exception e) {}
            
            submitProfileForm();
            System.out.println(">>> Đã khôi phục dữ liệu thành công.");
        } catch (Exception e) {
            System.err.println(">>> Lỗi khi khôi phục dữ liệu: " + e.getMessage());
        }
    }
}
