package saucedemo.com.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import saucedemo.com.properties.SauceDemoConfig;

/**
 * Checks the SauceDemo service over HTTP before a test seeds its client-side session cookie.
 */
public class SauceDemoLoginApi {
    public Response checkApplicationAvailable() {
        Response response = RestAssured.given()
                .baseUri(SauceDemoConfig.baseUrl())
                .get("/");
        if (response.statusCode() != 200) {
            throw new IllegalStateException("SauceDemo is unavailable (HTTP " + response.statusCode() + ")");
        }
        return response;
    }
}
