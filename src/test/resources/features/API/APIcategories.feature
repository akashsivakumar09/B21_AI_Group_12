Feature: Category Management API

  @API-CAT-01 @api @category
  Scenario: Verify retrieving categories list as an authenticated user
    Given the API user has authenticated as "admin"
    When the API user sends a GET request to "/api/categories"
    Then the API response status code should be 200
    And the API response should contain a list of categories

  @API-CAT-02 @api @category
  Scenario: Verify creating a new category as Admin via API
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name "Cat[timestamp]"
    Then the API response status code should be 201
    And the API response should confirm the category was created

  @API-CAT-03 @api @category
  Scenario: Verify creating a new sub-category under a parent category as Admin via API
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name "Sub[timestamp]" and parent category ID 1
    Then the API response status code should be 201
    And the API response should confirm the sub-category was created

  @API-CAT-04 @api @category
  Scenario: Verify attempting to create a category as a normal Test User is forbidden
    Given the API user has authenticated as "testuser"
    When the API user sends a POST request to "/api/categories" with name "Ferns"
    Then the API response status code should be 403

  @API-CAT-05 @api @category
  Scenario: Verify attempting to retrieve categories without authentication is rejected
    When the API user sends an unauthenticated GET request to "/api/categories"
    Then the API response status code should be 401

  @API-CAT-06 @api @category
  Scenario: Verify Admin can retrieve categories summary statistics via API
    Given the API user has authenticated as "admin"
    When the API user sends a GET request to "/api/categories/summary"
    Then the API response status code should be 200
    And the API response should contain mainCategories and subCategories counts

  @API-CAT-07 @api @category
  Scenario: Verify User can retrieve categories summary statistics via API
    Given the API user has authenticated as "testuser"
    When the API user sends a GET request to "/api/categories/summary"
    Then the API response status code should be 200
    And the API response should contain mainCategories and subCategories counts

  @API-CAT-08 @api @category
  Scenario: Verify attempting to create a category with a blank name via API is rejected
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name ""
    Then the API response status code should be 400

  @API-CAT-09 @api @category
  Scenario: Verify attempting to create a category with an invalid name length is rejected
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name "Ab"
    Then the API response status code should be 400

  @API-CAT-10 @api @category
  Scenario: Verify attempting to create a duplicate category name is rejected
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name "Roses"
    Then the API response status code should be 400

  @API-CAT-11 @api @category @defect
  Scenario: Verify attempting to create a sub-category with nonexistent parent ID is rejected
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name "SubFake" and nonexistent parent category ID 99999
    Then the API response status code should be 400

  @API-CAT-12 @api @category @defect
  Scenario: Verify attempting to update a category with a duplicate name is rejected
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name "UD[timestamp]"
    Then the API response status code should be 201
    And the API response should confirm the category was created
    When the API user sends a PUT request to update the created category name to "Roses"
    Then the API response status code should be 400

  @API-CAT-13 @api @category @defect
  Scenario: Verify attempting to update a category with an invalid name length is rejected
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name "VN[timestamp]"
    Then the API response status code should be 201
    And the API response should confirm the category was created
    When the API user sends a PUT request to update the created category name to "Ab"
    Then the API response status code should be 400

  @API-CAT-14 @api @category @defect
  Scenario: Verify attempting to delete a main category with subcategories returns client error
    Given the API user has authenticated as "admin"
    When the API user sends a POST request to "/api/categories" with name "DM[timestamp]"
    Then the API response status code should be 201
    And the API response should confirm the category was created
    When the API user sends a POST request to create a sub-category under the created category
    Then the API response status code should be 201
    When the API user sends a DELETE request to delete the created category
    Then the API response status code should be 400

