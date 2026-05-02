package org.example.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ProductPage extends BasePage {

    @FindBy(css = "button.btn-add-to-cart")
    private WebElement addToCartButton;

    @FindBy(css = "label.color-option-wrapper")
    private List<WebElement> colorOptions;

    @FindBy(css = "label.size-option-wrapper")
    private List<WebElement> sizeOptions;

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public void selectColor(int index) {
        WebElement option = wait.until(ExpectedConditions.visibilityOfAllElements(colorOptions))
                .get(index)
                .findElement(By.cssSelector(".color-option"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
    }

    public void selectSize(int index) {
        WebElement option = wait.until(ExpectedConditions.visibilityOfAllElements(sizeOptions))
                .get(index)
                .findElement(By.cssSelector(".size-option"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
    }

    public void clickAddToCart() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
    }

    public void addProductToCart(int colorIndex, int sizeIndex) {
        selectColor(colorIndex);
        selectSize(sizeIndex);
        clickAddToCart();
        waitForToast();
    }

    public void waitForToast() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast")));
    }

    public String getToastMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast .toast-message"))).getText();
    }

    public boolean isToastImageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast .toast-image img"))).isDisplayed();
    }

    public void clickViewCartOnToast() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".toast .toast-btn-view")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        wait.until(ExpectedConditions.attributeContains(By.id("cart-sidebar"), "class", "open"));
    }

    public String getAlertTextAndAccept() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }

    public boolean isToastHidden() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast")));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
