package simple.Tqs;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TqsStackTest {

    private TqsStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new TqsStack<>();
    }

    @Test
    void stackShouldBeEmptyOnConstruction() {
        assertThat(stack.isEmpty()).isTrue();
        assertThat(stack.size()).isZero();
    }

    @Test
    void afterPushStackIsNotEmpty() {
        stack.push(10);
        assertThat(stack.isEmpty()).isFalse();
        assertThat(stack.size()).isEqualTo(1);
    }

    @Test
    void pushThenPopReturnsSameValue() {
        stack.push(42);
        assertThat(stack.pop()).isEqualTo(42);
        assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    void pushThenPeekReturnsSameValueButSizeUnchanged() {
        stack.push(7);
        assertThat(stack.peek()).isEqualTo(7);
        assertThat(stack.size()).isEqualTo(1);
    }

    @Test
    void multiplePushesAndPopsWorkAsExpected() {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertThat(stack.pop()).isEqualTo(3);
        assertThat(stack.pop()).isEqualTo(2);
        assertThat(stack.pop()).isEqualTo(1);
        assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    void poppingFromEmptyStackThrows() {
        assertThatThrownBy(() -> stack.pop())
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void peekingIntoEmptyStackThrows() {
        assertThatThrownBy(() -> stack.peek())
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void popTopNReturnsNthElement() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);

        // topo é 4, depois 3, depois 2
        int result = stack.popTopN(3);
        assertThat(result).isEqualTo(2);
        assertThat(stack.size()).isEqualTo(1);
    }

    @Test
    void popTopNWithInvalidNThrows() {
        stack.push(1);
        assertThatThrownBy(() -> stack.popTopN(5))
            .isInstanceOf(NoSuchElementException.class);
    }
}

