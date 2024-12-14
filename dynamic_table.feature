Feature: Dynamic Table Validation

  Scenario: Validate table data
    Given I navigate to the DataTables example page
    When I filter the table by "Tiger"
    Then I should see "Tiger" in the table results
    And I should see the total number of entries is 1
    