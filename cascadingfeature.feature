Feature: Cascading Drop down Test

  Scenario: Select a country and verify the city drop  down updates
    Given I navigate to the W3Schools drop down example page
    When I select "Germany" from the country drop down
    Then I should see "Berlin" in the city drop down