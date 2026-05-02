package org.example.pages;

import org.openqa.selenium.By;
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

    @FindBy(css = "button.btn-submit-order, #btn-submit-order, button[type='submit']")
    private WebElement submitOrderBtn;

    @FindBy(css = "button.btn-cancel-order")
    private WebElement cancelOrderBtn;

    @FindBy(css = "button.btn-add-more")
    private WebElement addMoreBtn;

    @FindBy(css = ".total-amount-display")
    private WebElement totalAmountText;

    @FindBy(css = ".order-modal")
    private WebElement orderModal;

    @FindBy(css = "table.order-table tbody tr:first-child .status-label")
    private WebElement firstOrderStatus;

    @FindBy(css = ".error-message")
    private WebElement errorMessage;

    // ── Locators cho chức năng xóa đơn hàng ──────────────────────────────────

    @FindBy(css = "table.order-table tbody tr:first-child .btn-delete-order")
    private WebElement deleteFirstOrderBtn;

    @FindBy(css = ".modal-confirm-delete")
    private WebElement confirmDeleteModal;

    @FindBy(css = ".modal-confirm-delete .modal-body")
    private WebElement confirmDeleteMessage;

    @FindBy(css = ".modal-confirm-delete button.btn-confirm-delete")
    private WebElement confirmDeleteBtn;

    @FindBy(css = ".modal-confirm-delete button.btn-cancel-delete")
    private WebElement cancelDeleteBtn;

    @FindBy(css = ".modal-confirm-delete button.btn-close-modal")
    private WebElement closeDeleteModalBtn;

    @FindBy(css = "table.order-table tbody tr")
    private List<WebElement> orderRows;

    public OrdersPage(WebDriver driver) {
        super(driver); // Kế thừa từ BasePage trong cùng package
    }

    public void clickCreateOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(createOrderBtn)).click();
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
        // Tìm input số lượng của sản phẩm vừa thêm (dòng cuối cùng hoặc dòng chứa tên sp)
        By qtyLocator = By.xpath("//td[contains(text(),'" + name + "')]/..//input[@type='number']");
        WebElement qtyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(qtyLocator));
        qtyInput.clear();
        qtyInput.sendKeys(String.valueOf(quantity));
    }

    public void addMoreProductField() {
        addMoreBtn.click();
    }

    public void selectPaymentCash() {
        cashRadio.click();
    }

    public void selectPaymentTransfer() {
        transferRadio.click();
    }

    public void clickSubmitOrder() {
        submitOrderBtn.click();
    }

    public void clickCancelButton() {
        cancelOrderBtn.click();
    }

    public String getFirstOrderStatus() {
        return wait.until(ExpectedConditions.visibilityOf(firstOrderStatus)).getText().trim();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
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
        driver.get(baseUrl + "panel/orders");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("table.order-table")));
    }

    /** Số dòng đơn hàng hiện có trong bảng */
    public int getOrderCount() {
        try {
            return driver.findElements(By.cssSelector("table.order-table tbody tr")).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Nhấn nút xóa của đơn hàng đầu tiên trong bảng */
    public void clickDeleteFirstOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteFirstOrderBtn)).click();
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