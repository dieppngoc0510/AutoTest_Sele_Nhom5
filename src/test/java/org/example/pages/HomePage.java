package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class HomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // =======================================================
    // LOCATORS
    // =======================================================
    private final By _txtSearch = By.name("q");

    private final By _listProductCards = By.xpath("//div[contains(@class, 'product-card') or contains(@class, 'product-item')]");
    private final By _listProductImages = By.xpath("//div[contains(@class, 'product-card-image') or contains(@class, 'img')]//img");
    private final By _listProductNames = By.xpath("//p[contains(@class, 'product-card-name') or contains(@class, 'title')]");

    private final By _listProductColorDots = By.xpath(".//div[contains(@class, 'product-colors-preview') or contains(@class, 'color')]");
    private final By _menuCategoryAo = By.xpath("//a[contains(@href, '/products/ao') or contains(text(), 'Áo') or contains(text(), 'ÁO')]");

    // Locators cho user menu / logout
    private final By _userMenuToggle = By.cssSelector(".user-menu-toggle, .user-avatar, .navbar-user");
    private final By _logoutBtn = By.xpath("//a[contains(text(),'Đăng xuất') or contains(text(),'Logout')] | //button[contains(text(),'Đăng xuất') or contains(text(),'Logout')]");
    private final By _userMenuDropdown = By.cssSelector(".user-dropdown, .user-menu-dropdown, .dropdown-menu");

    // =======================================================
    // CONSTRUCTOR
    // =======================================================
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Constructor không tham số – dùng Constant.WEBDRIVER (cho ViewProductTest / SearchTest) */
    public HomePage() {
        this.driver = org.example.tests.Constant.WEBDRIVER.get();
        this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
    }

    // =======================================================
    // METHODS
    // =======================================================
    public void open(String baseUrl) {
        driver.get(baseUrl);
    }

    public void open() {
        driver.get("http://127.0.0.1:8000/");
    }

    public SearchResultPage searchProduct(String keyword) {
        WebElement txtSearch = driver.findElement(_txtSearch);
        txtSearch.clear();
        txtSearch.sendKeys(keyword);
        txtSearch.sendKeys(Keys.ENTER);
        return new SearchResultPage(driver);
    }

    // TC01: Kiểm tra cấu trúc thẻ sản phẩm (Bao gồm check linh hoạt giá mới/giá cũ)
    public boolean isProductListDisplayed() {
        List<WebElement> cards = driver.findElements(_listProductCards);
        if (cards.isEmpty()) {
            System.out.println("Không tìm thấy bất kỳ thẻ sản phẩm nào trên trang!");
            return false;
        }

        int validProductCount = 0;
        for (WebElement card : cards) {
            try {
                boolean hasImg = !card.findElements(By.xpath(".//img")).isEmpty();
                boolean hasName = !card.findElements(By.xpath(".//*[contains(@class, 'name') or contains(@class, 'title')]")).isEmpty();
                boolean hasPrice = !card.findElements(By.xpath(".//*[contains(@class, 'price')]")).isEmpty();
                boolean hasColors = !card.findElements(_listProductColorDots).isEmpty();

                if (hasImg && hasName && hasPrice && hasColors) {
                    validProductCount++;
                }
            } catch (Exception e) {
                // bỏ qua card lỗi
            }
        }
        System.out.println("Tổng số sản phẩm hiển thị chuẩn: " + validProductCount + "/" + cards.size());
        return validProductCount > 0;
    }

    // Click sản phẩm đầu tiên (Cho TC02)
    public ProductDetailPage clickFirstProduct() {
        return clickProductByIndex(0);
    }

    // Hàm mở trang chi tiết theo vị trí (Cho TC04)
    public ProductDetailPage clickProductByIndex(int index) {
        try {
            List<WebElement> images = driver.findElements(_listProductImages);
            if (images.size() > index) {
                // Click bằng JS tránh bị lỗi Click intercepted
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", images.get(index));
                System.out.println("Đã click vào sản phẩm ở vị trí index: " + index);
            }
        } catch (Exception e) {
            System.out.println("Lỗi: Không thể click vào sản phẩm! " + e.getMessage());
        }
        return new ProductDetailPage(driver);
    }

    public void clickCategoryAo() {
        try {
            driver.findElement(_menuCategoryAo).click();
            System.out.println("Đã click vào Menu danh mục Áo.");
        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy Menu Áo trên Navbar!");
        }
    }

    // TC03: Kiểm tra lọc danh mục
    public boolean areAllProductsBelongToCategory(String expectedKeyword) {
        List<WebElement> names = driver.findElements(_listProductNames);
        if (names.isEmpty()) return false;

        String lowerKeyword = expectedKeyword.toLowerCase();
        for (WebElement nameElement : names) {
            String productName = nameElement.getText().toLowerCase();
            if (!productName.contains(lowerKeyword) && !productName.isEmpty()) {
                System.out.println("Phát hiện SP sai danh mục: " + productName);
                return false;
            }
        }
        return true;
    }

    // =======================================================
    // USER MENU / LOGOUT
    // =======================================================

    /** Mở dropdown menu người dùng */
    public void openUserMenu() {
        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(_userMenuToggle));
        toggle.click();
    }

    /** Nhấn nút Đăng xuất */
    public void clickLogout() {
        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(_logoutBtn));
        logout.click();
    }

    /** Kiểm tra dropdown menu người dùng có hiển thị không */
    public boolean isUserMenuDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(_userMenuDropdown)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // =======================================================
    // BỔ SUNG CHO TC06 - NÚT XEM THÊM
    // =======================================================
    private final By _btnLoadMore = By.xpath("//*[contains(text(), 'Xem thêm') or contains(text(), 'XEM THÊM') or contains(@class, 'load-more')]");

    public int getProductCount() {
        return driver.findElements(_listProductCards).size();
    }

    public void clickLoadMoreButton() {
        try {
            List<WebElement> btns = driver.findElements(_btnLoadMore);
            if (!btns.isEmpty() && btns.get(0).isDisplayed()) {
                WebElement btn = btns.get(0);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView(true);", btn);
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                js.executeScript("arguments[0].click();", btn);
                System.out.println("Đã click nút 'Xem thêm'.");
            } else {
                System.out.println("Không tìm thấy nút 'Xem thêm' (Có thể số lượng SP ít, chưa vượt quá giới hạn trang).");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi click nút Xem thêm: " + e.getMessage());
        }
    }

}