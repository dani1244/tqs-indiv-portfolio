package webdriver.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage {

    private WebDriver driver;

    private By slowCalculatorLink = By.linkText("Slow calculator");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
    }

    public SlowCalculatorPage clickSlowCalculator() {
        WebElement link = driver.findElement(slowCalculatorLink);
        link.click();
        return new SlowCalculatorPage(driver);
    }

    public String getTitle() {
        return driver.getTitle();
    }
}