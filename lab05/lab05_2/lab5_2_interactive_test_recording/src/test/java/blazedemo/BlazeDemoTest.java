package blazedemo;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.2 - Interactive Test Recording
 * 
 * This test class was initially recorded using Selenium IDE,
 * then exported to Java and refactored to:
 * - Use JUnit 5 instead of JUnit 4
 * - Integrate with Selenium-Jupiter for dependency injection
 * - Add explicit waits to handle dynamic content
 * - Replace fragile XPath locators with more robust selectors
 * - Add comprehensive assertions at key points
 * 
 * Test Flow:
 * 1. Navigate to BlazeDemo homepage
 * 2. Select departure city (Paris)
 * 3. Select destination city (London)
 * 4. Search for flights
 * 5. Select first available flight
 * 6. Fill passenger information form
 * 7. Complete purchase
 * 8. Verify confirmation page
 */
@ExtendWith(SeleniumJupiter.class)
class BlazeDemoTest {

    @Test
    @DisplayName("5.2 - Complete flight booking flow on BlazeDemo")
    void testCompleteFlightBooking(FirefoxDriver driver) {
        // Create WebDriverWait for handling dynamic content
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Exercise 5.2a - Step 1: Navigate to BlazeDemo homepage
        driver.get("https://blazedemo.com/");
        assertThat(driver.getTitle())
            .as("Homepage title should contain BlazeDemo")
            .contains("BlazeDemo");
        
        // Exercise 5.2a - Step 2: Select departure city (Paris)
        WebElement fromPort = driver.findElement(By.name("fromPort"));
        fromPort.click();
        Select fromPortSelect = new Select(fromPort);
        fromPortSelect.selectByValue("Paris");
        
        // Exercise 5.2a - Step 3: Select destination city (London)
        WebElement toPort = driver.findElement(By.name("toPort"));
        toPort.click();
        Select toPortSelect = new Select(toPort);
        toPortSelect.selectByValue("London");
        
        // Exercise 5.2a - Step 4: Submit search for flights
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        
        // Wait for flight results page to load
        wait.until(ExpectedConditions.titleContains("Flights"));
        assertThat(driver.getTitle())
            .as("Results page should show route")
            .contains("Flights from Paris to London");
        
        // Exercise 5.2a - Step 5: Select first available flight
        // Wait for the flight selection button to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("tr:nth-child(1) .btn-primary")));
        driver.findElement(By.cssSelector("tr:nth-child(1) .btn-primary")).click();
        
        // Wait for purchase form page to load
        wait.until(ExpectedConditions.titleContains("Purchase"));
        assertThat(driver.getTitle())
            .as("Purchase page should be loaded")
            .contains("BlazeDemo Purchase");
        
        // Exercise 5.2a - Step 6: Fill passenger information
        driver.findElement(By.id("inputName")).sendKeys("John Doe");
        driver.findElement(By.id("address")).sendKeys("123 Test Street");
        driver.findElement(By.id("city")).sendKeys("Test City");
        driver.findElement(By.id("state")).sendKeys("Test State");
        driver.findElement(By.id("zipCode")).sendKeys("12345");
        
        // Select credit card type
        Select cardTypeSelect = new Select(driver.findElement(By.id("cardType")));
        cardTypeSelect.selectByValue("visa");
        
        // Fill credit card information
        driver.findElement(By.id("creditCardNumber")).sendKeys("4111111111111111");
        driver.findElement(By.id("creditCardMonth")).clear();
        driver.findElement(By.id("creditCardMonth")).sendKeys("12");
        driver.findElement(By.id("creditCardYear")).clear();
        driver.findElement(By.id("creditCardYear")).sendKeys("2028");
        driver.findElement(By.id("nameOnCard")).sendKeys("John Doe");
        
        // Exercise 5.2a - Step 7: Submit purchase
        driver.findElement(By.cssSelector(".btn-primary")).click();
        
        // Exercise 5.2a - Step 8: Verify confirmation page
        wait.until(ExpectedConditions.titleContains("Confirmation"));
        
        // Exercise 5.2b - Manual assertion added in Selenium IDE
        assertThat(driver.getTitle())
            .as("Confirmation page title should match exactly")
            .isEqualTo("BlazeDemo Confirmation");
        
        // Additional verifications
        assertThat(driver.getCurrentUrl())
            .as("URL should contain confirmation")
            .contains("confirmation");
        
        WebElement confirmationHeader = driver.findElement(By.tagName("h1"));
        assertThat(confirmationHeader.getText())
            .as("Confirmation message should be displayed")
            .contains("Thank you for your purchase");
        
        System.out.println("✓ Flight booking completed successfully!");
    }
    
    /**
     * Exercise 5.2a - Additional test to verify form validation
     * This demonstrates breaking the test intentionally to verify assertions work
     */
    @Test
    @DisplayName("5.2 - Verify BlazeDemo homepage loads correctly")
    void testHomepageLoads(FirefoxDriver driver) {
        driver.get("https://blazedemo.com/");
        
        // Verify page elements are present
        assertThat(driver.findElement(By.name("fromPort")).isDisplayed())
            .as("Departure city dropdown should be visible")
            .isTrue();
        
        assertThat(driver.findElement(By.name("toPort")).isDisplayed())
            .as("Destination city dropdown should be visible")
            .isTrue();
        
        assertThat(driver.findElement(By.cssSelector("input[type='submit']")).isDisplayed())
            .as("Find Flights button should be visible")
            .isTrue();
        
        System.out.println("✓ Homepage elements verified successfully!");
    }
}