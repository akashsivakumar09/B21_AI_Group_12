Feature: Plant Management UI for User

  Background:
    Given the user has logged in
    And he navigates to the Plant page

  @UI-PLANTS-U-01 @ui @user @search @plant
  Scenario: Verify User can search plant by using name
    Given Plant "Tulip" records exist in the system
    When types the plant name "Tulip" in the search plant field
    And clicks the Search button
    Then the Plant table should contain "Tulip"

  @UI-PLANTS-U-02 @ui @user @filter @plant
  Scenario: Verify User can filter Plants by category
    Given category "Best" exists in the system
    When user selects category "Good" from the category dropdown
    And clicks the Search button
    Then all displayed plants should belong to category "Good"

  @UI-PLANTS-U-03 @ui @user @plant @sort
  Scenario: Verify Name column sorting toggles between ascending and descending
    Given more than one plant records exist in the system
    When the user clicks on the Name column header
    Then the Plant list should be sorted in "descending" alphabetical order by Name
    When the user clicks on the Name column header
    Then the Plant list should be sorted in "ascending" alphabetical order by Name

  @UI-PLANTS-U-04 @ui @user @stock @plant
  Scenario: Verify Display Low badge when quantity is below 5
    Given at least one plant record exists with a stock quantity below 5
    Then a "Low" badge should be displayed near the stock quantity number for plants with less than 5 stock

  @UI-PLANTS-U-05 @ui @user @plant @empty-state
  Scenario: Verify display No plants found message when no plants exist
    Given no plant records exist in the system
    Then the Plant list should display a "No plants found" message



