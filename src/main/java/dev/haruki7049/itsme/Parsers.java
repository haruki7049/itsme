package dev.haruki7049.itsme;

public final class Parsers {
  public static Parser<String> string(String target) {
    return input -> {
      // Check if the input starts with the target string.
      if (input.startsWith(target)) {
        // Success: return the target string as value and the rest of the input.
        String remaining = input.substring(target.length());

        return new Success<>(target, remaining);
      } else {
        // Failure: return the original input and an error message.
        String message =
            String.format(
                "Expected '%s', but got '%s...'",
                target, input.length() > 10 ? input.substring(0, 10) : input);

        // Note: Since Failure is generic, we use the raw type here for simplicity.
        return (Result<String>) new Failure(input, message);
      }
    };
  }
}
