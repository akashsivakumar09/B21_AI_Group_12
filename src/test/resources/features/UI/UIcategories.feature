Feature: Category Management UI

  Background:
    Given the admin has logged in
    And he navigates to the Categories page

  @UI-CAT-01 @ui@admin @category
  Scenario: Verify Admin can see Add Category Button
    Then the Add Category button should be visible

  @UI-CAT-02 @ui @admin @category
  Scenario: Verify Navigation to Add Category page
    When the user clicks the Add Category button
    Then the Add Category page should be displayed

  @UI-CAT-03 @ui @admin @category @validation
  Scenario: Verify Admin can successfully create a new main category
    Given the user navigates to the Add Category page
    When the user enters category name "Cat[timestamp]"
    And clicks the Save button on the Add Category form
    Then the system should redirect to the Categories list page
    And the new category should be visible in the list

  @UI-CAT-04 @ui @admin @category @validation
  Scenario: Verify Admin can successfully create a new sub-category
    Given the user navigates to the Add Category page
    When the user enters category name "Sub[timestamp]"
    And selects parent category "Roses" from the parent dropdown
    And clicks the Save button on the Add Category form
    Then the system should redirect to the Categories list page
    And the new category should be visible in the list

  @UI-CAT-05 @ui @admin @category @validation
  Scenario: Verify Admin sees validation error for category name less than 3 characters
    Given the user navigates to the Add Category page
    When the user enters category name "Ab"
    And clicks the Save button on the Add Category form
    Then a validation error message "Category name must be between 3 and 10 characters" should be displayed for the name field

  @UI-CAT-06 @ui @admin @category @validation
  Scenario: Verify Admin sees validation error for category name greater than 10 characters
    Given the user navigates to the Add Category page
    When the user enters category name "VeryLongCategoryName"
    And clicks the Save button on the Add Category form
    Then a validation error message "Category name must be between 3 and 10 characters" should be displayed for the name field

  @UI-CAT-07 @ui @admin @category @validation
  Scenario: Verify Admin attempts to create a category with a duplicate name
    Given the user navigates to the Add Category page
    When the user enters category name "Roses"
    And clicks the Save button on the Add Category form
    Then a validation error message "Category already exists" should be displayed for the name field

  @UI-CAT-08 @ui @admin @category @navigation
  Scenario: Verify Admin cancels category creation
    Given the user navigates to the Add Category page
    When the user enters category name "CancelMe"
    And the user clicks the Cancel button on the Add Category form
    Then the system should redirect to the Categories list page

  @UI-CAT-09 @ui @admin @category
  Scenario: Verify Parent category dropdown contains main categories
    Given the user navigates to the Add Category page
    Then the parent category dropdown should contain "Roses"

  @UI-CAT-10 @ui @admin @category @validation @defect
  Scenario: Verify Admin sees only single validation error for empty category name
    Given the user navigates to the Add Category page
    When clicks the Save button on the Add Category form
    Then only a validation error message "Category name is required" should be displayed for the name field
