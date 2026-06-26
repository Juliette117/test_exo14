package org.example.mediacity.domain;

import java.time.LocalDate;
import java.util.Objects;

public record Reservation(Member member, Book book, LocalDate reservationDate) {
    public Reservation {
        Objects.requireNonNull(member);
        Objects.requireNonNull(book);
        Objects.requireNonNull(reservationDate);
    }
}
