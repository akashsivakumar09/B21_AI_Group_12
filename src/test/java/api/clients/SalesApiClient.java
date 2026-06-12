package api.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class SalesApiClient {
    private final String baseUrl;
    private String token;

    public SalesApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private io.restassured.specification.RequestSpecification authorized() {
        return given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");
    }

    public Response getAllSales() {
        return authorized().when().get("/api/sales");
    }

    public Response getSalesPage() {
        return authorized()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .queryParam("sort", "soldAt,desc")
                .when()
                .get("/api/sales/page");
    }

    public Response getSaleById(int saleId) {
        return authorized()
                .pathParam("id", saleId)
                .when()
                .get("/api/sales/{id}");
    }

    public Response sellPlant(int plantId, int quantity) {
        return authorized()
                .pathParam("plantId", plantId)
                .queryParam("quantity", quantity)
                .when()
                .post("/api/sales/plant/{plantId}");
    }

    public Response deleteSale(int saleId) {
        return authorized()
                .pathParam("id", saleId)
                .when()
                .delete("/api/sales/{id}");
    }

    public Response sellPlantWithoutToken(int plantId, int quantity) {
        return given()
                .baseUri(baseUrl)
                .pathParam("plantId", plantId)
                .queryParam("quantity", quantity)
                .when()
                .post("/api/sales/plant/{plantId}");
    }

    public Response getAllSalesWithInvalidToken() {
        return given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer invalid.token.value")
                .when()
                .get("/api/sales");
    }
}
