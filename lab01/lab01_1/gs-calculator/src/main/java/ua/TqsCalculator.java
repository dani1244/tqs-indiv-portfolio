package ua;

/**
 * Basic calculator class with methods for addition, subtraction, multiplication, and division.
 */
public class TqsCalculator {

    public double add(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }

    public double multiply(double a, double b) {
        return a * b;
    }

    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
        return a / b;
    }
  
    public double sqrt(double a) {
    if (a < 0) {
        throw new IllegalArgumentException("Square root of a negative number is not allowed.");
    }
    return Math.sqrt(a);
    }


    public double exp(double a, double exp) {
    if (a < 0 && exp % 1 != 0) {
        throw new IllegalArgumentException("Base negativa com expoente não inteiro não é permitida.");
    }
    return Math.pow(a, exp);
    }

}
