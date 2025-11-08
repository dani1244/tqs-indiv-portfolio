Feature: Online Bookstore Search
  As a bookstore customer
  I want to search and browse books online
  So that I can find books I'm interested in

  Background:
    Given I am on the bookstore homepage

  Scenario: View homepage content
    Then I should see books displayed on the homepage
    And the homepage should have a search bar

  Scenario: Search for books by keyword
    When I search for "Harry"
    Then I should see search results
    And the results should contain "Harry"

  Scenario: Browse books by category Fiction
    When I select the "Fiction" category
    Then I should see books from the Fiction category

  Scenario: Browse books by category Mystery
    When I select the "Mystery" category
    Then I should see books from the Mystery category

  Scenario: Browse books by category Horror
    When I select the "Horror" category
    Then I should see books from the Horror category