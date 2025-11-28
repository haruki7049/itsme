package io.github.haruki7049.itsme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParseResultTest {

    @Test
    void testSuccessResult() {
        ParseResult<String> result = ParseResult.success("value", "remaining");

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertTrue(result.getValue().isPresent());
        assertEquals("value", result.getValue().get());
        assertTrue(result.getRemaining().isPresent());
        assertEquals("remaining", result.getRemaining().get());
        assertTrue(result.getErrorMessage().isEmpty());
    }

    @Test
    void testFailureResult() {
        ParseResult<String> result = ParseResult.failure("error message");

        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertTrue(result.getValue().isEmpty());
        assertTrue(result.getRemaining().isEmpty());
        assertTrue(result.getErrorMessage().isPresent());
        assertEquals("error message", result.getErrorMessage().get());
    }

    @Test
    void testMapOnSuccess() {
        ParseResult<Integer> result = ParseResult.success(5, "rest");
        ParseResult<Integer> mapped = result.map(x -> x * 2);

        assertTrue(mapped.isSuccess());
        assertEquals(10, mapped.getValue().get());
        assertEquals("rest", mapped.getRemaining().get());
    }

    @Test
    void testMapOnFailure() {
        ParseResult<Integer> result = ParseResult.failure("error");
        ParseResult<Integer> mapped = result.map(x -> x * 2);

        assertTrue(mapped.isFailure());
        assertEquals("error", mapped.getErrorMessage().get());
    }

    @Test
    void testEqualsAndHashCode() {
        ParseResult<String> result1 = ParseResult.success("value", "remaining");
        ParseResult<String> result2 = ParseResult.success("value", "remaining");
        ParseResult<String> result3 = ParseResult.success("other", "remaining");

        assertEquals(result1, result2);
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1, result3);
    }

    @Test
    void testToString() {
        ParseResult<String> success = ParseResult.success("value", "rest");
        ParseResult<String> failure = ParseResult.failure("error");

        assertTrue(success.toString().contains("success"));
        assertTrue(success.toString().contains("value"));
        assertTrue(failure.toString().contains("failure"));
        assertTrue(failure.toString().contains("error"));
    }
}
