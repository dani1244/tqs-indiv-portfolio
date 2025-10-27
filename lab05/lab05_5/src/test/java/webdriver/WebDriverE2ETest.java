package webdriver;

import webdriver.pages.HomePage;
import webdriver.pages.SlowCalculatorPage;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * LAB 5.5 - End-to-End Test (E2E)
 * Interação completa com a página Slow Calculator
 */
@DisplayName("Lab 5.5 - E2E test on Slow Calculator")
public class WebDriverE2ETest {

    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setup() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        driver = new FirefoxDriver(options);
    }

    @Test
    @DisplayName("Executa um cálculo completo: 7 + 3 = 10")
    void testFullCalculationFlow() {
        HomePage homePage = new HomePage(driver);
        homePage.open();

        SlowCalculatorPage calcPage = homePage.clickSlowCalculator();
        assertThat(calcPage.isLoaded()).isTrue();

        calcPage.setDelay(2);
        calcPage.pressNumber(7);
        calcPage.pressOperator("+");
        calcPage.pressNumber(3);
        calcPage.pressEquals();

        // Esperar o resultado aparecer
        String result = calcPage.getResult();
        assertThat(result).contains("10");
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
