package io.cucumber.skeleton;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorSteps {
    
    private RPNCalculator calculator;
    
    @Given("an RPN calculator")
    public void anRPNCalculator() {
        calculator = new RPNCalculator();
    }
    
    @When("I push {int}")
    public void iPush(int number) {
        calculator.push(number);
    }
    
    @When("I press add")
    public void iPressAdd() {
        calculator.add();
    }
    
    @When("I press subtract")
    public void iPressSubtract() {
        calculator.subtract();
    }
    
    @When("I press multiply")
    public void iPressMultiply() {
        calculator.multiply();
    }
    
    @When("I press divide")
    public void iPressDivide() {
        calculator.divide();
    }
    
    @Then("the result should be {int}")
    public void theResultShouldBe(int expectedResult) {
        assertEquals(expectedResult, calculator.getResult());
    }
}