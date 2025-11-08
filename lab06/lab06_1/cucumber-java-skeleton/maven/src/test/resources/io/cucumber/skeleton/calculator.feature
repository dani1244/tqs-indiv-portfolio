Feature: RPN Calculator
  As a user of the RPN calculator
  I want to perform basic arithmetic operations
  So that I can calculate results using Reverse Polish Notation

  Scenario: Add two numbers
    Given an RPN calculator
    When I push 5
    And I push 3
    And I press add
    Then the result should be 8

  Scenario: Subtract two numbers
    Given an RPN calculator
    When I push 10
    And I push 4
    And I press subtract
    Then the result should be 6

  Scenario: Multiple operations
    Given an RPN calculator
    When I push 5
    And I push 3
    And I press add
    And I push 2
    And I press multiply
    Then the result should be 16

  Scenario: Division
    Given an RPN calculator
    When I push 20
    And I push 4
    And I press divide
    Then the result should be 5

  # NOVOS CENÁRIOS - Parte e)

  Scenario: Multiply two numbers
    Given an RPN calculator
    When I push 6
    And I push 7
    And I press multiply
    Then the result should be 42

  Scenario: Complex calculation with all operations
    Given an RPN calculator
    When I push 15
    And I push 7
    And I press add
    And I push 2
    And I press subtract
    And I push 3
    And I press multiply
    And I push 5
    And I press divide
    Then the result should be 12

  Scenario: Chain of additions
    Given an RPN calculator
    When I push 1
    And I push 2
    And I press add
    And I push 3
    And I press add
    And I push 4
    And I press add
    Then the result should be 10

  Scenario: Subtracting to get negative result
    Given an RPN calculator
    When I push 5
    And I push 10
    And I press subtract
    Then the result should be -5

  Scenario: Multiplying with zero
    Given an RPN calculator
    When I push 42
    And I push 0
    And I press multiply
    Then the result should be 0

  Scenario: Division resulting in integer truncation
    Given an RPN calculator
    When I push 7
    And I push 2
    And I press divide
    Then the result should be 3