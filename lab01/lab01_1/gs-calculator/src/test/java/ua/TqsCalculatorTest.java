package ua;

import static java.lang.invoke.MethodHandles.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;

class TqsCalculatorTest {

    // Don't use System.out.println, use a logger instead
    static final Logger log = org.slf4j.LoggerFactory.getLogger(lookup().lookupClass());

    @org.junit.jupiter.api.Test
    void add() {
        TqsCalculator t = new TqsCalculator();
        log.debug("Testing sum method in {}", t.getClass().getName());

        assertEquals(5, t.add(2, 3));
        assertEquals(-1, t.add(2, -3));
    }

    @org.junit.jupiter.api.Test
    void subtract() {
        TqsCalculator t = new TqsCalculator();
        log.debug("Testing subtract method in {}", t.getClass().getName());

        assertEquals(-1, t.subtract(2, 3));
        assertEquals(5, t.subtract(2, -3));
    }

    @DisplayName("Multiplies two numbers and returns the product")
    @org.junit.jupiter.api.Test
    void multiply() {
        TqsCalculator t = new TqsCalculator();
        assertEquals(6, t.multiply(2, 3));
        assertEquals(-6, t.multiply(2, -3));
        assertEquals(0, t.multiply(2, 0));
    }

    @DisplayName("Sqrt a number and returns the result")
    @org.junit.jupiter.api.Test
    void sqrt() {
        TqsCalculator t = new TqsCalculator();
        assertEquals(2, t.sqrt(4));
       
    }

    @DisplayName("Sqrt a number and returns the result")
    @org.junit.jupiter.api.Test
    void exp() {
        TqsCalculator t = new TqsCalculator();
        assertEquals(16, t.exp(4,2));
       
    }

    @DisplayName("Divides two numbers and returns the quotient, excludes division by zero")
    @org.junit.jupiter.api.Test
    void divide() {
        TqsCalculator t = new TqsCalculator();
        assertEquals(2, t.divide(6, 3));
        assertEquals(-2, t.divide(6, -3));

        assertThrows(IllegalArgumentException.class, () -> t.divide(2, 0));
    }
}