Feature: Create Waste Collection Booking
  As a citizen
  I want to create a waste collection booking
  So that I can schedule pickup of bulky items

  Background:
    Given the waste collection system is available
    And the following municipalities are available:
      | Aveiro |
      | Porto  |
      | Lisboa |

  Scenario: Successfully create a booking with valid data
    Given I am on the citizen booking page
    When I fill in the booking form with:
      | municipality    | Aveiro                                  |
      | itemDescription | Old sofa and two chairs                 |
      | collectionDate  | 5 days from now                         |
      | timeSlot        | MORNING                                 |
      | address         | Rua de Aveiro, 123, 3810-123 Aveiro    |
      | contactEmail    | citizen@example.com                     |
      | contactPhone    | 912345678                               |
      | numberOfItems   | 2                                       |
    And I submit the booking form
    Then the booking should be created successfully
    And I should see a success message
    And I should receive an access token
    And the booking status should be "RECEIVED"

  Scenario: Fail to create booking with invalid municipality
    Given I am on the citizen booking page
    When I fill in the booking form with:
      | municipality    | InvalidCity                             |
      | itemDescription | Old furniture                           |
      | collectionDate  | 5 days from now                         |
      | timeSlot        | MORNING                                 |
      | address         | Test Address, 123                       |
      | numberOfItems   | 1                                       |
    And I submit the booking form
    Then the booking should not be created
    And I should see an error message containing "Invalid municipality"

  Scenario: Fail to create booking with date too soon
    Given I am on the citizen booking page
    When I fill in the booking form with:
      | municipality    | Aveiro                                  |
      | itemDescription | Old furniture items                     |
      | collectionDate  | tomorrow                                |
      | timeSlot        | MORNING                                 |
      | address         | Test Address, 123                       |
      | numberOfItems   | 1                                       |
    And I submit the booking form
    Then the booking should not be created
    And I should see an error message containing "at least 2 days"

  Scenario: Fail to create booking with too many items
    Given I am on the citizen booking page
    When I fill in the booking form with:
      | municipality    | Aveiro                                  |
      | itemDescription | Many old furniture items                |
      | collectionDate  | 5 days from now                         |
      | timeSlot        | AFTERNOON                               |
      | address         | Test Address, 123                       |
      | numberOfItems   | 10                                      |
    And I submit the booking form
    Then the booking should not be created
    And I should see a validation error for "numberOfItems"

  Scenario: Fail to create booking with short description
    Given I am on the citizen booking page
    When I fill in the booking form with:
      | municipality    | Aveiro                                  |
      | itemDescription | Short                                   |
      | collectionDate  | 5 days from now                         |
      | timeSlot        | MORNING                                 |
      | address         | Test Address, 123                       |
      | numberOfItems   | 1                                       |
    And I submit the booking form
    Then the booking should not be created
    And I should see a validation error for "itemDescription"

  Scenario: Successfully query booking with valid token
    Given I have created a booking
    And I have the access token
    When I query the booking with my token
    Then I should see my booking details
    And the booking status should be "RECEIVED"
    And I should see the status history

  Scenario: Fail to query booking with invalid token
    Given I am on the citizen booking page
    When I query the booking with token "invalid-token-12345"
    Then I should see an error message containing "not found"

  Scenario: Successfully cancel a booking
    Given I have created a booking
    And the booking status is "RECEIVED"
    And I have the access token
    When I cancel the booking with my token
    Then the booking should be cancelled successfully
    And the booking status should be "CANCELLED"

  Scenario: Fail to cancel a completed booking
    Given I have created a booking
    And the booking status is "COMPLETED"
    When I try to cancel the booking
    Then the cancellation should fail
    And I should see an error message containing "Cannot cancel"