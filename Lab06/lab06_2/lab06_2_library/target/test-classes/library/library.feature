Feature: Library Book Search
  As a library user
  I want to search for books
  So that I can find books by different criteria

  Background:
    Given a library with the following books:
       title               author           category     publishedDate 
       Clean Code          Robert Martin    Programming  2008-08-01    
       The Pragmatic       Andy Hunt        Programming  1999-10-20    
       Design Patterns     Gang of Four     Programming  1994-10-21    
       Domain-Driven       Eric Evans       Architecture 2003-08-20    
       Refactoring         Martin Fowler    Programming  2018-11-20    

  Scenario: Search books by author
    When the customer searches for books by author "Robert Martin"
    Then 1 book should be found
    And the book title should be "Clean Code"

  Scenario: Search books by category
    When the customer searches for books in category "Programming"
    Then 4 books should be found

  Scenario: Search books by title keyword
    When the customer searches for books with title containing "Design"
    Then 1 book should be found

  Scenario: Search with no results
    When the customer searches for books by author "Unknown Author"
    Then 0 books should be found

  Scenario: Search books by date range
    When the customer searches for books published between 2000-01-01 and 2010-12-31
    Then 2 books should be found
