package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartSidebar extends BasePage {

    private final By cartSidebar = By.id("cart-sidebar");
    private final By cartItems = By.cssSelector("#cart-items .cart-item");
    private final By quantityValue = By.cssSelector(".qty-value");
    private final By quantityButtons = By.cssSelector(".qty-btn");
    private final By subtotalValue = By.id("cart-total-price");
    private final By cartBadge = By.id("cart-badge");
    private final By couponItems = By.cssSelector(".coupon-item-mini");
    private final By editButton = By.id("btn-edit-cart");
    private final By selectAllCheckbox = By.id("select-all-cart");

    public CartSidebar(WebDriver driver) {
        super(driver);
    }

    @Override
    public void openCartSidebar() {
        super.openCartSidebar();
        wait.until(ExpectedConditions.attributeContains(cartSidebar, "class", "open"));
    }

    private WebElement getCartItem(int index) {
        wait.until(d -> d.findElements(cartItems).size() > index);
        return driver.findElements(cartItems).get(index);
    }

    public void clickPlus(int index) {
        WebElement item = getCartItem(index);
        String before = item.findElement(quantityValue).getText();
        item.findElements(quantityButtons).get(1).click();
        wait.until(d -> !getCartItem(index).findElement(quantityValue).getText().equals(before));
    }

    public void clickMinus(int index) {
        WebElement item = getCartItem(index);
        String before = item.findElement(quantityValue).getText();
        item.findElements(quantityButtons).get(0).click();
        wait.until(d -> {
            List<WebElement> items = d.findElements(cartItems);
            if (items.size() <= index) {
                return true;
            }
            String after = items.get(index).findElement(quantityValue).getText();
            return !after.equals(before);
        });
    }

    public String getQuantity(int index) {
        wait.until(d -> !getCartItem(index).findElement(quantityValue).getText().trim().isEmpty());
        return getCartItem(index).findElement(quantityValue).getText().trim();
    }

    public void changeVariant(int itemIndex, String optionText) {
        WebElement item = getCartItem(itemIndex);
        item.findElement(By.cssSelector(".custom-select-trigger")).click();
        String before = item.findElement(By.cssSelector(".custom-select-trigger")).getText();
        item.findElement(By.xpath(".//div[contains(@class,'custom-option') and normalize-space()=\"" + optionText + "\"]")).click();
        wait.until(d -> !getCartItem(itemIndex).findElement(By.cssSelector(".custom-select-trigger")).getText().equals(before));
    }

    public String getVariantLabel(int index) {
        return getCartItem(index).findElement(By.cssSelector(".custom-select-trigger")).getText();
    }

    public void clickEdit() {
        wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
    }

    public void deleteFirstProduct() {
        deleteProduct(0);
    }

    public void deleteProduct(int index) {
        WebElement item = getCartItem(index);
        item.findElement(By.cssSelector("button[title='Xóa']")).click();
        wait.until(d -> d.findElements(cartItems).size() < index + 1 || !d.findElements(cartItems).contains(item));
    }

    public void selectAll() {
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(selectAllCheckbox));
        checkbox.click();
        wait.until(d -> checkbox.isSelected());
    }

    public void deselectProduct(int index) {
        WebElement item = getCartItem(index);
        WebElement checkbox = item.findElement(By.cssSelector("input[type='checkbox']"));
        boolean before = checkbox.isSelected();
        item.findElement(By.cssSelector(".checkmark")).click();
        wait.until(d -> getCartItem(index).findElement(By.cssSelector("input[type='checkbox']")).isSelected() != before);
    }

    public void openCouponSection() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("coupon-toggle-row"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#cart-coupon-list[style*='display: block'], #cart-coupon-list:not([style*='display:none'])")));
    }

    public void selectCoupon(int index) {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(couponItems)).get(index).click();
        wait.until(d -> !d.findElement(By.id("selected-coupon-label")).getText().contains("Chọn hoặc nhập mã"));
    }

    public String getSubtotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(subtotalValue)).getText();
    }

    public String getCartBadgeCount() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText().trim();
        return value.isEmpty() ? "0" : value;
    }

    public boolean isCouponExpired(int index) {
        WebElement coupon = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(couponItems)).get(index);
        return "not-allowed".equals(coupon.getCssValue("cursor")) || coupon.getAttribute("style").contains("not-allowed");
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }
}
