package org.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.example.tests.Constant.TOAST;

public class BaseTest {
    protected static final String BASE_URL = "http://127.0.0.1:8000/";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void beforeMethod() {
        WebDriverManager.chromedriver().setup();
        // Dòng này khởi tạo và bật trình duyệt Chrome mới trước mỗi test case
        Constant.WEBDRIVER.set(new ChromeDriver());
        // Phóng to cửa sổ trình duyệt
        Constant.WEBDRIVER.get().manage().window().maximize();
        driver = Constant.WEBDRIVER.get();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    protected void getUrl(String url) {
        driver.get(url);
    }

    protected void loginAsDefaultUser() {
        getUrl(BASE_URL + "login/");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("ngocdiep", "123456");
        wait.until(d -> !d.getCurrentUrl().contains("login/"));
    }

    protected void openHomePage() {
        getUrl(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-card")));
    }

    protected void goToProduct(String productName) {
        openHomePage();
        String xpath = "//div[contains(@class,'product-card')][.//p[contains(@class,'product-card-name') and normalize-space()=\""
                + productName + "\"]]";
        WebElement productCard = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        String onClick = productCard.getAttribute("onclick");
        Assert.assertNotNull(onClick, "Thiếu thuộc tính điều hướng trên thẻ sản phẩm: " + productName);
        String productPath = onClick.replace("location.href='", "").replace("'", "").replace(";", "").trim();
        driver.get(productPath.startsWith("http") ? productPath : BASE_URL.substring(0, BASE_URL.length() - 1) + productPath);
        wait.until(ExpectedConditions.urlContains("/product/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-detail-name")));
    }

    protected void clearCart() {
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(
                """
                const done = arguments[arguments.length - 1];
                const getCookie = (name) => {
                    const parts = document.cookie.split(';').map(v => v.trim());
                    for (const part of parts) {
                        if (part.startsWith(name + '=')) {
                            return decodeURIComponent(part.substring(name.length + 1));
                        }
                    }
                    return '';
                };

                const headers = {
                    'Content-Type': 'application/json',
                    'X-CSRFToken': getCookie('csrftoken')
                };

                fetch('/cart/data/')
                    .then(res => res.json())
                    .then(async (data) => {
                        for (const item of data.items) {
                            for (let i = 0; i < item.qty; i++) {
                                await fetch('/cart/update/', {
                                    method: 'POST',
                                    headers,
                                    body: JSON.stringify({
                                        product_id: item.product_id,
                                        color: item.color,
                                        size: item.size,
                                        action: 'delete'
                                    })
                                });
                            }
                        }

                        const finalData = await fetch('/cart/data/').then(res => res.json());
                        done(finalData.count);
                    })
                    .catch(error => done('ERROR:' + error.message));
                """
        );

        Assert.assertFalse(String.valueOf(result).startsWith("ERROR:"),
                "Không thể làm trống giỏ hàng trước khi chạy test: " + result);
        Assert.assertEquals(String.valueOf(result), "0", "Giỏ hàng phải trống trước mỗi test case");
    }

    protected String getCartDataJson() {
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(
                """
                const done = arguments[arguments.length - 1];
                fetch('/cart/data/')
                    .then(res => res.text())
                    .then(done)
                    .catch(error => done('ERROR:' + error.message));
                """
        );
        String json = String.valueOf(result);
        Assert.assertFalse(json.startsWith("ERROR:"), "Không thể đọc dữ liệu giỏ hàng: " + json);
        return json;
    }

    protected String postCartUpdate(String jsonBody) {
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(
                """
                const payload = arguments[0];
                const done = arguments[arguments.length - 1];
                const getCookie = (name) => {
                    const parts = document.cookie.split(';').map(v => v.trim());
                    for (const part of parts) {
                        if (part.startsWith(name + '=')) {
                            return decodeURIComponent(part.substring(name.length + 1));
                        }
                    }
                    return '';
                };

                fetch('/cart/update/', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRFToken': getCookie('csrftoken')
                    },
                    body: payload
                })
                    .then(res => res.text())
                    .then(done)
                    .catch(error => done('ERROR:' + error.message));
                """,
                jsonBody
        );
        String response = String.valueOf(result);
        Assert.assertFalse(response.startsWith("ERROR:"), "Không thể cập nhật giỏ hàng: " + response);
        return response;
    }

    protected String applyCoupon(String code) {
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(
                """
                const couponCode = arguments[0];
                const done = arguments[arguments.length - 1];
                const getCookie = (name) => {
                    const parts = document.cookie.split(';').map(v => v.trim());
                    for (const part of parts) {
                        if (part.startsWith(name + '=')) {
                            return decodeURIComponent(part.substring(name.length + 1));
                        }
                    }
                    return '';
                };

                fetch('/apply-coupon/', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRFToken': getCookie('csrftoken')
                    },
                    body: JSON.stringify({ code: couponCode })
                })
                    .then(res => res.text())
                    .then(done)
                    .catch(error => done('ERROR:' + error.message));
                """,
                code
        );
        String response = String.valueOf(result);
        Assert.assertFalse(response.startsWith("ERROR:"), "Không thể áp dụng mã ưu đãi: " + response);
        return response;
    }

    protected String addProductToCartDirect(int productId, String color, String size) {
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(
                """
                const productId = arguments[0];
                const color = arguments[1];
                const size = arguments[2];
                const done = arguments[arguments.length - 1];
                const getCookie = (name) => {
                    const parts = document.cookie.split(';').map(v => v.trim());
                    for (const part of parts) {
                        if (part.startsWith(name + '=')) {
                            return decodeURIComponent(part.substring(name.length + 1));
                        }
                    }
                    return '';
                };

                const formData = new FormData();
                formData.append('product_id', productId);
                formData.append('color', color);
                formData.append('size', size);

                fetch('/cart/add/', {
                    method: 'POST',
                    headers: {
                        'X-CSRFToken': getCookie('csrftoken')
                    },
                    body: formData
                })
                    .then(res => res.text())
                    .then(done)
                    .catch(error => done('ERROR:' + error.message));
                """,
                String.valueOf(productId),
                color,
                size
        );
        String response = String.valueOf(result);
        Assert.assertFalse(response.startsWith("ERROR:"), "Không thể thêm sản phẩm vào giỏ hàng: " + response);
        return response;
    }

    protected void openCartAndWaitForItems(int expectedCount) {
        driver.findElement(By.cssSelector("button.btn-cart-icon")).click();
        wait.until(ExpectedConditions.attributeContains(By.id("cart-sidebar"), "class", "open"));
        if (expectedCount == 0) {
            wait.until(d -> d.findElements(By.cssSelector("#cart-items .cart-item")).isEmpty());
        } else {
            wait.until(d -> d.findElements(By.cssSelector("#cart-items .cart-item")).size() == expectedCount);
        }
    }

    protected List<WebElement> getCartItems() {
        return driver.findElements(By.cssSelector("#cart-items .cart-item"));
    }

    protected boolean isAlertPresent() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    @AfterMethod
    public void tearDown() {
        // Driver is closed in TestListener after taking screenshot
    }





    protected void login() {
        loginAsDefaultUser();
    }
    /** Vào trang Thông tin cá nhân */
    protected void goToProfile() {
        driver.get(BASE_URL + "/profile/");
        sleep(1000);
    }

    /** Vào trang Đổi mật khẩu */
    protected void goToChangePassword() {
        driver.get(BASE_URL + "/change-password/");
        sleep(1000);
    }
    protected void login(String username, String password) {
        getUrl(BASE_URL + "login/");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        wait.until(d -> !d.getCurrentUrl().contains("login/"));
    }

    /**
     * Lấy nội dung toast notification.
     */
    protected String getToastMessage() {
        try {
            WebElement toast = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(TOAST));
            return toast.getText().toLowerCase();
        } catch (Exception e) {
            return driver.getPageSource().toLowerCase();
        }
    }

    /** Kiểm tra có thông báo thành công không */
    protected boolean isSuccess() {
        String msg = getToastMessage();
        return msg.contains("thành công") || msg.contains("success")
                || msg.contains("updated") || msg.contains("cập nhật");
    }

    /** Kiểm tra trang có chứa một trong các từ khóa lỗi không */
    protected boolean pageContainsError(String... keywords) {
        String src = driver.getPageSource().toLowerCase();
        for (String kw : keywords) {
            if (src.contains(kw.toLowerCase())) return true;
        }
        return false;
    }

    /** Xóa và nhập giá trị mới vào input */
    protected void clearAndType(By locator, String value) {
        WebElement el = driver.findElement(locator);
        el.clear();
        el.sendKeys(value);
    }

    protected void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }


}
