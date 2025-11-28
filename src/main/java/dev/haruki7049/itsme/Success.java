package dev.haruki7049.itsme;

import dev.haruki7049.itsme.Result;

public record Success<T>(T value, String remaining) implements Result<T> {}
