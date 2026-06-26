package org.example.mediacity.service;

import org.example.mediacity.domain.Book;
import org.example.mediacity.domain.Loan;
import org.example.mediacity.domain.Member;
import org.example.mediacity.exception.BookUnavailableException;
import org.example.mediacity.exception.SuspendedMemberException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoanService {
    private static final int LOAN_DURATION_IN_DAYS = 21;
    private static final int SIGNIFICANT_LATE_RETURN_THRESHOLD_IN_DAYS = 7;
    private static final BigDecimal LATE_FEE_PER_DAY = new BigDecimal("0.15");

    private final Map<Book, Loan> currentLoans = new HashMap<>();

    public Loan createLoan(Member member, Book book, LocalDate loanDate) {
        Objects.requireNonNull(member);
        Objects.requireNonNull(book);
        Objects.requireNonNull(loanDate);

        if (member.isSuspended()) {
            throw new SuspendedMemberException("Member " + member.name() + " is suspended");
        }
        if (currentLoans.containsKey(book)) {
            throw new BookUnavailableException("Book " + book.title() + " is unavailable");
        }

        Loan loan = new Loan(member, book, loanDate, loanDate.plusDays(LOAN_DURATION_IN_DAYS));
        currentLoans.put(book, loan);
        return loan;
    }

    public BigDecimal computeLateFee(Loan loan, LocalDate returnDate) {
        return LATE_FEE_PER_DAY.multiply(BigDecimal.valueOf(lateDays(loan, returnDate)));
    }

    public void returnBook(Loan loan, LocalDate returnDate) {
        Objects.requireNonNull(loan);
        Objects.requireNonNull(returnDate);

        loan.returnOn(returnDate);
        currentLoans.remove(loan.book());

        if (lateDays(loan, returnDate) > SIGNIFICANT_LATE_RETURN_THRESHOLD_IN_DAYS) {
            loan.member().registerSignificantLateReturn(Year.from(returnDate));
        }
    }

    public boolean isAvailable(Book book) {
        return !currentLoans.containsKey(book);
    }

    private long lateDays(Loan loan, LocalDate returnDate) {
        if (!returnDate.isAfter(loan.dueDate())) {
            return 0;
        }
        return ChronoUnit.DAYS.between(loan.dueDate(), returnDate);
    }
}
