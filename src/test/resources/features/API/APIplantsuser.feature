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