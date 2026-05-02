package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class OrderPage {

    WebDriver driver;

    public OrderPage(WebDriver driver) {
        this.driver = driver;
    }

    // ===== LOCATOR ĐÚNG =====
    By searchInput = By.cssSelector(".topbar-search input[name='q']");
    By searchBtn   = By.cssSelector(".topbar-search button");

    // ===== ACTION =====
    public void search(String keyword) {

        WebElement input = driver.findElement(searchInput);

        input.click();
        input.clear();
        input.sendKeys(keyword);

        driver.findElement(searchBtn).click(); // submit
    }

}