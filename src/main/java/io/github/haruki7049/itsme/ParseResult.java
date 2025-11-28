package io.github.haruki7049.itsme;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents the result of a parsing operation.
 *
 * @param <T> the type of the parsed value
 */
public final class ParseResult<T> {
    private final T value;
    private final String remaining;
    private final boolean success;
    private final String errorMessage;

    private ParseResult(T value, String remaining, boolean success, String errorMessage) {
        this.value = value;
        this.remaining = remaining;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a successful parse result.
     *
     * @param value     the parsed value
     * @param remaining the remaining input string
     * @param <T>       the type of the parsed value
     * @return a successful ParseResult
     */
    public static <T> ParseResult<T> success(T value, String remaining) {
        return new ParseResult<>(value, remaining, true, null);
    }

    /**
     * Creates a failed parse result.
     *
     * @param errorMessage the error message describing the failure
     * @param <T>          the type of the parsed value
     * @return a failed ParseResult
     */
    public static <T> ParseResult<T> failure(String errorMessage) {
        return new ParseResult<>(null, null, false, errorMessage);
    }

    /**
     * Returns whether the parsing was successful.
     *
     * @return true if parsing was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns whether the parsing failed.
     *
     * @return true if parsing failed, false otherwise
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * Returns the parsed value if successful.
     *
     * @return an Optional containing the parsed value if successful, empty otherwise
     */
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    /**
     * Returns the remaining input string after parsing.
     *
     * @return an Optional containing the remaining input if successful, empty otherwise
     */
    public Optional<String> getRemaining() {
        return Optional.ofNullable(remaining);
    }

    /**
     * Returns the error message if parsing failed.
     *
     * @return an Optional containing the error message if failed, empty otherwise
     */
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    /**
     * Maps the parsed value using the given function.
     *
     * @param mapper the function to apply to the parsed value
     * @param <U>    the type of the mapped value
     * @return a new ParseResult with the mapped value
     */
    public <U> ParseResult<U> map(Function<T, U> mapper) {
        if (success) {
            return ParseResult.success(mapper.apply(value), remaining);
        } else {
            return ParseResult.failure(errorMessage);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParseResult<?> that = (ParseResult<?>) o;
        return success == that.success &&
                Objects.equals(value, that.value) &&
                Objects.equals(remaining, that.remaining) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, remaining, success, errorMessage);
    }

    @Override
    public String toString() {
        if (success) {
            return "ParseResult.success(" + value + ", \"" + remaining + "\")";
        } else {
            return "ParseResult.failure(\"" + errorMessage + "\")";
        }
    }
}
