import static com.codeborne.selenide.Condition.href;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class DemoWebShopSearchTests {

    private Cookies cookies = null;

    @Test
    void recentlyViewedProductsTest() {

        step("Просматриваем товар 'Diamond Pave Earrings'", () -> {
            cookies = given()
                    .when()
                    .get("http://demowebshop.tricentis.com/diamond-pave-earrings")
                    .then()
                    .statusCode(200)
                    .extract().detailedCookies();
        });

        step("Просматриваем товар 'Sunglasses'", () -> {
            cookies = given()
                    .cookies(cookies)
                    .when()
                    .get("http://demowebshop.tricentis.com/sunglasses")
                    .then()
                    .statusCode(200)
                    .extract().detailedCookies();
        });

        step("Проверяем список просмотренных товаров на странице 'Recently viewed products'", () -> {
            Response response = given()
                    .cookies(cookies)
                    .when()
                    .get("http://demowebshop.tricentis.com/recentlyviewedproducts")
                    .then()
                    .statusCode(200)
                    .extract().response();
            assertThat(response.body().asString())
                    .contains("/diamond-pave-earrings")
                    .contains("/sunglasses");
        });

        step("Проверяем через UI список просмотренных товаров на странице 'Recently viewed products'", () -> {
            open("http://demowebshop.tricentis.com");
            for (Cookie cookie : cookies.asList()) {
                getWebDriver().manage().addCookie(toSeleniumCookie(cookie));
            }
            open("http://demowebshop.tricentis.com/recentlyviewedproducts/");
            $("h2.product-title a").shouldHave(href("/sunglasses"));
            $(".item-box").sibling(0).$("h2.product-title a").shouldHave(href("/diamond-pave-earrings"));
        });
    }

    private org.openqa.selenium.Cookie toSeleniumCookie(Cookie restCookie) {
        return new org.openqa.selenium.Cookie(
                restCookie.getName(),
                restCookie.getValue(),
                restCookie.getPath(),
                restCookie.getExpiryDate());
    }
}