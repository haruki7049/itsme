package io.github.haruki7049.itsme;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A collection of basic parser combinators.
 */
public final class Parsers {

    private Parsers() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a parser that matches a specific character.
     *
     * @param c the character to match
     * @return a parser that matches the specified character
     */
    public static Parser<Character> character(char c) {
        return input -> {
            if (input.isEmpty()) {
                return ParseResult.failure("Unexpected end of input, expected '" + c + "'");
            }
            if (input.charAt(0) == c) {
                return ParseResult.success(c, input.substring(1));
            }
            return ParseResult.failure("Expected '" + c + "' but found '" + input.charAt(0) + "'");
        };
    }

    /**
     * Creates a parser that matches a character satisfying the given predicate.
     *
     * @param predicate the predicate to test characters against
     * @param description a description of what characters are expected
     * @return a parser that matches characters satisfying the predicate
     */
    public static Parser<Character> satisfy(Predicate<Character> predicate, String description) {
        return input -> {
            if (input.isEmpty()) {
                return ParseResult.failure("Unexpected end of input, expected " + description);
            }
            char c = input.charAt(0);
            if (predicate.test(c)) {
                return ParseResult.success(c, input.substring(1));
            }
            return ParseResult.failure("Expected " + description + " but found '" + c + "'");
        };
    }

    /**
     * Creates a parser that matches any single digit character (0-9).
     *
     * @return a parser that matches a digit
     */
    public static Parser<Character> digit() {
        return satisfy(Character::isDigit, "a digit");
    }

    /**
     * Creates a parser that matches any single letter character.
     *
     * @return a parser that matches a letter
     */
    public static Parser<Character> letter() {
        return satisfy(Character::isLetter, "a letter");
    }

    /**
     * Creates a parser that matches any single alphanumeric character.
     *
     * @return a parser that matches an alphanumeric character
     */
    public static Parser<Character> alphanumeric() {
        return satisfy(Character::isLetterOrDigit, "an alphanumeric character");
    }

    /**
     * Creates a parser that matches any single whitespace character.
     *
     * @return a parser that matches a whitespace character
     */
    public static Parser<Character> whitespace() {
        return satisfy(Character::isWhitespace, "whitespace");
    }

    /**
     * Creates a parser that matches a specific string.
     *
     * @param s the string to match
     * @return a parser that matches the specified string
     */
    public static Parser<String> string(String s) {
        return input -> {
            if (input.startsWith(s)) {
                return ParseResult.success(s, input.substring(s.length()));
            }
            if (input.length() < s.length()) {
                return ParseResult.failure("Unexpected end of input, expected \"" + s + "\"");
            }
            return ParseResult.failure("Expected \"" + s + "\" but found \"" +
                    input.substring(0, Math.min(s.length(), input.length())) + "\"");
        };
    }

    /**
     * Creates a parser that applies the given parser zero or more times.
     *
     * @param parser the parser to repeat
     * @param <T>    the type of the parsed values
     * @return a parser that matches zero or more occurrences
     */
    public static <T> Parser<List<T>> many(Parser<T> parser) {
        return input -> {
            List<T> results = new ArrayList<>();
            String remaining = input;
            while (true) {
                ParseResult<T> result = parser.parse(remaining);
                if (result.isFailure()) {
                    break;
                }
                results.add(result.getValue().orElseThrow(() ->
                        new IllegalStateException("Successful parse result must have a value")));
                remaining = result.getRemaining().orElse("");
            }
            return ParseResult.success(results, remaining);
        };
    }

    /**
     * Creates a parser that applies the given parser one or more times.
     *
     * @param parser the parser to repeat
     * @param <T>    the type of the parsed values
     * @return a parser that matches one or more occurrences
     */
    public static <T> Parser<List<T>> many1(Parser<T> parser) {
        return input -> {
            ParseResult<List<T>> result = many(parser).parse(input);
            if (result.isSuccess() && result.getValue().map(List::isEmpty).orElse(true)) {
                return ParseResult.failure("Expected at least one match");
            }
            return result;
        };
    }

    /**
     * Creates a parser that makes the given parser optional.
     *
     * @param parser the parser to make optional
     * @param <T>    the type of the parsed value
     * @return a parser that matches zero or one occurrence
     */
    public static <T> Parser<T> optional(Parser<T> parser) {
        return input -> {
            ParseResult<T> result = parser.parse(input);
            if (result.isSuccess()) {
                return result;
            }
            return ParseResult.success(null, input);
        };
    }

    /**
     * Creates a parser that parses a sequence of digits as an integer.
     *
     * @return a parser that matches and returns an integer
     */
    public static Parser<Integer> integer() {
        return many1(digit()).map(chars -> {
            StringBuilder sb = new StringBuilder();
            for (Character c : chars) {
                sb.append(c);
            }
            return Integer.parseInt(sb.toString());
        });
    }

    /**
     * Creates a parser that skips zero or more whitespace characters.
     *
     * @return a parser that skips whitespace
     */
    public static Parser<Void> skipWhitespace() {
        return many(whitespace()).map(ignored -> null);
    }

    /**
     * Creates a parser that parses content between two delimiters.
     *
     * @param open    the opening delimiter parser
     * @param content the content parser
     * @param close   the closing delimiter parser
     * @param <O>     the type of the opening delimiter
     * @param <T>     the type of the content
     * @param <C>     the type of the closing delimiter
     * @return a parser that matches content between delimiters
     */
    public static <O, T, C> Parser<T> between(Parser<O> open, Parser<T> content, Parser<C> close) {
        return open.then(content).followedBy(close);
    }

    /**
     * Creates a parser that parses content separated by a delimiter.
     *
     * @param content   the content parser
     * @param separator the separator parser
     * @param <T>       the type of the content
     * @param <S>       the type of the separator
     * @return a parser that matches content separated by the separator
     */
    public static <T, S> Parser<List<T>> sepBy(Parser<T> content, Parser<S> separator) {
        return input -> {
            List<T> results = new ArrayList<>();
            ParseResult<T> first = content.parse(input);
            if (first.isFailure()) {
                return ParseResult.success(results, input);
            }
            results.add(first.getValue().orElseThrow(() ->
                    new IllegalStateException("Successful parse result must have a value")));
            String remaining = first.getRemaining().orElse("");

            while (true) {
                ParseResult<S> sepResult = separator.parse(remaining);
                if (sepResult.isFailure()) {
                    break;
                }
                ParseResult<T> nextResult = content.parse(sepResult.getRemaining().orElse(""));
                if (nextResult.isFailure()) {
                    break;
                }
                results.add(nextResult.getValue().orElseThrow(() ->
                        new IllegalStateException("Successful parse result must have a value")));
                remaining = nextResult.getRemaining().orElse("");
            }
            return ParseResult.success(results, remaining);
        };
    }
}
