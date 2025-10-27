package webdriver;

import static java.lang.invoke.MethodHandles.lookup;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * LAB 5 - Exercício 5.3 - Interagir com a Slow Calculator
 * 
 * Teste completo que simula a interação com o formulário e valida o resultado.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SlowCalculatorTest {

    static final Logger log = getLogger(lookup().lookupClass());
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setupClass() {
        WebDriverManager.firefoxdriver().setup();
        log.info("FirefoxDriver configurado com WebDriverManager");
    }

    @BeforeEach
    void setup() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");  // mantém headless
        driver = new FirefoxDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        log.info("Firefox iniciado em modo headless");
    }

    @Test
    @DisplayName("5.3 - Testar cálculo 7 + 3 = 10")
    void testSlowCalculatorOperation() {
        String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/slow-calculator.html";
        driver.get(sutUrl);
        log.info("Acessando: {}", sutUrl);

        // Define o delay (campo de input)
        WebElement delayField = driver.findElement(By.id("delay"));
        delayField.clear();
        delayField.sendKeys("2"); // segundos
        log.info("⏱Delay configurado para 2 segundos");

        // Clica em 7 + 3 =
        driver.findElement(By.xpath("//span[text()='7']")).click();
        driver.findElement(By.xpath("//span[text()='+']")).click();
        driver.findElement(By.xpath("//span[text()='3']")).click();
        driver.findElement(By.xpath("//span[text()='=']")).click();
        log.info("Operação 7 + 3 = iniciada");

        // Espera o resultado final (10)
        WebElement screen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("screen")));
        wait.until(ExpectedConditions.textToBePresentInElement(screen, "10"));

        String result = screen.getText();
        log.info("Resultado final no ecrã: {}", result);

        // Validação
        assertThat(result).isEqualTo("10");
        log.info("Teste 5.3 PASSOU!");
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
            log.info("Firefox fechado");
        }
    }
}
