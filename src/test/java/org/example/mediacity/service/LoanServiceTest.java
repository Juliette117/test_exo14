package org.example.mediacity.service;

import org.example.mediacity.domain.Book;
import org.example.mediacity.domain.Loan;
import org.example.mediacity.domain.Member;
import org.example.mediacity.exception.BookUnavailableException;
import org.example.mediacity.exception.SuspendedMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanServiceTest {
    private LoanService loanService;
    private Member alice;
    private Book dune;

    @BeforeEach
    void setUp() {
        loanService = new LoanService();
        alice = new Member("M-001", "Alice Martin");
        dune = new Book("ISBN-001", "Dune");
    }

    @Test
    void shouldCreateALoanWithADueDateTwentyOneDaysAfterTheLoanDate() {
        LocalDate loanDate = LocalDate.of(2026, 1, 5);

        Loan loan = loanService.createLoan(alice, dune, loanDate);

        assertThat(loan.member()).isEqualTo(alice);
        assertThat(loan.book()).isEqualTo(dune);
        assertThat(loan.loanDate()).isEqualTo(loanDate);
        assertThat(loan.dueDate()).isEqualTo(LocalDate.of(2026, 1, 26));
    }

    @Test
    void shouldRejectALoanWhenTheBookIsAlreadyBorrowed() {
        loanService.createLoan(alice, dune, LocalDate.of(2026, 1, 5));
        Member bob = new Member("M-002", "Bob Dupont");

        assertThatThrownBy(() -> loanService.createLoan(bob, dune, LocalDate.of(2026, 1, 6)))
                .isInstanceOf(BookUnavailableException.class)
                .hasMessageContaining("Dune");
    }

    @Test
    void shouldComputeLateFeesAtFifteenCentsPerLateDay() {
        Loan loan = loanService.createLoan(alice, dune, LocalDate.of(2026, 1, 5));

        BigDecimal lateFee = loanService.computeLateFee(loan, LocalDate.of(2026, 1, 30));

        assertThat(lateFee).isEqualByComparingTo("0.60");
    }

    @Test
    void shouldNotApplyLateFeesWhenTheBookIsReturnedOnTime() {
        Loan loan = loanService.createLoan(alice, dune, LocalDate.of(2026, 1, 5));

        BigDecimal lateFee = loanService.computeLateFee(loan, LocalDate.of(2026, 1, 26));

        assertThat(lateFee).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldSuspendAMemberAfterThreeSignificantLateReturnsInTheSameYear() {
        registerSignificantLateReturn(alice, "ISBN-101", LocalDate.of(2026, 1, 1));
        registerSignificantLateReturn(alice, "ISBN-102", LocalDate.of(2026, 3, 1));

        assertThat(alice.isSuspended()).isFalse();

        registerSignificantLateReturn(alice, "ISBN-103", LocalDate.of(2026, 5, 1));

        assertThat(alice.isSuspended()).isTrue();
    }

    @Test
    void shouldRejectALoanForASuspendedMember() {
        alice.suspend();

        assertThatThrownBy(() -> loanService.createLoan(alice, dune, LocalDate.of(2026, 1, 5)))
                .isInstanceOf(SuspendedMemberException.class)
                .hasMessageContaining("Alice Martin");
    }

    @Test
    void shouldMakeABookAvailableAgainAfterReturn() {
        Loan loan = loanService.createLoan(alice, dune, LocalDate.of(2026, 1, 5));
        loanService.returnBook(loan, LocalDate.of(2026, 1, 20));

        Loan nextLoan = loanService.createLoan(new Member("M-002", "Bob Dupont"), dune, LocalDate.of(2026, 1, 21));

        assertThat(nextLoan.book()).isEqualTo(dune);
    }

    private void registerSignificantLateReturn(Member member, String isbn, LocalDate loanDate) {
        Loan loan = loanService.createLoan(member, new Book(isbn, "Book " + isbn), loanDate);
        loanService.returnBook(loan, loan.dueDate().plusDays(8));
    }
}
