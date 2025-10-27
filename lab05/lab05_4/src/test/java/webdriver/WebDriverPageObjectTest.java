package webdriver;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import webdriver.pages.HomePage;
import webdriver.pages.SlowCalculatorPage;

@ExtendWith(SeleniumJupiter.class)
class WebDriverPageObjectTest {

    @Test
    @DisplayName("5.4 - Navegar usando Page Object Pattern")
    void testWithPageObjects(FirefoxDriver driver) {
        // Arrange
        HomePage home = new HomePage(driver);
        home.open();
        assertThat(home.getTitle()).isEqualTo("Hands-On Selenium WebDriver with Java");

        // Act
        SlowCalculatorPage calcPage = home.clickSlowCalculator();

        // Wait until page is loaded
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("slow-calculator.html"));

        // Assert
        assertThat(calcPage.isLoaded()).isTrue();
    }
}
