Feature: Plant Management API for Admin

  Background:
    Given the admin has logged in with valid credentials
    And the admin has a valid authorization token

    @API-PLANTS-A-01 @api @plant @admin @category
    Scenario: Verify Admin can retrieve Plants details using valid category ID
     Given a valid category ID exists in the system
     When the admin executes a GET request to "/api/plants/category/{categoryId}" using the valid category ID
     Then the HTTP status code should be 200
     And the response body should contain a list of plants with "name", "price", "quantity", and "category"

    @API-PLANTS-A-02 @api @plant @admin @update
     Scenario: Verify Update Plant Details with valid plant ID and valid Request body
        Given a valid plant ID exists in the system
        And a valid updated plant request body is prepared
        When the admin executes a PUT request to "/api/plants/{id}" to update plant details
        Then the HTTP status code should be 200
        And the response body should contain the updated plant details

     @API-PLANTS-A-03 @api @plant @admin @delete
       Scenario: Verify Delete Plant by ID with valid Plant ID
         Given a valid plant ID exists in the system for deletion
         When the admin executes a DELETE request to "/api/plants/{id}" with a valid plant ID
         Then the HTTP status code should be 204

     @API-PLANTS-A-04 @api @plant @admin @delete
       Scenario: Verify Delete Plant by ID with invalid Plant ID
         Given an invalid plant ID is prepared
         When the admin executes a DELETE request to "/api/plants/{id}" with the invalid plant ID
         Then the HTTP status code should be 404

     @API-PLANTS-A-05 @api @plant @admin @create
       Scenario: Verify Create a Plant Under sub-category with valid Category ID
         Given a valid category ID exists in the system
         And a valid new plant request body is prepared
         When the admin executes a POST request to "/api/plants/category/{categoryId}" to create a plant
         Then the HTTP status code should be 201
         And the response body should contain the created plant details

