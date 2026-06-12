Feature: Sales Management UI

  @TC-SALES-UI-001 @ui @sales @admin
  Scenario: Admin can access Sales List page
    Given the admin has logged in to the UI
    And sales records exist for UI testing
    When the admin opens the Sales list page
    Then the Sales list should be displayed with sorting and pagination

  @TC-SALES-UI-002 @ui @sales @admin
  Scenario: Admin can see Sell Plant button and open Sell Plant page
    Given the admin has logged in to the UI
    And a plant with stock exists for UI sale
    When the admin opens the Sell Plant page
    Then the Sell Plant button and form should be available to admin

  @TC-SALES-UI-003 @ui @sales @admin @validation
  Scenario: Admin sees validation errors on Sell Plant form
    Given the admin has logged in to the UI
    When the admin submits invalid Sell Plant form values
    Then the Sell Plant validation messages should be displayed

  @TC-SALES-UI-004 @ui @sales @admin
  Scenario: Admin successfully records a new sale
    Given the admin has logged in to the UI
    And a plant with stock exists for UI sale
    When the admin sells the plant with quantity 1
    Then the sale should appear in the UI and stock should be reduced by 1

  @TC-SALES-UI-005 @ui @sales @admin @delete
  Scenario: Admin can delete sale only after confirmation
    Given the admin has logged in to the UI
    And a sale record exists for UI deletion
    When the admin cancels and then confirms sale deletion
    Then the sale should be removed from the UI

  @TC-SALES-UI-006 @ui @sales @user
  Scenario: Test User can access Sales List page
    Given the user has logged in to the UI
    And sales records exist for UI testing
    When the user opens the Sales list page
    Then the user should be able to view the Sales list

  @TC-SALES-UI-007 @ui @sales @user
  Scenario: Test User cannot see Sell Plant button
    Given the user has logged in to the UI
    And sales records exist for UI testing
    Then the Sell Plant button should not be visible to the user

  @TC-SALES-UI-008 @ui @sales @user @authorization
  Scenario: Test User is forbidden from accessing Sell Plant page directly
    Given the user has logged in to the UI
    When the user directly opens the Sell Plant URL
    Then the user should be blocked from the Sell Plant page

  @TC-SALES-UI-009 @ui @sales @user
  Scenario: Test User cannot see Delete action
    Given the user has logged in to the UI
    And sales records exist for UI testing
    When the user opens the Sales list page
    Then the Delete action should not be visible to the user

  @TC-SALES-UI-010 @ui @sales @user
  Scenario: Test User can use Sales list sorting and pagination
    Given the user has logged in to the UI
    And sales records exist for UI testing
    When the user opens the Sales list page
    And the user opens sorted Sales pages
    Then the user should be able to view sorting and pagination
