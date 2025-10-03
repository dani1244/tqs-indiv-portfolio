package simple.Tqs;



import java.util.LinkedList;
import java.util.NoSuchElementException;

public class TqsStack<T> {

    private LinkedList<T> stack = new LinkedList<>();

    public void push(T item) {
        stack.addFirst(item);
    }

    public T pop() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return stack.removeFirst();
    }

    public T peek() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return stack.getFirst();
    }

    public int size() {
        return stack.size();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    // Extra: popTopN
    public T popTopN(int n) {
        if (n <= 0 || n > stack.size()) {
            throw new NoSuchElementException("Invalid index");
        }
        T element = null;
        for (int i = 0; i < n; i++) {
            element = stack.removeFirst();
        }
        return element;
    }
}
