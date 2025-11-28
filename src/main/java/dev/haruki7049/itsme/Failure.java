package dev.haruki7049.itsme;

import dev.haruki7049.itsme.Result;

public record Failure<T>(String original, String message) implements Result<T> {}
