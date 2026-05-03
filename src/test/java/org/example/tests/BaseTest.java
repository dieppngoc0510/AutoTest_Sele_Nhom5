package org.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.example.report.TestListener;

import java.time.Duration;

@Listeners(TestListener.class)
public class BaseTest {
    protected static final String BASE_URL = "http://127.0.0.1:8000/";

    protected WebDriver driver;
    protected WebDriverWait wait;

    /** Lazy-initialized helper – dùng để gọi các method trong BasePage */
    private BasePage basePage;

    protected BasePage bp() {
        if (basePage == null) basePage = new BasePage(driver);
        return basePage;
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        WebDriverManager.chromedriver().setup();
        Constant.WEBDRIVER.set(new ChromeDriver());
        Constant.WEBDRIVER.get().manage().window().maximize();
        driver = Constant.WEBDRIVER.get();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        basePage = null; // reset mỗi test
    }

    protected void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    protected void log(String msg) {
        System.out.println("  [LOG] " + msg);
    }

    protected WebElement waitFor(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // Driver closure is handled by TestListener
    }
}
