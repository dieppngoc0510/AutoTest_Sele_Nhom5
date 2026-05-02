package org.example.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

public class ApiSearchTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://127.0.0.1:8000";
    }

    @Test(description = "FE05-API01 - Tìm kiếm API theo từ khóa")
    public void FE05_API01_SearchProductsByKeyword() {
        // "sơ mi" 
        String searchKeyword = "s\u01a1 mi"; 
        
        Response response = given()
                .queryParam("search", searchKeyword)
                .when()
                .get("/api/products/")
                .then()
                .statusCode(200)
                .extract().response();

        List<String> productNames = response.jsonPath().getList("name");
        
        Assert.assertFalse(productNames.isEmpty(), "BUG API: Không tìm thấy sản phẩm nào với từ khóa '" + searchKeyword + "'");
        
        for (String name : productNames) {
            Assert.assertTrue(name.toLowerCase().contains(searchKeyword.toLowerCase()), 
                "BUG: Sản phẩm '" + name + "' không chứa từ khóa '" + searchKeyword + "'");
        }
    }

    @Test(description = "FE05-API02 - Tìm kiếm API kết hợp lọc danh mục")
    public void FE05_API02_SearchAndFilterCategory() {
        // "áo"
        String searchKeyword = "\u00e1o"; 
        int targetCategoryId = 2;

        Response response = given()
                .queryParam("search", searchKeyword)
                .queryParam("category", targetCategoryId)
                .when()
                .get("/api/products/")
                .then()
                .statusCode(200)
                .extract().response();

        List<Integer> categoryIds = response.jsonPath().getList("category_id");
        
        Assert.assertFalse(categoryIds.isEmpty(), "Không có sản phẩm nào thỏa mãn điều kiện lọc với từ khóa '" + searchKeyword + "' và category_id=" + targetCategoryId);

        for (Integer catId : categoryIds) {
            Assert.assertEquals(catId.intValue(), targetCategoryId, 
                "BUG: API trả về sản phẩm thuộc category_id=" + catId + " trong khi đang lọc id=" + targetCategoryId);
        }
    }
}