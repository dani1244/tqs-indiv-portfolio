package webdriver.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SlowCalculatorPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By screen = By.cssSelector("#result");
    private By calculateBtn = By.id("calculate");
    private By delayInput = By.id("delay");

    public SlowCalculatorPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("slow-calculator.html");
    }

    public void setDelay(int seconds) {
        WebElement delayField = wait.until(ExpectedConditions.visibilityOfElementLocated(delayInput));
        delayField.clear();
        delayField.sendKeys(String.valueOf(seconds));
    }

    public void pressNumber(int n) {
        driver.findElement(By.xpath("//span[text()='" + n + "']")).click();
    }

    public void pressOperator(String op) {
        driver.findElement(By.xpath("//span[text()='" + op + "']")).click();
    }

    public void pressEquals() {
        driver.findElement(calculateBtn).click();
    }

    public String waitForResult() {
        WebElement result = wait.until(ExpectedConditions.textToBePresentInElementLocated(screen, "="));
        return driver.findElement(screen).getText();
    }

    public String getResult() {
        return driver.findElement(screen).getText();
    }
}
