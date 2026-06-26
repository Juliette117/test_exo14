package org.example.mediacity.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.mediacity.domain.Book;
import org.example.mediacity.domain.Loan;
import org.example.mediacity.domain.Member;
import org.example.mediacity.domain.Reservation;
import org.example.mediacity.exception.BookAvailableException;
import org.example.mediacity.exception.SuspendedMemberException;
import org.example.mediacity.service.LoanService;
import org.example.mediacity.service.ReservationService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ReservationSteps {
    private LoanService loanService;
    private ReservationService reservationService;
    private Map<String, Member> members;
    private Map<String, Book> books;
    private Map<String, Loan> loans;
    private Throwable error;

    @Before
    public void setUp() {
        loanService = new LoanService();
        reservationService = new ReservationService(loanService);
        members = new HashMap<>();
        books = new HashMap<>();
        loans = new HashMap<>();
        error = null;
    }

    @Given("the book {string} is borrowed by {string}")
    public void theBookIsBorrowedBy(String title, String memberName) {
        Book book = book(title);
        Member member = member(memberName);
        loans.put(title, loanService.createLoan(member, book, LocalDate.of(2026, 1, 1)));
    }

    @Given("the book {string} is available")
    public void theBookIsAvailable(String title) {
        book(title);
    }

    @Given("{string} has reserved the book {string}")
    public void hasReservedTheBook(String memberName, String title) {
        reservationService.reserve(member(memberName), book(title), LocalDate.of(2026, 1, 2));
    }

    @Given("member {string} is suspended")
    public void memberIsSuspended(String memberName) {
        member(memberName).suspend();
    }

    @When("{string} reserves the book {string}")
    public void reservesTheBook(String memberName, String title) {
        reservationService.reserve(member(memberName), book(title), LocalDate.of(2026, 1, 2));
    }

    @When("{string} tries to reserve the book {string}")
    public void triesToReserveTheBook(String memberName, String title) {
        error = catchThrowable(() -> reservationService.reserve(member(memberName), book(title), LocalDate.of(2026, 1, 2)));
    }

    @When("{string} returns the book {string}")
    public void returnsTheBook(String memberName, String title) {
        loanService.returnBook(loans.get(title), LocalDate.of(2026, 1, 10));
    }

    @Then("{string}'s reservation is recorded for the book {string}")
    public void reservationIsRecordedForTheBook(String memberName, String title) {
        assertThat(reservationService.reservationsFor(book(title)))
                .extracting(reservation -> reservation.member().name())
                .containsExactly(memberName);
    }

    @Then("{string} is the first member in the reservation queue for {string}")
    public void firstMemberInReservationQueue(String memberName, String title) {
        assertThat(reservationService.firstReservationFor(book(title)))
                .map(Reservation::member)
                .map(Member::name)
                .contains(memberName);
    }

    @Then("{string} is in position {int} in the reservation queue for {string}")
    public void memberIsInPosition(String memberName, int position, String title) {
        assertThat(reservationService.positionInQueue(member(memberName), book(title))).isEqualTo(position);
    }

    @Then("{string} has priority to borrow the book {string}")
    public void hasPriorityToBorrowTheBook(String memberName, String title) {
        assertThat(reservationService.firstReservationFor(book(title)))
                .map(Reservation::member)
                .map(Member::name)
                .contains(memberName);
    }

    @Then("the reservation is rejected because the member is suspended")
    public void reservationIsRejectedBecauseMemberIsSuspended() {
        assertThat(error).isInstanceOf(SuspendedMemberException.class);
    }

    @Then("the reservation is rejected because the book is available")
    public void reservationIsRejectedBecauseBookIsAvailable() {
        assertThat(error).isInstanceOf(BookAvailableException.class);
    }

    private Member member(String name) {
        return members.computeIfAbsent(name, key -> new Member("M-" + members.size(), key));
    }

    private Book book(String title) {
        return books.computeIfAbsent(title, key -> new Book("ISBN-" + books.size(), key));
    }
}
