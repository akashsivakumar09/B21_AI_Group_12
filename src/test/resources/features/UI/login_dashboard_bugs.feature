Feature: Login and Dashboard Known UI Bugs

  @BUG-011 @loginDashboardBug @ui @bug
  Scenario: Authenticated user should be redirected away from Login page
    Given the login dashboard UI user logs in as admin
    When the authenticated user opens the Login page again
    Then the authenticated user should be redirected to the Dashboard page

  @BUG-013 @loginDashboardBug @ui @bug
  Scenario: User dashboard card buttons should show read-only labels
    Given the login dashboard UI user logs in as user
    Then the user dashboard category and plant buttons should show read-only labels

  @BUG-012 @loginDashboardBug @ui @bug
  Scenario: Categories and Plants sidebar links should be highlighted on active pages
    Given the login dashboard UI user logs in as admin
    When the admin checks active sidebar highlighting on Categories and Plants pages
    Then the Categories and Plants sidebar links should be highlighted
