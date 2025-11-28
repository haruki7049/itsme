package io.github.haruki7049.itsme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testMap() {
        Parser<Character> charParser = input -> {
            if (input.isEmpty()) {
                return ParseResult.failure("empty input");
            }
            return ParseResult.success(input.charAt(0), input.substring(1));
        };

        Parser<Integer> intParser = charParser.map(c -> (int) c);
        ParseResult<Integer> result = intParser.parse("abc");

        assertTrue(result.isSuccess());
        assertEquals((int) 'a', result.getValue().get());
        assertEquals("bc", result.getRemaining().get());
    }

    @Test
    void testFlatMap() {
        Parser<Character> charParser = input -> {
            if (input.isEmpty()) {
                return ParseResult.failure("empty input");
            }
            return ParseResult.success(input.charAt(0), input.substring(1));
        };

        Parser<String> combined = charParser.flatMap(c ->
                charParser.map(c2 -> "" + c + c2)
        );

        ParseResult<String> result = combined.parse("abc");

        assertTrue(result.isSuccess());
        assertEquals("ab", result.getValue().get());
        assertEquals("c", result.getRemaining().get());
    }

    @Test
    void testOr() {
        Parser<String> helloParser = input ->
                input.startsWith("hello")
                        ? ParseResult.success("hello", input.substring(5))
                        : ParseResult.failure("expected 'hello'");

        Parser<String> worldParser = input ->
                input.startsWith("world")
                        ? ParseResult.success("world", input.substring(5))
                        : ParseResult.failure("expected 'world'");

        Parser<String> combined = helloParser.or(worldParser);

        ParseResult<String> result1 = combined.parse("hello!");
        assertTrue(result1.isSuccess());
        assertEquals("hello", result1.getValue().get());

        ParseResult<String> result2 = combined.parse("world!");
        assertTrue(result2.isSuccess());
        assertEquals("world", result2.getValue().get());

        ParseResult<String> result3 = combined.parse("other");
        assertTrue(result3.isFailure());
    }

    @Test
    void testFollowedBy() {
        Parser<Character> aParser = Parsers.character('a');
        Parser<Character> bParser = Parsers.character('b');

        Parser<Character> combined = aParser.followedBy(bParser);
        ParseResult<Character> result = combined.parse("abc");

        assertTrue(result.isSuccess());
        assertEquals('a', result.getValue().get());
        assertEquals("c", result.getRemaining().get());
    }

    @Test
    void testThen() {
        Parser<Character> aParser = Parsers.character('a');
        Parser<Character> bParser = Parsers.character('b');

        Parser<Character> combined = aParser.then(bParser);
        ParseResult<Character> result = combined.parse("abc");

        assertTrue(result.isSuccess());
        assertEquals('b', result.getValue().get());
        assertEquals("c", result.getRemaining().get());
    }
}
