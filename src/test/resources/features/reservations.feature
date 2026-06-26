Feature: Book reservations
  In order to manage waiting lists for borrowed books
  As a library system
  I want to reserve unavailable books for members

  Scenario: Reserving an unavailable book
    Given the book "Dune" is borrowed by "Alice"
    When "Bob" reserves the book "Dune"
    Then "Bob"'s reservation is recorded for the book "Dune"

  Scenario: Several reservations on the same book
    Given the book "Dune" is borrowed by "Alice"
    When "Bob" reserves the book "Dune"
    And "Chloe" reserves the book "Dune"
    Then "Bob" is the first member in the reservation queue for "Dune"
    And "Chloe" is in position 2 in the reservation queue for "Dune"

  Scenario: Returning a reserved book
    Given the book "Dune" is borrowed by "Alice"
    And "Bob" has reserved the book "Dune"
    When "Alice" returns the book "Dune"
    Then "Bob" has priority to borrow the book "Dune"

  Scenario: First member in queue borrows a returned reserved book
    Given the book "Dune" is borrowed by "Alice"
    And "Bob" has reserved the book "Dune"
    When "Alice" returns the book "Dune"
    And "Bob" borrows the returned reserved book "Dune"
    Then "Bob" has a loan for the book "Dune"
    And the reservation queue for "Dune" is empty

  Scenario: A member who is not first in queue cannot borrow a returned reserved book
    Given the book "Dune" is borrowed by "Alice"
    And "Bob" has reserved the book "Dune"
    And "Chloe" has reserved the book "Dune"
    When "Alice" returns the book "Dune"
    And "Chloe" tries to borrow the returned reserved book "Dune"
    Then the loan is rejected because "Bob" has reservation priority

  Scenario: Rejecting a reservation for a suspended member
    Given the book "Dune" is borrowed by "Alice"
    And member "Bob" is suspended
    When "Bob" tries to reserve the book "Dune"
    Then the reservation is rejected because the member is suspended

  Scenario: Rejecting a reservation for an available book
    Given the book "Dune" is available
    When "Bob" tries to reserve the book "Dune"
    Then the reservation is rejected because the book is available
