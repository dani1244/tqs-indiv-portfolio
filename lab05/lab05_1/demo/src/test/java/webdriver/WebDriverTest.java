package webdriver;

import static java.lang.invoke.MethodHandles.lookup;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * LAB 5 - Exercício 5.1 - Testes com WebDriver usando Firefox
 * 
 * Este ficheiro contém TODOS os testes do exercício 5.1:
 * - Parte a) e b): Testes com setup manual (Traditional approach)
 * - Parte c): Testes com Selenium-Jupiter (Dependency Injection)
 */
class WebDriverTest {

    static final Logger log = getLogger(lookup().lookupClass());

    
    // EXERCÍCIO 5.1a e 5.1b - Abordagem tradicional com setup manual
    
    
    @Nested
    @DisplayName("5.1a/b - Traditional WebDriver approach with Firefox")
    class TraditionalApproachTests {
        
        private WebDriver driver;
        private WebDriverWait wait;

        @BeforeAll
        static void setupClass() {
            WebDriverManager.firefoxdriver().setup();
            log.info("WebDriverManager (Firefox) configurado");
        }

        @BeforeEach
        void setup() {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless");
            options.addPreference("browser.startup.homepage_override.mstone", "ignore");
            options.addPreference("startup.homepage_welcome_url.additional", "about:blank");
            
            log.info("Iniciando Firefox em modo headless (traditional approach)...");
            driver = new FirefoxDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            log.info("Firefox iniciado com sucesso!");
        }

        @Test
        @DisplayName("5.1a - Hello World test")
        void testHelloWorld() {
            // Exercise
            String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
            log.info("Navegando para: {}", sutUrl);
            driver.get(sutUrl);
            
            String title = driver.getTitle();
            log.debug("The title of {} is {}", sutUrl, title);
            log.info("Título da página: {}", title);

            // Verify
            assertThat(title).isEqualTo("Hands-On Selenium WebDriver with Java");
            
            log.info("Teste 5.1a PASSOU!");
        }

        @Test
        @DisplayName("5.1b - Navigate to Slow Calculator")
        void testNavigateToSlowCalculator() {
            String baseUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
            
            // 1. Navega para a página inicial
            log.info("Navegando para página inicial...");
            driver.get(baseUrl);
            log.debug("Navigated to: {}", driver.getCurrentUrl());

            // 2. Encontra e clica no link "Slow calculator"
            log.info("Procurando link 'Slow calculator'...");
            WebElement slowCalcLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Slow calculator"))
            );
            
            log.info("Clicando em 'Slow calculator'...");
            slowCalcLink.click();
            log.debug("Clicked on 'Slow calculator' link");

            // 3. Espera que a URL mude
            wait.until(ExpectedConditions.urlContains("slow-calculator.html"));

            // 4. Verifica a URL e o título
            String currentUrl = driver.getCurrentUrl();
            log.debug("Current URL: {}", currentUrl);
            assertThat(currentUrl).contains("slow-calculator.html");

            String pageTitle = driver.getTitle();
            log.debug("Page title: {}", pageTitle);
            assertThat(pageTitle).contains("Slow calculator");
            
            log.info("URL após clique: {}", currentUrl);
            log.info("Título da página: {}", pageTitle);
            log.info("Teste 5.1b PASSOU!");
        }

        @AfterEach
        void teardown() {
            if (driver != null) {
                log.info("Fechando Firefox...");
                driver.quit();
                log.info("Firefox fechado");
            }
        }
    }

    
    // EXERCÍCIO 5.1c - Abordagem com Selenium-Jupiter
    
    
    @Nested
    @ExtendWith(SeleniumJupiter.class)
    @DisplayName("5.1c - Selenium-Jupiter approach (Dependency Injection) with Firefox")
    class SeleniumJupiterApproachTests {

        @Test
        @DisplayName("5.1c - Hello World with Jupiter")
        void testHelloWorldWithJupiter(FirefoxDriver driver) {
            log.info("Firefox iniciado (via Jupiter - dependency injection)");
            
            // Exercise
            String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
            log.info("Navegando para: {}", sutUrl);
            driver.get(sutUrl);
            
            String title = driver.getTitle();
            log.debug("The title of {} is {}", sutUrl, title);
            log.info("Título da página: {}", title);

            // Verify
            assertThat(title).isEqualTo("Hands-On Selenium WebDriver with Java");
            
            log.info("Teste 5.1c (Jupiter #1) PASSOU!");
            // Nota: NÃO é preciso fazer driver.quit() - Jupiter trata disso automaticamente!
        }

        @Test
        @DisplayName("5.1c - Navigate to Slow Calculator with Jupiter")
        void testNavigateToSlowCalculatorWithJupiter(FirefoxDriver driver) {
            log.info("Firefox iniciado (via Jupiter - dependency injection)");
            
            // Configura espera explícita
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            String baseUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
            log.info("Navegando para página inicial...");
            driver.get(baseUrl);
            log.debug("Navigated to: {}", driver.getCurrentUrl());

            log.info("Procurando link 'Slow calculator'...");
            WebElement slowCalcLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Slow calculator"))
            );
            
            log.info("Clicando em 'Slow calculator'...");
            slowCalcLink.click();
            log.debug("Clicked on 'Slow calculator' link");

            // Espera que a URL mude
            wait.until(ExpectedConditions.urlContains("slow-calculator.html"));

            String currentUrl = driver.getCurrentUrl();
            log.debug("Current URL: {}", currentUrl);
            assertThat(currentUrl).contains("slow-calculator.html");

            String pageTitle = driver.getTitle();
            log.debug("Page title: {}", pageTitle);
            assertThat(pageTitle).contains("Slow calculator");
            
            log.info("URL após clique: {}", currentUrl);
            log.info("Título da página: {}", pageTitle);
            log.info("Teste 5.1c (Jupiter #2) PASSOU!");
            
        }
    }
}