package org.example.pages;
import org.example.tests.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;

public class SearchResultPage {

    // =======================================================
    // PHẦN 1: LOCATORS
    // =======================================================

    private final By _listProductNames = By.xpath("//*[contains(@class, 'product-card-name') or contains(@class, 'title')]");
    private final By _lblSearchMessage = By.xpath("//*[contains(text(), 'Tìm thấy') or contains(text(), 'Không tìm thấy') or contains(text(), 'kết quả')]");

    private final By _listProductCards = By.xpath("//div[@class='product-card']");

    // =======================================================
    // PHẦN 2: METHODS
    // =======================================================

    public boolean areProductsFoundContain(String keyword) {
        List<WebElement> products = Constant.WEBDRIVER.get().findElements(_listProductNames);

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
        return Constant.WEBDRIVER.get().findElements(_listProductCards).size();
    }

    public String getSearchMessage() {
        try {
            return Constant.WEBDRIVER.get().findElement(_lblSearchMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isProductListUIValid() {
        List<WebElement> cards = Constant.WEBDRIVER.get().findElements(_listProductCards);
        if (cards.isEmpty()) return true;

        boolean hasValidCard = false;

        for (WebElement card : cards) {
            try {
                if (!card.isDisplayed() || card.getText().trim().isEmpty()) {
                    continue;
                }

                hasValidCard = true;

                boolean hasImg = !card.findElements(By.xpath(".//img")).isEmpty();
                boolean hasName = !card.findElements(By.xpath(".//*[contains(@class, 'name') or contains(@class, 'title')]")).isEmpty();
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