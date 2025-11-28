package dev.haruki7049.itsme;

public interface Parser<T> {
  Result<T> parse(String input);

  default <U> Parser<Pair<T, U>> andThen(Parser<U> next) {
    // Return a new parser (a lambda function)
    return input -> {
      // 1. Run the current parser (this)
      Result<T> res1 = this.parse(input);

      // 2. Check the result of the first parser using Java's pattern matching.
      if (res1 instanceof Success<T> success1) {
        // 3. If success, run the next parser on the remaining input.
        Result<U> res2 = next.parse(success1.remaining());

        // 4. Check the result of the second parser.
        if (res2 instanceof Success<U> success2) {
          // Both succeeded. Combine values into a Pair.
          Pair<T, U> combinedValue = new Pair<>(success1.value(), success2.value());

          return new Success<>(combinedValue, success2.remaining());
        } else {
          // The second parser failed. Return its failure result.
          // Safe cast is necessary because Failure is generic.
          return (Result<Pair<T, U>>) res2;
        }
      } else {
        // The first parser failed. Return its failure result.
        return (Result<Pair<T, U>>) res1;
      }
    };
  }
}
