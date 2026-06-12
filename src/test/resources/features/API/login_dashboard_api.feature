Feature: Login and Dashboard API

  @TC-LD-API-001 @loginDashboard @api @admin
  Scenario: Authenticate successfully as Admin
    When the login API is called with username "admin" and password "admin123"
    Then the login dashboard API status code should be 200
    And the response should contain a bearer token

  @TC-LD-API-002 @loginDashboard @api @user
  Scenario: Authenticate successfully as normal Test User
    When the login API is called with username "testuser" and password "test123"
    Then the login dashboard API status code should be 200
    And the response should contain a bearer token

  @TC-LD-API-003 @loginDashboard @api @negative
  Scenario: Reject invalid login credentials
    When the login API is called with username "wronguser" and password "wrongpass"
    Then the login dashboard API status code should be 401

  @TC-LD-API-004 @loginDashboard @api @admin
  Scenario: Admin can retrieve category summary
    Given the login dashboard API user is authenticated as admin
    When the login dashboard API user sends a GET request to "/api/categories/summary"
    Then the login dashboard API status code should be 200
    And the response should contain data

  @TC-LD-API-005 @loginDashboard @api @admin
  Scenario: Admin can retrieve plant summary
    Given the login dashboard API user is authenticated as admin
    When the login dashboard API user sends a GET request to "/api/plants/summary"
    Then the login dashboard API status code should be 200
    And the response should contain data

  @TC-LD-API-006 @loginDashboard @api @admin
  Scenario: Admin can retrieve sales data
    Given the login dashboard API user is authenticated as admin
    When the login dashboard API user sends a GET request to "/api/sales"
    Then the login dashboard API status code should be 200
    And the response should contain data

  @TC-LD-API-007 @loginDashboard @api @user
  Scenario: Test User can retrieve category summary
    Given the login dashboard API user is authenticated as user
    When the login dashboard API user sends a GET request to "/api/categories/summary"
    Then the login dashboard API status code should be 200
    And the response should contain data

  @TC-LD-API-008 @loginDashboard @api @user
  Scenario: Test User can retrieve plant summary
    Given the login dashboard API user is authenticated as user
    When the login dashboard API user sends a GET request to "/api/plants/summary"
    Then the login dashboard API status code should be 200
    And the response should contain data

  @TC-LD-API-009 @loginDashboard @api @user @authorization
  Scenario: Test User cannot perform Admin-only category creation
    Given the login dashboard API user is authenticated as user
    When the login dashboard API user tries to create a category
    Then the login dashboard API status code should be 403

  @TC-LD-API-010 @loginDashboard @api @security
  Scenario: Unauthorized access without or invalid token
    When protected dashboard related APIs are called without or with invalid token
    Then both unauthorized requests should return 401
