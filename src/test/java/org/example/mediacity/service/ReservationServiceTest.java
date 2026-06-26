package org.example.mediacity.service;

import org.example.mediacity.domain.Book;
import org.example.mediacity.domain.Loan;
import org.example.mediacity.domain.Member;
import org.example.mediacity.exception.DuplicateReservationException;
import org.example.mediacity.exception.ReservationPriorityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationServiceTest {
    private LoanService loanService;
    private ReservationService reservationService;
    private Member alice;
    private Member bob;
    private Member chloe;
    private Book dune;

    @BeforeEach
    void setUp() {
        loanService = new LoanService();
        reservationService = new ReservationService(loanService);
        alice = new Member("M-001", "Alice Martin");
        bob = new Member("M-002", "Bob Dupont");
        chloe = new Member("M-003", "Chloe Bernard");
        dune = new Book("ISBN-001", "Dune");
    }

    @Test
    void shouldLetTheFirstMemberInQueueBorrowTheReturnedReservedBookAndConsumeTheReservation() {
        Loan initialLoan = loanService.createLoan(alice, dune, LocalDate.of(2026, 1, 1));
        reservationService.reserve(bob, dune, LocalDate.of(2026, 1, 2));
        loanService.returnBook(initialLoan, LocalDate.of(2026, 1, 10));

        Loan nextLoan = reservationService.borrowReservedBook(bob, dune, LocalDate.of(2026, 1, 11));

        assertThat(nextLoan.member()).isEqualTo(bob);
        assertThat(nextLoan.book()).isEqualTo(dune);
        assertThat(reservationService.reservationsFor(dune)).isEmpty();
    }

    @Test
    void shouldRejectBorrowingAReturnedReservedBookWhenMemberIsNotFirstInQueue() {
        Loan initialLoan = loanService.createLoan(alice, dune, LocalDate.of(2026, 1, 1));
        reservationService.reserve(bob, dune, LocalDate.of(2026, 1, 2));
        reservationService.reserve(chloe, dune, LocalDate.of(2026, 1, 3));
        loanService.returnBook(initialLoan, LocalDate.of(2026, 1, 10));

        assertThatThrownBy(() -> reservationService.borrowReservedBook(chloe, dune, LocalDate.of(2026, 1, 11)))
                .isInstanceOf(ReservationPriorityException.class)
                .hasMessageContaining("Bob Dupont");
    }

    @Test
    void shouldRejectADuplicateReservationForTheSameBookAndMember() {
        loanService.createLoan(alice, dune, LocalDate.of(2026, 1, 1));
        reservationService.reserve(bob, dune, LocalDate.of(2026, 1, 2));

        assertThatThrownBy(() -> reservationService.reserve(bob, dune, LocalDate.of(2026, 1, 3)))
                .isInstanceOf(DuplicateReservationException.class)
                .hasMessageContaining("Bob Dupont")
                .hasMessageContaining("Dune");
    }
}
