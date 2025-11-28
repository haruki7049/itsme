package dev.haruki7049.itsme;

public record Success<T>(T value, String remaining) implements Result<T> {}
