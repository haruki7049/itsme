package io.github.haruki7049.itsme;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParsersTest {

    @Test
    void testCharacter() {
        Parser<Character> parser = Parsers.character('a');

        ParseResult<Character> result = parser.parse("abc");
        assertTrue(result.isSuccess());
        assertEquals('a', result.getValue().get());
        assertEquals("bc", result.getRemaining().get());

        ParseResult<Character> failure = parser.parse("xyz");
        assertTrue(failure.isFailure());

        ParseResult<Character> emptyFailure = parser.parse("");
        assertTrue(emptyFailure.isFailure());
    }

    @Test
    void testDigit() {
        Parser<Character> parser = Parsers.digit();

        ParseResult<Character> result = parser.parse("123");
        assertTrue(result.isSuccess());
        assertEquals('1', result.getValue().get());
        assertEquals("23", result.getRemaining().get());

        ParseResult<Character> failure = parser.parse("abc");
        assertTrue(failure.isFailure());
    }

    @Test
    void testLetter() {
        Parser<Character> parser = Parsers.letter();

        ParseResult<Character> result = parser.parse("abc");
        assertTrue(result.isSuccess());
        assertEquals('a', result.getValue().get());

        ParseResult<Character> failure = parser.parse("123");
        assertTrue(failure.isFailure());
    }

    @Test
    void testAlphanumeric() {
        Parser<Character> parser = Parsers.alphanumeric();

        assertTrue(parser.parse("abc").isSuccess());
        assertTrue(parser.parse("123").isSuccess());
        assertTrue(parser.parse("!@#").isFailure());
    }

    @Test
    void testWhitespace() {
        Parser<Character> parser = Parsers.whitespace();

        assertTrue(parser.parse(" abc").isSuccess());
        assertTrue(parser.parse("\tabc").isSuccess());
        assertTrue(parser.parse("abc").isFailure());
    }

    @Test
    void testString() {
        Parser<String> parser = Parsers.string("hello");

        ParseResult<String> result = parser.parse("hello world");
        assertTrue(result.isSuccess());
        assertEquals("hello", result.getValue().get());
        assertEquals(" world", result.getRemaining().get());

        assertTrue(parser.parse("world").isFailure());
        assertTrue(parser.parse("hel").isFailure());
    }

    @Test
    void testMany() {
        Parser<List<Character>> parser = Parsers.many(Parsers.digit());

        ParseResult<List<Character>> result = parser.parse("123abc");
        assertTrue(result.isSuccess());
        assertEquals(List.of('1', '2', '3'), result.getValue().get());
        assertEquals("abc", result.getRemaining().get());

        // many should succeed with empty result on no matches
        ParseResult<List<Character>> emptyResult = parser.parse("abc");
        assertTrue(emptyResult.isSuccess());
        assertTrue(emptyResult.getValue().get().isEmpty());
    }

    @Test
    void testMany1() {
        Parser<List<Character>> parser = Parsers.many1(Parsers.digit());

        ParseResult<List<Character>> result = parser.parse("123abc");
        assertTrue(result.isSuccess());
        assertEquals(List.of('1', '2', '3'), result.getValue().get());

        // many1 should fail with no matches
        ParseResult<List<Character>> failure = parser.parse("abc");
        assertTrue(failure.isFailure());
    }

    @Test
    void testOptional() {
        Parser<Character> parser = Parsers.optional(Parsers.digit());

        ParseResult<Character> result = parser.parse("123");
        assertTrue(result.isSuccess());
        assertEquals('1', result.getValue().get());

        ParseResult<Character> noMatch = parser.parse("abc");
        assertTrue(noMatch.isSuccess());
        assertNull(noMatch.getValue().orElse(null));
        assertEquals("abc", noMatch.getRemaining().get());
    }

    @Test
    void testInteger() {
        Parser<Integer> parser = Parsers.integer();

        ParseResult<Integer> result = parser.parse("123abc");
        assertTrue(result.isSuccess());
        assertEquals(123, result.getValue().get());
        assertEquals("abc", result.getRemaining().get());

        assertTrue(parser.parse("abc").isFailure());
    }

    @Test
    void testSkipWhitespace() {
        Parser<Void> parser = Parsers.skipWhitespace();

        ParseResult<Void> result = parser.parse("   abc");
        assertTrue(result.isSuccess());
        assertEquals("abc", result.getRemaining().get());

        ParseResult<Void> noWhitespace = parser.parse("abc");
        assertTrue(noWhitespace.isSuccess());
        assertEquals("abc", noWhitespace.getRemaining().get());
    }

    @Test
    void testBetween() {
        Parser<Integer> parser = Parsers.between(
                Parsers.character('('),
                Parsers.integer(),
                Parsers.character(')')
        );

        ParseResult<Integer> result = parser.parse("(123)abc");
        assertTrue(result.isSuccess());
        assertEquals(123, result.getValue().get());
        assertEquals("abc", result.getRemaining().get());

        assertTrue(parser.parse("123)").isFailure());
        assertTrue(parser.parse("(123").isFailure());
    }

    @Test
    void testSepBy() {
        Parser<List<Integer>> parser = Parsers.sepBy(
                Parsers.integer(),
                Parsers.character(',')
        );

        ParseResult<List<Integer>> result = parser.parse("1,2,3abc");
        assertTrue(result.isSuccess());
        assertEquals(List.of(1, 2, 3), result.getValue().get());
        assertEquals("abc", result.getRemaining().get());

        ParseResult<List<Integer>> singleResult = parser.parse("42abc");
        assertTrue(singleResult.isSuccess());
        assertEquals(List.of(42), singleResult.getValue().get());

        ParseResult<List<Integer>> emptyResult = parser.parse("abc");
        assertTrue(emptyResult.isSuccess());
        assertTrue(emptyResult.getValue().get().isEmpty());
    }

    @Test
    void testSatisfy() {
        Parser<Character> parser = Parsers.satisfy(c -> c == 'x' || c == 'y', "'x' or 'y'");

        assertTrue(parser.parse("xyz").isSuccess());
        assertTrue(parser.parse("yxz").isSuccess());
        assertTrue(parser.parse("abc").isFailure());
    }
}
