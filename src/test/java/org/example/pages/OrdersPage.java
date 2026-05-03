package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class OrdersPage extends BasePage {

    @FindBy(css = ".btn-create-custom, .btn-create-order, button[onclick*='create']")
    private WebElement createOrderBtn;

    @FindBy(xpath = "//input[contains(@id, 'name') or contains(@name, 'name')] | //*[contains(@class, 'customer-name')]")
    private WebElement customerNameInput;
    
    @FindBy(xpath = "//input[contains(@id, 'phone') or contains(@name, 'phone')] | //*[contains(@class, 'customer-phone')]")
    private WebElement customerPhoneInput;
    
    @FindBy(xpath = "//textarea[contains(@id, 'address') or contains(@name, 'address')] | //input[contains(@id, 'address') or contains(@name, 'address')]")
    private WebElement customerAddressInput;
    
    @FindBy(xpath = "//input[contains(@class, 'search-product') or contains(@id, 'product-search')]")
    private WebElement productSearchInput;

    @FindBy(css = ".product-result-item, #product-search-results div, .search-result-item")
    private List<WebElement> productResults;

    @FindBy(css = "#payment-cash, input[value='cash'], input[value='COD']")
    private WebElement cashRadio;

    @FindBy(css = "#payment-transfer, input[value='transfer'], input[value='banking']")
    private WebElement transferRadio;

    @FindBy(css = "button[type='submit'], .mockup-btn-primary")
    private WebElement submitOrderBtn;

    @FindBy(xpath = "//button[contains(text(), 'Huỷ')]")
    private WebElement cancelOrderBtn;

    @FindBy(css = ".btn-create-add")
    private WebElement addMoreBtn;

    @FindBy(id = "txt-total")
    private WebElement totalAmountText;

    @FindBy(id = "create-order-modal")
    private WebElement orderModal;

    @FindBy(css = "table.order-table tbody tr:first-child td:nth-child(6) span")
    private WebElement firstOrderStatus;

    @FindBy(css = "[id^='err-']")
    private List<WebElement> errorMessages;

    // ── Locators cho chức năng xóa đơn hàng ──────────────────────────────────

    @FindBy(css = "table.order-table tbody tr:first-child .fa-trash-alt, table tbody tr:first-child .fa-trash-alt, table tbody tr:first-child button.btn-delete")
    private WebElement deleteFirstOrderBtn;

    @FindBy(css = ".modal-content, #deleteOrderModal .modal-content")
    private WebElement confirmDeleteModal;

    @FindBy(css = ".modal-body, .modal-confirm-body, .modal-message")
    private WebElement confirmDeleteMessage;

    @FindBy(xpath = "//*[contains(@class, 'modal-content')]//*[@id='del-btn-confirm' or contains(@class, 'btn-danger')]")
    private WebElement confirmDeleteBtn;
    
    @FindBy(xpath = "//*[contains(@class, 'modal-content')]//button[(contains(@class, 'btn-secondary') or contains(@class, 'btn-cancel')) and (contains(text(), 'Huỷ') or contains(text(), 'Hủy'))]")
    private WebElement cancelDeleteBtn;

    @FindBy(css = "button.close")
    private WebElement closeDeleteModalBtn;

    @FindBy(css = "table.order-table tbody tr, table tbody tr")
    private List<WebElement> orderRows;

    public OrdersPage(WebDriver driver) {
        super(driver); // Kế thừa từ BasePage trong cùng package
    }

    public void clickCreateOrder() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createOrderBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void fillOrderInfo(String name, String phone, String address) {
        // Nhập tên khách hàng
        wait.until(ExpectedConditions.visibilityOf(customerNameInput)).clear();
        customerNameInput.sendKeys(name);

        // Nhập số điện thoại
        wait.until(ExpectedConditions.visibilityOf(customerPhoneInput)).clear();
        customerPhoneInput.sendKeys(phone);

        // Nhập địa chỉ
        wait.until(ExpectedConditions.visibilityOf(customerAddressInput)).clear();
        customerAddressInput.sendKeys(address);
    }

    public void fillCustomerName(String name) {
        wait.until(ExpectedConditions.visibilityOf(customerNameInput)).clear();
        customerNameInput.sendKeys(name);
    }

    public void fillCustomerPhone(String phone) {
        wait.until(ExpectedConditions.visibilityOf(customerPhoneInput)).clear();
        customerPhoneInput.sendKeys(phone);
    }

    public void fillCustomerAddress(String address) {
        wait.until(ExpectedConditions.visibilityOf(customerAddressInput)).clear();
        customerAddressInput.sendKeys(address);
    }

    public void searchAndSelectProduct(String productName) {
        wait.until(ExpectedConditions.visibilityOf(productSearchInput)).clear();
        productSearchInput.sendKeys(productName);
        // Chờ kết quả tìm kiếm hiển thị
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product-result-item, #product-search-results div, .search-result-item")));
        List<WebElement> results = driver.findElements(By.cssSelector(".product-result-item, #product-search-results div, .search-result-item"));
        if (!results.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(results.get(0))).click();
        } else {
            System.out.println("❌ Không tìm thấy sản phẩm: " + productName);
        }
    }

    public void selectProductByName(String name, int quantity) {
        searchAndSelectProduct(name);
        // Find the row for the product and use the stepper to set quantity
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//tbody[@id='create-order-tbody']/tr[td[1]//select/option[@selected and contains(text(), '" + name + "')] or td[1]//select/option[text()='" + name + "']] | //tbody[@id='create-order-tbody']/tr[last()]")
        ));
        
        WebElement qtyVal = row.findElement(By.cssSelector(".stepper-val"));
        WebElement plusBtn = row.findElement(By.xpath(".//button[text()='+']"));
        
        int currentQty = Integer.parseInt(qtyVal.getText());
        while (currentQty < quantity) {
            plusBtn.click();
            currentQty++;
        }
    }

    public void addMoreProductField() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addMoreBtn);
    }

    public void selectPaymentCash() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cashRadio);
    }
    
    public void selectPaymentTransfer() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", transferRadio);
    }
    
    public void clickSubmitOrder() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitOrderBtn);
    }
    
    public void clickCancelButton() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cancelOrderBtn);
    }

    public String getFirstOrderStatus() {
        return wait.until(ExpectedConditions.visibilityOf(firstOrderStatus)).getText().trim();
    }

    public boolean isErrorMessageDisplayed() {
        for (WebElement msg : errorMessages) {
            if (msg.isDisplayed()) return true;
        }
        return false;
    }

    public String getTotalAmountCalculated() {
        return totalAmountText.getText().trim();
    }

    public boolean isOrderModalHidden() {
        return wait.until(ExpectedConditions.invisibilityOf(orderModal));
    }

    // ── Methods cho chức năng xóa đơn hàng ───────────────────────────────────

    /** Mở trang danh sách đơn hàng */
    public void open(String baseUrl) {
        String targetUrl = baseUrl;
        if (!targetUrl.endsWith("/")) targetUrl += "/";
        targetUrl += "panel/orders";
        
        driver.get(targetUrl);
        
        // Đợi trang tải xong
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        
        // Chờ bảng hoặc nút tạo đơn xuất hiện (tùy cái nào đến trước)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.order-table")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn-create-order")),
                ExpectedConditions.visibilityOfElementLocated(By.tagName("table"))
        ));
    }

    /** Số dòng đơn hàng hiện có trong bảng */
    public int getOrderCount() {
        try {
            return driver.findElements(By.cssSelector("table.order-table tbody tr, table tbody tr")).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Nhấn nút xóa của đơn hàng đầu tiên trong bảng */
    public void clickDeleteFirstOrder() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(deleteFirstOrderBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    /** Kiểm tra modal xác nhận xóa có hiển thị không */
    public boolean isConfirmDeleteModalVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(confirmDeleteModal)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Lấy nội dung text của modal xác nhận xóa */
    public String getConfirmDeleteMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(confirmDeleteMessage))
                    .getText().trim().toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }

    /** Kiểm tra modal có đủ 2 nút Hủy và Xóa không */
    public boolean isConfirmDeleteHasTwoButtons() {
        try {
            // Đảm bảo modal đã hiện trước khi tìm nút
            wait.until(ExpectedConditions.visibilityOf(confirmDeleteModal));
            // Chờ cả 2 nút hiển thị và có text
            wait.until(ExpectedConditions.visibilityOf(cancelDeleteBtn));
            wait.until(ExpectedConditions.visibilityOf(confirmDeleteBtn));
            return cancelDeleteBtn.isDisplayed() && confirmDeleteBtn.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Nhấn nút "Xóa" (xác nhận xóa) trong modal */
    public void clickConfirmDelete() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn)).click();
        wait.until(ExpectedConditions.invisibilityOf(confirmDeleteModal));
    }

    /** Nhấn nút "Hủy" trong modal */
    public void clickCancelDelete() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelDeleteBtn)).click();
        wait.until(ExpectedConditions.invisibilityOf(confirmDeleteModal));
        // Đợi thêm một chút để hiệu ứng đóng modal hoàn tất và bảng ổn định
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    /** Nhấn nút X đóng modal */
    public void clickCloseDeleteModal() {
        wait.until(ExpectedConditions.elementToBeClickable(closeDeleteModalBtn)).click();
        wait.until(ExpectedConditions.invisibilityOf(confirmDeleteModal));
    }

    /** Kiểm tra một mã đơn hàng có còn trong danh sách không */
    public boolean isOrderInList(String orderCode) {
        List<WebElement> links = driver.findElements(
                By.xpath("//a[normalize-space()='" + orderCode + "']"));
        return !links.isEmpty();
    }
}