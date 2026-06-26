package org.example.mediacity.service;

import org.example.mediacity.domain.Book;
import org.example.mediacity.domain.Member;
import org.example.mediacity.domain.Reservation;
import org.example.mediacity.exception.BookAvailableException;
import org.example.mediacity.exception.SuspendedMemberException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ReservationService {
    private final LoanService loanService;
    private final Map<Book, List<Reservation>> reservationsByBook = new HashMap<>();

    public ReservationService(LoanService loanService) {
        this.loanService = Objects.requireNonNull(loanService);
    }

    public Reservation reserve(Member member, Book book, LocalDate reservationDate) {
        Objects.requireNonNull(member);
        Objects.requireNonNull(book);
        Objects.requireNonNull(reservationDate);

        if (member.isSuspended()) {
            throw new SuspendedMemberException("Member " + member.name() + " is suspended");
        }
        if (loanService.isAvailable(book)) {
            throw new BookAvailableException("Book " + book.title() + " is available");
        }

        Reservation reservation = new Reservation(member, book, reservationDate);
        reservationsByBook.computeIfAbsent(book, ignored -> new ArrayList<>()).add(reservation);
        return reservation;
    }

    public List<Reservation> reservationsFor(Book book) {
        return List.copyOf(reservationsByBook.getOrDefault(book, List.of()));
    }

    public Optional<Reservation> firstReservationFor(Book book) {
        return reservationsFor(book).stream().findFirst();
    }

    public int positionInQueue(Member member, Book book) {
        List<Reservation> reservations = reservationsFor(book);
        for (int index = 0; index < reservations.size(); index++) {
            if (reservations.get(index).member().equals(member)) {
                return index + 1;
            }
        }
        return 0;
    }
}
