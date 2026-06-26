package org.example.mediacity.domain;

import java.util.Objects;

public record Book(String isbn, String title) {
    public Book {
        Objects.requireNonNull(isbn);
        Objects.requireNonNull(title);
    }
}
