package net.thucydides.core.steps;

/**
 * A named test method was not found in a test class.
 * This would be strange: a low-level error, probably due to a bug in the Thucydides core.
 */
public class TestMethodNotFoundException extends RuntimeException {
    public TestMethodNotFoundException(String message) {
        super(message);
    }
}
