package dev.haruki7049.itsme;

public record Failure<T>(String original, String message) implements Result<T> {}
