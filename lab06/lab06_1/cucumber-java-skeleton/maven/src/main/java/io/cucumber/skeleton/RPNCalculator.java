package io.cucumber.skeleton;

import java.util.Stack;

public class RPNCalculator {
    private Stack<Integer> stack;

    public RPNCalculator() {
        this.stack = new Stack<>();
    }

    public void push(int number) {
        stack.push(number);
    }

    public void add() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough operands");
        }
        int b = stack.pop();
        int a = stack.pop();
        stack.push(a + b);
    }

    public void subtract() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough operands");
        }
        int b = stack.pop();
        int a = stack.pop();
        stack.push(a - b);
    }

    public void multiply() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough operands");
        }
        int b = stack.pop();
        int a = stack.pop();
        stack.push(a * b);
    }

    public void divide() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough operands");
        }
        int b = stack.pop();
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        int a = stack.pop();
        stack.push(a / b);
    }

    public int getResult() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack.peek();
    }

    public void clear() {
        stack.clear();
    }
}