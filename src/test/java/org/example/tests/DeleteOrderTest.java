package org.example.tests;

import org.example.pages.OrdersPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeleteOrderTest extends BaseTest {

    private OrdersPage ordersPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void beforeMethod() {
        super.beforeMethod();
        // 1. Đăng nhập với tài khoản Quản trị viên
        bp().loginAsAdmin();
        ordersPage = new OrdersPage(driver);
        // 2. Vào chức năng "Quản lý đơn đặt hàng"
        ordersPage.open(BASE_URL);
    }

    @Test(description = "FE15-TC01: Xoá đơn hàng thành công")
    public void TC01_DeleteOrderSuccess() {
        int countBefore = ordersPage.getOrderCount();
        if (countBefore == 0) {
            System.out.println("⚠️ Danh sách trống, không có đơn để xóa.");
            return;
        }

        // 3. Chọn một đơn hàng bất kỳ (mặc định chọn đơn đầu tiên)
        // 4. Nhấn icon Xoá (thùng rác)
        ordersPage.clickDeleteFirstOrder();

        // 5. Hộp thoại xác nhận hiện ra, nhấn "Xoá"
        Assert.assertTrue(ordersPage.isConfirmDeleteModalVisible(), "Hộp thoại xác nhận phải xuất hiện");
        ordersPage.clickConfirmDelete();
        sleep(2000);

        // Đơn hàng bị xóa khỏi hệ thống, không còn xuất hiện trong danh sách
        int countAfter = ordersPage.getOrderCount();
        Assert.assertEquals(countAfter, countBefore - 1, "Số lượng đơn hàng phải giảm đi 1 sau khi xóa");
    }

    @Test(description = "FE15-TC02: Hộp thoại xác nhận hiển thị trước khi xoá")
    public void TC02_VerifyDeleteConfirmModal() {
        if (ordersPage.getOrderCount() == 0) return;

        // 2. Nhấn icon Xoá trên một đơn hàng bất kỳ
        ordersPage.clickDeleteFirstOrder();

        // Hộp thoại xác nhận xuất hiện với nội dung rõ ràng và 2 nút: "Huỷ" và "Xoá"
        Assert.assertTrue(ordersPage.isConfirmDeleteModalVisible(), "Hộp thoại xác nhận phải hiển thị");
        Assert.assertTrue(ordersPage.isConfirmDeleteHasTwoButtons(), "Hộp thoại phải có đủ 2 nút: Huỷ và Xoá");
        
        String msg = ordersPage.getConfirmDeleteMessage();
        Assert.assertFalse(msg.isEmpty(), "Nội dung hộp thoại xác nhận không được để trống");
    }

    @Test(description = "FE15-TC03: Huỷ thao tác xoá, đơn hàng không bị xoá")
    public void TC03_CancelDeleteAction() {
        int countBefore = ordersPage.getOrderCount();
        if (countBefore == 0) return;

        // 2. Nhấn icon Xoá trên một đơn hàng
        ordersPage.clickDeleteFirstOrder();
        
        // 4. Nhấn nút "Huỷ"
        ordersPage.clickCancelDelete();
        
        // Kiểm tra hộp thoại đã đóng
        Assert.assertFalse(ordersPage.isConfirmDeleteModalVisible(), "Hộp thoại xác nhận phải đóng lại sau khi nhấn Huỷ");

        // Đơn hàng vẫn còn trong danh sách, không bị xóa
        int countAfter = ordersPage.getOrderCount();
        Assert.assertEquals(countAfter, countBefore, "Số lượng đơn hàng không được thay đổi sau khi nhấn Huỷ");
    }

    @Test(description = "FE15-TC04: Danh sách cập nhật đúng sau khi xoá")
    public void TC04_VerifyListUpdatedAfterDelete() {
        // 1. Ghi nhận tổng số đơn hàng hiện có
        int countBefore = ordersPage.getOrderCount();
        if (countBefore == 0) return;

        // 2. Xoá thành công 1 đơn hàng
        ordersPage.clickDeleteFirstOrder();
        ordersPage.clickConfirmDelete();
        sleep(2000);

        // 3. Quan sát danh sách: Đơn vừa xóa biến mất, tổng số đơn giảm đúng 1
        int countAfter = ordersPage.getOrderCount();
        Assert.assertEquals(countAfter, countBefore - 1, "Tổng số đơn hàng hiển thị phải giảm đúng 1");
    }

    @Test(description = "FE15-TC05: Xoá nhiều đơn hàng liên tiếp")
    public void TC05_DeleteMultipleOrdersConsecutively() {
        int countInitial = ordersPage.getOrderCount();
        if (countInitial < 2) {
            System.out.println("⚠️ Cần ít nhất 2 đơn hàng để thực hiện test case này.");
            return;
        }

        // 1. Xoá đơn hàng thứ nhất
        ordersPage.clickDeleteFirstOrder();
        ordersPage.clickConfirmDelete();
        sleep(2000);

        // 2. Tiếp tục xoá đơn hàng thứ hai
        ordersPage.clickDeleteFirstOrder();
        ordersPage.clickConfirmDelete();
        sleep(2000);

        // Cả 2 đơn đều bị xóa thành công, danh sách cập nhật chính xác
        int countFinal = ordersPage.getOrderCount();
        Assert.assertEquals(countFinal, countInitial - 2, "Tổng số đơn hàng phải giảm đi 2 sau khi xóa 2 đơn");
    }
}
