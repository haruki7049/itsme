package dev.haruki7049.itsme;

public sealed interface Result<T> permits Success, Failure {}
