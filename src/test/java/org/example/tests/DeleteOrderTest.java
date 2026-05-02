package org.example.tests;

import org.example.pages.OrdersPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class DeleteOrderTest extends BaseTest {

    private OrdersPage ordersPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void beforeMethod() {
        super.beforeMethod();
        loginAsAdmin();
        ordersPage = new OrdersPage(driver);
        ordersPage.open(BASE_URL);
    }

    @Test(description = "FE015-TC01: Xoá đơn hàng thành công")
    public void TC01_DeleteOrderSuccess() {
        int countBefore = ordersPage.getOrderCount();
        System.out.println("Số đơn trước khi xoá: " + countBefore);

        // Nhấn icon xoá đơn hàng đầu tiên
        ordersPage.clickDeleteFirstOrder();

        // Kiểm tra modal xác nhận hiện ra
        Assert.assertTrue(
                ordersPage.isConfirmDeleteModalVisible(),
                "Modal xác nhận xoá phải hiển thị"
        );

        // Nhấn nút "Xoá" (đỏ)
        ordersPage.clickConfirmDelete();
        sleep(1500);

        // Kiểm tra số đơn giảm đi 1
        int countAfter = ordersPage.getOrderCount();
        System.out.println("Số đơn sau khi xoá: " + countAfter);

        if (countBefore > 0) {
            Assert.assertEquals(countAfter, countBefore - 1,
                    "Tổng đơn hàng phải giảm đi 1 sau khi xoá");
        }
        System.out.println("TC01 PASSED: Xoá đơn hàng thành công");
    }

    @Test(description = "FE015-TC02: Modal xác nhận xoá hiển thị đúng nội dung")
    public void TC02_ConfirmDeleteModalContent() {
        // Nhấn icon xoá
        ordersPage.clickDeleteFirstOrder();

        // Kiểm tra modal hiển thị
        Assert.assertTrue(
                ordersPage.isConfirmDeleteModalVisible(),
                "Modal 'Xác nhận xoá đơn hàng' phải hiển thị"
        );

        // Kiểm tra nội dung cảnh báo
        String msg = ordersPage.getConfirmDeleteMessage();
        Assert.assertTrue(
                msg.contains("không thể hoàn tác") || msg.contains("vĩnh viễn"),
                "Nội dung modal phải có cảnh báo 'không thể hoàn tác'. Thực tế: " + msg
        );

        // Kiểm tra có đủ 2 nút Huỷ và Xoá
        Assert.assertTrue(
                ordersPage.isConfirmDeleteHasTwoButtons(),
                "Modal phải có đủ 2 nút: Huỷ và Xoá"
        );
        System.out.println("TC02 PASSED: Modal xác nhận xoá hiển thị đúng nội dung");
    }


    @Test(description = "FE015-TC03: Nhấn Huỷ trong modal – đơn hàng không bị xoá")
    public void TC03_CancelDelete() {
        int countBefore = ordersPage.getOrderCount();

        // Nhấn xoá
        ordersPage.clickDeleteFirstOrder();
        Assert.assertTrue(ordersPage.isConfirmDeleteModalVisible(), "Modal phải hiện");

        // Nhấn Huỷ
        ordersPage.clickCancelDelete();
        sleep(800);

        // Số đơn không đổi
        int countAfter = ordersPage.getOrderCount();
        Assert.assertEquals(countAfter, countBefore,
                "Số đơn hàng không được thay đổi khi nhấn Huỷ"
        );
        System.out.println("TC03 PASSED: Nhấn Huỷ, đơn hàng không bị xoá");
    }


    @Test(description = "FE015-TC04: Đóng modal bằng nút X – đơn hàng không bị xoá")
    public void TC04_CloseDeleteModalByX() {
        int countBefore = ordersPage.getOrderCount();

        ordersPage.clickDeleteFirstOrder();
        Assert.assertTrue(ordersPage.isConfirmDeleteModalVisible(), "Modal phải hiện");

        // Nhấn X đóng modal
        ordersPage.clickCloseDeleteModal();
        sleep(800);

        int countAfter = ordersPage.getOrderCount();
        Assert.assertEquals(countAfter, countBefore,
                "Số đơn hàng không được thay đổi khi đóng modal bằng X"
        );
        System.out.println("TC04 PASSED: Đóng modal X, đơn hàng không bị xoá");
    }


    @Test(description = "FE015-TC05: Danh sách và thống kê cập nhật đúng sau khi xoá")
    public void TC05_ListUpdatedAfterDelete() {
        // Lấy mã đơn hàng đầu tiên
        String firstOrderCode = "";
        try {
            firstOrderCode = driver.findElement(
                    By.xpath("(//a[starts-with(text(),'HD')])[1]")
            ).getText().trim();
            System.out.println("Đơn hàng sẽ xoá: " + firstOrderCode);
        } catch (Exception e) {
            System.out.println("Không lấy được mã đơn hàng");
        }

        int countBefore = ordersPage.getOrderCount();

        // Xoá đơn
        ordersPage.clickDeleteFirstOrder();
        ordersPage.clickConfirmDelete();
        sleep(1500);

        // Kiểm tra số đơn giảm
        int countAfter = ordersPage.getOrderCount();
        if (countBefore > 0) {
            Assert.assertEquals(countAfter, countBefore - 1,
                    "Tổng đơn hàng phải giảm đúng 1"
            );
        }

        // Kiểm tra đơn đã biến mất khỏi danh sách
        if (!firstOrderCode.isEmpty()) {
            Assert.assertFalse(
                    ordersPage.isOrderInList(firstOrderCode),
                    "Đơn " + firstOrderCode + " phải biến mất khỏi danh sách"
            );
        }
        System.out.println("TC05 PASSED: Danh sách và thống kê cập nhật đúng sau xoá");
    }


    @Test(description = "FE015-TC06: Xoá nhiều đơn hàng liên tiếp")
    public void TC06_DeleteMultipleOrders() {
        int countBefore = ordersPage.getOrderCount();
        System.out.println("Số đơn ban đầu: " + countBefore);

        // Xoá lần 1
        ordersPage.clickDeleteFirstOrder();
        Assert.assertTrue(ordersPage.isConfirmDeleteModalVisible(), "Modal lần 1 phải hiện");
        ordersPage.clickConfirmDelete();
        sleep(1500);

        int countAfter1 = ordersPage.getOrderCount();
        System.out.println("Sau xoá lần 1: " + countAfter1);

        // Xoá lần 2 (chỉ thực hiện nếu còn đơn)
        if (countAfter1 > 0) {
            ordersPage.clickDeleteFirstOrder();
            Assert.assertTrue(ordersPage.isConfirmDeleteModalVisible(), "Modal lần 2 phải hiện");
            ordersPage.clickConfirmDelete();
            sleep(1500);

            int countAfter2 = ordersPage.getOrderCount();
            System.out.println("Sau xoá lần 2: " + countAfter2);

            if (countBefore > 1) {
                Assert.assertEquals(countAfter2, countBefore - 2,
                        "Sau 2 lần xoá, tổng đơn phải giảm đúng 2"
                );
            }
        }
        System.out.println("TC06 PASSED: Xoá nhiều đơn hàng liên tiếp thành công");
    }
}
