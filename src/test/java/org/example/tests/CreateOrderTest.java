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
        // 1. Đăng nhập với tài khoản Quản trị viên
        bp().loginAsAdmin();
        ordersPage = new OrdersPage(driver);
        // 2. Vào chức năng "Quản lý đơn đặt hàng" (mở trực tiếp URL panel orders)
        ordersPage.open(BASE_URL);
    }

    @Test(description = "FE13-TC01: Tạo đơn hàng thành công với đầy đủ thông tin")
    public void TC01_CreateOrderSuccess() {
        int countBefore = ordersPage.getOrderCount();
        
        // 3. Nhấn nút "Tạo đơn mới"
        ordersPage.clickCreateOrder();

        // 4. Nhập thông tin khách hàng: Nguyễn Văn A, 0942835448, tại quán
        ordersPage.fillCustomerName("Nguyễn Văn A");
        ordersPage.fillCustomerPhone("0942835448");
        ordersPage.fillCustomerAddress("tại quán");

        // 5. Tìm và chọn sản phẩm: Áo khoác da lộn dáng ngắn (Size M, Màu Trắng, SL: 1)
        ordersPage.searchAndSelectProduct("Áo khoác da lộn dáng ngắn");
        
        // 6. Chọn phương thức thanh toán: Tiền mặt
        ordersPage.selectPaymentCash();

        // 7. Nhấn nút "Tạo đơn mới" (Submit)
        ordersPage.clickSubmitOrder();
        sleep(2000);

        // Kiểm tra đơn hàng được tạo thành công và lưu vào hệ thống
        int countAfter = ordersPage.getOrderCount();
        Assert.assertEquals(countAfter, countBefore + 1, "Số lượng đơn hàng phải tăng thêm 1");
        
        // Trạng thái đơn mới tạo phải là "Chờ xử lý"
        Assert.assertEquals(ordersPage.getFirstOrderStatus(), "Chờ xử lý", "Trạng thái đơn hàng mới phải là 'Chờ xử lý'");
    }

    @Test(description = "FE13-TC02: Tạo đơn hàng với phương thức thanh toán Chuyển khoản")
    public void TC02_CreateOrderWithTransfer() {
        ordersPage.clickCreateOrder();
        ordersPage.fillCustomerName("Nguyễn Văn A");
        ordersPage.fillCustomerPhone("0942835448");
        ordersPage.fillCustomerAddress("tại quán");

        ordersPage.searchAndSelectProduct("Áo khoác da lộn dáng ngắn");
        
        // 6. Chọn phương thức thanh toán: Chuyển khoản
        ordersPage.selectPaymentTransfer();

        ordersPage.clickSubmitOrder();
        sleep(2000);

        Assert.assertTrue(driver.getCurrentUrl().contains("/orders"), "Hệ thống phải quay về trang danh sách đơn hàng");
        Assert.assertEquals(ordersPage.getFirstOrderStatus(), "Chờ xử lý", "Đơn hàng thanh toán chuyển khoản cũng phải ở trạng thái 'Chờ xử lý'");
    }

    @Test(description = "FE13-TC03: Tạo đơn hàng khi để trống trường Khách hàng")
    public void TC03_CreateOrderEmptyCustomer() {
        ordersPage.clickCreateOrder();
        
        // 3. Bỏ trống trường Khách hàng
        ordersPage.fillCustomerName("");
        
        // 4. Chọn sản phẩm bất kỳ
        ordersPage.searchAndSelectProduct("Áo thun");
        
        // 5. Nhấn "Tạo đơn mới"
        ordersPage.clickSubmitOrder();

        // Hệ thống yêu cầu nhập thông tin khách hàng, không cho phép tạo đơn
        Assert.assertTrue(ordersPage.isErrorMessageDisplayed(), "Hệ thống phải báo lỗi khi thiếu tên khách hàng");
    }

    @Test(description = "FE13-TC04: Tạo đơn hàng khi không chọn sản phẩm")
    public void TC04_CreateOrderNoProduct() {
        ordersPage.clickCreateOrder();
        
        // 3. Nhập khách hàng: Lê Thị C
        ordersPage.fillCustomerName("Lê Thị C");
        
        // 4. Bỏ trống phần Sản phẩm
        // 5. Nhấn "Tạo đơn mới"
        ordersPage.clickSubmitOrder();

        // Hệ thống yêu cầu thêm ít nhất 1 sản phẩm trước khi tạo đơn
        Assert.assertTrue(ordersPage.isErrorMessageDisplayed(), "Hệ thống phải báo lỗi khi không có sản phẩm nào được chọn");
    }

    @Test(description = "FE13-TC05: Thêm nhiều sản phẩm vào một đơn hàng")
    public void TC05_AddMultipleProductsToOrder() {
        int countBefore = ordersPage.getOrderCount();
        ordersPage.clickCreateOrder();
        ordersPage.fillCustomerName("Khách hàng mua nhiều");
        ordersPage.fillCustomerPhone("0912345678");
        ordersPage.fillCustomerAddress("Hà Nội");

        // 4. Thêm SP1: Áo thun (S, Trắng, SL:1)
        ordersPage.searchAndSelectProduct("Áo thun");
        
        // 5. Nhấn dấu "+" để thêm SP2: Quần (M, Đen, SL:1)
        ordersPage.addMoreProductField();
        ordersPage.searchAndSelectProduct("Quần");

        // 6. Nhấn "Tạo đơn mới"
        ordersPage.clickSubmitOrder();
        sleep(2000);

        int countAfter = ordersPage.getOrderCount();
        Assert.assertEquals(countAfter, countBefore + 1, "Đơn hàng chứa nhiều sản phẩm phải được tạo thành công");
    }

    @Test(description = "FE13-TC06: Tổng tiền được tính đúng khi thêm sản phẩm")
    public void TC06_VerifyTotalCalculation() {
        ordersPage.clickCreateOrder();
        
        // Giả sử có 2 sản phẩm với giá cố định để test
        // SP1: Áo khoác - 400.000đ
        // SP2: Quần tây - 180.000đ
        ordersPage.selectProductByName("Áo khoác", 1); // 400.000 * 1 = 400.000
        ordersPage.addMoreProductField();
        ordersPage.selectProductByName("Quần tây", 2); // 180.000 * 2 = 360.000
        
        // Tổng tiền hiển thị đúng = 400.000 + 360.000 = 760.000đ
        String total = ordersPage.getTotalAmountCalculated();
        Assert.assertTrue(total.contains("760.000"), "Tổng tiền tính toán không chính xác: " + total);
    }

    @Test(description = "FE13-TC07: Huỷ tạo đơn hàng giữa chừng")
    public void TC07_CancelOrderCreation() {
        int countBefore = ordersPage.getOrderCount();
        ordersPage.clickCreateOrder();
        
        ordersPage.fillCustomerName("Khách hàng tạm");
        ordersPage.searchAndSelectProduct("Áo thun");
        
        // 3. Nhấn nút "Huỷ"
        ordersPage.clickCancelButton();
        sleep(1000);

        // Hệ thống đóng form, không lưu bất kỳ dữ liệu nào
        Assert.assertTrue(ordersPage.isOrderModalHidden(), "Modal tạo đơn không đóng sau khi nhấn Huỷ");
        Assert.assertEquals(ordersPage.getOrderCount(), countBefore, "Số lượng đơn hàng không được thay đổi");
    }

    @Test(description = "FE13-TC08: Trạng thái đơn mới tạo phải là 'Chờ xử lý'")
    public void TC08_VerifyInitialOrderStatus() {
        // Tương tự TC01 nhưng tập trung verify trạng thái
        ordersPage.clickCreateOrder();
        ordersPage.fillCustomerName("Test Status");
        ordersPage.fillCustomerPhone("0987654321");
        ordersPage.fillCustomerAddress("Đà Nẵng");
        ordersPage.searchAndSelectProduct("Áo thun");
        ordersPage.clickSubmitOrder();
        sleep(2000);

        // Trạng thái đơn hàng hiển thị đúng là "Chờ xử lý"
        String status = ordersPage.getFirstOrderStatus();
        Assert.assertEquals(status, "Chờ xử lý", "Trạng thái đơn hàng mới tạo phải là 'Chờ xử lý'");
    }

    @Test(description = "FE13-TC09: Tìm kiếm sản phẩm trong form tạo đơn")
    public void TC09_SearchProductInForm() {
        ordersPage.clickCreateOrder();
        
        // 2. Nhập từ khóa "Áo thun" vào ô tìm kiếm sản phẩm
        ordersPage.searchAndSelectProduct("Áo thun");
        
        // 3. Quan sát kết quả hiển thị (Phương thức searchAndSelectProduct đã có bước wait cho kết quả)
        // Nếu không ném Exception Timeout thì nghĩa là kết quả có hiển thị
        System.out.println("Tìm kiếm sản phẩm 'Áo thun' thành công");
    }
}
