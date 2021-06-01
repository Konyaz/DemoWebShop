package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ShoppingCartTests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
        Configuration.startMaximized = true;
        Configuration.baseUrl = "http://demowebshop.tricentis.com";
    }

    @Test
    public void addItemToCartAsNewUserTest() {
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("product_attribute_74_5_26=81&product_attribute_74_6_27=83&product_attribute_74_3_28=86&addtocart_74.EnteredQuantity=1")
                .when()
                .post("/addproducttocart/details/74/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("success", is(true))
                .body("updatetopcartsectionhtml", is("(1)"));
    }

    @Test
    void addItemToCartAsExistingUserTest() {
        // request cart size
        Response response = given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("product_attribute_74_5_26=81&product_attribute_74_6_27=83&product_attribute_74_3_28=86&addtocart_74.EnteredQuantity=1")
                .cookie("ARRAffinity=55622bac41413dfac968dd8f036553a9415557909fd0cd3244e7e0e656e4adc8; NOPCOMMERCE.AUTH=B0F8E0FCD23BA4E3A92AB710F866A02B6DB080575EDAD34F7A6F465CE16DE0EE9DF0E41DB45BAB5EF1A942DECFE3A59BC836E40161195BD2A47F05D02C03790C19C23865AFB89EED99B05DF622871C8B08105FBDF4C2E5EE2F814674B4F184B95335C9EA15C49E71F561189340F7C04B6066E5EDEA7F006DB4BE57B58FB4F466ABE560435B8D70813CBAF62C75999B44; Nop.customer=ecd6dba6-fd86-4557-89cb-e01783f4948e; NopCommerce.RecentlyViewedProducts=RecentlyViewedProductIds=74&RecentlyViewedProductIds=16")
                .when()
                .post("/addproducttocart/details/74/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("success", is(true))
                .extract().response();

        String cartSizeInBrackets = response.jsonPath().get("updatetopcartsectionhtml");
        int cartSize = Integer.parseInt(cartSizeInBrackets.substring(1, cartSizeInBrackets.length() - 1));

        // expected cart size +=1
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("product_attribute_74_5_26=81&product_attribute_74_6_27=83&product_attribute_74_3_28=86&addtocart_74.EnteredQuantity=1")
                .cookie("ARRAffinity=55622bac41413dfac968dd8f036553a9415557909fd0cd3244e7e0e656e4adc8; NOPCOMMERCE.AUTH=B0F8E0FCD23BA4E3A92AB710F866A02B6DB080575EDAD34F7A6F465CE16DE0EE9DF0E41DB45BAB5EF1A942DECFE3A59BC836E40161195BD2A47F05D02C03790C19C23865AFB89EED99B05DF622871C8B08105FBDF4C2E5EE2F814674B4F184B95335C9EA15C49E71F561189340F7C04B6066E5EDEA7F006DB4BE57B58FB4F466ABE560435B8D70813CBAF62C75999B44; Nop.customer=ecd6dba6-fd86-4557-89cb-e01783f4948e; NopCommerce.RecentlyViewedProducts=RecentlyViewedProductIds=74&RecentlyViewedProductIds=16")
                .when()
                .post("/addproducttocart/details/74/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("success", is(true))
                .body("updatetopcartsectionhtml", is("(" + (cartSize + 1) + ")"));
    }

    @Test
    void addItemToCartAsExistingUserAndCheckOnUITest() {

            open("/build-your-own-expensive-computer-2");
            $("#add-to-cart-button-74").click();
            $("#topcartlink .ico-cart").shouldHave(text("(1)"));
            String nopCustomerCookie = WebDriverRunner.getWebDriver().manage().getCookieNamed("Nop.customer").getValue();

            given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("product_attribute_74_5_26=81&product_attribute_74_6_27=83&product_attribute_74_3_28=86&addtocart_74.EnteredQuantity=1")
                .cookie("Nop.customer", nopCustomerCookie)
                .when()
                .post("/addproducttocart/details/74/1")
                .then()
                .statusCode(200)
                .log().body()
                .body("success", is(true))
                .body("updatetopcartsectionhtml", is("(2)"));

        Selenide.refresh();
        $("#topcartlink a[href='/cart']").shouldHave(text("(2)"));
    }
}
