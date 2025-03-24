import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    private fun add(a: Int, b: Int): Int {
        return a+b;
    }

    @Test
    fun simpleTest() {
        assertEquals(3, add(1, 2));
    }
}