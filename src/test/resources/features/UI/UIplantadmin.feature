Feature: Plant Management UI for Admin

  Background:
    Given the admin has logged in
    And he navigates to the Plant page


  @UI-PLANTS-A-01 @ui @admin @plant
  Scenario: Verify Admin can see Add Plant Button
    Then the Add Plant button should be visible

  @UI-PLANTS-A-02 @ui @admin @plant
  Scenario: Verify Navigation to Add Plant page
    When the user clicks the Add Plant button
    Then the Add Plant page should be displayed

  @UI-PLANTS-A-03 @ui @admin @plant @validation
  Scenario: Verify Mandatory fields are filled before Adding Plant
    Given the user navigates to the Add Plant page
    When the user clicks the Save button without entering information
    Then validation error messages should be displayed for the required fields

  @UI-PLANTS-A-04 @ui @admin @plant @validation
  Scenario: Verify Quantity Field rejects negative values
    Given the user navigates to the Add Plant page
    When the user enters "-5" into the quantity field
    And clicks the Save button on the Add Plant form
    Then a validation error message "Quantity cannot be negative" should be displayed for the quantity field

  @UI-PLANTS-A-05 @ui @admin @plant @navigation
  Scenario: Verify Cancel button returns to plant list
    Given the user navigates to the Add Plant page
    When the user clicks the Cancel button on the Add Plant form
    Then the system should redirect to the Plant list page

  @UI-PLANTS-A-06 @ui @admin @plant @delete
    Scenario: Verify Admin can delete a plant
      # Assuming the Background already navigated to the plant page
      Given Plant "Rose" records exist in the system
      When the user clicks the delete icon for plant "Rose" and confirms the deletion
      Then the Plant table should not contain "Rose"

  @UI-PLANTS-A-07 @ui @admin @plant @update
    Scenario: Verify Update Plant Category via UI
      Given Plant "Anthurium" records exist in the system
      When the user clicks the Edit button for plant "Anthurium"
      And updates the Category to "SubRoses"
      And clicks the Save button on the Edit Plant form
      Then the system should redirect to the Plant list page
      And the Plant table should display "Anthurium" with the category "SubRoses"

