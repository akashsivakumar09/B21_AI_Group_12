Feature: Plant Management API

  Background:
    Given the user has logged in with valid credentials
    And the user has a valid authorization token

  @API-PLANTS-U-01 @api @plant @user
  Scenario: Verify User can get a Plant using a valid Plant ID
    Given a valid plant ID exists in the system
    When the user executes a GET request to "/api/plants/{id}" using the valid plant ID
    Then the HTTP status code should be 200
    And the response body should contain "name", "price", and "quantity"

  @API-PLANTS-U-02 @api @plant @security
  Scenario: Verify getting a Plant with unauthorized access
     Given authorization information is not given in the header
     When the user executes a GET request to "/api/plants/1" without authorization
     Then the HTTP status code should be 401
     And the response body should contain a "UNAUTHORIZED" error message

  @API-PLANTS-U-03 @api @plant @user @category
    Scenario: Verify User can get Plants using a valid Category ID
      Given a valid category ID exists in the system
      When the user executes a GET request to "/api/plants/category/{categoryId}" using the valid category ID
      Then the HTTP status code should be 200
      And the response body should contain a list of plants with "name", "price", "quantity", and "category"

  @API-PLANTS-U-04 @api @plant @security @authorization
    Scenario: Verify User cannot create a plant (Admin-only operation)
      Given a valid category ID exists in the system
      And a valid plant request body is prepared
      When the user executes a POST request to "/api/plants/category/{categoryId}" to create a plant
      Then the HTTP status code should be 403
      And the response body should contain a "Forbidden" error message

  @API-PLANTS-U-05 @api @plant @user
    Scenario: Verify User can retrieve all plant details
      When the user executes a GET request to "/api/plants" to retrieve all plants
      Then the HTTP status code should be 200
      And the response body should contain a list of plants with "name", "price", "quantity", and "category"

  @API-PLANTS-U-06 @api @plant @summary
    Scenario: Verify User can retrieve plant summary
      When the user executes a GET request to "/api/plants/summary" to retrieve the plant summary
      Then the HTTP status code should be 200
      And the response body should contain the summary fields "totalPlants" and "lowStockPlants"
