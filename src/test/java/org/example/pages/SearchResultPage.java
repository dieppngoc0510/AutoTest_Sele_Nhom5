package org.example.pages;
import org.example.tests.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class SearchResultPage {

    private final WebDriver driver;

    // =======================================================
    // CONSTRUCTORS
    // =======================================================

    /** Constructor nhận WebDriver – dùng khi tạo từ test có driver riêng */
    public SearchResultPage(WebDriver driver) {
        this.driver = driver;
    }

    /** Constructor không tham số – dùng Constant.WEBDRIVER (cho SearchTest) */
    public SearchResultPage() {
        this.driver = Constant.WEBDRIVER.get();
    }

    // =======================================================
    // LOCATORS
    // =======================================================

    private final By _listProductNames = By.xpath("//*[contains(@class, 'product-card-name') or contains(@class, 'title')]");
    private final By _lblSearchMessage = By.xpath("//*[contains(text(), 'Tìm thấy') or contains(text(), 'Không tìm thấy') or contains(text(), 'kết quả')]");
    private final By _listProductCards = By.xpath("//div[@class='product-card']");

    // =======================================================
    // METHODS
    // =======================================================

    public boolean areProductsFoundContain(String keyword) {
        List<WebElement> products = driver.findElements(_listProductNames);

        if (products.isEmpty()) {
            return false;
        }

        String lowerKeyword = keyword.toLowerCase();
        boolean hasValidProduct = false;

        for (WebElement product : products) {
            String productName = product.getText().trim().toLowerCase();

            if (productName.isEmpty()) {
                continue;
            }

            hasValidProduct = true;

            if (!productName.contains(lowerKeyword)) {
                System.out.println("Cảnh báo: Có sản phẩm không khớp chuẩn xác: [" + productName + "]");
                return false;
            }
        }
        return hasValidProduct;
    }

    public int getProductCount() {
        return driver.findElements(_listProductCards).size();
    }

    public String getSearchMessage() {
        try {
            return driver.findElement(_lblSearchMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isProductListUIValid() {
        List<WebElement> cards = driver.findElements(_listProductCards);
        if (cards.isEmpty()) return true;

        boolean hasValidCard = false;

        for (WebElement card : cards) {
            try {
                if (!card.isDisplayed() || card.getText().trim().isEmpty()) {
                    continue;
                }

                hasValidCard = true;

                boolean hasImg   = !card.findElements(By.xpath(".//img")).isEmpty();
                boolean hasName  = !card.findElements(By.xpath(".//*[contains(@class, 'name') or contains(@class, 'title')]")).isEmpty();
                boolean hasPrice = !card.findElements(By.xpath(".//*[contains(@class, 'price')]")).isEmpty();

                if (!hasImg || !hasName || !hasPrice) {
                    System.out.println("Lỗi UI (Thiếu Ảnh/Tên/Giá) ở thẻ: [" + card.getText().replace("\n", " ") + "]");
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return hasValidCard;
    }
}
