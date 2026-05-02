package org.example.tests;

import org.example.pages.OrdersPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateOrderTest extends BaseTest {

    private OrdersPage ordersPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void beforeMethod() {
        super.beforeMethod();
        loginAsAdmin();
        ordersPage = new OrdersPage(driver);
        ordersPage.open(BASE_URL);
    }

    @Test(description = "FE014-TC01: Tạo đơn hàng thành công")
    public void TC01_CreateOrderSuccess() {
        int countBefore = ordersPage.getOrderCount();
        System.out.println("📦 Số đơn trước khi tạo: " + countBefore);

        // Nhấn nút tạo đơn hàng
        ordersPage.clickCreateOrder();

        // Nhập thông tin khách hàng theo thứ tự: tên → điện thoại → địa chỉ
        ordersPage.fillCustomerName("Nguyễn Văn A");
        ordersPage.fillCustomerPhone("0905123456");
        ordersPage.fillCustomerAddress("tại quán");

        // Chọn sản phẩm và phương thức thanh toán
        ordersPage.searchAndSelectProduct("Áo khoác da lộn dáng ngắn");
        ordersPage.selectPaymentCash();
        ordersPage.clickSubmitOrder();
        sleep(1500);

        int countAfter = ordersPage.getOrderCount();
        System.out.println("Số đơn sau khi tạo: " + countAfter);

        Assert.assertEquals(countAfter, countBefore + 1,
                "TC01: Tổng đơn hàng phải tăng lên 1 sau khi tạo");
        Assert.assertEquals(ordersPage.getFirstOrderStatus(), "Chờ xử lý",
                "TC01: Trạng thái đơn mới phải là 'Chờ xử lý'");
        System.out.println("TC01 PASSED: Tạo đơn hàng thành công");
    }

    @Test(priority = 2, description = "FE014-TC02: Tạo đơn hàng với phương thức Chuyển khoản")
    public void TC02_CreateOrderWithTransfer() {
        // Nhấn nút tạo đơn hàng
        ordersPage.clickCreateOrder();

        // Nhập thông tin khách hàng theo thứ tự: tên → điện thoại → địa chỉ
        ordersPage.fillCustomerName("Trần Thị B");
        ordersPage.fillCustomerPhone("0905666777");
        ordersPage.fillCustomerAddress("Đà Nẵng");

        // Chọn sản phẩm và phương thức thanh toán
        ordersPage.searchAndSelectProduct("Áo khoác da lộn dáng ngắn");
        ordersPage.selectPaymentTransfer();
        ordersPage.clickSubmitOrder();
        sleep(1500);

        Assert.assertTrue(driver.getCurrentUrl().contains("/orders"),
                "TC02: Không quay về trang danh sách sau khi tạo đơn");
        System.out.println("TC02 PASSED: Tạo đơn hàng với phương thức Chuyển khoản thành công");
    }

    @Test(description = "FE014-TC03: Lỗi khi để trống tên khách hàng")
    public void TC03_EmptyCustomerNameError() {
        // Nhấn nút tạo đơn hàng
        ordersPage.clickCreateOrder();

        // Bỏ trống tên, vẫn nhập điện thoại và địa chỉ
        ordersPage.fillCustomerName("");
        ordersPage.fillCustomerPhone("0905123456");
        ordersPage.fillCustomerAddress("Đà Nẵng");

        ordersPage.searchAndSelectProduct("Áo khoác da lộn dáng ngắn");
        ordersPage.clickSubmitOrder();

        Assert.assertTrue(ordersPage.isErrorMessageDisplayed(),
                "TC03: Hệ thống không báo lỗi khi thiếu tên khách hàng!");
        System.out.println("TC03 PASSED: Hiển thị lỗi khi để trống tên khách hàng");
    }

    @Test(description = "FE014-TC04: Lỗi khi không chọn sản phẩm")
    public void TC04_NoProductError() {
        // Nhấn nút tạo đơn hàng
        ordersPage.clickCreateOrder();

        // Nhập đầy đủ thông tin khách hàng nhưng không chọn sản phẩm
        ordersPage.fillCustomerName("Lê Thị C");
        ordersPage.fillCustomerPhone("0901234567");
        ordersPage.fillCustomerAddress("Hà Nội");

        // Bỏ qua bước chọn sản phẩm
        ordersPage.clickSubmitOrder();

        Assert.assertTrue(ordersPage.isErrorMessageDisplayed(),
                "TC04: Hệ thống không báo lỗi khi đơn hàng không có sản phẩm!");
        System.out.println("TC04 PASSED: Hiển thị lỗi khi không chọn sản phẩm");
    }

    @Test(description = "FE014-TC05: Thêm nhiều sản phẩm vào một đơn hàng")
    public void TC05_AddMultipleProducts() {
        int countBefore = ordersPage.getOrderCount();

        ordersPage.clickCreateOrder();

        ordersPage.fillCustomerName("Nhóm 08");
        ordersPage.fillCustomerPhone("0909090909");
        ordersPage.fillCustomerAddress("Đà Nẵng");

        // Thêm sản phẩm 1
        ordersPage.searchAndSelectProduct("Áo thun");
        // Nhấn nút "+" để thêm dòng sản phẩm mới
        ordersPage.addMoreProductField();
        // Thêm sản phẩm 2
        ordersPage.searchAndSelectProduct("Quần tây");

        ordersPage.clickSubmitOrder();
        sleep(1500);

        int countAfter = ordersPage.getOrderCount();
        Assert.assertEquals(countAfter, countBefore + 1,
                "TC05: Lỗi khi tạo đơn hàng chứa nhiều sản phẩm");
        System.out.println("TC05 PASSED: Thêm nhiều sản phẩm vào một đơn hàng thành công");
    }

    @Test(description = "FE014-TC06: Kiểm tra tính tổng tiền tự động")
    public void TC06_VerifyTotalCalculation() {
        // Nhấn nút tạo đơn hàng
        ordersPage.clickCreateOrder();

        ordersPage.fillCustomerName("Khách Test Tổng Tiền");
        ordersPage.fillCustomerPhone("0900000001");
        ordersPage.fillCustomerAddress("Đà Nẵng");

        ordersPage.selectProductByName("Sản phẩm 200k", 1);
        ordersPage.addMoreProductField();
        ordersPage.selectProductByName("Sản phẩm 150k", 2);


        String actualTotal = ordersPage.getTotalAmountCalculated();
        Assert.assertEquals(actualTotal, "500.000đ",
                "TC06: Tổng tiền tính toán bị sai!");
        System.out.println("TC06 PASSED: Tổng tiền tính toán đúng");
    }

    @Test(priority = 7, description = "FE014-TC07: Hủy tạo đơn giữa chừng (Đóng Modal)")
    public void TC07_CancelOrderCreation() {
        int countBefore = ordersPage.getOrderCount();

        // Nhấn nút tạo đơn hàng
        ordersPage.clickCreateOrder();

        // Nhập một phần thông tin rồi hủy
        ordersPage.fillCustomerName("Khách hàng hủy");
        ordersPage.fillCustomerPhone("0900000002");
        ordersPage.fillCustomerAddress("Hồ Chí Minh");

        // Nhấn nút Hủy
        ordersPage.clickCancelButton();
        sleep(800);

        // Kiểm tra modal đóng và số đơn không thay đổi
        Assert.assertTrue(ordersPage.isOrderModalHidden(),
                "TC07: Modal tạo đơn không đóng sau khi nhấn Hủy");
        Assert.assertEquals(ordersPage.getOrderCount(), countBefore,
                "TC07: Số đơn hàng không được thay đổi khi hủy tạo đơn");
        System.out.println("TC07 PASSED: Hủy tạo đơn, modal đóng và danh sách không đổi");
    }
}
