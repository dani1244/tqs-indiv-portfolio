package library;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarySteps {
    
    private Library library;
    private List<Book> searchResults;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Custom ParameterType for dates
    @ParameterType("\\d{4}-\\d{2}-\\d{2}")
    public LocalDate iso8601Date(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    @Given("a library with the following books:")
    public void aLibraryWithTheFollowingBooks(DataTable dataTable) {
        library = new Library();
        
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            String title = row.get("title");
            String author = row.get("author");
            String category = row.get("category");
            LocalDate publishedDate = LocalDate.parse(row.get("publishedDate"), DATE_FORMATTER);
            
            Book book = new Book(title, author, category, publishedDate);
            library.addBook(book);
        }
    }

    @When("the customer searches for books by author {string}")
    public void theCustomerSearchesForBooksByAuthor(String author) {
        searchResults = library.searchByAuthor(author);
    }

    @When("the customer searches for books in category {string}")
    public void theCustomerSearchesForBooksInCategory(String category) {
        searchResults = library.searchByCategory(category);
    }

    @When("the customer searches for books with title containing {string}")
    public void theCustomerSearchesForBooksWithTitleContaining(String keyword) {
        searchResults = library.searchByTitle(keyword);
    }

    @When("the customer searches for books published between {iso8601Date} and {iso8601Date}")
    public void theCustomerSearchesForBooksPublishedBetween(LocalDate startDate, LocalDate endDate) {
        searchResults = library.searchByDateRange(startDate, endDate);
    }

    @Then("{int} book(s) should be found")
    public void booksShouldBeFound(int expectedCount) {
        assertEquals(expectedCount, searchResults.size());
    }

    @Then("the book title should be {string}")
    public void theBookTitleShouldBe(String expectedTitle) {
        assertFalse(searchResults.isEmpty(), "No books found");
        assertEquals(expectedTitle, searchResults.get(0).getTitle());
    }
}
