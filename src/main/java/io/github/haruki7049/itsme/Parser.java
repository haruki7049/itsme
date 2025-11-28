package io.github.haruki7049.itsme;

import java.util.function.Function;

/**
 * A functional interface representing a parser that takes an input string
 * and produces a ParseResult.
 *
 * @param <T> the type of the parsed value
 */
@FunctionalInterface
public interface Parser<T> {
    /**
     * Parses the given input string.
     *
     * @param input the input string to parse
     * @return the result of the parsing operation
     */
    ParseResult<T> parse(String input);

    /**
     * Maps the result of this parser using the given function.
     *
     * @param mapper the function to apply to the parsed value
     * @param <U>    the type of the mapped value
     * @return a new Parser that applies the mapper to this parser's result
     */
    default <U> Parser<U> map(Function<T, U> mapper) {
        return input -> parse(input).map(mapper);
    }

    /**
     * Chains this parser with another parser using flatMap.
     *
     * @param mapper a function that takes the result of this parser
     *               and returns another parser
     * @param <U>    the type of the next parser's result
     * @return a new Parser that applies the first parser, then uses its result
     *         to determine the next parser
     */
    default <U> Parser<U> flatMap(Function<T, Parser<U>> mapper) {
        return input -> {
            ParseResult<T> result = parse(input);
            if (result.isFailure()) {
                return ParseResult.failure(result.getErrorMessage().orElse("Unknown error"));
            }
            return mapper.apply(result.getValue().orElseThrow(() ->
                            new IllegalStateException("Successful parse result must have a value")))
                    .parse(result.getRemaining().orElse(""));
        };
    }

    /**
     * Combines this parser with another parser, keeping only the result of this parser.
     *
     * @param other the other parser to apply after this one
     * @param <U>   the type of the other parser's result
     * @return a new Parser that returns only the result of this parser
     */
    default <U> Parser<T> followedBy(Parser<U> other) {
        return flatMap(result -> other.map(ignored -> result));
    }

    /**
     * Combines this parser with another parser, keeping only the result of the other parser.
     *
     * @param other the other parser to apply after this one
     * @param <U>   the type of the other parser's result
     * @return a new Parser that returns only the result of the other parser
     */
    default <U> Parser<U> then(Parser<U> other) {
        return flatMap(ignored -> other);
    }

    /**
     * Creates an alternative parser that tries this parser first,
     * and if it fails, tries the other parser.
     *
     * @param other the alternative parser
     * @return a new Parser that tries this parser first, then the other
     */
    default Parser<T> or(Parser<T> other) {
        return input -> {
            ParseResult<T> result = parse(input);
            if (result.isSuccess()) {
                return result;
            }
            return other.parse(input);
        };
    }
}
