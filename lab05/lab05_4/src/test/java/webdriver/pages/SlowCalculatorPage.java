package webdriver.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SlowCalculatorPage {

    private WebDriver driver;

    private By resultScreen = By.cssSelector("#result");
    private By delayField = By.id("delay");
    private By calculateButton = By.id("calculate");

    public SlowCalculatorPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver.getTitle().contains("Slow calculator");
    }

    public void setDelay(String value) {
        WebElement input = driver.findElement(delayField);
        input.clear();
        input.sendKeys(value);
    }

    public void clickCalculate() {
        driver.findElement(calculateButton).click();
    }

    public String getResult() {
        return driver.findElement(resultScreen).getText();
    }
}
