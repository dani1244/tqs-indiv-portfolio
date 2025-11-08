package bookstore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BookstoreSteps {
    
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private BookstorePage bookstorePage;

    @Before
    public void setUp() {
        playwright = Playwright.create();
        
        // Headless = false para ver o que está acontecendo (útil para debug)
        // Mude para true quando quiser execução silenciosa
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(300));  // Slow motion para ver ações
        
        context = browser.newContext();
        page = context.newPage();
        
        // Timeout aumentado para sites lentos
        page.setDefaultTimeout(60000);
        
        bookstorePage = new BookstorePage(page);
    }

    @After
    public void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Given("I am on the bookstore homepage")
    public void iAmOnTheBookstoreHomepage() {
        bookstorePage.navigateToHomepage();
    }

    @Then("I should see books displayed on the homepage")
    public void iShouldSeeBooksDisplayedOnTheHomepage() {
        assertTrue(bookstorePage.hasSearchResults(),
                "Expected to see books on homepage");
    }

    @Then("the homepage should have a search bar")
    public void theHomepageShouldHaveASearchBar() {
        int inputCount = page.locator("input").count();
        assertTrue(inputCount > 0, "Expected to find a search bar");
    }

    @When("I search for {string}")
    public void iSearchFor(String query) {
        bookstorePage.searchFor(query);
    }

    @Then("I should see search results")
    public void iShouldSeeSearchResults() {
        assertTrue(bookstorePage.hasSearchResults(), 
                "Expected to see search results");
    }

    @Then("the results should contain {string}")
    public void theResultsShouldContain(String keyword) {
        assertTrue(bookstorePage.resultsContainKeyword(keyword),
                "Expected results to contain: " + keyword);
    }

    @When("I select the {string} category")
    public void iSelectTheCategory(String category) {
        bookstorePage.selectCategory(category);
    }

    @Then("I should see books from the {word} category")
    public void iShouldSeeBooksFromTheCategory(String category) {
        assertTrue(bookstorePage.allBooksAreFromCategory(category),
                "Expected to see books from category: " + category);
    }
}