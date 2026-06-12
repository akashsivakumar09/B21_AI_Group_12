Feature: Sales Management API

  @TC-API-SALES-001 @api @sales @admin
  Scenario: Admin retrieves all sales
    Given the admin API token is available
    And an existing sale record is available for sales API testing
    When the admin retrieves all sales through API
    Then the API status code should be 200
    And the sales response should be a list

  @TC-API-SALES-002 @api @sales @admin
  Scenario: Admin retrieves paginated sales
    Given the admin API token is available
    And an existing sale record is available for sales API testing
    When the admin retrieves paginated sales through API
    Then the API status code should be 200
    And the sales page response should contain pagination details

  @TC-API-SALES-003 @api @sales @admin
  Scenario: Admin successfully records a sale
    Given the admin API token is available
    And a plant with stock exists for sales API testing
    When the admin records a sale through API with quantity 2
    Then the API status code should be 201
    And the sale should be recorded and plant stock should be reduced by 2

  @TC-API-SALES-004 @api @sales @admin @negative
  Scenario: Sales API rejects invalid sale requests
    Given the admin API token is available
    And a plant with stock exists for sales API testing
    When the admin sends invalid sales API requests
    Then the invalid sales API responses should be rejected

  @TC-API-SALES-005 @api @sales @admin
  Scenario: Admin successfully deletes a sale
    Given the admin API token is available
    And an existing sale record is available for sales API testing
    When the admin deletes the sale through API
    Then the API status code should be 204
    And the deleted sale should not be retrievable through API

  @TC-API-SALES-006 @api @sales @user
  Scenario: Test User retrieves all sales
    Given the user API token is available
    When the user retrieves all sales through API
    Then the API status code should be 200
    And the sales response should be a list

  @TC-API-SALES-007 @api @sales @user
  Scenario: Test User retrieves paginated sales
    Given the user API token is available
    And an existing sale record is available for sales API testing
    When the user retrieves paginated sales through API
    Then the API status code should be 200
    And the sales page response should contain pagination details

  @TC-API-SALES-008 @api @sales @user
  Scenario: Test User retrieves sale by ID
    Given the user API token is available
    And an existing sale record is available for sales API testing
    When the user retrieves the sale by ID through API
    Then the API status code should be 200
    And the sale response should contain the requested sale ID

  @TC-API-SALES-009 @api @sales @user @authorization
  Scenario: Test User should not be able to create sale
    Given the user API token is available
    And a plant with stock exists for sales API testing
    When the user tries to record a sale through API
    Then the user sale action should be forbidden

  @TC-API-SALES-010 @api @sales @user @authorization
  Scenario: Test User should not be able to delete sale
    Given the user API token is available
    And an existing sale record is available for sales API testing
    When the user tries to delete the sale through API
    Then the user sale action should be forbidden

  @TC-API-SALES-011 @BUG-SALES-009 @api @sales @admin @sorting @bug
  Scenario: Invalid or unsupported Sales API sort field returns internal server error
    Given the admin API token is available
    When the admin requests paginated sales using invalid sort field "badField"
    Then the invalid sales sort request should return bad request
