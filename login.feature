Feature: Login Functionality for Practice Test Automation

  Scenario: Successful login
    Given I navigate to the login page
    When I enter valid credentials
    Then I should be redirected to the success page
    And I should see a logout button

  Scenario: Unsuccessful login with invalid username
    Given I navigate to the login page
    When I enter invalid username
    And I enter valid password
    Then I should see an error message for invalid username

  Scenario: Unsuccessful login with invalid password
    Given I navigate to the login page
    When I enter valid username
    And I enter invalid password
    Then I should see an error message for invalid password