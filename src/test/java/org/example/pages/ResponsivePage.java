package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class ResponsivePage extends BasePage {
    public ResponsivePage(WebDriver driver) {
        super(driver);
    }

    public void setViewport(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void assertNoHorizontalOverflow(String message) {
        long scrollWidth = numberScript(
                "return Math.max(document.body.scrollWidth, document.documentElement.scrollWidth);");
        long clientWidth = numberScript(
                "return Math.min(window.innerWidth, document.documentElement.clientWidth || window.innerWidth);");
        Assert.assertTrue(scrollWidth <= clientWidth + 4,
                message + " scrollWidth=" + scrollWidth + ", clientWidth=" + clientWidth);
    }

    public void assertTopLevelWidthFits(String selector, int tolerance, String message) {
        Map<String, Object> rect = getRect(selector);
        long viewportWidth = numberScript("return window.innerWidth;");
        double width = rectNumber(rect, "width");
        Assert.assertTrue(width <= viewportWidth + tolerance,
                message + " elementWidth=" + width + ", viewportWidth=" + viewportWidth);
    }

    public void assertCenteredWithinViewport(String selector, double tolerance, String message) {
        Map<String, Object> rect = getRect(selector);
        double viewportCenter = numberScript("return window.innerWidth;") / 2.0;
        double elementCenter = rectNumber(rect, "left") + (rectNumber(rect, "width") / 2.0);
        Assert.assertTrue(Math.abs(elementCenter - viewportCenter) <= tolerance,
                message + " delta=" + Math.abs(elementCenter - viewportCenter));
    }

    public void assertContainerNotTooWide(String selector, double maxViewportRatio, String message) {
        Map<String, Object> rect = getRect(selector);
        double viewportWidth = numberScript("return window.innerWidth;");
        double width = rectNumber(rect, "width");
        Assert.assertTrue(width <= viewportWidth * maxViewportRatio,
                message + " elementWidth=" + width + ", viewportWidth=" + viewportWidth);
    }

    public void assertWidthNearContainer(String elementSelector, String containerSelector,
                                         double minRatio, String message) {
        Map<String, Object> elementRect = getRect(elementSelector);
        Map<String, Object> containerRect = getRect(containerSelector);
        double elementWidth = rectNumber(elementRect, "width");
        double containerWidth = rectNumber(containerRect, "width");
        double ratio = elementWidth / containerWidth;
        Assert.assertTrue(ratio >= minRatio,
                message + " ratio=" + ratio + ", elementWidth=" + elementWidth
                        + ", containerWidth=" + containerWidth);
    }

    public void assertWidthRatioBetween(String elementSelector, String containerSelector,
                                        double minRatio, double maxRatio, String message) {
        Map<String, Object> elementRect = getRect(elementSelector);
        Map<String, Object> containerRect = getRect(containerSelector);
        double elementWidth = rectNumber(elementRect, "width");
        double containerWidth = rectNumber(containerRect, "width");
        double ratio = elementWidth / containerWidth;
        Assert.assertTrue(ratio >= minRatio && ratio <= maxRatio,
                message + " ratio=" + ratio + ", elementWidth=" + elementWidth
                        + ", containerWidth=" + containerWidth);
    }

    public void assertElementsStackVertically(String selector, int minCount, String message) {
        List<List<Number>> rects = getElementPositions(selector);
        Assert.assertTrue(rects.size() >= minCount,
                "Expected at least " + minCount + " visible elements for selector: " + selector);
        for (int i = 1; i < minCount; i++) {
            double previousTop = rects.get(i - 1).get(1).doubleValue();
            double currentTop = rects.get(i).get(1).doubleValue();
            Assert.assertTrue(Math.abs(currentTop - previousTop) > 24,
                    message + " top positions were too close: " + previousTop + " and " + currentTop);
        }
    }

    public void assertElementsShareRow(String selector, int minCount, String message) {
        List<List<Number>> rects = getElementPositions(selector);
        Assert.assertTrue(rects.size() >= minCount,
                "Expected at least " + minCount + " visible elements for selector: " + selector);
        double firstTop = rects.get(0).get(1).doubleValue();
        double secondTop = rects.get(1).get(1).doubleValue();
        Assert.assertTrue(Math.abs(firstTop - secondTop) < 80,
                message + " top positions were " + firstTop + " and " + secondTop);
    }

    @SuppressWarnings("unchecked")
    public List<List<Number>> getElementPositions(String selector) {
        return (List<List<Number>>) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll(arguments[0]))" +
                        ".filter(el => !!(el.offsetWidth || el.offsetHeight || el.getClientRects().length))" +
                        ".map(el => { const r = el.getBoundingClientRect(); return [r.left, r.top, r.width, r.height]; });",
                selector
        );
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getRect(String selector) {
        return (Map<String, Object>) ((JavascriptExecutor) driver).executeScript(
                "const el = document.querySelector(arguments[0]);" +
                        "if (!el) { return null; }" +
                        "const r = el.getBoundingClientRect();" +
                        "return {left: r.left, top: r.top, width: r.width, height: r.height};",
                selector
        );
    }

    public int countDistinctLefts(String selector, int limit) {
        Long count = (Long) ((JavascriptExecutor) driver).executeScript(
                "const nodes = Array.from(document.querySelectorAll(arguments[0]))" +
                        ".filter(el => !!(el.offsetWidth || el.offsetHeight || el.getClientRects().length))" +
                        ".slice(0, arguments[1]);" +
                        "const groups = [];" +
                        "nodes.forEach(el => {" +
                        "  const left = Math.round(el.getBoundingClientRect().left);" +
                        "  const matched = groups.some(value => Math.abs(value - left) <= 20);" +
                        "  if (!matched) { groups.push(left); }" +
                        "});" +
                        "return groups.length;",
                selector, limit
        );
        return count.intValue();
    }

    public void openResponsiveCartAndWaitForItems(int expectedCount) {
        ((JavascriptExecutor) driver).executeScript(
                "if (typeof openCart === 'function') { openCart(); }" +
                        "else { document.querySelector('button.btn-cart-icon')?.click(); }");
        wait.until(d -> Boolean.TRUE.equals(((JavascriptExecutor) d).executeScript(
                "const el = document.getElementById('cart-sidebar');" +
                        "if (!el) return false;" +
                        "const style = getComputedStyle(el);" +
                        "const rect = el.getBoundingClientRect();" +
                        "return el.className.includes('open') || style.right === '0px' || rect.right <= window.innerWidth + 2;"
        )));
        if (expectedCount == 0) {
            wait.until(d -> d.findElements(By.cssSelector("#cart-items .cart-item")).isEmpty());
        } else {
            wait.until(d -> d.findElements(By.cssSelector("#cart-items .cart-item")).size() == expectedCount);
        }
    }

    public double getElementWidth(String selector) {
        return rectNumber(getRect(selector), "width");
    }

    private double rectNumber(Map<String, Object> rect, String key) {
        Object value = rect.get(key);
        Assert.assertNotNull(value, "Missing rect key: " + key);
        return ((Number) value).doubleValue();
    }

    private long numberScript(String script) {
        return ((Number) ((JavascriptExecutor) driver).executeScript(script)).longValue();
    }
}
