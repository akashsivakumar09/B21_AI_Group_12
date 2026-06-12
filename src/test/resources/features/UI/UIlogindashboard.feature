Feature: Login and Dashboard UI

  @TC-LD-UI-001 @loginDashboard @ui @admin
  Scenario: Successful login as Admin
    When the login dashboard UI user logs in with username "admin" and password "admin123"
    Then the dashboard page should be visible

  @TC-LD-UI-002 @loginDashboard @ui @user
  Scenario: Successful login as normal Test User
    When the login dashboard UI user logs in with username "testuser" and password "test123"
    Then the dashboard page should be visible

  @TC-LD-UI-003 @loginDashboard @ui @negative
  Scenario: Unsuccessful login with incorrect credentials
    When the login dashboard UI user logs in with username "wronguser" and password "wrongpass"
    Then the invalid login message should be visible

  @TC-LD-UI-004 @loginDashboard @ui @validation
  Scenario: Validation errors for empty login fields
    When the login dashboard UI user submits empty login fields
    Then username and password validation messages should be visible

  @TC-LD-UI-005 @loginDashboard @ui @admin
  Scenario: Admin dashboard page loads correctly
    Given the login dashboard UI user logs in as admin
    Then the dashboard page should be visible
    And the dashboard sidebar navigation should be visible

  @TC-LD-UI-006 @loginDashboard @ui @admin
  Scenario: Admin dashboard module cards are visible
    Given the login dashboard UI user logs in as admin
    Then dashboard module areas should be visible

  @TC-LD-UI-007 @loginDashboard @ui @admin
  Scenario: Admin dashboard navigation links work
    Given the login dashboard UI user logs in as admin
    Then the dashboard sidebar navigation should be visible

  @TC-LD-UI-008 @loginDashboard @ui @user
  Scenario: Test User dashboard page loads correctly
    Given the login dashboard UI user logs in as user
    Then the dashboard page should be visible
    And the dashboard sidebar navigation should be visible

  @TC-LD-UI-009 @loginDashboard @ui @authorization
  Scenario: Test User cannot access Admin-only page directly
    Given the login dashboard UI user logs in as user
    When the login dashboard UI user opens admin-only pages directly
    Then admin-only direct access should be blocked

  @TC-LD-UI-010 @loginDashboard @ui @security
  Scenario: Unauthenticated user cannot access dashboard directly
    When an unauthenticated UI user opens the dashboard directly
    Then unauthenticated dashboard access should be blocked


  @BUG-011 @loginDashboard @ui @bug
  Scenario: Authenticated user should be redirected away from Login page
    Given the login dashboard UI user logs in as admin
    When the authenticated user opens the Login page again
    Then the authenticated user should be redirected to the Dashboard page

  @BUG-013 @loginDashboard @ui @bug
  Scenario: User dashboard card buttons should show read-only labels
    Given the login dashboard UI user logs in as user
    Then the user dashboard category and plant buttons should show read-only labels

  @BUG-012 @loginDashboard @ui @bug
  Scenario: Categories and Plants sidebar links should be highlighted on active pages
    Given the login dashboard UI user logs in as admin
    When the admin checks active sidebar highlighting on Categories and Plants pages
    Then the Categories and Plants sidebar links should be highlighted


