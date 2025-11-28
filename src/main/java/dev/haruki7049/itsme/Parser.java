package dev.haruki7049.itsme;

public interface Parser<T> {
  Result<T> parse(String input);
}
