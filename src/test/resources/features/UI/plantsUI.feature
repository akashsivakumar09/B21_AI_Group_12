Feature: Plant Management UI

  Background:
    Given the user has logged in
    And the user navigates to the Plant page

  @UI-PLANTS-U-01 @user @search @plant
  Scenario: Verify User can search plant by using name
    Given Plant "Rose" records exist in the system
    When types the plant name "Rose" in the search plant field
    And clicks the Search button
    Then the Plant table should contain "Rose"


  @UI-PLANTS-U-02 @user @filter @plant
  Scenario: Verify User can filter Plants by category
    Given category "Best" exists in the system
    When user selects category "Best" from the category dropdown
    And clicks the Search button
    Then all displayed plants should belong to category "Best"

  @UI-PLANTS-U-04 @user @stock @plant
  Scenario: Verify Display Low badge when quantity is below 5
    Given at least one plant record exists with a stock quantity below 5
    Then a "Low" badge should be displayed near the stock quantity number for plants with less than 5 stock

  @UI-PLANTS-U-05 @user @plant @empty-state
  Scenario: Verify display No plants found message when no plants exist
    Given no plant records exist in the system
    Then the Plant list should display a "No plants found" message