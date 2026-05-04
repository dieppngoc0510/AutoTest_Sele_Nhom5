package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BasePage {

    private static final String BASE_URL = "http://127.0.0.1:8000";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @FindBy(css = "button.btn-cart-icon")
    protected WebElement cartIcon;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void openCartSidebar() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(cartIcon));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        wait.until(ExpectedConditions.attributeContains(By.id("cart-sidebar"), "class", "open"));
    }

    public void openUserMenu() {
        // Chờ toggle hiển thị và sẵn sàng
        WebElement toggle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userMenuToggle")));
        
        // Click bằng Javascript để tránh bị che khuất bởi toast-container (ElementClickInterceptedException)
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", toggle);
        
        // Chờ dropdown menu thực sự hiển thị
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userMenuDropdown")));
    }

    public void clickLogout() {
        // Chờ nút logout hiển thị trong dropdown
        WebElement logout = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#userMenuDropdown .logout-link")));
        
        // Click bằng Javascript để đảm bảo thao tác thành công kể cả khi có animation
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logout);
    }

    public boolean isUserMenuDisplayed() {
        try {
            return driver.findElement(By.id("userMenuDropdown")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Login helpers ─────────────────────────────────────────────────────────

    /**
     * Đăng nhập bằng tài khoản TEST_USERNAME / TEST_PASSWORD (dùng trong
     * ChangePasswordTest, ProfileTest).
     */
    public void login() {
        login(org.example.tests.Constant.TEST_USERNAME,
              org.example.tests.Constant.TEST_PASSWORD);
    }

    /**
     * Đăng nhập với username/password tuỳ ý.
     */
    public void login(String username, String password) {
        driver.get(BASE_URL + "/login/");
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(20));
        
        try {
            WebElement userEl = w.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            userEl.clear();
            userEl.sendKeys(username);
            
            WebElement passEl = driver.findElement(By.name("password"));
            passEl.clear();
            passEl.sendKeys(password);
            
            WebElement btn = driver.findElement(By.cssSelector("button.btn-auth-submit"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            
            // Đợi URL thay đổi (không còn ở trang login)
            w.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login/")));
        } catch (TimeoutException e) {
            // NẾU LỖI: Kiểm tra xem có thông báo lỗi hiển thị trên màn hình không
            String errorMsg = "Không xác định";
            try {
                // Thử tìm các class thông báo lỗi phổ biến của Django/Bootstrap
                errorMsg = driver.findElement(By.cssSelector(".alert-danger, .error-message, .invalid-feedback")).getText();
            } catch (Exception ignored) {
                errorMsg = "Không tìm thấy thông báo lỗi cụ thể trên UI, trang web có thể bị treo hoặc nút bấm không hoạt động.";
            }
            throw new RuntimeException(">>> ĐĂNG NHẬP THẤT BẠI cho user [" + username + "]. Lý do: " + errorMsg);
        }
    }

    public void loginAsAdmin() {
        login(org.example.tests.Constant.TEST_USERNAME,
              org.example.tests.Constant.TEST_PASSWORD);
    }

    public void loginAsUser() {
        login(org.example.tests.Constant.VALID_USERNAME,
              org.example.tests.Constant.VALID_PASSWORD);
    }

    public void loginAsDefaultUser() {
        loginAsUser();
    }

    // ── Page navigation helpers ───────────────────────────────────────────────

    public void goToProfile() {
        driver.get(org.example.tests.Constant.PROFILE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("form.shopee-form-section")));
    }

    public void goToChangePassword() {
        driver.get(org.example.tests.Constant.CHANGE_PW_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("current_password")));
    }

    /**
     * Mở trang chủ.
     */
    public void openHomePage() {
        driver.get(BASE_URL + "/");
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    /**
     * Tìm sản phẩm theo tên trên trang chủ rồi mở trang chi tiết.
     */
    public void goToProduct(String productName) {
        driver.get(BASE_URL + "/");
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));

        // Tìm thẻ sản phẩm có tên khớp
        List<WebElement> cards = driver.findElements(
                By.xpath("//div[contains(@class,'product-card') or contains(@class,'product-item')]"));
        for (WebElement card : cards) {
            try {
                String name = card.findElement(
                        By.xpath(".//*[contains(@class,'name') or contains(@class,'title')]"))
                        .getText().trim();
                if (name.equalsIgnoreCase(productName)) {
                    WebElement img = card.findElement(By.tagName("img"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", img);
                    wait.until(d -> d.getCurrentUrl().contains("/product/"));
                    return;
                }
            } catch (Exception ignored) {}
        }
        // Fallback: tìm kiếm qua search bar
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
        searchInput.clear();
        searchInput.sendKeys(productName);
        searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
        List<WebElement> results = driver.findElements(
                By.xpath("//div[contains(@class,'product-card')]//img"));
        if (!results.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", results.get(0));
            wait.until(d -> d.getCurrentUrl().contains("/product/"));
        }
    }

    // ── Cart helpers ──────────────────────────────────────────────────────────

    /**
     * Xóa toàn bộ giỏ hàng qua API.
     */
    public void clearCart() {
        try {
            String csrfToken = getCsrfTokenFromCookie();
            if (csrfToken == null) return;

            // Lấy danh sách item trong giỏ
            HttpClient client = buildHttpClient();
            HttpResponse<String> dataResp = client.send(
                    HttpRequest.newBuilder(URI.create(BASE_URL + "/cart/data/"))
                            .GET()
                            .timeout(Duration.ofSeconds(10))
                            .header("Accept", "application/json")
                            .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            String body = dataResp.body();
            // Parse items đơn giản bằng regex để tránh dependency thêm
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("\"product_id\"\\s*:\\s*(\\d+).*?\"color\"\\s*:\\s*\"([^\"]+)\".*?\"size\"\\s*:\\s*\"([^\"]+)\"")
                    .matcher(body);
            while (m.find()) {
                String json = String.format(
                        "{\"product_id\":%s,\"color\":\"%s\",\"size\":\"%s\",\"action\":\"delete\"}",
                        m.group(1), m.group(2), m.group(3));
                postCartUpdateRaw(json, csrfToken, client);
            }
        } catch (Exception e) {
            System.out.println("[clearCart] " + e.getMessage());
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng trực tiếp qua API (không qua UI).
     */
    public void addProductToCartDirect(int productId, String color, String size) {
        try {
            String csrfToken = getCsrfTokenFromCookie();
            if (csrfToken == null) return;
            HttpClient client = buildHttpClient();
            StringJoiner form = new StringJoiner("&");
            form.add("product_id=" + productId);
            form.add("color=" + URLEncoder.encode(color, StandardCharsets.UTF_8));
            form.add("size=" + URLEncoder.encode(size, StandardCharsets.UTF_8));
            client.send(
                    HttpRequest.newBuilder(URI.create(BASE_URL + "/cart/add/"))
                            .timeout(Duration.ofSeconds(10))
                            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .header("X-CSRFToken", csrfToken)
                            .header("Origin", BASE_URL)
                            .header("Referer", BASE_URL + "/login/")
                            .POST(HttpRequest.BodyPublishers.ofString(form.toString(), StandardCharsets.UTF_8))
                            .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("[addProductToCartDirect] " + e.getMessage());
        }
    }

    /**
     * Gửi POST cập nhật giỏ hàng với JSON body, trả về response body.
     */
    public String postCartUpdate(String json) {
        try {
            String csrfToken = getCsrfTokenFromCookie();
            if (csrfToken == null) return "";
            HttpClient client = buildHttpClient();
            return postCartUpdateRaw(json, csrfToken, client);
        } catch (Exception e) {
            System.out.println("[postCartUpdate] " + e.getMessage());
            return "";
        }
    }

    private String postCartUpdateRaw(String json, String csrfToken, HttpClient client)
            throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder(URI.create(BASE_URL + "/cart/update/"))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .header("Accept", "application/json")
                        .header("X-CSRFToken", csrfToken)
                        .header("Origin", BASE_URL)
                        .header("Referer", BASE_URL + "/login/")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return resp.body();
    }

    /**
     * Áp dụng mã coupon qua API, trả về response body.
     */
    public String applyCoupon(String code) {
        try {
            String csrfToken = getCsrfTokenFromCookie();
            if (csrfToken == null) return "";
            HttpClient client = buildHttpClient();
            String json = "{\"code\":\"" + code + "\"}";
            HttpResponse<String> resp = client.send(
                    HttpRequest.newBuilder(URI.create(BASE_URL + "/apply-coupon/"))
                            .timeout(Duration.ofSeconds(10))
                            .header("Content-Type", "application/json; charset=UTF-8")
                            .header("Accept", "application/json")
                            .header("X-CSRFToken", csrfToken)
                            .header("Origin", BASE_URL)
                            .header("Referer", BASE_URL + "/")
                            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                            .build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return resp.body();
        } catch (Exception e) {
            System.out.println("[applyCoupon] " + e.getMessage());
            return "";
        }
    }

    /**
     * Lấy danh sách WebElement các item trong cart sidebar.
     */
    public List<WebElement> getCartItems() {
        return driver.findElements(By.cssSelector("#cart-items .cart-item"));
    }

    // ── Toast notification helpers ────────────────────────────────────────────

    public String getToastMessage() {
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    org.example.tests.Constant.TOAST));
            return toast.getText().toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isSuccess() {
        String msg = getToastMessage();
        return msg.contains("thành công") || msg.contains("success")
                || msg.contains("updated") || msg.contains("cập nhật");
    }

    // ── API call helpers (dùng JavascriptExecutor / XMLHttpRequest) ───────────

    /**
     * Gọi API qua XMLHttpRequest trong browser (giữ nguyên session cookie).
     * Trả về chuỗi "STATUS|BODY".
     */
    public String callAPI(String method, String path, String body) {
        String url = BASE_URL + path;
        String script;
        if (body == null) {
            script = String.format(
                    "var xhr = new XMLHttpRequest();" +
                    "xhr.open('%s', '%s', false);" +
                    "xhr.setRequestHeader('Accept','application/json');" +
                    "xhr.send(null);" +
                    "return xhr.status + '|' + xhr.responseText;",
                    method, url);
        } else {
            String escaped = body.replace("\\", "\\\\").replace("'", "\\'");
            script = String.format(
                    "var xhr = new XMLHttpRequest();" +
                    "xhr.open('%s', '%s', false);" +
                    "xhr.setRequestHeader('Content-Type','application/json');" +
                    "xhr.setRequestHeader('Accept','application/json');" +
                    "xhr.setRequestHeader('X-CSRFToken', (document.cookie.match(/csrftoken=([^;]+)/)||[])[1]||'');" +
                    "xhr.send('%s');" +
                    "return xhr.status + '|' + xhr.responseText;",
                    method, url, escaped);
        }
        try {
            Object result = ((JavascriptExecutor) driver).executeScript(script);
            return result != null ? result.toString() : "0|";
        } catch (Exception e) {
            return "0|" + e.getMessage();
        }
    }

    /**
     * Gọi API đổi mật khẩu qua XMLHttpRequest.
     * Trả về chuỗi "STATUS|BODY".
     */
    public String callChangePwAPI(String method, String body) {
        return callAPI(method, org.example.tests.Constant.API_CHANGE_PW, body);
    }

    /**
     * Lấy HTTP status từ chuỗi "STATUS|BODY".
     */
    public String statusOf(String result) {
        if (result == null || !result.contains("|")) return "0";
        return result.split("\\|", 2)[0].trim();
    }

    /**
     * Lấy response body từ chuỗi "STATUS|BODY".
     */
    public String bodyOf(String result) {
        if (result == null || !result.contains("|")) return "";
        return result.split("\\|", 2)[1];
    }

    // ── Form / validation helpers ─────────────────────────────────────────────

    /**
     * Xóa nội dung field rồi gõ text mới.
     */
    public void clearAndType(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        if (text != null && !text.isEmpty()) {
            el.sendKeys(text);
        }
    }

    /**
     * Kiểm tra trang hiện tại có chứa bất kỳ từ khóa lỗi nào không
     * (tìm trong page source + validation message của HTML5).
     */
    public boolean pageContainsError(String... keywords) {
        String source = driver.getPageSource().toLowerCase();
        for (String kw : keywords) {
            if (source.contains(kw.toLowerCase())) return true;
        }
        // Kiểm tra HTML5 validation message trên các input
        try {
            List<WebElement> inputs = driver.findElements(By.cssSelector("input, textarea, select"));
            for (WebElement input : inputs) {
                String msg = (String) ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].validationMessage;", input);
                if (msg != null && !msg.isEmpty()) {
                    String msgLower = msg.toLowerCase();
                    for (String kw : keywords) {
                        if (msgLower.contains(kw.toLowerCase())) return true;
                    }
                    return true; // Có validation message là có lỗi
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Lấy HTML5 validation message của một field.
     */
    public String validationMsg(By locator) {
        try {
            WebElement el = driver.findElement(locator);
            String msg = (String) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].validationMessage;", el);
            return msg != null ? msg : "";
        } catch (Exception e) {
            return "";
        }
    }

    // ── Internal HTTP helpers (dùng session cookie từ WebDriver) ─────────────

    private String getCsrfTokenFromCookie() {
        return driver.manage().getCookies().stream()
                .filter(c -> "csrftoken".equals(c.getName()))
                .map(org.openqa.selenium.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private HttpClient buildHttpClient() {
        // Truyền session cookie từ WebDriver sang HttpClient
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        driver.manage().getCookies().forEach(seleniumCookie -> {
            java.net.HttpCookie httpCookie = new java.net.HttpCookie(
                    seleniumCookie.getName(), seleniumCookie.getValue());
            httpCookie.setDomain(seleniumCookie.getDomain() != null
                    ? seleniumCookie.getDomain() : "127.0.0.1");
            httpCookie.setPath(seleniumCookie.getPath() != null
                    ? seleniumCookie.getPath() : "/");
            httpCookie.setHttpOnly(seleniumCookie.isHttpOnly());
            httpCookie.setSecure(seleniumCookie.isSecure());
            try {
                cm.getCookieStore().add(URI.create(BASE_URL), httpCookie);
            } catch (Exception ignored) {}
        });
        return HttpClient.newBuilder()
                .cookieHandler(cm)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }
}
