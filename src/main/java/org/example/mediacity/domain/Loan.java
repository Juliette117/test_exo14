package org.example.mediacity.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {
    private final Member member;
    private final Book book;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(Member member, Book book, LocalDate loanDate, LocalDate dueDate) {
        this.member = Objects.requireNonNull(member);
        this.book = Objects.requireNonNull(book);
        this.loanDate = Objects.requireNonNull(loanDate);
        this.dueDate = Objects.requireNonNull(dueDate);
    }

    public Member member() {
        return member;
    }

    public Book book() {
        return book;
    }

    public LocalDate loanDate() {
        return loanDate;
    }

    public LocalDate dueDate() {
        return dueDate;
    }

    public LocalDate returnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void returnOn(LocalDate returnDate) {
        this.returnDate = Objects.requireNonNull(returnDate);
    }
}
